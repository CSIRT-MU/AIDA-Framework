#!/usr/bin/python3
import argparse
import uuid
from pathlib import Path
from kafka import KafkaConsumer


if __name__ == '__main__':
    argp = argparse.ArgumentParser(description="Start script that will read messages from given kafka topic and save "
                                               "them to given directory (one message per file).")
    argp.add_argument("--dir",
                      required=True,
                      type=Path,
                      help="Set absolute path to dir to save messages to.")
    argp.add_argument("--topic",
                      required=True,
                      type=str,
                      help="Set input kafka topic name.")
    argp.add_argument("--zookeeper",
                      required=True,
                      type=str,
                      help="Set zookeeper (broker-list) for Kafka consumer, e.g. localhost:9092.")
    argp.add_argument("--kafka-group-id",
                      required=False,
                      default=None,
                      type=str,
                      help="Kafka consumers group id")

    args = argp.parse_args()

    consumer = KafkaConsumer(args.topic,
                             group_id=args.kafka_group_id,
                             bootstrap_servers=[args.zookeeper])

    args.dir.mkdir(parents=True, exist_ok=True)
    file_template = args.dir / str(uuid.uuid4())

    for i, message in enumerate(consumer):
        with file_template.with_suffix(".{}".format(i)).open('wb') as message_file:
            message_file.write(message.value)
