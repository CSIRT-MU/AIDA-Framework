package cz.muni.csirt.aida.sanitization;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.idea.kafka.IdeaSerde;
import cz.muni.csirt.aida.sanitization.transformations.mappers.GetFieldsOfInterest;
import cz.muni.csirt.aida.sanitization.transformations.mappers.RemoveCategory;
import cz.muni.csirt.aida.sanitization.transformations.mappers.RemoveNodesWithoutName;
import cz.muni.csirt.aida.sanitization.transformations.mappers.RemoveWardenFilerNodes;
import cz.muni.csirt.aida.sanitization.transformations.predicates.HasTargetPort;
import cz.muni.csirt.aida.sanitization.transformations.predicates.HasCategoryWithPrefix;
import cz.muni.csirt.aida.sanitization.transformations.predicates.HasSourceIp;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Sanitization {

    private static final String APPLICATION_ID = "aida-sanitization";

    private static final Logger logger = LoggerFactory.getLogger(Sanitization.class);

    @Parameter(names = {"--kafka-brokers"}, description = "List of Kafka bootstrap servers separated by comma")
    private String kafkaBrokers = "127.0.0.1:9092";

    @Parameter(names = {"--kafka-input-topic"}, description = "Kafka topic name for incoming IDEA events")
    private String kafkaInputTopic = "input";

    @Parameter(names = {"--kafka-output-topic"}, description = "Kafka topic name for sanitized IDEA events")
    private String kafkaOutputTopic = "sanitized";

    @Parameter(names = {"-h", "--help"}, help = true)
    private boolean help;

    public static void main(String[] args) {
        Sanitization sanitization = new Sanitization();
        JCommander jcommander = JCommander.newBuilder()
                .addObject(sanitization)
                .programName("Sanitization")
                .build();
        jcommander.parse(args);

        if (sanitization.help) {
            jcommander.usage();
            return;
        }

        sanitization.run();
    }

    private void run() {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, IdeaSerde.class.getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Idea> inputStream = builder.stream(kafkaInputTopic);
        inputStream
                // Drop nulls
                .filter((key, value) -> value != null)

                // Drop alerts of no interest for AIDA
                .filterNot(new HasCategoryWithPrefix("Vulnerable"))
                .filterNot(new HasCategoryWithPrefix("Abusive.Sexual"))

                // Drop alerts without Source IP or Target Port
                .filter(new HasSourceIp())
                .filter(new HasTargetPort())

                // Remove unwanted Nodes
                .mapValues(new RemoveNodesWithoutName())
                .mapValues(new RemoveWardenFilerNodes())

                // Remove Test categories
                .mapValues(new RemoveCategory("Test"))

                // Output just wanted fields
                .mapValues(new GetFieldsOfInterest())
                .to(kafkaOutputTopic);

        Topology topology = builder.build();
        logger.debug("Following Kafka Streams Topologies will be deployed.\n{}", topology.describe());

        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
