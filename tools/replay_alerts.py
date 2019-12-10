import argparse
import math
import time
from pathlib import Path
from datetime import datetime

from kafka import KafkaProducer


def file_lines(file_path) -> int:
    """ Calculate number of lines in file. """
    with open(file_path) as f:
        i = 0
        for i, l in enumerate(f, start=1):
            pass
    return i


if __name__ == '__main__':
    argp = argparse.ArgumentParser(description="Script will read IDEA messages from a file and sends them into a"
                                               "Kafka topic in specified time interval in uniform distribution "
                                               "per seconds.")
    argp.add_argument("--bootstrap-servers",
                      default='localhost:9092',
                      help="Set bootstrap-servers for Kafka producer.")
    argp.add_argument("--topic",
                      default="input",
                      type=str,
                      help="Set the target Kafka topic for events.")
    argp.add_argument("--alerts",
                      required=True,
                      type=Path,
                      help="Path to file with alerts (one alert per line)")
    argp.add_argument("--interval-seconds",
                      required=True,
                      type=int,
                      help="Approximate interval in seconds. The script will run for approximately this time.")

    args = argp.parse_args()

    producer = KafkaProducer(bootstrap_servers=[args.bootstrap_servers], value_serializer=str.encode)

    number_of_alerts = file_lines(str(args.alerts))
    send_each_second = math.ceil(number_of_alerts / args.interval_seconds)

    print(f"Number of alerts in file {number_of_alerts}")
    print(f"Will be sending {send_each_second} alerts per second")

    start_time = time.time()
    for i, alert in enumerate(args.alerts.open('r')):

        producer.send(args.topic, alert.strip())

        if i % send_each_second == 0:
            producer.flush()
            should_be_current = start_time + (i / send_each_second)
            wait_for = should_be_current - time.time()
            print(f"{datetime.now()} {i} alerts have been send, going to wait for {wait_for} sec")
            if wait_for > 0:
                time.sleep(wait_for)

    producer.flush()
    print("All alerts have been send")
