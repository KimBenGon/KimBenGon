/*
 *	문자열을 세자리마다 콤마형식으로 변환 (소수점 포함)
 */
exports.PrtComma = function(s) {
  var prt = '';
  var lng = 0;
  var chk = 0;
  var str = '';

  var decimalPoint = '';
  var decimalPointChk = s.indexOf('.');

  if (decimalPointChk > -1) {
    decimalPoint = s.substring(decimalPointChk + 1);
    str = s.substring(0, decimalPointChk);
  }
  else {
    str = s;
  }

  // 부호확인
  var sign = '';

  if (str.substr(0, 1) == '-') {
    sign = '-';
    str = str.substr(1);
  }

  if (str.isNum() == false) return str;
  lng = str.length;

  for (var i = lng - 1; i >= 0; i--) {
    ++chk;

    if ((chk % 3) == 0) {
      if (i == 0)
        prt = str.substr(i, 1) + prt;
      else
        prt = ',' + str.substr(i, 1) + prt;
    }
    else {
      prt = str.substr(i, 1) + prt;
    }
  }

  if (decimalPointChk > -1) prt += '.' + decimalPoint;
  if (sign.length > 0) prt = sign + prt;

  return prt;
};

/*
 * HTML Tag Clear
 */
exports.removeTag = function(s) {
  return s.replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/g, '');
};

/*
 * 문자열 숫자여부 확인
 */
exports.isNum = function(s) {
  if (s == null) return false;
  return /^[0-9]+$/.test(s);
};

/*
 * 문자열 영문자/숫자여부 확인
 */
exports.isEng = function(s) {
  if (s == null) return false;
  return /^[0-9A-Za-z]+$/.test(s);
};

/*
 * 문자열 이메일형식여부 확인
 */
exports.isEMail = function(s) {
  if (s == null) return false;
  return /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/.test(s);
};

/*
 * 문자열 #메일형식여부 확인
 */
exports.isShapMail = function(s) {
  if (s == null) return false;
  return /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*#[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/.test(s);
};

/*
 * 문자열 전화번호 형식여부 확인
 */
exports.isTel = function(s) {
  if (s == null) return false;
  return /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$/.test(s);
};

/*
 * 문자열 날짜형식 여부 확인
 */
exports.isDate = function(s) {
  var valid = false;

  if (s.search(/\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|[0-3][0-9])/) == 0) {
    var arrDay = s.split('-');
    var year = parseInt(arrDay[0]);
    var month = parseInt(arrDay[1].replace(/^0(\d)/g, '$1'));
    var day = parseInt(arrDay[2].replace(/^0(\d)/g, '$1'));
    var d = new Date(year, month - 1, day);
    if (d.getMonth() == month - 1 && d.getDate() == day) valid = true;
  }

  return valid;
};

/*
 * 문자열 사업자번호 확인
 */
exports.isBusinessNo = function(s) {
  var str = s;
  if (str.length == 12) str = str.replace(/-/g, '');
  if (str.length != 10) return false;

  for (var i = 0; i < str.length; i++) {
    if (!str.charAt(i).isNum()) return false;
  }

  var checkID = new Array(1, 3, 7, 1, 3, 7, 1, 3, 5, 1);
  var i, chkSum = 0,
    c2, remander;

  for (i = 0; i <= 7; i++) {
    chkSum += checkID[i] * parseInt(str.charAt(i));
  }

  c2 = '0' + (checkID[8] * parseInt(str.charAt(8)));
  c2 = c2.substring(c2.length - 2, c2.length);
  chkSum += Math.floor(c2.charAt(0)) + Math.floor(c2.charAt(1));
  remander = (10 - (chkSum % 10)) % 10;

  if (Math.floor(str.charAt(9)) == remander) return true;
  return false;
};

/*
 * byte단위 길이반환
 */
exports.byteLen = function(s) {
  var str = s;
  var length = 0;

  for (var i = 0; i < str.length; i++) {
    if (escape(str.charAt(i)).length >= 4)
      length += 2;
    else if (escape(str.charAt(i)) == '%A7')
      length += 2;
    else
    if (escape(str.charAt(i)) != '%0D')
      length++;
  }

  return length;
};

/*
 * byte단위 자르기 
 */
exports.cutByte = function(s, len) {
  var str = s;
  var count = 0;

  for (var i = 0; i < str.length; i++) {
    if (escape(str.charAt(i)).length >= 4)
      count += 2;
    else
    if (escape(str.charAt(i)) != '%0D')
      count++;

    if (count > len) {
      if (escape(str.charAt(i)) == '%0A')
        i--;

      break;
    }
  }

  return str.substring(0, i);
};
