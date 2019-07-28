package cz.muni.csirt.aida.mining.kafka;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import cz.muni.csirt.aida.idea.Idea;

public class ConsumeAllRebalanceListener implements ConsumerRebalanceListener {

	private KafkaConsumer<?, Idea> consumer;
	private Map<TopicPartition, Long> partitionEnds;

	public ConsumeAllRebalanceListener(KafkaConsumer<?, Idea> consumer) {
		this.consumer = consumer;
		this.partitionEnds = new HashMap<>();
	}

	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		partitions.forEach(p -> partitionEnds.remove(p));
	}

	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
		partitionEnds.putAll(consumer.endOffsets(partitions));
		consumer.seekToBeginning(partitions);
	}

	public boolean isAllRead() {
		if (partitionEnds.isEmpty()) {
			return false;
		}

		for (Map.Entry<TopicPartition, Long> partitionEnd : partitionEnds.entrySet()) {
			if (consumer.position(partitionEnd.getKey()) < partitionEnd.getValue()) {
				return false;
			}
		}

		return true;
	}
}
