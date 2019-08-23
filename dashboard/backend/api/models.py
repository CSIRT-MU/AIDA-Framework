from django.db import models
# from django.contrib.postgres.fields import ArrayField

class PanelGroup(models.Model):
    name = models.CharField(max_length=200)
    description = models.TextField(max_length=1000, blank=True)
    visible = models.BooleanField(default=True)

    def __unicode__(self):
        return self.name

class Panel(models.Model):
    panel_group = models.ForeignKey(PanelGroup, on_delete=models.CASCADE, null=True)
    name = models.CharField(max_length=200)
    menu_name = models.CharField(max_length=100)
    alias = models.CharField(max_length=100, unique=True)
    description = models.TextField(max_length=1000, blank=True)
    visible = models.BooleanField(default=True)
    include_filter = models.BooleanField(default=True)
    external_link = models.BooleanField(default=False)
    external_link_url = models.CharField(max_length=1000, blank=True)
    def __unicode__(self):
        return self.name

class Widget(models.Model):
    panel = models.ForeignKey(Panel, on_delete=models.CASCADE)
    name = models.CharField(max_length=200)
    type = models.CharField(max_length=50)
    data_type = models.CharField(max_length=50, blank=True, null=True)
    description = models.TextField(max_length=1000, blank=True)
    # data = models.TextField()

    # Width of widget (12 is full-width, 6 is half-width as in Bootstrap
    # default is 12 (full-width)
    cols = models.IntegerField(default=12)
    visible = models.BooleanField(default=True)

    def __unicode__(self):
        return self.name

class WidgetGroup(models.Model):
    panel = models.ForeignKey(Panel, on_delete=models.CASCADE)
    title = models.CharField(max_length=200, blank=True) 
    name = models.CharField(max_length=200)
    # Width of widget-group (12 is full-width, 6 is half-width as in Bootstrap
    # default is 12 (full-width)
    cols = models.IntegerField(default=12)
    # description = models.TextField(max_length=1000, blank=True)
    visible = models.BooleanField(default=True)

    def __unicode__(self):
        return self.name
