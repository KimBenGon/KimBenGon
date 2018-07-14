package struct.model;

import java.util.Calendar;
import control.TrashControl_Start;


/**
 * 좌석 모델~ 좌석에서 로그인하면 이걸로 받는다.
 */
public class Trash extends Thread {
	
	TrashControl_Start vc = TrashControl_Start.getInstance("모델:쓰레기통"); //싱글톤불러오기
	private int num_seat;				// 연결번호
	private String username; 			// 이름
	private String time; 				// 연결시간
	private Calendar date;				// 시작시간 - 연결시간 계산 할때 씀
	private boolean isFirst=false;		// 채팅 내용 유지를 위해 사용하는 변수, 첫 로그인 시 true로 변환되고 서버가 종료 될 때까지 유지된다.
	private int status = 0;				//0: 연결상태, 1: 연결상태 자동모드ON, 2 연결상태 자동모드OFF
		
	private boolean isLogin = false; 	// 접속여부
	private boolean isTurn = false;		// 클라이언트의 모드가 바뀔 때
	private boolean isMember = false;	// 등록 ID 접속 확인

	// APM에 등록된 ID로 로그인
	public Trash(int i, String nick) {
		num_seat = i;
		username = nick;
		time = "00:00";
		isMember = true;
	}

	// Thread
	public void run() {
		try {

			while (true) {
				
				/*
				 * 10초마다 연결시간을 갱신 시켜준다.
				 * 연결시간 갱신을 빠르게 하고 싶다면 숫자를 줄여주면 된다.
				 */
				Thread.sleep(10000);
				vc.continueTime(num_seat, date);
			}

		} catch (InterruptedException e) {
			System.out.println("쓰레기통 연결해제");
			return;
		}
	}

	// GET, SET 메소드
	public void setNum_seat(int num_seat) {
		this.num_seat = num_seat;
	}

	public int getNum_seat() {
		return num_seat;
	}

	public String getUserame() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
		if(isLogin){
			//로그인 직후 연결시간을 00:00으로 초기화 시키고 시작
			date = Calendar.getInstance();
			date.setTimeInMillis(System.currentTimeMillis());
		}
		
	}

	public boolean isMember() {
		return isMember;
	}

	public void setMember(boolean isMember) {
		this.isMember = isMember;
	}

	public boolean isTurn() {
		return isTurn;
	}

	public void setTurn(boolean isTurn) {
		this.isTurn = isTurn;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	
}
