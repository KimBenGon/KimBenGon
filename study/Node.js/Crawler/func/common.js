let fs = require('fs');
let css = require('css');
let crypto = require('crypto');
let cryptoKey = 'Nodejs_2018_Study_Project_Crawler_kbg';
let mysql = require('mysql');
let mysql_pool = mysql.createPool({
    connectionLimit: 500,
    acquireTimeout: 1000 * 10,
    host: "111.111.111.111",
    user: "kbg",
    password: "kbg",
    database: "Node_Crawler"
});

exports.StrFunc = require('./__String');
exports.DateFunc = require('./__Date');

/**
 * 범위에 속한 난수(실수형) 반환
 */
exports.random = function (low, high) {
    return Math.random() * (high - low) + low;
};

/**
 * 범위에 속한 난수(정수형) 반환
 */
exports.randomInt = function (low, high) {
    return Math.floor(Math.random() * (high - low) + low);
};

/**
 * XML 내용을 JSON 객체로 반환
 */
let xml2js = require('xml2js');
exports.getXML = function (path, callback) {
    let parser = new xml2js.Parser();
    let xml = fs.readFileSync(path, 'utf-8');

    try {
        // xml = xml.replace(/\n/g, "");
        // xml = xml.replace(/\r/g, "");
        // xml = xml.replace(/\t/g, " ");

        parser.parseString(xml, function (err, result) {
            if (err) {
                console.error(err);
                if (callback) callback(null);
            }

            if (callback) callback(result);
        });
    }
    catch (e) {
        console.error(e);
        if (callback) callback(null);
    }
};

exports.getCSS = function (path, callback) {
    let options = null;
    let cssCont = fs.readFileSync(__rootPath + path, 'utf-8');
    let obj = null;
    try {
        obj = css.parse(cssCont, options);
        css.stringify(obj, options);
    }
    catch (e) {
        console.error(e);
        if (callback) callback(null);
    }
};

/**
 * AES128 암호화
 */
exports.encrypted = function (str) {
    let cipher = crypto.createCipher('aes-256-cbc', cryptoKey);

    try {
        let rtn = cipher.update(str, 'utf8', 'hex');
        rtn += cipher.final('hex');

        return rtn;
    }
    catch (e) {
        console.error(e);
        return null;
    }
};

/**
 * AES128 복호화
 */
exports.decrypted = function (str) {
    let decipher = crypto.createDecipher('aes-256-cbc', cryptoKey);

    try {
        let rtn = decipher.update(str, 'hex', 'utf8');
        rtn += decipher.final('utf8');

        return rtn;
    }
    catch (e) {
        console.error(e);
        return null;
    }
};

/**
 * SHA 256 암호화
 */
exports.sha256Encrypted = function (str) {
    try {
        let hash = crypto.createHash('sha256').update(str).digest('hex', 'utf8');
        return hash;
    }
    catch (e) {
        console.error(e);
        return null;
    }
};

/**
 * 회원 Key 복호화
 */
exports.keyDecrypted = function (keyStr) {
    let loginInfo = {
        USER_NO: '0',
        ID: '',
        GRADE: 'M',
        AUTH_KEY: ''
    };

    if (!keyStr) return loginInfo;

    let temp = this.decrypted(keyStr);

    if (temp.length > 0) {
        let tempArr = temp.split('|');

        if (tempArr.length == 5) {
            loginInfo.USER_NO = tempArr[0];
            loginInfo.ID = tempArr[1];
            loginInfo.GRADE = tempArr[2];
            loginInfo.AUTH_KEY = tempArr[3];
        }
    }

    return loginInfo;
};

/**
 * Query Multi 실행
 */
exports.executeSelectBatch = function (jobs, callback, showQuery) {
    if (!jobs) return;
    this.executeSelectBatchJob(jobs, 0, callback, showQuery, []);
};

exports.executeSelectBatchJob = function (jobs, index, callback, showQuery, rtnData) {
    if (jobs.length == index) {
        if (callback) callback(rtnData);
        return;
    }

    let oThis = this;

    this.executeSelect(jobs[index].query, jobs[index].param, function (data) {
        rtnData.push(data);
        oThis.executeSelectBatchJob(jobs, ++index, callback, showQuery, rtnData);
    }, showQuery);
};

exports.executeUpdateBatch = function (jobs, callback, showQuery) {
    if (!jobs) return;
    this.executeUpdateBatchJob(jobs, 0, callback, showQuery, []);
};

exports.executeUpdateBatchJob = function (jobs, index, callback, showQuery, rtnData) {
    if (jobs.length == index) {
        if (callback) callback(rtnData);
        return;
    }

    let oThis = this;

    this.executeUpdate(jobs[index].query, jobs[index].param, function (data) {
        rtnData.push(data);
        oThis.executeUpdateBatchJob(jobs, ++index, callback, showQuery, rtnData);
    }, showQuery);
};

/**
 * Select Query 실행
 */
exports.executeSelect = function (query, param, callback, showQuery) {
    let rtnData = {
        res: false,
        rows: [],
        error: null
    };

    if (typeof (showQuery) != 'boolean') showQuery = false;

    mysql_pool.getConnection(function (err, connection) {
        if (err) {
            try { connection.release(); }
            catch (e) { }
            rtnData.error = err;
            if (callback) callback(rtnData);
            return;
        }

        try {
            connection.config.queryFormat = function (query, values) {
                if (!values) return query;
                return query.replace(/\:(\w+)/g, function (txt, key) {
                    if (values.hasOwnProperty(key)) {
                        return this.escape(values[key]);
                    }
                    return txt;
                }.bind(this));
            };

            let executedQuery = connection.query(query, param, function (err, rows, fields) {
                if (err) {
                    try { connection.release(); }
                    catch (e) { }
                    rtnData.error = err;
                    if (callback) callback(rtnData, fields);
                    return;
                }

                try { connection.release(); }
                catch (e) { }

                rtnData.res = true;

                if (rows === null) {
                    rtnData.rows = [];
                }
                else {
                    rtnData.rows = rows;
                }

                if (callback) callback(rtnData, fields);
            });

            if (showQuery) console.log(executedQuery.sql);
        }
        catch (e) {
            try { connection.release(); }
            catch (e) { }
            rtnData.error = e;
            if (callback) callback(rtnData);
            return;
        }
    });
};

/**
 * Update/Delete/Insert Query 실행
 */
exports.executeUpdate = function (query, param, callback, showQuery) {
    let rtnData = {
        res: false,
        count: 0,
        insertId: -1,
        error: null
    };

    if (typeof (showQuery) != 'boolean') showQuery = false;

    mysql_pool.getConnection(function (err, connection) {
        if (err) {
            try { connection.release(); }
            catch (e) { }
            rtnData.error = err;
            if (callback) callback(rtnData);
            return;
        }

        try {
            connection.config.queryFormat = function (query, values) {
                if (!values) return query;
                return query.replace(/\:(\w+)/g, function (txt, key) {
                    if (values.hasOwnProperty(key)) {
                        return this.escape(values[key]);
                    }
                    return txt;
                }.bind(this));
            };

            let executedQuery = connection.query(query, param, function (err, result, fields) {
                if (err) {
                    try { connection.release(); }
                    catch (e) { }
                    rtnData.error = err;
                    if (callback) callback(rtnData, fields);
                    return;
                }

                try { connection.release(); }
                catch (e) { }

                rtnData.res = true;
                rtnData.count = result.affectedRows;
                rtnData.insertId = result.insertId;

                if (callback) callback(rtnData, fields);
            });

            if (showQuery) console.log(executedQuery.sql);
        }
        catch (e) {
            try { connection.release(); }
            catch (e) { }
            rtnData.error = e;
            callback(rtnData);
            return;
        }
    });
};