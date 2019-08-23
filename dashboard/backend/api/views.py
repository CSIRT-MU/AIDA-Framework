from rest_framework.viewsets import ModelViewSet
from django_filters.rest_framework import DjangoFilterBackend
from .models import Panel, Widget, PanelGroup, WidgetGroup
from .serializers import WidgetSerializer, PanelSerializer, PanelGroupSerializer, WidgetGroupSerializer
from rest_framework.decorators import permission_classes

# This solution containing duplicated viewsets is not permanent and
# should be refactored in the future.

# VIEWS NOT PROTECTED BY OIDC (local login required)
class WidgetViewSet(ModelViewSet):
    queryset = Widget.objects.filter(visible=True)
    serializer_class = WidgetSerializer
    filter_backends = (DjangoFilterBackend,)
    filter_fields = ('panel_id','id')

class WidgetGroupViewSet(ModelViewSet):
    queryset = WidgetGroup.objects.filter(visible=True)
    serializer_class = WidgetGroupSerializer
    filter_backends = (DjangoFilterBackend,)
    filter_fields = ('panel_id',)

class PanelViewSet(ModelViewSet):
    queryset = Panel.objects.filter(visible=True)
    serializer_class = PanelSerializer
    lookup_field = 'alias'

class PanelGroupViewSet(ModelViewSet):
    queryset = PanelGroup.objects.filter(visible=True)
    serializer_class = PanelGroupSerializer

# VIEWS PROTECTED BY OIDC (local login not required)
@permission_classes([])
class OIDCWidgetViewSet(ModelViewSet):
    queryset = Widget.objects.filter(visible=True)
    serializer_class = WidgetSerializer
    filter_backends = (DjangoFilterBackend,)
    filter_fields = ('panel_id','id')

@permission_classes([])
class OIDCPanelViewSet(ModelViewSet):
    queryset = Panel.objects.filter(visible=True)
    serializer_class = PanelSerializer
    lookup_field = 'alias'

@permission_classes([])
class OIDCPanelGroupViewSet(ModelViewSet):
    queryset = PanelGroup.objects.filter(visible=True)
    serializer_class = PanelGroupSerializer

@permission_classes([])
class OIDCWidgetGroupViewSet(ModelViewSet):
    queryset = WidgetGroup.objects.filter(visible=True)
    serializer_class = WidgetGroupSerializer
    filter_backends = (DjangoFilterBackend,)
    filter_fields = ('panel_id',)
