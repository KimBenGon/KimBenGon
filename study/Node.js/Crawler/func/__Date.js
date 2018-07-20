/**
 * 두 날짜 차이 비교
 */
exports.DateDiff = {
	Second: function(d1, d2) {
    	var t2 = d2.getTime();
    	var t1 = d1.getTime();
    	return parseInt((t2-t1)/1000);
	},

	Minute: function(d1, d2) {
    	var t2 = d2.getTime();
    	var t1 = d1.getTime();
    	return parseInt((t2-t1)/1000/60);
	},

    Day: function(d1, d2) {
    	var t2 = d2.getTime();
    	var t1 = d1.getTime();
    	return parseInt((t2-t1)/(24*3600*1000));
    },

    Weeks: function(d1, d2) {
    	var t2 = d2.getTime();
    	var t1 = d1.getTime();
    	return parseInt((t2-t1)/(24*3600*1000*7));
    },

    Month: function(d1, d2) {
    	var d1Y = d1.getFullYear();
    	var d2Y = d2.getFullYear();
    	var d1M = d1.getMonth();
    	var d2M = d2.getMonth();
    	return (d2M+12*d2Y)-(d1M+12*d1Y);
    },

    Year: function(d1, d2) {
    	var t = d2.getFullYear() - d1.getFullYear();
    	return t;
    }
};

/**
 * 오늘기준 +/- (년/월/일) 날짜 가져오기
 */
exports.DateAdd = {
    Day: function(v) {
    	var today = new Date();
    	return new Date(today.getFullYear(), today.getMonth(), today.getDate() + v);
    },

    Month: function(v) {
    	var today = new Date();
    	return new Date(today.getFullYear(), today.getMonth() + v, today.getDate());
    },

    Year: function(v) {
    	var today = new Date();
    	return new Date(today.getFullYear() + v, today.getMonth(), today.getDate());
    },
    
    Hour: function(v) {
    	var today = new Date();
    	return new Date(today.getFullYear(), today.getMonth(), today.getDate(), today.getHours() + v, today.getMinutes(), today.getSeconds());
    },

    Minute: function(v) {
    	var today = new Date();
    	return new Date(today.getFullYear(), today.getMonth(), today.getDate(), today.getHours(), today.getMinutes() + v, today.getSeconds());
    },

    Second: function(v) {
    	var today = new Date();
    	return new Date(today.getFullYear(), today.getMonth(), today.getDate(), today.getHours(), today.getMinutes(), today.getSeconds() + v);
    }
};

/**
 * 임의 날짜 +/- (년/월/일) 날짜 가져오기
 */
exports.DateAddCustom = {
    Day: function(d, v) {
    	return new Date(d.getFullYear(), d.getMonth(), d.getDate() + v);
    },

    Month: function(d, v) {
    	return new Date(d.getFullYear(), d.getMonth() + v, d.getDate());
    },

    Year: function(d, v) {
    	return new Date(d.getFullYear() + v, d.getMonth(), d.getDate());
    },
    
    Hour: function(d, v) {
    	return new Date(d.getFullYear(), d.getMonth(), d.getDate(), d.getHours() + v, d.getMinutes(), d.getSeconds());
    },

    Minute: function(d, v) {
    	return new Date(d.getFullYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes() + v, d.getSeconds());
    },

    Second: function(d, v) {
    	return new Date(d.getFullYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds() + v);
    }
};

/**
 * 문자열을 날짜형식으로 변경
 */
exports.Str2Date = function(s) {
	var year = 0, month = 0, day = 0;
	var hour = 0, minute = 0, second = 0;

	if (s.indexOf(" ") > -1) {
		var items = s.split(" ");

		if (isDate(s) == false) {
			return null;
		}

		var arrDay = items[0].split("-");
		year = parseInt(arrDay[0]);
		month = parseInt(arrDay[1].replace(/^0(\d)/g,"$1"));
		day = parseInt(arrDay[2].replace(/^0(\d)/g,"$1"));

		if (items[1].split(":").length == 3) {
			var arrTime = items[1].split(":");
			hour = parseInt(arrTime[0]);
			minute = parseInt(arrTime[1]);
			second = parseInt(arrTime[2]);
		}
		else {
			return null;
		}
	}
	else {
		if (isDate(s) == false) {
			return null;
		}

		var arrDay = s.split("-");
		year = parseInt(arrDay[0]);
		month = parseInt(arrDay[1].replace(/^0(\d)/g,"$1"));
		day = parseInt(arrDay[2].replace(/^0(\d)/g,"$1"));
	}

	return new Date(year, month - 1, day, hour, minute, second);
};

/*
 * 문자열 날짜형식 여부 확인
 */
function isDate(s) {
	var valid = false;

	if (s.search(/\d{4}-([1-9]|0[1-9]|1[0-2])-([1-9]|[0-3][0-9])/) == 0) {
		var arrDay = s.split("-");
		var year = parseInt(arrDay[0]);
		var month = parseInt(arrDay[1].replace(/^0(\d)/g,"$1"));
		var day = parseInt(arrDay[2].replace(/^0(\d)/g,"$1"));
		var d = new Date(year, month - 1, day);
		if (d.getMonth() == month - 1 && d.getDate() == day) valid = true ;
	}

	return valid;
}

/**
 * 날짜를 yyyy-MM-dd 형식으로 출력
 */
exports.PrtDate = function(d) {
	var prtString = d.getFullYear().toString() + "-";
	var month = d.getMonth() + 1;
	var day = d.getDate();

	if (month < 10) { prtString += "0" + month.toString(); }
	else { prtString += month.toString(); }

	prtString += "-";

	if (day < 10) { prtString += "0" + day.toString(); }
	else { prtString += day.toString(); }

	return prtString;
};

/**
 * 날짜를 yyyyMMdd 형식으로 출력
 * @param d
 * @returns
 */
exports.PrtDateNoDash = function(d) {
	var prtString = d.getFullYear().toString();
	var month = d.getMonth() + 1;
	var day = d.getDate();

	if (month < 10) { prtString += "0" + month.toString(); }
	else { prtString += month.toString(); }

	if (day < 10) { prtString += "0" + day.toString(); }
	else { prtString += day.toString(); }

	return prtString;
};

/**
 * 날짜를 yyyyMMddHHmmss 형식으로 출력
 * @param d
 * @returns
 */
exports.PrtDateTimeNoDash = function(d) {
	var prtString = d.getFullYear().toString();
	var month = d.getMonth() + 1;
	var day = d.getDate();
	var hours = d.getHours();
	var minutes = d.getMinutes();
	var seconds = d.getSeconds();

	if (month < 10) { prtString += "0" + month.toString(); }
	else { prtString += "" + month.toString(); }

	if (day < 10) { prtString += "0" + day.toString(); }
	else { prtString += "" + day.toString(); }

	if (hours < 10) { prtString += "0" + hours.toString(); }
	else { prtString += "" + hours.toString(); }

	if (minutes < 10) { prtString += "0" + minutes.toString(); }
	else { prtString += "" + minutes.toString(); }

	if (seconds < 10) { prtString += "0" + seconds.toString(); }
	else { prtString += "" + seconds.toString(); }

	return prtString;
};

/**
 * 날짜를 yyyy-MM-dd hh:mm:ss 형식으로 출력
 */
exports.PrtDateTime = function(d) {
	var prtString = d.getFullYear().toString();
	var month = d.getMonth() + 1;
	var day = d.getDate();
	var hours = d.getHours();
	var minutes = d.getMinutes();
	var seconds = d.getSeconds();

	if (month < 10) { prtString += "-0" + month.toString(); }
	else { prtString += "-" + month.toString(); }
	
	if (day < 10) { prtString += "-0" + day.toString(); }
	else { prtString += "-" + day.toString(); }

	if (hours < 10) { prtString += " 0" + hours.toString(); }
	else { prtString += " " + hours.toString(); }

	if (minutes < 10) { prtString += ":0" + minutes.toString(); }
	else { prtString += ":" + minutes.toString(); }

	if (seconds < 10) { prtString += ":0" + seconds.toString(); }
	else { prtString += ":" + seconds.toString(); }
	
	return prtString;
};

/**
 * 날짜를 hh:mm:ss 형식으로 출력
 */
exports.PrtTime = function(d) {
	var hours = d.getHours();
	var minutes = d.getMinutes();
	var seconds = d.getSeconds();

	var prtString = "";

	if (hours < 10) { prtString += "0" + hours.toString(); }
	else { prtString += "" + hours.toString(); }

	if (minutes < 10) { prtString += ":0" + minutes.toString(); }
	else { prtString += ":" + minutes.toString(); }

	if (seconds < 10) { prtString += ":0" + seconds.toString(); }
	else { prtString += ":" + seconds.toString(); }

	return prtString;
};

/**
 * 날짜를 hhmmss 형식으로 출력
 */
exports.PrtTimeNoDash = function(d) {
	var hours = d.getHours();
	var minutes = d.getMinutes();
	var seconds = d.getSeconds();

	var prtString = "";

	if (hours < 10) { prtString += "0" + hours.toString(); }
	else { prtString += "" + hours.toString(); }

	if (minutes < 10) { prtString += "0" + minutes.toString(); }
	else { prtString += "" + minutes.toString(); }

	if (seconds < 10) { prtString += "0" + seconds.toString(); }
	else { prtString += "" + seconds.toString(); }

	return prtString;
};


/**
 * 해당월의 마지막일을 가져오기
 */
exports.GetMonthLastDay = function(y, m) {
	if (y + "-" + m + "1".isDate() == false) return 0;
	y = parseInt(y);
	m = parseInt(m);

	var monthLastDay = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31); //각 달별 마지막날 저장
    var currentLastDay = monthLastDay[m - 1];

    //윤년계산
    if (m == 2) {
        if ((y % 400) == 0)
        	currentLastDay = 29;
        else if (((y % 4) == 0) && ((y % 100) > 0))
        	currentLastDay = 29;
    }
    
    return currentLastDay;
};

/**
 * 초단위로 시간단위 환산
 * return : {h:시, m:분, s:초, d:일, dh:일기준 시}
 */
exports.secondsToTime = function(secs) {
	var hours = Math.floor(secs / (60 * 60));

	var divisor4minutes = secs % (60 * 60);
	var minutes = Math.floor(divisor4minutes / 60);
	if (minutes < 10) minutes = "0" + minutes;

	var divisor4seconds = divisor4minutes % 60;
	var seconds = Math.floor(divisor4seconds);
	if (seconds < 10) seconds = "0" + seconds;

	var days = Math.floor(hours / 24);
	var daysHour = hours % 24;
	if (daysHour < 10) daysHour = "0" + daysHour;
	
	var obj = {
		h: hours,
		m: minutes,
		s: seconds,
		d: days,
		dh: daysHour
	};
	
	return obj;
};