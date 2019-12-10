"""
 Script for aggregation of IDEA events. Aggregates are marked in json IDEA message and send to output kafka topic.
 Example of marking: {..., '_aida’:{'Duplicate’: 'true’, 'Continuing’: <first_idea_id>}, ...}
"""

import os
import sys
import argparse
import configparser
import ujson as json

from idea import Idea
from kafka import KafkaProducer
from pyspark import SparkConf
from pyspark import SparkContext
from pyspark import AccumulatorParam
from pyspark.streaming.kafka import KafkaUtils
from pyspark.streaming import StreamingContext


class IDAccumulatorParam(AccumulatorParam):
    """
    Custom accumulator for duplicate/continuous events
        - for each key there is a tuple (ID, DetectTime)
    """

    def zero(self, initialValue):
        return {}

    def addInPlace(self, value1, value2):

        # Erase accumulator value
        if value1 == 'del' or value2 == 'del':
            return {}

        # Join dicts
        toReturn = value1.copy()
        toReturn.update(value2)

        return toReturn


def get_args():
    """
    Argument parser
    """

    argp = argparse.ArgumentParser()
    argp.add_argument("-c", "--config",
        default="/etc/aida/aggregation.ini",
        dest="config",
        action="store",
        help="set path to configuration file")

    return argp.parse_args()


def sendToKafka(producer, topic_out, list):
    """
    Save idea messages to kafka topic
    :param producer: producer for output topic
    :param topic_out: topic to which will be messages sent
    :param list: list with idea messages
    """

    for idea in list:
        if idea:
            producer.send(topic_out, json.dumps(idea).encode('utf-8'))


def leaveOlder(x, y):
    """
    In reduceByKey, leave idea with older DetectTime (x[1]/y[1])
    :param x: first element in reduce process
    :param y: second element in reduce process
    """
    if x[1] <= y[1]:
        return x
    else:
        return y


def markDuplicate(key, idea, oldest_idea_id):
    """
    Mark duplicate. Mark is for statistics purpose.
    :return: marked key, IDEA
    """
    # If idea is present
    if idea:
        # Equality of ID's in tuple and idea, if true mark will be added
        if oldest_idea_id != idea.id:
            # Add True mark for duplicate event
            idea.aida_duplicate='True'

        # Return tuple: key for next deduplication phase and IDEA
        return (key[0:4], idea)


def markContinuing(key, idea, oldest_idea_id, oldest_idea_detect_time, accum):
    """
    Mark IDEA as continuing event.
    :return: marked key, IDEA
    """
    # If idea is present
    if idea:
        # Equality of ID's in tuple and idea, if true mark will be added
        if oldest_idea_id != idea.id:
            # Add {key: (ID, DetectTime)} to accumulator
            accum.add(dict([(key, (oldest_idea_id, oldest_idea_detect_time))]))
            # Add id mark for continuing event
            idea.aida_continuing=oldest_idea_id

        # Return tuple: key for next deduplication phase and IDEA
        return (key[0:3], idea)


def markOverlapp(key, idea, oldest_idea_id, oldest_idea_node_name):
    """
    Mark IDEA as overlapping event.
    :return: marked key, IDEA
    """
    # If idea is present
    if idea:
        # Node.Name has to be different, if true mark will be added
        if oldest_idea_node_name != idea.node_name:
            # Add id mark for overlapping event
            idea.aida_overlapping=oldest_idea_id

        # Return tuple: key for next deduplication phase and IDEA
        return (key[0:2], idea)


def markNonoverlapp(idea, oldest_idea_id, oldest_idea_node_name, oldest_idea_target):
    """
    Mark IDEA as non-overlapping event.
    :return: marked IDEA
    """
    # If idea is present
    if idea:
        # Node.Name and Target has to be different, if true mark will be added
        if oldest_idea_node_name != idea.node_name and oldest_idea_target != idea.target_ip4:
            # Add id mark for non-overlapping event
            idea.aida_non_overlapping=oldest_idea_id

        # Return only IDEA
        return idea


def getAccumulatorValue(accum):
    """
    Make array of objects which will be then parallelized to RDD
    :param acc: value of accumulator is key:(ID, DetectTime) of duplicates in last batch
    :return rddQueue: list of tuples which will be parallized into RDD and joined with DStream
    """
    rddQueue = accum.value.items()

    # Content of accum is going to be joined with current DStream - current content is not needed anymore
    accum.add('del')

    return rddQueue


if __name__ == '__main__':

    config = configparser.ConfigParser()
    args = get_args()
    config.read(args.config)

    # Creating Kafka stuffs
    config_kafka = config['kafka']
    topic_in = config_kafka['topic_in']
    topic_out = config_kafka['topic_out']
    zookeper_in = config_kafka['zookeper_in']
    kafka_brokers_out = config_kafka['kafka_brokers_out']
    offset = config_kafka['offset']

    # Windows and batch
    # Slide windows has to be the same size as bacth, otherwise applying window on first tuples is needed
    config_streaming = config['streaming']
    batch_size = int(config_streaming['batch_size'])
    slide_size = int(config_streaming['slide_size'])
    window_duplicate = int(config_streaming['window_duplicate'])
    window_continuing = int(config_streaming['window_continuing'])
    window_overlapping = int(config_streaming['window_overlapping'])
    window_nonoverlapping = int(config_streaming['window_nonoverlapping'])

    # Creating Spark/Streaming context and conf
    sc = SparkContext(appName=" ".join(sys.argv[0:]))
    ssc = StreamingContext(sc, batch_size)

    # Creating accumulator
    accum = sc.accumulator({}, IDAccumulatorParam())


    """ Kafka init producer and load messages from topic """

    # Producer for topic_out
    producer = KafkaProducer(bootstrap_servers=kafka_brokers_out)

    # Kafka messages are input, groupId (id8641233) has to be unique - if 2 consumers are present
    kafkaStream = KafkaUtils.createStream(ssc, zookeper_in, offset, {topic_in: 1})


    """ Mark duplicates """

    # Build key:(category, source_ip, target_ip, node_name, detect_time) !Do not change order! and value:idea
    tuples = kafkaStream.map(lambda message: Idea(message[1])). \
        map(lambda idea: ((idea.category,
                           idea.source_ip4,
                           idea.target_ip4,
                           idea.node_name,
                           idea.detect_time), idea))

    # Reduce current batch
    batch_reduced = tuples.mapValues(lambda idea: (idea.id, idea.detect_time)). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Apply window and leave only needed idea values (id, detect_time) then reduce
    reduced = batch_reduced.window(window_duplicate, slide_size). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Join reduced and raw IDEA with key, apply marking function (see def for tuple members) and filter None
    tuples = reduced.leftOuterJoin(tuples). \
        map(lambda tuple: markDuplicate(tuple[0], tuple[1][1], tuple[1][0][0])). \
        filter(lambda x: x)

    # Filter duplicates and map to take only idea (without key)
    duplicates = tuples.filter(lambda tuple: tuple[1].aida_duplicate). \
        map(lambda tuple: tuple[1])

    # Filter non-duplicates and leave key
    tuples = tuples.filter(lambda tuple: not tuple[1].aida_duplicate)


    """ Mark continuing events """

    # Leave only needed idea values and union with keys in accumulator
    joined = tuples.mapValues(lambda idea: (idea.id, idea.detect_time)). \
        transform(lambda rdd: rdd.union(sc.parallelize(getAccumulatorValue(accum))))

    # Reduce current batch
    batch_reduced = joined.reduceByKey(lambda x, y: leaveOlder(x, y))

    # Apply window and reduce
    window_reduced = batch_reduced.window(window_continuing, slide_size). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Join reduced and raw IDEA with key, apply marking function and filter None
    tuples = window_reduced.leftOuterJoin(tuples). \
        map(lambda tuple: markContinuing(tuple[0], tuple[1][1], tuple[1][0][0], tuple[1][0][1], accum)). \
        filter(lambda x: x)


    """ Overlapping sensors """

    # Reduce current batch
    batch_reduced = tuples.mapValues(lambda idea: (idea.id, idea.detect_time, idea.node_name)). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Apply window and leave only needed idea values then reduce
    reduced = batch_reduced.window(window_overlapping, slide_size). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Join reduced and raw IDEA with key, apply marking function and filter None
    tuples = reduced.leftOuterJoin(tuples). \
        map(lambda tuple: markOverlapp(tuple[0], tuple[1][1], tuple[1][0][0], tuple[1][0][2])). \
        filter(lambda x: x)


    """ Non-overlapping sensors """

    # Reduce current batch
    batch_reduced = tuples.mapValues(lambda idea: (idea.id, idea.detect_time, idea.node_name, idea.target_ip4)). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Apply window and leave only needed idea values then reduce
    reduced = batch_reduced.window(window_nonoverlapping, slide_size). \
        reduceByKey(lambda x, y: leaveOlder(x, y))

    # Join reduced and raw IDEA with key, apply marking,filter None, union with duplicates, make json and send to Kafka
    reduced.leftOuterJoin(tuples). \
        map(lambda tuple: markNonoverlapp(tuple[1][1], tuple[1][0][0], tuple[1][0][2], tuple[1][0][3])). \
        filter(lambda x: x). \
        union(duplicates). \
        map(lambda idea: idea.json). \
        foreachRDD(lambda rdd: sendToKafka(producer, topic_out, rdd.collect()))


    ssc.start()
    ssc.awaitTermination()
