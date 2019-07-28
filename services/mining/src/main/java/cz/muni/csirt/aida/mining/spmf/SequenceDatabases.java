package cz.muni.csirt.aida.mining.spmf;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.mining.kafka.ConsumeAllRebalanceListener;
import cz.muni.csirt.aida.mining.model.KeyType;

public class SequenceDatabases {

	private static final Logger logger = LoggerFactory.getLogger(SequenceDatabases.class);

	private SequenceDatabases() {
		throw new IllegalStateException("Cannot initialize utility class");
	}

	public static IdeaSequenceDatabase fromKafka(String topic, String bootstrapServers, String consumerGroup,
			KeyType keyType) {

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", bootstrapServers);
		props.setProperty("group.id", consumerGroup);
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "cz.muni.csirt.aida.idea.kafka.IdeaDeserializer");

		KafkaConsumer<String, Idea> consumer = new KafkaConsumer<>(props);
		ConsumeAllRebalanceListener consumeAllRebalanceListener = new ConsumeAllRebalanceListener(consumer);
		consumer.subscribe(Collections.singleton(topic), consumeAllRebalanceListener);

		SequenceDatabaseBuilder databaseBuilder = new SequenceDatabaseBuilder(keyType);

		while (!consumeAllRebalanceListener.isAllRead()) {
			ConsumerRecords<String, Idea> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, Idea> record : records) {
				try {
					// TODO filter duplicates and so on
					databaseBuilder.addEvent(record.value());
				} catch (Exception e) {
					logger.error("Event cannot be added into database", e);
				}
			}
		}

		return databaseBuilder.build();
	}
}
