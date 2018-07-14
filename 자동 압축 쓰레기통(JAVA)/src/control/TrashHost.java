package control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

/*
 *  
 * 
 */
public class TrashHost extends Thread {
	TrashControl_Start vc = TrashControl_Start.getInstance("호스트서버"); // 싱글톤불러오기;
	ServerReceiver receiver = null;
	public static int[] userConnectCheck = new int[50];
	public boolean connectCheck = false;



	// TEST용
	public void startFromFrame() {
			vc.selectFrame();
	}

	//서버 시작
	public void run() {
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		for(int i = 0; i < 50; i++)
			userConnectCheck[i] = 0;

		/*
		 * SERVER PORT: 7777;
		 * - 클라이언트가 접속할 때 마다 개별 스레드를 생성해준다.
		 * - 아래에선 receiver가 그 역할을 하고 있다.
		 */
		try {
			serverSocket = new ServerSocket(7777);
			System.out.println("쓰레기통 : " + "쓰레기통 호스트가 시작됩니다");

			// 접속을 계속 받아내는 쓰레드
			while (true) {
				socket = serverSocket.accept();
				System.out.println("쓰레기통 : " + "[" + socket.getInetAddress()
						+ "]에서 접속하였다!");

				receiver = new ServerReceiver(socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 클라이언트가 접속 될 때마다 생성되는 스레드다.
	 * 그렇기 때문에 다중접속이 가능함~ 개꿀띠><
	*/
	class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream in;
		DataOutputStream out;

		/*
		 * 생성자에서는 서버의 인풋아웃풋 스트림을 받아서 연결한다.
		 */
		ServerReceiver(Socket socket) {
			this.socket = socket;

			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				System.out.println("호스트 쓰레기통 : in,out stream 생성완료!");
			} catch (IOException e) {
				System.out.println("호스트 쓰레기통 : " + "소켓 생성 에러");
			}
		}

		// 클라이언트 개별 관리 스레드 시작
		public void run() {
			int num = 0;
			String name = "";

			try {
				num = in.readInt();
				name = in.readUTF();
				System.out.println("호스트 쓰레기통 : " + "쓰레기통 번호는 " + num);
				System.out.println("호스트 쓰레기통 : " + "이 쓰레기통에 접속한 ID는 " + name + "입니다.");
				if(userConnectCheck[num] == 1){
					connectCheck = true;
					return;
				}
				System.out.println("ConnectCheck: " + connectCheck);
				userConnectCheck[num] = 1;

				// 클라이언트의 아이콘을 연결상태로 바꾸기
				vc.turnOn(num);
				
				// 클라이언트에서 보낸 값에 맞게 처리
				while (in != null) {
					String s = in.readUTF();
					switch (s) {
					case "로그인":
						vc.newSeat(num, name, socket);
						vc.login(num, name);
						break;
					case "로그아웃":
						vc.logout(num);
						break; // 화면변환 메소드
					case "컴퓨터끔":
						break;
					case "메시지":
						String message = in.readUTF();
						vc.messageFromPC(num, message);
						break; // 메시지처리 메소드
					case "상태":
						String status = in.readUTF();
						System.out.println(num +"번 쓰레기통의 상태 값: " + status);
						vc.statusNumFromClient(num, status);
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("호스트쓰레기통: " + "클라이언트와의 접속중 에러 : 나가거나..");
			} finally {
				if(connectCheck == true){
					System.out.println("너가 여기서 왜 나와...?");
					connectCheck = false;
					return;
				}
				//클라이언트와의 연결이 해제됐을 때
				userConnectCheck[num] = 0;
				System.out.println(userConnectCheck[num]);
				vc.turnOff(num); // 중앙집권 클래스의 turnoff 함수 실행
				//vc.logout(num);

				System.out.println("호스트쓰레기통: " + "클라이언트 가 꺼짐~");
			}
		}
	}
}
