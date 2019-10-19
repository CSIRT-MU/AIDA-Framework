#!/usr/bin/python3
import argparse
from pathlib import Path
import inotify.adapters
from inotify.constants import *
from kafka import KafkaProducer
import logging.handlers

log = logging.getLogger(__name__)


def configure_logger():
    log.setLevel(logging.ERROR)
    handler = logging.handlers.SysLogHandler(address='/dev/log')
    formatter = logging.Formatter('%(module)s.%(funcName)s: %(message)s')
    handler.setFormatter(formatter)
    log.addHandler(handler)


def send_file_to_kafka(file_path, producer, topic):
    try:
        file = Path(file_path)
        with file.open('rb') as f:
            producer.send(topic, f.read())
        file.unlink()
    except FileNotFoundError:
        log.warning("Trying to send non-existing file to kafka. Don't worry, this is most probably known race "
                    "condition. File was already send to kafka, there shouldn't be any data losses. "
                    "You should investigate this just if this happened after long run of the service.", exc_info=True)
    except Exception:
        log.exception("Error occurred while sending file '{}' to kafka topic.".format(file_path))


def send_files_to_kafka(directory, producer, topic):
    """ Send content of all files in given directory into kafka topic. """
    for f in Path(directory).iterdir():
        if f.is_file():
            send_file_to_kafka(f, producer, topic)


def main(directory, zookeeper, topic):
    producer = KafkaProducer(bootstrap_servers=zookeeper)

    # Start watching directory for new files
    i = inotify.adapters.Inotify(block_duration_s=10)
    i.add_watch(directory, mask=IN_ONLYDIR | IN_CLOSE_WRITE | IN_MOVED_TO)

    # Send all files currently existing in directory into kafka
    send_files_to_kafka(directory, producer, topic)

    # Start sending new files into kafka
    for event in i.event_gen(yield_nones=False):
        _, _, path, filename = event
        send_file_to_kafka(Path(path) / filename, producer, topic)


if __name__ == '__main__':
    argp = argparse.ArgumentParser(description="Start daemon that will read files from given dir, send them to given "
                                               "kafka topic and then remove them from dir.")
    argp.add_argument("--dir",
                      required=True,
                      help="Set absolute path to dir to read IDEA messages from.")
    argp.add_argument("--topic",
                      required=True,
                      type=str,
                      help="Set output kafka topic name.")
    argp.add_argument("--zookeeper",
                      required=True,
                      type=str,
                      help="Set zookeeper (broker-list) for Kafka consumer, e.g. localhost:9092.")
    argp.add_argument("--daemonize",
                      action='store_true',
                      help="Start as a daemon.")

    args = argp.parse_args()

    if args.daemonize:
        configure_logger()
        import daemon
        with daemon.DaemonContext():
            main(args.dir, args.zookeeper, args.topic)
    else:
        main(args.dir, args.zookeeper, args.topic)
