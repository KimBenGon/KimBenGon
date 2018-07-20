const common = require('./../../func/common');

let express = require('express');
let router = express.Router();

let queries = null;
common.getXML(__rootPath + "/query/common.xml", function(data) {
	if (data.query) {
		queries = data.query;
	}
});

function getQuery(tag) {
	tag = "Q" + tag;

	if (queries[tag]) {
		let currentQuery = queries[tag];
		return currentQuery[0] ? currentQuery[0] : "";
	}
	else {
		return "";
	}
}

router.get('/', function (req, res, next) {
	res.end();
});

module.exports = router;