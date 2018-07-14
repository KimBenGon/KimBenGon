package control;

/*
 * GUI에서 하는 대부분의 일들을 처리해주는 중앙집권클래스
 * 순서도
 * 
 * 00. 바깥의 프레임에서 메인프레임을 보이게 한다.
 * 01. 컨트롤타워 싱글톤만들기
 * 02. 새로운 자리받는 메소드
 * 03. 컴퓨터 켜짐 from HostPcServer
 * 04. 컴퓨터 꺼짐 from HostPcServer
 * 05. 로그인 처리 from HostPcServer
 * 06. 로그아웃처리 from HostPcServer
 * 07. 나머지 따옴표 처리 from HostPcServer
 * 08. 계속 좌석 계산해주고 바로 밑에 센드 from Seat
 * 09. 계산메소드 from HostPcServer
 * 10. 클라이언트로 부터 받은 메시지
 * 11. 단체 제어(자동모드 ON, OFF, 연결해제)
 * 유지보수 일지
 * 여기서 모든 Login HashMap을 가지고 있음
 * 번호 파라미터 하나만 받아서 모두 처리하도록.. 
 */
import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import interfaceMonitor.LoginMonitor;
import interfaceMonitor.ManageMonitor;
import struct.model.Client;
import struct.model.Trash;
import view.Manage;
import view.StatusFromClient;
import view.MsgToClient;

public class TrashControl_Start {
	
	Socket socket;
	DataOutputStream out;
	
	// 핵심 필드: 위치 객체모델을 저장함과 동시에 소켓을 저장
	public HashMap<Trash, Socket> clients = new HashMap<Trash, Socket>();
	public Trash[] trashseat = new Trash[50];
	Manage mf;
	
	public static MsgToClient[] chatClient = new MsgToClient[50];				//채팅 연결을 시도한 Client Array
	public static StatusFromClient[] statusClient = new StatusFromClient[50];	//
	static LoginMonitor Server_Login_Main;

	public static void main(String[] args) {
		Server_Login_Main = new LoginMonitor();
	}

	// 00.바깥의 프레임에서 메인프레임을 보이게 한다.
	public void mainFrameHUD() {
		mf = new ManageMonitor();			//서버의 관제화면을 생성하고
		Server_Login_Main.dispose();				//로그인 GUI를 없앤다.
		Thread host = new TrashHost();		//ACCEPT() 함수로 클라이언트를 계속 받아내는 Class를 스레드로 실행한다.
		host.start();
	}

	// 코드 테스트를 위해 생성
	public void selectFrame() {
			mf = new ManageMonitor();
	}

	// 01. 총괄 싱글톤 생성
	private static TrashControl_Start instance = new TrashControl_Start();

	public static TrashControl_Start getInstance() {
		System.out.println("쓰레기통 관제 프로그램 실행");
		return instance;
	}

	public static TrashControl_Start getInstance(String s) {
		System.out.println(s + "에서 쓰레기통호출");
		return instance;
	}

	private TrashControl_Start() {
		System.out.println("관제 프로그램 시작 ");
		// 동기화시켜서 쓰레드간 비동기화발생하지않도록
		Collections.synchronizedMap(clients);
	}

	// 02.새로운 자리받는 메소드
	public void newSeat(int num, String name, Socket socket) {
		trashseat[num] = new Trash(num, name);
		clients.put(trashseat[num], socket);
	}

	// 03.클라이언트의 연결 요청 시 실행되는 메소드
	public void turnOn(int num) {
		System.out.println("쓰레기통  관제 시작!");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].label[1].setText("자리 켜짐");
		mf.pan[num].turnOn();
		ddaom(num);
	}

	// 04 클라이언트 연결해제
	public void turnOff(int num) {
		System.out.println("쓰레기통 Auto모드 해제!");
		mf.pan[num].setBackground(Color.gray);
		mf.pan[num].label[0].setText("");
		ddaom(num);
		mf.pan[num].turnOff();
	}

	// 05.로그인 처리 from HostPcServer
	public void login(int num, String name) {

		mf.pan[num].setBackground(Color.blue);
		mf.pan[num].label[0].setForeground(Color.red);
		mf.pan[num].label[0].setText((num) + ". 로그인");
		mf.pan[num].label[1].setText(name);
		mf.pan[num].label[2].setText("");
		mf.pan[num].isLogined = true;
		mf.pan[num].nickname = name;
		trashseat[num].setUsername(name);
		trashseat[num].setLogin(true);
		if (!trashseat[num].isFirst()) {
			trashseat[num].setFirst(true);
			trashseat[num].start();
		}

	}

	// 06.로그아웃 메소드
	public void logout(int num) {
		TrashHost.userConnectCheck[num] = 0;
		System.out.println("Trash Control : " + num + "번째 쓰레기통 연결해제");
		trashseat[num].interrupt();
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].label[0].setForeground(new Color(36, 205, 198));
		mf.pan[num].label[0].setText("");
		ddaom(num);
		mf.pan[num].isLogined = false;
		
		// 클라이언트 스레드 인터럽트
		trashseat[num].interrupt();
		trashseat[num].setLogin(false);

		try {
			socket = clients.get(trashseat[num]);
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("로그아웃");
			clients.remove(trashseat[num]);
		} catch (IOException e) {
			System.out.println("클라이언트에 로그아웃 메시지 보내는 데 실패함");
		}
	}

	// 07. 클라이언트 라벨에 들어가는 문자열 클리어
	public void ddaom(int num) {
		mf.pan[num].label[1].setText("");
		mf.pan[num].label[2].setText("");
		mf.pan[num].label[3].setText("");
	}

	// 08. 클라이언트와 서버의 연결유지시간 표시부분
	public void continueTime(int num, Calendar date) {
		

		Calendar dateAfter = Calendar.getInstance();
		dateAfter.setTimeInMillis(System.currentTimeMillis());

		long differ = (dateAfter.getTimeInMillis() - date.getTimeInMillis()) / (1000);
		long differ_hour = differ / 60;

		String gametime = differ_hour + ":" + (differ % 60) + "초";
		mf.pan[num].label[2].setText(gametime);
		
		sendTime(trashseat[num], gametime);
	}

	// 클라이언트의 연결유지시간을 클라이언트로 보내준다.
	public void sendTime(Trash trashseat, String gametime) {
		try {
			out = new DataOutputStream(clients.get(trashseat).getOutputStream());
			System.out.println("클라이언트로 연결유지시간 전송");
			out.writeUTF("연결시간");
			out.writeUTF(gametime);
		} catch (IOException e) {
			System.out.println("쓰레기통의 사용시간을 보내는데 에러가 넘친다...");
		}
	}


	// 10. 클라이언트와의 채팅 기능 구현
	public void messageFromPC(int num, String message) {
		if (chatClient[num] == null)
			chatClient[num] = new MsgToClient(num);
		chatClient[num].setVisible(true);
		chatClient[num].ta.append(message);
	}

	// 11. 자동모드 OFF
	public void groupOff(int x, int num) {
		ArrayList<Client> peoples = new ArrayList<Client>();

		if (x == 1) {
			peoples.add(new Client(num, trashseat[num].getUserame(),
					mf.pan[num].label[2].getText()));
		} else {
			for (int a = 0; a < 50; a++) {
				if (mf.pan[a].isChecked && mf.pan[a].isLogined) {
					peoples.add(new Client(a, trashseat[a].getUserame(),
							mf.pan[a].label[2].getText()));
					mf.pan[a].checkOff();
				}
			}

		}
		
		for (int i = 0; i < peoples.size(); i++) {
			logout(peoples.get(i).getNum());
		}
	}
	
	// 10. 클라이언트로 부터 받은 상태값
	/*
	 * 문 열린 모드
	 * 1. 자동모드 ON 경고
	 * 2. 자동모드 ON 유의
	 * 3. 자동모드 ON 양호
	 * 4. 자동모드 OFF 경고
	 * 5. 자동모드 OFF 유의
	 * 6. 자동모드 OFF 양호
	 * 
	 * 문 닫힌 모드
	 * 7. 자동모드 ON 경고
	 * 8. 자동모드 ON 유의
	 * 9. 자동모드 ON 양호
	 * 10. 자동모드 OFF 경고
	 * 11. 자동모드 OFF 유의
	 * 12. 자동모드 OFF 양호
	 * 
	 */
	public void statusNumFromClient(int num, String status) {
		
		if(status.equals("1")){
			redTrash(num);
		}
		else if(status.equals("2")){
			yellowTrash(num);
		}
		else if(status.equals("3")){
			greenTrash(num);
		} 
		else if(status.equals("4")){
			openRedOFF(num);
		}
		else if(status.equals("5")){
			openYellowOFF(num);
		}
		else if(status.equals("6")){
			openGreenOFF(num);
		}
		else if(status.equals("7")){
			closeRedON(num);
		}
		else if(status.equals("8")){
			closeYellowON(num);
		}
		else if(status.equals("9")){
			closeGreenON(num);
		}
		else if(status.equals("10")){
			closeRedOFF(num);
		}
		else if(status.equals("11")){
			closeYellowOFF(num);
		}
		else if(status.equals("12")){
			closeGreenOFF(num);
		}
		else if(status.equals("15")){
			full(num);
		}
		
	}
	
	// 클라이언트 제어 기능 실행 시, 아래 메소드가 작동한다.
	public void controlToClient(int num, int status){
		try {
			if(status == 1){
				out = new DataOutputStream(clients.get(trashseat[num]).getOutputStream());
				out.writeUTF("자동모드ON");
				System.out.println("자동모드를 켜라!");
			} 
			else if (status == 2) {
				out = new DataOutputStream(clients.get(trashseat[num]).getOutputStream());
				out.writeUTF("자동모드OFF");
				System.out.println("자동모드를 꺼라!");
			}
			else if (status == 3) {
				out = new DataOutputStream(clients.get(trashseat[num]).getOutputStream());
				out.writeUTF("강제압축");
				System.out.println("강제압축을 해라!");
			}
			else if (status == 4) {
				out = new DataOutputStream(clients.get(trashseat[num]).getOutputStream());
				System.out.println("투입구 여닫아라!");
				out.writeUTF("여닫기");
				System.out.println("투입구 여닫아라!");
			}
				
		} catch (IOException e) {
			System.out.println("쓰레기통에 제어값을 보내는데 에러가 넘친다...");
		}
	}
	
	public void greenTrash(int num) {
		System.out.println(num + "번 쓰레기통의 상태: 양호, 문열림, ON");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].greenTrash();
	}
	
	public void yellowTrash(int num) {
		System.out.println(num + "번 쓰레기통의 상태: 유의, 문열림, ON");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].yellowTrash();
	}
	
	public void redTrash(int num) {
		System.out.println(num + "번 쓰레기통의 상태: 경고, 문열림, ON");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].redTrash();
	}
	
	public void openRedOFF(int num){
		System.out.println(num + "번 쓰레기통의 상태: 경고, 문열림, OFF");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].openRedOFF();
	}
	public void openYellowOFF(int num){
		System.out.println(num + "번 쓰레기통의 상태: 유의, 문열림, OFF");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].openYellowOFF();
	}
	public void openGreenOFF(int num){
		System.out.println(num + "번 쓰레기통의 상태: 양호, 문열림, OFF");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].openGreenOFF();
	}
	public void closeRedON(int num){
		System.out.println(num + "번 쓰레기통의 상태: 경고, 문닫힘, ON");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].closeRedON();
	}
	public void closeYellowON(int num){
		System.out.println(num + "번 쓰레기통의 상태: 유의, 문닫힘, ON");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].closeYellowON();
	}
	public void closeGreenON(int num){
		System.out.println(num + "번 쓰레기통의 상태: 양호, 문닫힘, ON");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].closeGreenON();
	}
	public void closeRedOFF(int num){
		System.out.println(num + "번 쓰레기통의 상태: 경고, 문닫힘, OFF");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].closeRedOFF();
	}
	public void closeYellowOFF(int num){
		System.out.println(num + "번 쓰레기통의 상태: 유의, 문닫힘, OFF");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].closeYellowOFF();
	}
	public void closeGreenOFF(int num){
		System.out.println(num + "번 쓰레기통의 상태: 양호, 문닫힘, OFF");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].closeGreenOFF();
	}
	public void full(int num){
		System.out.println(num + "번 쓰레기통의 상태: 압축 해도 꽉참, 비워야함");
		mf.pan[num].setBackground(Color.white);
		mf.pan[num].full();
	}
	
	
	//코드 테스트를 위해 생성2
	public void test(int num) {
		System.out.println("쓰레기통 : " + num);
	}
}
