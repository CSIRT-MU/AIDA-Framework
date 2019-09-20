from configparser import ConfigParser
from datetime import datetime
from os import popen
from rest_framework.decorators import api_view
from rest_framework.response import Response
from sysdmanager import SystemdManager

import sqlite3

CFG_FILE = '/etc/aida/rest.ini'


def get_manager():
    return SystemdManager()


def connect_to_sqlite3_db():
    """
    Connects to the sqlite3 database.
    :return: Connection of type sqlite3.Connection.
    """
    config = ConfigParser()
    config.read(CFG_FILE)
    conn = sqlite3.connect(config['paths']['path_to_database'])
    conn.row_factory = sqlite3.Row
    return conn


def execute_sql(sql, rule_id):
    """
    Executes sql command with given rule id.
    :param sql: Sql command to be executed.
    :param rule_id: Rule id.
    """
    conn = connect_to_sqlite3_db()
    conn.row_factory = sqlite3.Row
    conn.cursor().execute(sql, rule_id)
    conn.commit()
    conn.cursor().close()


def is_running(service):
    """
    Checks if service is running using sysdmanager library.
    :param service: Service to be checked.
    :return: Information if service is running or not.
    """
    manager = get_manager()
    if manager.is_active(service + ".service"):
        return 1
    return 0


def print_info(service):
    """
    Prints info about service.
    :param service: Service which info will be printed.
    :return: System call containing info.
    """
    cmd = 'systemctl status ' + service + '.service -n 0'  # '-n 0' disables printing logs
    return popen(cmd).read()


def print_logs(service):
    """
    Prints last n logs of a service, n set to 5.
    :param service: Service which logs will be printed.
    :return: System call containing info.
    """
    cmd = 'journalctl -u ' + service + '.service -n 5'
    return popen(cmd).read()


@api_view(['GET'])
def system_state_with_more_info(request):
    components_dict = {'kafka': {'status': is_running('kafka'),
                                 'description': print_info('kafka'),
                                 'log': print_logs('kafka')},
                       'kafka_filer': {'status': is_running('aida-input'),
                                       'description': print_info('aida-input'),
                                       'log': print_logs('aida-input')},
                       'warden': {'receiver': {'status': is_running('warden_filer_receiver'),
                                               'description': print_info('warden_filer_receiver'),
                                               'log': print_logs('warden_filer_receiver')},
                                  'sender': {'status': is_running('warden_filer_sender'),
                                             'description': print_info('warden_filer_sender'),
                                             'log': print_logs('warden_filer_sender')}
                                  },
                       # TODO remove relationship_discovery if it won't be needed
                       'spark': {'relationship_discovery': {'status': is_running('sparkapp-graphdb'),
                                                            'description': print_info('sparkapp-graphdb'),
                                                            'log': print_logs('sparkapp-graphdb')},
                                 'sanitization': {'status': is_running('sanitization'),
                                                  'description': print_info('sanitization'),
                                                  'log': print_logs('sanitization')
                                                  },
                                 'aggregation': {'status': is_running('aggregation'),
                                                 'description': print_info('aggregation'),
                                                 'log': print_logs('aggregation')},
                                 'data_mining': {'status': is_running('mining'),
                                                 'description': print_info('mining'),
                                                 'log': print_logs('mining')}
                                 },
                       'esper': {'status': is_running('matching'),
                                 'description': print_info('matching'),
                                 'log': print_logs('matching')},
                       # TODO feedback components status
                       'feedback': {'status': 0,
                                    'description': "",
                                    'log': ""}
                       }

    return Response(components_dict)


# -------------------------------------------------DATABASE RELATED-----------------------------------------------------

@api_view(['GET'])
def get_rules(request, date1):
    date1 = str(date1) + '%'
    return Response(connect_to_sqlite3_db().cursor().execute("SELECT * FROM rule WHERE inserted LIKE ?", (date1,)))


@api_view(['GET'])
def active_rules(request):
    sql = "SELECT * FROM rule WHERE active=1"
    return Response(connect_to_sqlite3_db().cursor().execute(sql))


@api_view(['POST'])
def activate_rule(request, rule_id):
    rule_id = (int(rule_id),)
    sql = "UPDATE rule SET active=1 WHERE id=?"
    execute_sql(sql, rule_id)
    return Response()


@api_view(['POST'])
def deactivate_rule(request, rule_id):
    rule_id = (int(rule_id),)
    sql = "UPDATE rule SET active=0 WHERE id=?"
    execute_sql(sql, rule_id)
    return Response()


@api_view(['POST'])  # TODO change http method to delete
def delete_rule(request, rule_id):
    rule_id = (int(rule_id),)
    sql = "DELETE FROM rule WHERE id=?"
    execute_sql(sql, rule_id)
    return Response()


@api_view(['POST'])
def add_rule(request, given_rule):
    count_id = 1
    now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    conn = connect_to_sqlite3_db()
    for _ in conn.cursor().execute("SELECT * FROM rule"):
        count_id += 1
    sql = "INSERT INTO rule VALUES (?,?,?,0,0,0.0,0,NULL,'custom','')"
    conn.cursor().execute(sql, (count_id, now, given_rule))
    conn.commit()
    conn.cursor().close()
    return Response()


# ----------------------------------------------------------------------------------------------------------------------

@api_view(['POST'])
def enforce_data_mining(request):
    # TODO
    return Response()


@api_view(['POST'])
def reload_rule_matching(request):
    manager = get_manager()
    manager.restart_unit("matching.service")
    return Response()
