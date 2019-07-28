""" The Idea class is supposed to facilitate reading values from IDEA format.
Class provides simple access to often used values inside IDEA through parameters. None is returned when value is missing.
Complete IDEA alert is accessible as 'json' property.
"""

import ujson as json  # Fast JSON parser
from functools import wraps
import dateutil.parser


def not_found(method):
    """ This decorator make sure that None will be return when method throws KeyError, IndexError or TypeError. """

    @wraps(method)
    def decorator(self, *args, **kwargs):
        try:
            return method(self, *args, **kwargs)
        except (KeyError, IndexError, TypeError):
            return None

    return decorator


def idea_property(method):
    """ idea_property decorator just combine @property and @not_found decorators. """
    return property(not_found(method))


class Idea(object):
    def __init__(self, idea):
        """
        :param idea: One IDEA alert as string
        """
        self._json = json.loads(idea)

    @property
    def json(self):
        return self._json

    @idea_property
    def id(self):
        return self.json["ID"]

    @idea_property
    def aggr_id(self):
        return self.json["AggrID"]

    @idea_property
    def conn_count(self):
        return self.json["ConnCount"]

    @idea_property
    def flow_count(self):
        return self.json["FlowCount"]

    @idea_property
    def categories(self):
        return self.json['Category']

    @idea_property
    def category(self):
        return self.categories[0]

    # NODES

    @idea_property
    def nodes(self):
        return self.json['Node']

    @idea_property
    def node_name(self):
        # Remove warden filler Nodes from list of nodes
        nodes = filter(lambda x: "warden_filer" not in x['Name'], self.nodes)
        return next(nodes, None)['Name']

    # SOURCE

    @idea_property
    def sources(self):
        return self.json["Source"]

    @idea_property
    def source_ip4(self):
        return self.sources[0]["IP4"][0]

    @idea_property
    def source_proto(self):
        return self.sources[0]["Proto"][0]

    # TARGET

    @idea_property
    def targets(self):
        return self.json["Target"]

    @idea_property
    def target_ip4(self):
        return self.targets[0]["IP4"][0]

    @idea_property
    def target_port(self):
        return self.targets[0]["Port"][0]

    @idea_property
    def target_proto(self):
        return self.targets[0]["Proto"][0]

    # TIME PARAMS

    @idea_property
    def _detect_time(self):
        """ String representation of 'DetectTime' """
        return self.json["DetectTime"]

    @idea_property
    def detect_time(self):
        """ Datetime object """
        return dateutil.parser.parse(self._detect_time)

    # AIDA params

    @idea_property
    def aida(self):
        if '_aida' not in self._json:
            self.aida = {}
        return self.json['_aida']

    @aida.setter
    def aida(self, value):
        self.json['_aida'] = value

    @idea_property
    def aida_duplicate(self):
        return self.aida['Duplicate']

    @aida_duplicate.setter
    def aida_duplicate(self, value):
        self.aida['Duplicate'] = value

    @idea_property
    def aida_continuing(self):
        return self.aida['Continuing']

    @aida_continuing.setter
    def aida_continuing(self, value):
        self.aida['Continuing'] = value

    @idea_property
    def aida_overlapping(self):
        return self.aida['Overlapping']

    @aida_overlapping.setter
    def aida_overlapping(self, value):
        self.aida['Overlapping'] = value

    @idea_property
    def aida_non_overlapping(self):
        return self.aida['NonOverlapping']

    @aida_non_overlapping.setter
    def aida_non_overlapping(self, value):
        self.aida['NonOverlapping'] = value
