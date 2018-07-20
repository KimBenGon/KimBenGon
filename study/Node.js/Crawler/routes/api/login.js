const common = require('./../../func/common');
let express = require('express');
let router = express.Router();

let queries = null;
common.getXML(__rootPath + '/query/login.xml', function(data) {
  if (data.query) {
    queries = data.query;
  }
});

function getQuery(tag) {
  tag = 'Q' + tag;

  if (queries[tag]) {
    let currentQuery = queries[tag];
    return currentQuery[0] ? currentQuery[0] : '';
  }
  else {
    return '';
  }
}
/**
 * 회원인증
 * 
 * key : 인증키
 */
router.post('/0020', function(req, res, next) {
    let rtnData = {
      res: false,
      msg: ''
    };
  
    if (!req.body.key) {
      rtnData.msg = '세션이 종료되었습니다. 로그인을 다시 해주시기 바랍니다.';
      return res.json(rtnData);
    }
  
    let loginInfo = common.keyDecrypted(req.body.key);
  
    if (loginInfo.ID.length === 0) {
      rtnData.msg = '세션이 종료되었습니다. 로그인을 다시 해주시기 바랍니다.';
      return res.json(rtnData);
    }
  
    let queryParam = {
      USER_NO: loginInfo.USER_NO,
      AUTH_KEY: loginInfo.AUTH_KEY
    };
  
    common.executeSelect(getQuery('0020'), queryParam, function(data) {
      let checkCount = 0;
  
      if (data.res && data.rows.length > 0) {
        checkCount = data.rows[0].CHECK_COUNT;
      }
  
      if (checkCount > 0) {
        rtnData = {
          res: true,
          USER_NO: loginInfo.USER_NO,
          ID: data.rows[0].USER_ID,
          NAME: data.rows[0].USER_NM,
          GRADE: data.rows[0].GRADE,
        };
      }
      else {
        rtnData.msg = '세션이 종료되었습니다. 로그인을 다시 해주시기 바랍니다.';
      }

      res.json(rtnData);
    });
  });

module.exports = router;