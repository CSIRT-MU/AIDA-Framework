from django.contrib import admin

from .models import Panel, Widget, PanelGroup, WidgetGroup


class PanelAdmin(admin.ModelAdmin):
    # description field is not used on front-end part, it is possible it will be used in future
    # fields = ['name', 'menu_name', 'alias', 'description', 'visible']
    fields = ['name', 'panel_group', 'menu_name', 'alias', 'include_filter', 'visible', 'external_link', 'external_link_url']
    list_display = ['name', 'include_filter', 'visible', 'external_link', 'external_link_url']

class WidgetAdmin(admin.ModelAdmin):
    # description field is not used on front-end part, it is possible it will be used in future
    # fields = ['panel', 'name', 'type', 'description', 'visible']
    fields = ['panel', 'name', 'type', 'data_type', 'cols', 'visible']
    list_display = ['name', 'panel', 'type', 'data_type', 'cols', 'visible']

class PanelGroupAdmin(admin.ModelAdmin):
    fields = ['name', 'description', 'visible']
    list_display = ['name', 'description', 'visible']

class WidgetGroupAdmin(admin.ModelAdmin):
    fields = ['panel', 'name', 'title', 'cols', 'visible'] #, 'description'
    list_display = ['panel', 'name', 'title', 'cols', 'visible'] #, 'description'

admin.site.register(Panel, PanelAdmin)
admin.site.register(Widget, WidgetAdmin)
admin.site.register(PanelGroup, PanelGroupAdmin)
admin.site.register(WidgetGroup, WidgetGroupAdmin)