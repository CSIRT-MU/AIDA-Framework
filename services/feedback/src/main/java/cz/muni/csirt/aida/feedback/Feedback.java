package cz.muni.csirt.aida.feedback;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import cz.muni.csirt.aida.feedback.jmx.Metrics;
import cz.muni.csirt.aida.feedback.jmx.RuleMeasures;
import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.idea.aida.AidaUtils;
import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.model.Rules;

public class Feedback {

	private static final Logger logger = LoggerFactory.getLogger(Feedback.class);

	@Parameter(names = {"--kafka-brokers"}, description = "List of Kafka bootstrap servers separated by comma")
	private String kafkaBrokers = "127.0.0.1:9092";

	@Parameter(names = {"--kafka-consumer-group"}, description = "Kafka consumer group ID")
	private String kafkaConsumerGroupId = "feedback";

	@Parameter(names = {"--kafka-aggregates-topic"},
			description = "Kafka topic name for counting Aggregates")
	private String kafkaAggregatedTopic = "aggregated";

	@Parameter(names = {"--kafka-predictions-topic"},
			description = "Kafka topic name for measuring predictions")
	private String kafkaPredictionsTopic = "predictions";

	@Parameter(names = {"--kafka-observations-topic"},
			description = "Kafka topic name for measuring observations")
	private String kafkaObservationsTopic = "observations";

	@Parameter(names = {"-h", "--help"}, help = true)
	private boolean help;

	private Metrics metrics = Metrics.getInstance();

	public static void main(String[] args) {
		Feedback feedback = new Feedback();

		JCommander jcommander = JCommander.newBuilder()
				.addObject(feedback)
				.programName("cz.muni.csirt.aida.feedback.Feedback")
				.build();
		jcommander.parse(args);

		if (feedback.help) {
			jcommander.usage();
			return;
		}

		feedback.run();
	}

	public void run() {

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", kafkaBrokers);
		props.setProperty("group.id", kafkaConsumerGroupId);
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "cz.muni.csirt.aida.idea.kafka.IdeaDeserializer");

		KafkaConsumer<String, Idea> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(kafkaAggregatedTopic, kafkaPredictionsTopic, kafkaObservationsTopic));

		while (true) {
			final ConsumerRecords<String, Idea> consumerRecords = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, Idea> consumerRecord : consumerRecords) {
				Idea value = consumerRecord.value();
				String topic = consumerRecord.topic();

				if (topic.equals(kafkaAggregatedTopic)) {
					measureAggregates(value);
				} else if (topic.equals(kafkaPredictionsTopic)) {
					measurePredictions(value);
				} else if (topic.equals(kafkaObservationsTopic)) {
					measureObservations(value);
				} else {
					logger.warn("Consuming record from Kafka from unknown topic '{}'", consumerRecord.topic());
				}
			}
		}

	}

	private void measureAggregates(Idea idea) {
		Map<String, AtomicInteger> measures = metrics.getMeasures();
		measures
			.computeIfAbsent("Aggregated topic sum", x -> new AtomicInteger())
			.incrementAndGet();

		if (AidaUtils.isDuplicate(idea)) {
			measures.computeIfAbsent("Duplicates", x -> new AtomicInteger())
					.incrementAndGet();
		}

		if (AidaUtils.isContinuing(idea)) {
			measures.computeIfAbsent("Continuing", x -> new AtomicInteger())
					.incrementAndGet();
		}

		if (AidaUtils.isOverlapping(idea)) {
			measures.computeIfAbsent("Overlapping", x -> new AtomicInteger())
					.incrementAndGet();
		}

		if (AidaUtils.isNonOverlapping(idea)) {
			measures.computeIfAbsent("NonOverlapping", x -> new AtomicInteger())
					.incrementAndGet();
		}

		if (! (AidaUtils.isDuplicate(idea) ||
				AidaUtils.isContinuing(idea) ||
				AidaUtils.isOverlapping(idea) ||
				AidaUtils.isNonOverlapping(idea))) {
			measures.computeIfAbsent("no-aggregate", x -> new AtomicInteger())
					.incrementAndGet();
		}
	}

	private void measurePredictions(Idea idea) {
		Rule rule = Rules.fromSpmf(idea.getNote(), 0, 0);

		metrics.getRulesMeasures()
				.computeIfAbsent(rule, x -> new RuleMeasures())
				.getPredictions().getAndIncrement();
	}

	private void measureObservations(Idea idea) {
		Rule rule = Rules.fromSpmf(idea.getNote(), 0, 0);

		RuleMeasures ruleMeasures = metrics.getRulesMeasures()
				.computeIfAbsent(rule, x -> new RuleMeasures());

		ruleMeasures.getObservations().getAndIncrement();

		int mitigationTime = AidaUtils.getMitigationTime(idea);
		ruleMeasures.updateMitigationExtremes(mitigationTime);
		ruleMeasures.updateMitigationAverages(mitigationTime);
	}

}
