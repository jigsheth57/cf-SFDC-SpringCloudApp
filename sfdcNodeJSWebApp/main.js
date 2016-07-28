// set up ========================
var express = require('express');
var session = require('express-session');
var request = require('request');
var morgan = require('morgan'); // log requests to the console (express4)
var bodyParser = require('body-parser'); // pull information from HTML POST
var cfenv = require("cfenv");
var http = require("http");

var appEnv = cfenv.getAppEnv();
var app = express(); // create our app w/ express
var server = http.createServer(app);

// configuration =================
app.use(session({
	  secret: 'ssshhhhh',
	  resave: false,
	  saveUninitialized: true
	}));

app.use(express.static(__dirname + '/public')); // set the static files location /public/img will be /img for users

app.use(morgan('dev')); // log every request to the console
app.use(bodyParser.urlencoded({
	'extended' : 'true'
})); // parse application/x-www-form-urlencoded
app.use(bodyParser.json()); // parse application/json
app.use(bodyParser.json({
	type : 'application/vnd.api+json'
})); // parse application/vnd.api+json as json

// listen (start app with node server.js) ======================================
server.listen(appEnv.port, appEnv.bind, function() {
	  console.log("sfdc-web is listening on port " + appEnv.url)
});

// Global Variable
var sess;
var endpoint;
var gatewayURI = appEnv.getServiceCreds("sfdcgateway");

if(gatewayURI) {
	endpoint = gatewayURI.uri.toString();
} else {
	endpoint = "http://localhost:9011";
}
console.log("endpoint: "+endpoint);

// routes ======================================================================

// api ---------------------------------------------------------------------

//get all contacts group by accounts
app.get('/api/accounts', function(req, res) {
	var reqObj = {url: endpoint + "/accountservice/accounts", method: req.method, body: ""};
	getSFDCObjs(reqObj, req, res);
});

//create/retrieve/update/delete account by id
app.all('/api/account/:id', function(req, res) {
	//console.log("account id: "+req.params.id);
	var reqObj = "";
	switch(req.method) {
		case "POST" : reqObj = {url: endpoint + "/accountservice/account", method: req.method, body: JSON.stringify(req.body)};break;
		case "PUT" : delete req.body.Id; reqObj = {url: endpoint + "/accountservice/account/"+req.params.id, method: req.method, body: JSON.stringify(req.body)};break;
		case "DELETE" : reqObj = {url: endpoint + "/accountservice/account/"+req.params.id, method: req.method, body: ""};break;
		default : reqObj = {url: endpoint + "/accountservice/account/"+req.params.id, method: "GET", body: ""};break;
	}
	//console.log("body: "+reqObj.body);
	getSFDCObjs(reqObj, req, res);
});

//get all opportunities group by accounts
app.get('/api/opp_by_accts', function(req, res) {
	var reqObj = {url: endpoint + "/accountservice/opp_by_accts", method: req.method, body: ""};
	getSFDCObjs(reqObj, req, res);
});

//create/retrieve/update/delete contact by id
app.all('/api/contact/:id', function(req, res) {
	var reqObj = "";
	switch(req.method) {
		case "POST" : reqObj = {url: endpoint + "/contactservice/contact", method: req.method, body: JSON.stringify(req.body)};break;
		case "PUT" : delete req.body.Id; delete req.body.Name; reqObj = {url: endpoint + "/contactservice/contact/"+req.params.id, method: req.method, body: JSON.stringify(req.body)};break;
		case "DELETE" : reqObj = {url: endpoint + "/contactservice/contact/"+req.params.id, method: req.method, body: ""};break;
		default : reqObj = {url: endpoint + "/contactservice/contact/"+req.params.id, method: "GET", body: ""};break;
	}
	getSFDCObjs(reqObj, req, res);
});

//create/retrieve/update/delete opportunity by id
app.all('/api/opportunity/:id', function(req, res) {
	var reqObj = "";
	switch(req.method) {
		case "POST" : reqObj = {url: endpoint + "/opportunityservice/opportunity", method: req.method, body: JSON.stringify(req.body)};break;
		case "PUT" : delete req.body.Id; delete req.body.ExpectedRevenue; delete req.body.IsClosed; delete req.body.IsWon; reqObj = {url: endpoint + "/opportunityservice/opportunity/"+req.params.id, method: req.method, body: JSON.stringify(req.body)};break;
		case "DELETE" : reqObj = {url: endpoint + "/opportunityservice/opportunity/"+req.params.id, method: req.method, body: ""};break;
		default : reqObj = {url: endpoint + "/opportunityservice/opportunity/"+req.params.id, method: "GET", body: ""};break;
	}
	getSFDCObjs(reqObj, req, res);
});

function getSFDCObjs(reqObj, req, res) {
	console.log("sending data to: "+reqObj.url);
	console.log("send data: "+reqObj.body);
	sess = req.session;
	if (sess.token) {
		request({
			url : reqObj.url,
			headers : {
				'content-type': 'application/json',
				'token' : sess.token
			},
			method : reqObj.method,
			body : reqObj.body
		}, function(error, response, body) {
			console.log("body: "+body);
			console.log("response.statusCode: "+response.statusCode);
			(response.statusCode >= 200 && response.statusCode <= 299) ? res.end(body) : res.end(error);
		});
	} else {
		login(sess, res, req.path);
	}
}

function login(sess, res, uri) {
	var url = endpoint + "/authservice/oauth2";
	request({
		url : url,
		method : "GET"
	}, function(error, response, body) {
		if (response.statusCode == 200) {
			//console.log(body);
			var jsonObj = JSON.parse(body);
			sess.token = jsonObj.accessToken;
			res.redirect(uri);
		}
	});
}
/*
function getSFDCObjs(reqObj, req, res) {
//	console.log("sending data to: "+reqObj.url);
//	console.log("send data: "+reqObj.body);
		request({
			url : reqObj.url,
			headers : {
				'content-type': 'application/json',
			},
			method : reqObj.method,
			body : reqObj.body
		}, function(error, response, body) {
//			console.log("body: "+body);
//			console.log("response.statusCode: "+response.statusCode);
			(response.statusCode >= 200 && response.statusCode <= 299) ? res.end(body) : res.end(error);
		});
}
*/
// application -------------------------------------------------------------
app.get('*', function(req, res) {
	res.sendFile(__dirname + '/public/index.html'); // load the single view file
											// (angular will handle the page
											// changes on the front-end)
});
