package cz.muni.csirt.aida.matching;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.Module;
import com.espertech.esper.common.client.soda.EPStatementObjectModel;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esperio.kafka.EsperIOKafkaConfig;
import com.espertech.esperio.kafka.EsperIOKafkaInputAdapterPlugin;
import com.espertech.esperio.kafka.EsperIOKafkaInputProcessorDefault;
import com.espertech.esperio.kafka.EsperIOKafkaInputSubscriberByTopicList;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.idea.kafka.IdeaDeserializer;
import cz.muni.csirt.aida.matching.esper.soda.IdeaWindows;
import cz.muni.csirt.aida.matching.esper.soda.RuleStatements;
import cz.muni.csirt.aida.matching.esper.listeners.RuleListener;
import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.repository.SqliteRuleRepository;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class RulesMatching {

    private final static Logger logger = LoggerFactory.getLogger(RulesMatching.class);

    @Parameter(names = {"--kafka-brokers"}, description = "List of Kafka bootstrap servers separated by comma")
    private String kafkaBrokers = "127.0.0.1:9092";

    @Parameter(names = {"--kafka-consumer-group"}, description = "Kafka consumer group ID")
    private String kafkaConsumerGroupId = "esper-event-matcher";

    @Parameter(names = {"--kafka-input-topic"}, description = "Kafka topic name for incoming IDEA events")
    private String kafkaInputTopic = "aggregated";

    @Parameter(names = {"--kafka-predictions-topic"}, description = "Kafka topic name for predicted IDEA events")
    private String kafkaPredictionsTopic = "predictions";

    @Parameter(names = {"--kafka-observations-topic"},
            description = "Kafka topic name for observations of matched rules")
    private String kafkaObservationsTopic = "observations";

    @Parameter(names = {"--sqlite-url"},
            description = "SQLite URL from which will be rules fetched (example 'jdbc:sqlite:/var/aida/rules/rules.db')",
            required = true)
    private String sqliteUrl;

    @Parameter(names = {"-h", "--help"}, help = true)
    private boolean help;

    public static void main(String[] args) throws InterruptedException {
        RulesMatching rulesMatching = new RulesMatching();
        JCommander jcommander = JCommander.newBuilder()
                .addObject(rulesMatching)
                .programName("RulesMatching")
                .build();
        jcommander.parse(args);

        if (rulesMatching.help) {
            jcommander.usage();
            return;
        }

        rulesMatching.run();
    }

    private Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType(Idea.class);

        Properties props = new Properties();

        // Kafka Consumer Properties
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IdeaDeserializer.class.getName());

        // EsperIO Kafka Input Adapter Properties
        props.put(EsperIOKafkaConfig.INPUT_SUBSCRIBER_CONFIG, EsperIOKafkaInputSubscriberByTopicList.class.getName());
        props.put(EsperIOKafkaConfig.TOPICS_CONFIG, kafkaInputTopic);
        props.put(EsperIOKafkaConfig.INPUT_PROCESSOR_CONFIG, EsperIOKafkaInputProcessorDefault.class.getName());

        configuration.getRuntime().addPluginLoader(
                "KafkaInput",
                EsperIOKafkaInputAdapterPlugin.class.getName(),
                props,
                null);

        return configuration;
    }

    public void run() throws InterruptedException {

        // Obtain Rules

        List<Rule> rules = new SqliteRuleRepository(sqliteUrl).getActiveRules();

        // Get statements for rules

        List<EPStatementObjectModel> predictionStatements = rules.stream()
                .map(RuleStatements::prediction).collect(Collectors.toList());
        List<EPStatementObjectModel> observationStatements = rules.stream()
                .map(RuleStatements::observation).collect(Collectors.toList());

        logger.info("Following statements for predictions will be deployed:");
        predictionStatements.stream().map(EPStatementObjectModel::toEPL).forEach(logger::info);

        logger.info("Following statements for observation of rules will be deployed:");
        observationStatements.stream().map(EPStatementObjectModel::toEPL).forEach(logger::info);

        // Pack statements into modules

        Module windowModule = EsperFacade.wrapInModule(
                IdeaWindows.createIdeaWindow(),
                IdeaWindows.insertIntoIdeaWindow());
        logger.info("Following window will be used as event source:");
        windowModule.getItems().forEach(x -> logger.info(x.getModel().toEPL()));

        Module predictionsModule = EsperFacade.wrapInModule(predictionStatements);
        predictionsModule.setImports(
                Collections.singleton(cz.muni.csirt.aida.matching.esper.soda.annotations.Rule.class.getName()));

        Module observationsModule = EsperFacade.wrapInModule(observationStatements);
        observationsModule.setImports(
                Collections.singleton(cz.muni.csirt.aida.matching.esper.soda.annotations.Rule.class.getName()));

        // Compile rules

        Configuration configuration = getConfiguration();
        CompilerArguments compilerArgs = new CompilerArguments(configuration);

        EPCompiled windowCompiled = EsperFacade.compile(windowModule, compilerArgs);
        compilerArgs.getPath().add(windowCompiled);

        EPCompiled predictionsCompiled = EsperFacade.compile(predictionsModule, compilerArgs);
        EPCompiled observationsCompiled = EsperFacade.compile(observationsModule, compilerArgs);

        // Deploy rules

        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);

        EsperFacade.deploy(runtime, windowCompiled);
        EPDeployment predictionsDeployment = EsperFacade.deploy(runtime, predictionsCompiled);
        EPDeployment observationsDeployment = EsperFacade.deploy(runtime, observationsCompiled);

        // Attach listeners

        RuleListener predictionListener = new RuleListener(kafkaBrokers, kafkaPredictionsTopic);
        for (EPStatement statement : predictionsDeployment.getStatements()) {
            statement.addListener(predictionListener);
        }

        RuleListener observationListener = new RuleListener(kafkaBrokers, kafkaObservationsTopic);
        for (EPStatement statement : observationsDeployment.getStatements()) {
            statement.addListener(observationListener);
        }

        // Run until signal is received

        final CountDownLatch shutdownLatch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownLatch::countDown));
        shutdownLatch.await();
    }

}
