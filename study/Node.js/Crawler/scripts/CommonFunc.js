/**
 * 기본상수
 */
let __API_URL = '/api';
let __Socket_URL = 'http://localhost:3301';
let __ServerNotConnectMSG = '서버 연결 대기 시간이 초과되었습니다.';
let Global = {};
Object.defineProperty(Global, 'KEY', { value: Cookies.Get('userKey'), configurable: false, enumerable: false, writable: false });

// console.log("global key: " + Global.KEY);

jQuery.support.cors = true;

function GetLoginInfo(callback) {
  if ((typeof(callback) == 'object' || typeof(callback) == 'function') == false) {
    return;
  }

  let check = false;
  $.ajax({
    async: true,
    type: 'POST',
    dataType: 'json',
    url: __API_URL + '/login/0020',
    data: { 'key': Global.KEY },
    beforeSend: function() {},
    success: function(data) {
      let res = (typeof(data.res) == 'boolean') ? data.res : false;

      if (res) {
        let memberID = (data.ID) ? data.ID.Trim() : '';

        if (memberID.length > 0) {
          Object.defineProperty(Global, 'USER_NO', { value: data.USER_NO, configurable: false, enumerable: false, writable: false });
          Object.defineProperty(Global, 'ID', { value: memberID, configurable: false, enumerable: false, writable: false });
          Object.defineProperty(Global, 'NAME', { value: data.NAME.Trim(), configurable: false, enumerable: false, writable: true });
          Object.defineProperty(Global, 'GRADE', { value: data.GRADE.Trim(), configurable: false, enumerable: false, writable: false });

          check = true;
          callback(check);
        }
        else {
          _ShowError('세션이 종료되었습니다. 로그인을 다시 해주시기 바랍니다.', function() {
            callback(check);
          });
        }
      }
      else {
        let msg = (typeof(data.msg) == 'string') ? data.msg : '알 수 없는 오류가 발생했습니다.';

        _ShowError(msg, function() {
          callback(check);
        });
      }
    },
    error: function(xhr, ajaxOptions, thrownError) {
      _ShowError(__ServerNotConnectMSG, function() {
        callback(check);
      });
    },
    complete: function(jqXHR, textStatus) {}
  });
}

/**
 * 로그아웃
 */
function Logout() {
  Cookies.Set('userKey', '');
  location.href = '/html/Login.html?no=' + __NewNo;
}

/**
 * yyyymmdd -> yyyy-mm-dd 
 */
function dateSetFormat(str) {
  if (!str) return '';
  str = String(str);
  str = str.Trim();

  if (str.isNum() == false) { str = ''; }
  else if (str.length == 8) { str = str.substring(0, 4) + '-' + str.substring(4, 6) + '-' + str.substring(6, 8); }
  else if (str.length == 12) { str = str.substring(0, 4) + '-' + str.substring(4, 6) + '-' + str.substring(6, 8) + ' ' + str.substring(8, 10) + ':' + str.substring(10, 12); }
  else if (str.length == 14) { str = str.substring(0, 4) + '-' + str.substring(4, 6) + '-' + str.substring(6, 8) + ' ' + str.substring(8, 10) + ':' + str.substring(10, 12) + ':' + str.substring(12, 14); }
  else if (str.length == 16) { str = str.substring(0, 4) + '-' + str.substring(4, 6) + '-' + str.substring(6, 8) + ' ' + str.substring(8, 10) + ':' + str.substring(10, 12) + ':' + str.substring(12, 14); }

  return str;
}

/**
 * 빈값 -> 0으로
 */
function setZero(str) {
  if (!str) return '0';
  str = String(str);
  str = str.Trim();

  if (str.length == 0) { return '0'; }
  else { return str; }
}

/**
 * 문자열을 전화번호 형식으로 반환 (000-0000-0000)
 * @param str
 */
function str2Tel(str) {
  return str.replace(/^(01[0126789]{1}|02|0[3-9]{1}[0-9]{1}|0507)-?([0-9]{3,4})-?([0-9]{4})$/, '$1-$2-$3');
}

/**
 * 모바일 여부 확인(현재 화면 크기로 구분) 
 */
function isMobile() {
  let filter = 'win16|win32|win64|mac|macintel';
  /*
  if(navigator.platform) { 
  	if( filter.indexOf(navigator.platform.toLowerCase()) < 0 ) { 
  		//mobile alert('mobile 접속'); 
  		console.log("모바일 접속");
  	} else { 
  		//pc alert('pc 접속'); 
  		console.log("PC 접속");
  	} 
  }*/

  let windowWidth = $(window).width();
  let windowHeight = $(window).height();
  if (windowWidth < 800 || windowHeight < 600) {
    //창 가로 크기가 800 미만일 경우 
    return true;
  }
  else {
    //창 가로 크기가 800보다 클 경우
    return false;
  }
}

Array.prototype.objectIndexOf = function(field, searchElement) {
  for (let i = 0; i < this.length; i++) {
    let item = this[i];

    if (item[field] !== null) {
      if (item[field] == searchElement) {
        return i;
      }
    }
  }

  return -1;
};
