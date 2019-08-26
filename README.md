# AIDA Framework

A framework for the Analysis of Intrusion Detection Alerts.

## About the AIDA Framework

AIDA is an analytical framework for processing intrusion detection alerts with a focus on alert correlation and predictive analytics. The framework contains components that filter, aggregate, and correlate the alerts, and predict future security events using the predictive rules distilled from historical records. The components are based on stream processing and use selected features of data mining (namely sequential rule mining) and complex event processing. The framework was designed to be deployed as an analytical component of an alert processing platform. Alternatively, it can be deployed locally for experimentations over datasets.

## Framework features

![Schema of the AIDA Framework](/aida_schema.png "Schema of the AIDA Framework")

## Getting started

For a quick start, go to the provision directory and run Vagrant:

`cd provision`

`vagrant up`

AIDA framework will start in few minutes. Then, send your data to the framework using the following command (you need to have netcat installed):

`nc localhost 4164 < path_to_file_with_your_data`

If you do not have your own data, we recommend trying AIDA framework out with our [dataset](http://dx.doi.org/10.17632/p6tym3fghz.1). Download and unzip the main file in the datase (dataset.idea.zip) and use it in the command above.

**Run data mining**

Trigger the data mining procedure (otherwise, it starts every 24 hours that you would have to wait):

`sudo systemctl start mining`

Check the logs of the data mining component:

`sudo journalctl -u mining`

**Update rules**

Open the database with the mined rules:

`sqlite3 /var/aida/rules/rule.db`

Check the rules in the database:

`select * from rule;`

Activate all the rules so that they are used by the rule matching component:

`update rule set active=1;`

Restart matching component to start matching activated rules:

`sudo systemctl restart matching`

Send some more data into AIDA, they will be matched against the rules to predict upcoming events:

`nc localhost 4164 < path_to_file_with_your_data`

**Check outputs**

Predicted rules are saved in the root directory of this repository in `predictions.json` file.

You can also get the predictions directly from Kafka:

`/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic predictions --from-beginning` 

## How to cite

There is a design paper on the AIDA framework: [AIDA Framework: Real-Time Correlation and Prediction of Intrusion Detection Alerts](http://dx.doi.org/10.1145/3339252.3340513). We recommend citing the paper, bibliography entries are provided as follows.

### Plain text

Martin Husák and Jaroslav Kašpar. 2019. "AIDA Framework: Real-Time Correlation and Prediction of Intrusion Detection Alerts". In *Proceedings of the 14th International Conference on Availability, Reliability and Security (ARES '19)*. ACM, New York, NY, USA, Article 81, 8 pages. DOI: https://doi.org/10.1145/3339252.3340513

### BibTex

    @inproceedings{AIDAframework,
      author = {Hus\'{a}k, Martin and Ka\v{s}par, Jaroslav},
      title = {AIDA Framework: Real-Time Correlation and Prediction of Intrusion Detection Alerts},
      booktitle = {Proceedings of the 14th International Conference on Availability, Reliability and Security},
      series = {ARES '19},
      year = {2019},
      isbn = {978-1-4503-7164-3},
      location = {Canterbury, CA, United Kingdom},
      pages = {81:1--81:8},
      doi = {10.1145/3339252.3340513},
      publisher = {ACM},
      address = {New York, NY, USA},
      keywords = {alert correlation, data mining, information sharing, intrusion detection, prediction}
    }

### Related publications

If you are interested in our work, you might be interested in our papers related to the topic:

[Survey of Attack Projection, Prediction, and Forecasting in Cyber Security](http://dx.doi.org/10.1109/COMST.2018.2871866)

[Towards Predicting Cyber Attacks Using Information Exchange and Data Mining](http://dx.doi.org/10.1109/IWCMC.2018.8450512)

[On the Sequential Pattern and Rule Mining in the Analysis of Cyber Security Alerts](http://dx.doi.org/10.1145/3098954.3098981)

[Exchanging Security Events: Which And How Many Alerts Can We Aggregate?](http://dx.doi.org/10.23919/INM.2017.7987340)

[A Graph-based Representation of Relations in Network Security Alert Sharing Platforms](http://dx.doi.org/10.23919/INM.2017.7987399)

## Acknowledgement

The development of the framework and related research were supported by the Security Research Programme of the Czech Republic 2015 - 2020 (BV III / 1 VS) granted by the Ministry of the Interior of the Czech Republic under No. VI20162019029 The Sharing and analysis of security events in the Czech Republic.

Further research was supported by ERDF "CyberSecurity, CyberCrime and Critical Information Infrastructures Center of Excellence" (No.CZ.02.1.01/0.0/0.0/16\_019/0000822).
