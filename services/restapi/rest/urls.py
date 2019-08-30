from django.conf.urls import url

from . import views

app_name = 'rest'
urlpatterns = [
    url(r'^componentsinfo/$', views.system_state_with_more_info, name='system_state_with_more_info'),
    url(r'^db/rules/(?P<date1>[0-9]{4}-[0-9]{2}-[0-9]{2})/$', views.get_rules, name='get_rules'),
    url(r'^db/activerules/$', views.active_rules, name='active_rules'),
    url(r'^db/activerules/(?P<rule_id>\d+)/$', views.activate_rule, name='activate_rule'),
    url(r'^db/inactiverules/(?P<rule_id>\d+)/$', views.deactivate_rule, name='deactivate_rule'),
    url(r'^db/deleterules/(?P<rule_id>\d+)/$', views.delete_rule, name='delete_rule'),
    url(r'^db/addrules/(?P<given_rule>[a-zA-Z0-9._,]{3,} => [a-zA-Z0-9._]{3,})/$', views.add_rule, name='add_rule'),
    url(r'^enforcedatamining/$', views.enforce_data_mining, name='enforce_data_mining'),
    url(r'^reloadrulematching/$', views.reload_rule_matching, name='reload_rule_matching')
]
