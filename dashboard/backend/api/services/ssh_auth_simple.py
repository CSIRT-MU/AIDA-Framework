import elasticsearch
import collections
from elasticsearch_dsl import Search, Q, A
from api import settings

class SshAuthSimple(object):

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
        # beginning = "2017-11-05T20:34:30.000Z"
        beginning = query["date_from"]
        end = query["date_to"]
        aggregation = query["aggregation"]
        filter = query["filter"]
        # TODO parametrize number for piechart
        number = 10

        if obj.name == "Attacks histogram":
            try:
                # Elastic query
                client = elasticsearch.Elasticsearch(
                    [{'host': host, 'port': port}])
                elastic_bool = []
                elastic_bool.append({'range': {'@timestamp': {'gte': beginning, 'lte': end}}})
                elastic_bool.append({'term': {'@type': 'ssh_auth_simple'}})
                # Set filter
                if filter != 'none':
                    elastic_should = []
                    elastic_should.append({'term': {'src_ip': filter}})
                    elastic_should.append({'term': {'dst_ip': filter}})
                    elastic_bool.append({'bool': {'should': elastic_should}})
                # Prepare query
                qx = Q({'bool': {'must': elastic_bool}})

                # Get histogram data
                search_histogram = Search(using=client, index='_all').query(qx)
                search_histogram.aggs.bucket('by_time', 'date_histogram', field='@timestamp', interval=aggregation) \
                    .bucket('by_src', 'terms', field='src_ip', size=2147483647) \
                    .bucket('sum_of_flows', 'sum', field='flows_increment')
                histogram = search_histogram.execute()

                # Prepare obtained data
                detections = {}
                for interval in histogram.aggregations.by_time.buckets:
                    timestamp = interval.key
                    for source in interval.by_src.buckets:
                        # Create a new key of not exists
                        if source.key not in detections:
                            detections[source.key] = []
                        # Append array of timestamp and number of flows
                        detections[source.key].append([timestamp, source.sum_of_flows.value])

                # if not detections:
                #     # Return No data info message
                #     return '{"status": "Empty", "data": "No data found"}'

                # Return data as JSON
                return detections

            except Exception as e:
                json_response = '{"status": "Error", "data": "Elasticsearch query exception: ' + str(e) + '"}'
                return json_response


        elif obj.name == "Top 10 Statistics by source IP":
            try:
                # Elastic query
                client = elasticsearch.Elasticsearch([{'host': host, 'port': port}])
                elastic_bool = []
                elastic_bool.append({'range': {'@timestamp': {'gte': beginning, 'lte': end}}})
                elastic_bool.append({'term': {'@type': 'ssh_auth_simple'}})
                # Set filter
                if filter != 'none':
                    elastic_should = []
                    elastic_should.append({'term': {'src_ip': filter}})
                    elastic_should.append({'term': {'dst_ip': filter}})
                    elastic_bool.append({'bool': {'should': elastic_should}})
                # Prepare query
                qx = Q({'bool': {'must': elastic_bool}})

                # Get ordered data (with maximum size aggregation)
                search = Search(using=client, index='_all').query(qx)
                search.aggs.bucket('by_src', 'terms', field='src_ip', size=2147483647)\
                      .bucket('by_dst', 'terms', field='dst_ip', size=2147483647)\
                      .bucket('top_src_dst', 'top_hits', size=1, sort=[{'@timestamp': {'order': 'desc'}}])
                results = search.execute()

                # Prepare ordered collection
                counter = collections.Counter()
                # TODO parametrize type
                type = "sources"
                for src_buckets in results.aggregations.by_src.buckets:
                    if type == "sources":
                        counter[src_buckets.key] = len(src_buckets.by_dst.buckets)
                    else:
                        for dst_buckets in src_buckets.by_dst.buckets:
                            counter[dst_buckets.key] += 1

                # Select first N (number) values
                data = []
                for ip, count in counter.most_common(number):
                    data.append([ip, count])

                # if data == "":
                #     json_response = '{"status": "Empty", "data": "No data found"}'
                # else:
                #     json_response = '{"status": "Ok", "data": "' + data + '"}'
                return data

            except Exception as e:
                json_response = '{"status": "Error", "data": "Elasticsearch query exception: ' + str(e) + '"}'
                return json_response

        elif obj.name == "Top 10 Statistics by destination IP":
            try:
                # Elastic query
                client = elasticsearch.Elasticsearch([{'host': host, 'port': port}])
                elastic_bool = []
                elastic_bool.append({'range': {'@timestamp': {'gte': beginning, 'lte': end}}})
                elastic_bool.append({'term': {'@type': 'ssh_auth_simple'}})
                # Set filter
                if filter != 'none':
                    elastic_should = []
                    elastic_should.append({'term': {'src_ip': filter}})
                    elastic_should.append({'term': {'dst_ip': filter}})
                    elastic_bool.append({'bool': {'should': elastic_should}})
                # Prepare query
                qx = Q({'bool': {'must': elastic_bool}})

                # Get ordered data (with maximum size aggregation)
                search = Search(using=client, index='_all').query(qx)
                search.aggs.bucket('by_src', 'terms', field='src_ip', size=2147483647)\
                      .bucket('by_dst', 'terms', field='dst_ip', size=2147483647)\
                      .bucket('top_src_dst', 'top_hits', size=1, sort=[{'@timestamp': {'order': 'desc'}}])
                results = search.execute()

                # Prepare ordered collection
                counter = collections.Counter()
                # TODO parametrize type
                type = "destination"
                for src_buckets in results.aggregations.by_src.buckets:
                    if type == "sources":
                        counter[src_buckets.key] = len(src_buckets.by_dst.buckets)
                    else:
                        for dst_buckets in src_buckets.by_dst.buckets:
                            counter[dst_buckets.key] += 1

                # Select first N (number) values
                data = []
                for ip, count in counter.most_common(number):
                    data.append([ip, count])

                # if data == "":
                #     json_response = '{"status": "Empty", "data": "No data found"}'
                # else:
                #     json_response = '{"status": "Ok", "data": "' + data + '"}'
                return data

            except Exception as e:
                json_response = '{"status": "Error", "data": "Elasticsearch query exception: ' + str(e) + '"}'
                return json_response

        elif obj.name == "Attacks details":
            try:
                # Elastic query
                client = elasticsearch.Elasticsearch([{'host': host, 'port': port}])
                elastic_bool = []
                elastic_bool.append({'range': {'@timestamp': {'gte': beginning, 'lte': end}}})
                elastic_bool.append({'term': {'@type': 'ssh_auth_simple'}})
                # Set filter
                if filter != 'none':
                    elastic_should = []
                    elastic_should.append({'term': {'src_ip': filter}})
                    elastic_should.append({'term': {'dst_ip': filter}})
                    elastic_bool.append({'bool': {'should': elastic_should}})
                qx = Q({'bool': {'must': elastic_bool}})

                # Search with maximum size aggregations
                search = Search(using=client, index='_all').query(qx)
                search.aggs.bucket('by_src', 'terms', field='src_ip', size=2147483647) \
                      .bucket('by_dst', 'terms', field='dst_ip', size=2147483647) \
                      .bucket('top_src_dst', 'top_hits', size=1, sort=[{'@timestamp': {'order': 'desc'}}])
                results = search.execute()

                # Result Parsing into CSV in format: timestamp, source_ip, destination_ip, flows, duration
                data = []
                for src_aggregations in results.aggregations.by_src.buckets:
                    for result in src_aggregations.by_dst.buckets:
                        record = result.top_src_dst.hits.hits[0]["_source"]
                        m, s = divmod(record["duration_in_milliseconds"]/ 1000, 60)
                        h, m = divmod(m, 60)
                        duration = "%d:%02d:%02d" % (h, m, s)
                        data.append({
                            'timestamp': record["timestamp"].replace("T", " ").replace("Z",""),
                            'source': record["src_ip"],
                            'destination': record["dst_ip"],
                            'flows': str(record["flows"]),
                            'duration': str(duration)
                        })
                # data = data[:-1]

                # json_response = '{"status": "Ok", "data": "' + data + '"}'

                return data
            except Exception as e:
                json_response = '{"status": "Error", "data": "Elasticsearch query exception: ' + str(e) + '"}'
                return json_response
        # TODO refactor
        elif obj.type == "LINE_CHART_PROTOCOL":
            try:
                client = elasticsearch.Elasticsearch([{'host': host, 'port': port}])
                elastic_bool = []
                elastic_bool.append({'range': {'@timestamp': {'gte': beginning, 'lte': end}}})
                elastic_bool.append({'term': {'@type': 'protocols_statistics'}})

                qx = Q({'bool': {'must': elastic_bool}})
                s = Search(using=client, index='_all').query(qx)
                # TODO parametrize type
                type = 'flows'
                s.aggs.bucket('by_time', 'date_histogram', field='@timestamp', interval=aggregation)\
                      .bucket('by_type', 'terms', field='protocol.raw')\
                      .bucket('sum_of_flows', 'sum', field=type)
                s.sort('@timestamp')
                result = s.execute()

                # Result Parsing into CSV in format: timestamp, tcp protocol value, udp protocol value, other protocols value
                data_raw = {}
                data = "Timestamp,TCP protocol,UDP protocol,Other protocols;"  # CSV header
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

                    data += str(timestamp) + "," + str(data_raw[timestamp][0]) + "," + str(data_raw[timestamp][1]) + "," + str(data_raw[timestamp][2]) + ";"

                #json_response = '{"status": "Ok", "data": "' + data + '"}'
                #return json_response
                return data

            except Exception as e:
                json_response = '{"status": "Error", "data": "Elasticsearch query exception: ' + str(e) + '"}'
                return json_response
        else:
            return "none"
