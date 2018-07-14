package view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;
import control.TrashControl_Start;

public class StatusFromClient {
	TrashControl_Start vc = TrashControl_Start.getInstance("상태값을 쓰레기통으로");

	public JTextArea ta = new JTextArea(25, 25);
	//JTextField tf = new JTextField(15);
	public boolean flag;

	int num;
	Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	/* 제어값을 클라이언트로 보내는 함수 시작 */
	public StatusFromClient(int num) {
		this.num = num;
		
		try {
			System.out.println(num + "쓰레기통.클라이언트에서 소켓얻어오기!");
			/** 중요 여기서 삽질좀함.. vc에서 받아올때는 vc.pcseat */
			socket = vc.clients.get(vc.trashseat[num]);
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("메세지를 쓰레기통으로 : 아웃 스트림 받아오기 실패");
		}

		Thread receiver = new StatusReceiver();
		receiver.start();
	}
	/* 스레드 생성  */
	
	/* 리시버 클래스 시작 */
	class StatusReceiver extends Thread {
		public void run() {
			while (in != null) {
				try {
					String s = in.readUTF();
					System.out.println(s);
					//ta.append(s + "\n");
					System.out.println("1231231");
				} catch (IOException e) {
					System.out.println("채팅 리시버 메소드 실행중 입출력 에러");
				}
			}
		}
	}
	/* 리시버 클래스 종료 */

	public static void main(String[] args) {

	}


}
