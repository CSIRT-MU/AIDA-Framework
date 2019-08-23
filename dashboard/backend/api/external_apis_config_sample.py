# Add credentials and remove "_sample" from filename

# Flowmon ADS API
flowmon_ads_api = '''{
"name" : "Flowmon ADS",
"baseURL" : "https://collector2.csirt.muni.cz",
"username" : "ADD USERNAME HERE",
"password" : "ADD PASSWORD HERE",
"timeRange" : 1435,
"refreshPeriod" : 10,
"perspective" : 5,
"nfSource": 3
}'''

# RT Tickets API
rt_tickets_api = '''{"name" : "RTIR",
"baseURL" : "https://rt.csirt.muni.cz",
"username" : "ADD USERNAME HERE",
"password" : "ADD PASSWORD HERE",
"refreshPeriod" : 5,
"ticketStatus" : ["new","open"]
}'''

# TWITTER API
twitter_api = '''{ "twitterList" : "https://twitter.com/SipkaMatej/lists/thesis", "refreshPeriod" : 10 }'''