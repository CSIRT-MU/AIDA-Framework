from django.conf.urls import include, url
from django.contrib.auth.models import User
from rest_framework import routers
from . import views

router = routers.DefaultRouter()

# ROUTE NOT PROTECTED BY OIDC (local login required)
router.register(r'panels', views.PanelViewSet)
router.register(r'widgets', views.WidgetViewSet)
router.register(r'widget_groups', views.WidgetGroupViewSet)
router.register(r'panel_groups', views.PanelGroupViewSet)

# ROUTE PROTECTED BY OIDC (local login not required)
router.register(r'oidc-protected/panels', views.OIDCPanelViewSet)
router.register(r'oidc-protected/widgets', views.OIDCWidgetViewSet)
router.register(r'oidc-protected/widget_groups', views.OIDCWidgetGroupViewSet)
router.register(r'oidc-protected/panel_groups', views.OIDCPanelGroupViewSet)

urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
