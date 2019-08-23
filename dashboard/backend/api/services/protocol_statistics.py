# -*- coding: utf-8 -*-

# Import Elasticsearch library
import elasticsearch
from elasticsearch_dsl import Search, Q, A
from api import settings

class ProtocolStatistics(object):

    @staticmethod
    def get_data_from_query(obj, query):
        host = settings.SERVICE_HOST
        port = settings.SERVICE_PORT

        if 'date_from' not in query:
            return None
        if 'date_to' not in query:
            return None
        if 'aggregation' not in query:
            return None
        if 'filter' not in query:
            return None

        # Parse inputs and set correct format
        beginning = query["date_from"]
        end = query["date_to"]
        aggregation = query["aggregation"]
        filter = query["filter"]

        # type = escape(request.get_vars.type)  # name of field to create sum from, one of {flows, packets, bytes }
        type = obj.data_type

        try:
            # Elastic query
            client = elasticsearch.Elasticsearch(
                    [{'host': host, 'port': port}])
            elastic_bool = []
            elastic_bool.append({'range': {'@timestamp': {'gte': beginning, 'lte': end}}})
            elastic_bool.append({'term': {'@type': 'protocols_statistics'}})

            qx = Q({'bool': {'must': elastic_bool}})
            s = Search(using=client, index='_all').query(qx)

            s.aggs.bucket('by_time', 'date_histogram', field='@timestamp', interval=aggregation)\
                .bucket('by_type', 'terms', field='protocol.raw')\
                .bucket('sum_of_flows', 'sum', field=type)
            s.sort('@timestamp')
            result = s.execute()

            data_raw = {}
            protocol_stats = {}
            protocol_stats["TCP protocol"] = []
            protocol_stats["UDP protocol"] = []
            protocol_stats["Other protocols"] = []

            for interval in result.aggregations.by_time.buckets:
                timestamp = interval.key
                timestamp_values = [''] * 3
                data_raw[timestamp] = timestamp_values
                for bucket in interval.by_type.buckets:
                    value = bucket.sum_of_flows.value
                    if bucket.key == "tcp":
                        data_raw[timestamp][0] = str(int(value))
                    elif bucket.key == "udp":
                        data_raw[timestamp][1] = str(int(value))
                    elif bucket.key == "other":
                        data_raw[timestamp][2] = str(int(value))

                protocol_stats["TCP protocol"].append([str(timestamp), str(data_raw[timestamp][0])])
                protocol_stats["UDP protocol"].append([str(timestamp), str(data_raw[timestamp][1])])
                protocol_stats["Other protocols"].append([str(timestamp), str(data_raw[timestamp][2])])

                

            # json_response = '{"status": "Ok", "data": "' + data + '"}'
            # return json_response
            return protocol_stats

        except Exception as e:
            json_response = '{"status": "Error", "data": "Elasticsearch query exception: ' + str(e) + '"}'
            return json_response