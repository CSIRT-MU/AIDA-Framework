# -*- coding: utf-8 -*-
# Generated by Django 1.11.7 on 2017-12-01 13:45
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0004_auto_20171201_1341'),
    ]

    operations = [
        migrations.AlterField(
            model_name='panel',
            name='description',
            field=models.TextField(max_length=1000),
        ),
        migrations.AlterField(
            model_name='widget',
            name='description',
            field=models.TextField(max_length=1000),
        ),
    ]