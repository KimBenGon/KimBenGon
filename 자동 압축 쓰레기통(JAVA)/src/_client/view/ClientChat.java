package _client.view;

import java.io.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class ClientChat { // 클라이언트 채팅을 위한 클래스

	private DataOutputStream out;	//데이터아웃풋스트림
	private String trash;			//클라이언트 trash
	private JFrame chatFrame;		//채팅 gui 프레임
	private JTextField text;		//텍스트필드
	private JTextArea textArea;		//텍스트아리아

	ClientChat(DataOutputStream out, String trash) { //클라이언트 채팅창 생성자

		this.out = out;
		this.trash = trash;
		
		chatFrame = new JFrame("서버에게 보내는 메세지 창");

		// 현재 스크린사이즈를 받아온다
		int width = Toolkit.getDefaultToolkit().getScreenSize().width / 3;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height / 4;

		// 콤포넌트 정의
		textArea = new JTextArea();
		text = new JTextField(25);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		JPanel south = new JPanel();
		JButton transBtn = new JButton("전송");
		JScrollPane center = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// 컴포넌트 결합
		south.add(text);
		south.add(transBtn);
		
		// 이벤트 추가
		transBtn.addActionListener(new TransBtnEvent());
		text.addActionListener(new TransKeyEvent());

		chatFrame.add(south, "South");
		chatFrame.add(center, "Center");
		chatFrame.setBounds(width, height, 400, 300);
		chatFrame.setResizable(false);
		chatFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		chatFrame.setVisible(true);

	} //클라이언트 채팅창 생성자 종료

	/* 클라이언트 채팅창 버튼 이벤트 처리 클래스 */
	private class TransBtnEvent implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			try {
				out.writeUTF("메시지");		//서버에게 채팅을 보낸다고 넌지시
				String msg = trash + "번 쓰레기통 :" + text.getText() + "\n"; //채팅창으로 보낼 메세지
				text.setText("");			//text필드값을 비움
				textArea.append(msg);		//메세지를 버퍼에 저장
				out.writeUTF(msg);			//메세지를 올림
				out.flush();				//버퍼에 저장된 내용을 서버에 보내고 버퍼를 비움

			} catch (IOException e) {

				chatFrame.dispose();
			}
		}
	}
	/* 클라이언트 채팅창 버튼 이벤트 처리 클래스 종료 */
	
	
	/* 클라이언트 채팅 키보드 값 처리 클래스 시작 */
	private class TransKeyEvent implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			try {
				out.writeUTF("메시지");
				String msg = trash + "번 쓰레기통 :" + text.getText() + "\n";
				text.setText("");
				textArea.append(msg);
				out.writeUTF(msg);
				out.flush();

			} catch (IOException e) {
				chatFrame.dispose();
			}
		}
	}
	/* 클라이언트 채팅 키보드 값 처리 클래스 종료 */
	
	
	/* 관리자에게서 받은 메시지를 표시하기위한 함수 */
	void addChat(String s) {
		textArea.append(s + "\n");
	}

	
	/* 채팅창 종료하는 함수  */
	void closeFrame() {
		if (chatFrame != null) {
			chatFrame.dispose();
		}
	}

	
	/* 사라진 채팅창 다시 생성하는 함수 */
	void chatFrameVisible(){
		chatFrame.setVisible(true);
	}

} // 클라이언트 챗 클래스 종료
