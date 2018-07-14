package _client.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

//import com.pi4j.io.gpio.Pin;
//import com.pi4j.io.gpio.RaspiPin;

import _client.dbprocess.LoginClientProcess;
import _client.view.ClientTrash.ClientConnector;

public class ClientLogin {

	private JTextField id;
	private JTextField trash;
	private JPasswordField pass;
	private JFrame login;

	Runnable run, run5;
	Thread piRun, piRun5;

	ClientLogin() {											//생성자 시작
		// 프레임 생성
		login = new JFrame("로그인");

		// 라벨 생성
		JLabel id_label = new JLabel("아이디");
		JLabel pass_label = new JLabel("비밀번호");
		JLabel number_label = new JLabel("연결번호");
		JPanel panel = new JPanel();

		// 버튼 생성
		JButton log_btn = new JButton("연결");
		JButton exit_btn = new JButton("종료");

		// id,pass,연결번호 필드 생성
		id = new JTextField();
		pass = new JPasswordField();
		trash = new JTextField();

		// 아이디 필드와 패스워드 필드, 연결번호 필드 입력글자수 제한
		id.setDocument(new JTextFieldLimit(10));
		pass.setDocument(new JTextFieldLimit(10));
		trash.setDocument(new JTextFieldLimit(2));

		// 현재 스크린사이즈를 받아온다
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		// GUI 배치
		id_label.setBounds(30, 5, 95, 30);
		pass_label.setBounds(30, 30, 95, 30);
		number_label.setBounds(30, 55, 95, 30);
		id.setBounds(100, 10, 95, 20);
		pass.setBounds(100, 35, 95, 20);
		trash.setBounds(100, 60, 65, 20);
		log_btn.setBounds(55, 90, 80, 30);
		exit_btn.setBounds(135, 90, 60, 30);

		// GUI 결합
		panel.add(id);
		panel.add(pass);
		panel.add(id_label);
		panel.add(pass_label);
		panel.add(log_btn);
		panel.add(exit_btn);
		panel.add(number_label);
		panel.add(trash);
		panel.setLayout(null);
		login.add(panel);
		
		// 버튼에 이벤트 리스너 결합부
		exit_btn.addActionListener(new ExitProcess());
		log_btn.addActionListener(new LoginProcess());
		
		run = new PiControl();
		run5 = new ArduinoControl();
		//run5 = new MotorControl();
		piRun = new Thread(run);
		piRun5 = new Thread(run5);
		//piRun5 = new Thread(run5); 
		piRun.start();
		piRun5.start();

		login.setBounds(width - 300, height / 5 - 100, 270, 150);
		login.setResizable(false);
		login.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		login.setVisible(true);
	} 													

	
	//입력 글자수를 제한하자~
	private class JTextFieldLimit extends PlainDocument {
		private int limit; // 제한할 길이

		private JTextFieldLimit(int limit) { 				
			super();
			this.limit = limit;
		}
		
		@Override
		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException {
			if (str == null)
				return;
			if (getLength() + str.length() <= limit)
				super.insertString(offset, str, attr);
		}
	}

	//로그인 창 종료 클래스
	private class ExitProcess implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}

	//로그인 처리 클래스
	private class LoginProcess implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				if(Integer.parseInt(trash.getText()) > 49 || Integer.parseInt(trash.getText()) < 0){
					JOptionPane.showMessageDialog(null, "0-49 사이의 번호를 입력하세요.");
					return;
				}
	
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "연결번호를 입력하세요.");
				return;
			}
			
			boolean logIn = LoginClientProcess.loginMember(id.getText(), pass.getText());
			
			if (logIn == false) {
				JOptionPane.showMessageDialog(null,
						"아이디가 존재하지 않거나 잘못 입력하셨습니다.");
			} else {
				piRun.interrupt();
				piRun5.interrupt();
				JOptionPane.showMessageDialog(null, "연결 되었습니다.");
				login.dispose();
				ClientTrash.doClient=true;
				ClientTrash cl = new ClientTrash(id.getText(), trash.getText());
			}
		}
	}
}
