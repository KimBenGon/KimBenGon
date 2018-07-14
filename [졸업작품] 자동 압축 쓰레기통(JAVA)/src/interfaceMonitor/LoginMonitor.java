package interfaceMonitor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import control.DB_Query;
import control.TrashControl_Start;

@SuppressWarnings("serial")
public class LoginMonitor extends JFrame implements ActionListener {
	TrashControl_Start vc= TrashControl_Start.getInstance();
	// 이미지와 버튼은 전역변수설정
	BufferedImage img = null;
	JButton bt;
	JTextField tf;
	JPasswordField tf2;
	

	public static void main(String[] args) {
		new LoginMonitor();
	}

	// 생성자
	public LoginMonitor() {
		// 프레임 설정
		setTitle("쓰레기통 관리 프로그램");
		setSize(1600, 900);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // X버튼 누르면 프로그램 종료하기
		setBackground(Color.black);
		setLayout(null);	//레이아웃 설정을 하지 않음
		// 중앙사이즈조절
		int width = (Toolkit.getDefaultToolkit().getScreenSize().width - 1600) / 2;
		int height = (Toolkit.getDefaultToolkit().getScreenSize().height - 900) / 2;
		setLocation(width, height);
		// 이미지 받아오기
		try {
			img = ImageIO.read(new File("img/login.png"));

		} catch (IOException e) {
			System.out.println("Login 이미지 불러오기 실패!");
			System.exit(0);
		}
		// 패널 생성 후 올리기, 가장 큰패널 부터 생성
		JLayeredPane lpane = new JLayeredPane();	//메인 패널
		lpane.setBounds(0, 0, 1600, 900); // x,y, 높이, 넓이
		lpane.setLayout(null);

		// 로그인 패널, ID 텍스트 필드, PW 텍스트 필드가 들어감
		MyPanel panel = new MyPanel();			// 로그인배경화면 올리기
		panel.setBounds(0, 0, 1600, 900);
		panel.setLayout(null);

		// 로그인 버튼 패널
		JPanel panel2 = new JPanel();			// 로그인 버튼 올리기
		panel2.setLayout(null);
		panel2.setBounds(0, 0, 1600, 900);
		//panel2.setBounds(755, 689, 104, 48);
		panel2.setBackground(Color.black);
		panel2.setOpaque(false);

		// 첫 번째 ID 텍스트필드 731, 399
		tf = new JTextField(15);
		tf.setBounds(731, 399, 280, 30); //ID 입력 텍스트창
		tf.setOpaque(false);
		tf.setForeground(Color.blue);	//아이디 폰트 글자색 변경
		tf.setBorder(javax.swing.BorderFactory.createEmptyBorder()); //테두리 없애기
		panel2.add(tf);				//ID 입력 텍스트창 추가
		
		// 두 번째 PW 텍스트필드 731, 529
		tf2 = new JPasswordField(15);
		tf2.setBounds(731, 529, 280, 30); // 비밀번호 입력 텍스트창
		tf2.setOpaque(false);
		// tf2.setBackground(Color.black);
		tf2.setForeground(Color.blue);	//비밀번호 폰트 글자색 변경
		tf2.setBorder(javax.swing.BorderFactory.createEmptyBorder()); // 테두리 없애기
		
		//ID, PW, 텍스트 필드 입력 제한 걸어주기
		tf.setDocument(new JTextFieldLimit(15));
		tf2.setDocument(new JTextFieldLimit(15));
		panel2.add(tf2);

		//로그인 버튼 587 458
		bt = new JButton(new ImageIcon("img/btLogin_hud.png"));
		bt.setBorderPainted(false);
		bt.setFocusPainted(false);
		bt.setContentAreaFilled(false);
		
		//버튼에 이미지를 씌운다.(이미지만 보여주는 것과 같음 ㅇㅇ) 
		bt.setBounds(755, 689, 104, 48);
		bt.addActionListener(this);
		panel2.add(bt);

		//ID field, PW field, LOGIN button 패널 붙여넣기
		lpane.add(panel, new Integer(0), 0);
		lpane.add(panel2, new Integer(1), 0);
		getContentPane().add(lpane);
		setVisible(true);
	}

	// 텍스트 필드 글자수 제한을 위해 PlainDocument안에 있는 메소드를 오버라이드하여 이용한다.
	private class JTextFieldLimit extends PlainDocument {
		
		private int limit; // 제한할 길이를 받음
		
		private JTextFieldLimit(int limit) {  //제한할 길이를 받아 생성자 호출
			super();
			this.limit = limit;
		}

		// 텍스트 필드를 채우는 메소드
		@Override
		public void insertString(int offset, String str, AttributeSet attr) 
				throws BadLocationException {
			
			if (str == null)
				return;

			if (getLength() + str.length() <= limit)
				super.insertString(offset, str, attr);
		}
	}

	// 로그인 배경화면 올리는 클래스
	class MyPanel extends JPanel {
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}

	// 로그인 버튼 액션처리, DB와 비교하여 로그인 액션을 처리한다.
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) { 
		
		//ID, PW를 입력했는지 검사
		if (tf.getText().equals("") || tf2.getText().equals("")){
			if (tf.getText().equals("")) {			//ID가 비었다면
				JOptionPane.showMessageDialog(null, "아이디를 입력하세요", "아이디 입력",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (tf2.getText().equals("")) {	//PW가 비었다면
				JOptionPane.showMessageDialog(null, "비밀번호를 입력하세요", "비번 입력",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} else {
			//ID, PW를 DB 쿼리로 넘긴다. true: 로그인 성공, false: 로그인 실패(ID, PW가 틀릴경우)
			boolean existId = DB_Query.loginMember(tf.getText(), tf2.getText());
			
			//existId = true;
			if (existId == true) // 로그인 가능 판별, true면 성공
			{
				//로그인 진입 액션
				JOptionPane.showMessageDialog(null, "로그인에 성공하였습니다.", "로그인 성공",
						JOptionPane.INFORMATION_MESSAGE);
				vc.mainFrameHUD();
				
			} else {
				JOptionPane.showMessageDialog(null, "로그인에 실패하였습니다.", "로그인 실패",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}// 액션 끝
}
