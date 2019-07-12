# AIDA Framework

A framework for the Analysis of Intrusion Detection Alerts.

## About AIDA Framework

AIDA is an analytical framework for processing intrusion detection alerts with a focus on alert correlation and predictive analytics. The framework contains components that filter, aggregate, and correlate the alerts, and predict future security events using the predictive rules distilled from historical records. The components are based on stream processing and use selected features of data mining (namely sequential rule mining) and complex event processing. The framework was designed to be deployed as an analytical component of an alert processing platform. Alternatively, it can be deployed locally for experimentations over datasets.

## Framework Features

TODO

## Use-cases

TODO

## Getting started

For a quick start, go to the provision directory and run Vagrant:

`cd provision`
`vagrant up`

AIDA framework will start in few minutes. Then, send your data to the framework using the following command (you need to have netcat installed):

`nc localhost 4164 < path_to_file_with_your_data`

If you do not have your own data, we recommend trying AIDA framework out with our [dataset](http://dx.doi.org/10.17632/p6tym3fghz.1). Download and unzip the main file in the datase (dataset.idea.zip) and use it in the command above.

## How to reference

### Bibtex

TODO

### Plain text

TODO

### Related publications

[AIDA Framework: Real-Time Correlation and Prediction of Intrusion Detection Alerts]()

[Towards Predicting Cyber Attacks Using Information Exchange and Data Mining](http://dx.doi.org/10.1109/IWCMC.2018.8450512)

[On the Sequential Pattern and Rule Mining in the Analysis of Cyber Security Alerts](http://dx.doi.org/10.1145/3098954.3098981)

[Exchanging Security Events: Which And How Many Alerts Can We Aggregate?](http://dx.doi.org/10.23919/INM.2017.7987340)

[A Graph-based Representation of Relations in Network Security Alert Sharing Platforms](http://dx.doi.org/10.23919/INM.2017.7987399)

## Acknowledgement

The development of the framework and related research were supported by the Security Research Programme of the Czech Republic 2015 - 2020 (BV III / 1 VS) granted by the Ministry of the Interior of the Czech Republic under No. VI20162019029 The Sharing and analysis of security events in the Czech Republic.

Further research was supported by ERDF "CyberSecurity, CyberCrime and Critical Information Infrastructures Center of Excellence" (No.CZ.02.1.01/0.0/0.0/16\_019/0000822).
