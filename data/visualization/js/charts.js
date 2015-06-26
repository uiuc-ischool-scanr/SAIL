var margin = {top: 40, right: 20, bottom: 30, left: 40},
    width = 1000 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var margin = {top: 10, right: 10, bottom: 100, left: 40},
    margin2 = {top: 450, right: 10, bottom: 30, left: 40},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom,
    height2 = 500 - margin2.top - margin2.bottom;

var fObj = null;
var uids = ["10154643193000024","728525023"];
var drawViz = function(error, data){
	if (error) return console.warn(error);
	var fbVizObj = new fbViz();
	console.log(data);
	var dataTmp = {};
	//var formatDateTime = d3.time.format("%m/%d/%Y %H:%M:%S");
	data.forEach(function(d) {
		// body...
		if(!(d["user"] in dataTmp)){
			dataTmp[d["user"]] = {
			 "children": [],
			 "user": d["user"],
			 "first_post": new Date(),
			 "Total Retweets": 0,
			 "Total Tweets": 0,
			 "Sentiments" : {"positive": 0, "neutral": 0, "negative": 0},
			 "Total Sentiment": 0,
			 "Overall Sentiment": "neutral",
			 "Twitter Followers": +d["'Twitter Followers'"]
			}
		}
		console.log("Time: ", d["published_date"], d["published_date"].replace(/\'/g,""), formatDateTime.parse(d["published_date"].replace(/\'/g,"")))
		var tObj = {
			"text": d["tweet_text"],
			"time": formatDateTime.parse(d["published_date"].replace(/\'/g,"")),
			//"time": formatDateTime.parse(d["published_date"]),
			"c_url": +d["c_url"],
			"c_hash": +d["c_hash"],
			"c_mention": +d["c_mention"],
			"Twitter Retweets": +d["'Twitter Retweets'"],
			"Twitter Reply Count": +d["'Twitter Reply Count'"],
			"sentiment": d["sentiment"]
		}
		dataTmp[d["user"]]["children"].push(tObj);
		dataTmp[d["user"]]["Total Retweets"] += tObj["Twitter Retweets"];
		dataTmp[d["user"]]["Total Tweets"] += 1;
		dataTmp[d["user"]]["first_post"] = d3.min(
			[dataTmp[d["user"]]["first_post"], tObj["time"]]);
		var t_sent = 0;
		if(tObj["sentiment"] == "positive"){
			t_sent = 1;
		} else if(tObj["sentiment"] == "negative"){
			t_sent = -1;
		}
		dataTmp[d["user"]]["Total Sentiment"] += t_sent;
		if(t_sent > 0){
			dataTmp[d["user"]]["Overall Sentiment"] = "positive";
		} else if(t_sent < 0){
			dataTmp[d["user"]]["Overall Sentiment"] = "negative";
		}

		dataTmp[d["user"]]["Sentiments"][tObj["sentiment"]] += 1;
		//console.log(tObj);
	});
	var data_Arr = [];
	//console.log(dataTmp);
	$.each(dataTmp, function(e){
		data_Arr.push(dataTmp[e]);
	});
	console.log(data_Arr);
	data = data_Arr;

	fbVizObj.init(data, {
			margin: margin,
			width: width,
			height: height,
			divId: "#graphs"
		},
		{
			margin: margin2,
			height: height2
		}, uids, dOpts);
	fbVizObj.drawChart();
	fObj = fbVizObj;

};

d3.tsv(files[0], drawViz);