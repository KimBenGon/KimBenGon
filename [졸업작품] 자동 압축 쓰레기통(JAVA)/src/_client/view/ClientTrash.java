package _client.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

//밑으론 라즈베리파이 라이브러리 선언
import _client.view.PiControl;

/* 로그인 후 연결시간과 아이디, 연결된 번호를 표시해주는 클래스 */
public class ClientTrash {
	
	private String id; 									// 현재 사용중인 아이디 저장
	private String trash; 								// 현재 사용중인 연결 번호 저장
	private JFrame clFrame;
	private JLabel clientId;
	private JLabel clientTrash;
	private JLabel connectTime;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private ClientChat chat;
	protected static boolean doClient = true;

	private boolean flag;								//소켓 연결 유무를 판단, 스레드 인터럽트를 위한 flag 변수
	public static int controlTrash = 1; 							//1: 자동모드ON, 2: OFF, 4: 문닫기
	public static int stayStatus = 0;					//클라이언트로 많은 정보를 보내는 것을 방지함, trashStatus와 값이 달라야 전송
	public static boolean compulsionDoor = false;		//문이 열렸는지 닫혔는지 표시 false: 열림, true: 닫힘
	
	/* 
	 * Runnable
	 * run2: 라즈베리파이 제어 
	 * run3: 쓰레기통 상태값
	 * 
	 * Thread
	 * piRun2: 라즈베리파이를 제어하는 스레드
	 * piRun3: 쓰레기통 상태값을 보내는 스레드
	 */
	Runnable run2, run3, run4, weightRun;								
	Thread piRun2, piRun3, piRun4, arduinoRun;
	
	ClientTrash(){
		
	}
	
	ClientTrash(String id, String trash) {
		/* 라즈베리파이 도스창에서 강제종료(Ctrl + C) 했을 시 처리해주는 스레드 */
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(new Thread(new Shutdown()));
		/* 위 처리를 해주지 않으면 각종 오류 발생 */
		
		this.id = id;
		this.trash = trash;
		clFrame = new JFrame("이용중");

		// 모니터크기 받아오기
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		// 화면에 표시될 라벨
		clientId = new JLabel(id);
		clientTrash = new JLabel(trash);
		connectTime = new JLabel("");

		JLabel pc_label = new JLabel("쓰레기통 번호:");
		JLabel id_label = new JLabel("연결 아이디:");
		JLabel time_label = new JLabel("연결 시간:");


		// 채팅 버튼
		JButton chatBtn = new JButton("채팅");

		// 컴포넌트가 붙을 패널 생성
		JPanel panel = new JPanel();

		// 컴포넌트 배치
		pc_label.setBounds(30, 30, 95, 30);
		id_label.setBounds(30, 5, 95, 30);
		time_label.setBounds(30, 55, 95, 30);
		clientId.setBounds(130, 5, 95, 30);
		clientTrash.setBounds(130, 30, 95, 30);
		connectTime.setBounds(130, 55, 95, 30);
		chatBtn.setBounds(85, 120, 95, 30);
		

		// 컴포넌트 결합
		panel.add(pc_label);
		panel.add(id_label);
		panel.add(time_label);
		panel.add(clientId);
		panel.add(connectTime);
		panel.add(clientTrash);
		panel.add(chatBtn);
		panel.setLayout(null);
		clFrame.add(panel);

		//버튼 이벤트 처리
		chatBtn.addActionListener(new ChatEvent());
		new ChatEvent();
		
		// 현재 프레임 위치 및 크기
		clFrame.setBounds(width - 300, height / 5 - 100, 270, 200);
		clFrame.setResizable(false);

		// 강제로 창을 종료시키는 것을 막아두기
		clFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		clFrame.setVisible(true);
		
		//라즈베리파이 컨트롤 스레드 시작
		run2 = new PiControl();
		//weightRun = new ArduinoControl();

		piRun2 = new Thread(run2);
		//arduinoRun = new Thread(weightRun);

		piRun2.start();
		//arduinoRun.start();

		
		
		// 소켓 쓰레드시작
		flag = false;
		new Thread(new ClientConnector()).start();
	}
	

	// 챗이벤트클래스 시작(채팅창을 불러온다)
	private class ChatEvent implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {

			if (chat == null) {
				chat = new ClientChat(out, trash);
				return;
			}
			chat.chatFrameVisible();

		}

	}
	// 챗이벤트 클래스 종료
	
	// 서버와 클라이언트를 연결하는 클래스
	public class ClientConnector implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println("소켓스레드 진입");
				//String serverIp = "211.49.147.247";
				String serverIp = "192.168.43.38";
				int port = 7777;
				int timeout = 100;
				
				SocketAddress socketAddress = new InetSocketAddress(serverIp, port);
				socket = new Socket();
				socket.connect(socketAddress, timeout);
				System.out.println("연결성공");
				
				flag = true;
				in = new DataInputStream(new BufferedInputStream(
						socket.getInputStream()));
				out = new DataOutputStream(new BufferedOutputStream(
						socket.getOutputStream()));

				int clientNum = Integer.parseInt(trash);
				out.writeInt(clientNum);
				out.writeUTF(id);
				out.writeUTF("로그인");
				out.flush();
				
				run3 = new ClientStatus(out);
				weightRun = new ArduinoControl();
				arduinoRun = new Thread(weightRun);
				piRun3 = new Thread(run3);
				piRun3.start();
				arduinoRun.start();
				
				PiControl.trashStatus = 0;
				
				while (true) {
					String str = in.readUTF();
					System.out.println("");
					
					//연결시간 처리
					if (str.equals("연결시간")) {
						connectTime.setText(in.readUTF());
					}
					
					//서버에서 명령한 제어를 처리
					if (str.equals("자동모드ON")) {
						PiControl.autoOff = false;
						System.out.println("서버에서 자동모드 키란다!");
					}
					
					if (str.equals("자동모드OFF")) {
						PiControl.autoOff = true;
						System.out.println("서버에서 자동모드 끄란다!");
					}
					
					if (str.equals("여닫기")) {
						//controlTrash = 4;
						compulsionDoor = true;
						System.out.println("서버에서 문 여닫으란다!");
					}
					
					// 채팅메시지 처리
					if (str.equals("메시지")) {
						String msg = in.readUTF();
						System.out.println(msg);
						if (chat == null) {
							chat = new ClientChat(out, trash);
						}
						chat.chatFrameVisible();
						chat.addChat(msg);
					}
					// 로그아웃 처리
					if (str.equals("로그아웃")) {
						socket.close();
					}
				}

			} catch (IOException e) {			//소켓 연결이 끊어지면 로그인창으로 바꾸기
				if (chat != null) {
					chat.closeFrame();
				}
				doClient=false;
				clFrame.dispose();

			} finally {
				if(flag == true){
					piRun3.interrupt();
					flag = false;
				}
				//piRun4.interrupt();
				piRun2.interrupt();
				arduinoRun.interrupt();
				ClientLogin cl = new ClientLogin();
				
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/* 강제종료(Ctrl + C) 처리 클래스*/
	public class Shutdown implements Runnable{
	    @Override
		public void run(){
	    	try {
				out.writeUTF("로그아웃");
				out.flush();
				socket.close();
				//piRun4.interrupt();
		    	piRun3.interrupt();
				piRun2.interrupt();
				arduinoRun.interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }    
	}
	
}
