#!/usr/bin/python3
import argparse
from collections import Counter
from kafka import KafkaConsumer, TopicPartition
import logging.handlers
from idea import Idea

log = logging.getLogger(__name__)


def configure_logger():
    log.setLevel(logging.ERROR)
    handler = logging.handlers.SysLogHandler(address='/dev/log')
    formatter = logging.Formatter('%(module)s.%(funcName)s: %(message)s')
    handler.setFormatter(formatter)
    log.addHandler(handler)


if __name__ == '__main__':
    argp = argparse.ArgumentParser(description="The script will read IDEA messages from given Kafka topic and print " +
                                                "counts of different types of aggregates.")
    argp.add_argument("--zookeeper",
                      default='localhost:9092',
                      help="Set zookeeper (broker-list) for Kafka consumer.")
    argp.add_argument("--topic",
                      default="aggregated",
                      type=str,
                      help="Set input kafka topic name.")

    args = argp.parse_args()

    consumer = KafkaConsumer(args.topic,
                             bootstrap_servers=[args.zookeeper],
                             auto_offset_reset='earliest',
                             value_deserializer=lambda x: Idea(x))

    consumer.topics()
    partition_ids = consumer.partitions_for_topic(args.topic)
    partitions = [TopicPartition(args.topic, partition_id) for partition_id in partition_ids]
    partitions_ends = consumer.end_offsets(partitions)

    counters = Counter()

    def is_at_the_end(consumer, partitions_ends):
        for k, v in partitions_ends.items():
            if consumer.position(k) < v:
                return False
        return True

    if is_at_the_end(consumer, partitions_ends):
        print(f"No messages in the topic '{args.topic}'")
        exit(0)

    for i, message in enumerate(consumer):
        idea = message.value

        if i % 100000 == 0:
            print(f"Processed {i} messages")

        counters['sum'] += 1
        if idea.aida_duplicate:
            counters['duplicate'] += 1
        if idea.aida_continuing:
            counters['continuing'] += 1
        if idea.aida_overlapping:
            counters['overlapping'] += 1
        if idea.aida_non_overlapping:
            counters['non_overlapping'] +=1
        if not (idea.aida_duplicate or
                idea.aida_continuing or
                idea.aida_overlapping or
                idea.aida_non_overlapping):
            counters['no-aggregate'] += 1

        if is_at_the_end(consumer, partitions_ends):
            print(f"All {i} messages processed.")
            break

    print(counters)