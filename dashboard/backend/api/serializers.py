from rest_framework.response import Response
from rest_framework.serializers import ModelSerializer, SerializerMethodField
from .services import dns_statistics, protocol_statistics, ssh_auth_simple
from .models import Panel, Widget, PanelGroup, WidgetGroup
from .external_apis_config import *

class PanelSerializer(ModelSerializer):
    class Meta:
        model = Panel
        fields = ('id', 'name', 'alias', 'menu_name',
                  'description', 'panel_group', 'include_filter', 'external_link', 'external_link_url')
        lookup_field = 'alias'
        extra_kwargs = {
            'url': {'lookup_field': 'alias'}
        }


class WidgetSerializer(ModelSerializer):
    data = SerializerMethodField()

    class Meta:
        model = Widget
        fields = ('id', 'panel_id', 'name', 'type', 'data_type', 'data', 'cols')

    def get_data(self, obj):
        query = self.context["request"].query_params
        if obj.panel_id == 1:
            return ssh_auth_simple.SshAuthSimple.get_data_from_query(obj, query)
        if obj.panel_id == 3:
            return protocol_statistics.ProtocolStatistics.get_data_from_query(obj, query)
        if obj.panel_id == 4:
            return dns_statistics.DnsStatistics.get_data_from_query(obj, query)


class PanelGroupSerializer(ModelSerializer):
    class Meta:
        model = PanelGroup
        fields = ('id', 'name', 'description')


class WidgetGroupSerializer(ModelSerializer):
    customConfig = SerializerMethodField()

    class Meta:
        model = WidgetGroup
        fields = ('id', 'panel_id', 'name', 'title', 'cols', 'customConfig') #, 'description'

    def get_customConfig(self, obj):
        id = obj.id

        if (id == 3 or id == 6):
            return rt_tickets_api
        if (id == 7):
            return flowmon_ads_api
        if (id == 4):
            return twitter_api
        else:
            return "None"
