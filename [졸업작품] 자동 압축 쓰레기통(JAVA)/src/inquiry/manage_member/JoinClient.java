package inquiry.manage_member;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import _client.dbprocess.IdCheckProcess;
import control.manage_member.dbprocess.JoinClientProcess;
import db.connection.BCrypt;

import java.awt.event.*;

//회원가입창을 생성하는 클래스
public class JoinClient {

	private boolean dupl_id = true; // 중복되는 아이디가 있는지 검사

	private JFrame join;
	private JTextField id;
	private JTextField tel;
	private JTextField age;			
	private JPasswordField pass; 	
	private JTextField loc;
	private String reConfirmId;

	//회원 가입 창 생성
	public JoinClient() {
		
		join = new JFrame("회원가입");

		// 회원 정보를 입력하는 텍스트필드
		id = new JTextField();
		pass = new JPasswordField();
		tel = new JTextField();
		loc = new JTextField();
		age = new JTextField();
		
		// 텍스트 필드에 글자 제한 걸기
		id.setDocument(new JTextFieldLimit(15));
		pass.setDocument(new JTextFieldLimit(15));
		tel.setDocument(new JTextFieldLimit(14));
		loc.setDocument(new JTextFieldLimit(20));
		age.setDocument(new JTextFieldLimit(3));
		
		// 라벨 생성
		JLabel id_label = new JLabel("아이디");
		JLabel pass_label = new JLabel("비밀번호");
		JLabel tel_label = new JLabel("핸드폰 번호");
		JLabel loc_label = new JLabel("관리 지역");
		JLabel age_label = new JLabel("나이");

		// 버튼 생성
		JButton id_btn = new JButton("중복확인");
		JButton join_btn = new JButton("회원가입");
		JButton close_btn = new JButton("닫기");

		// 패널 사이즈 및 배치설정
		id.setBounds(100, 10, 95, 20);
		pass.setBounds(100, 35, 95, 20);
		tel.setBounds(100, 60, 110, 20);
		loc.setBounds(100, 85, 110, 20);
		age.setBounds(100, 110, 30, 20);
		id_label.setBounds(30, 5, 95, 30);
		pass_label.setBounds(30, 30, 95, 30);
		tel_label.setBounds(20, 55, 95, 30);
		loc_label.setBounds(20, 80, 95, 30);
		age_label.setBounds(40, 105, 95, 30);
		id_btn.setBounds(200, 5, 90, 25);
		join_btn.setBounds(30, 140, 90, 25);
		close_btn.setBounds(130, 140, 90, 25);
		JPanel panel = new JPanel();

		// 버튼 액션 부여
		id_btn.addActionListener(new IdConfirm());
		join_btn.addActionListener(new JoinProcess());
		close_btn.addActionListener(new CloseProcess());

		// 디스플레이 정보를 받아옴
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		panel.setLayout(null);
		panel.add(id_btn);
		panel.add(id_label);
		panel.add(pass_label);
		panel.add(tel_label);
		panel.add(loc_label);
		panel.add(loc);
		panel.add(tel);
		panel.add(id);
		panel.add(pass);
		panel.add(join_btn);
		panel.add(close_btn);
		panel.add(age_label);
		panel.add(age);

		join.add(panel);
		join.setBounds(width / 3, height / 4, 300, 200);
		join.setResizable(false);
		join.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		join.setVisible(true);
	}
	
	// 텍스트 필드 글자수 제한을 위해 PlainDocument안에 있는 메소드를 오버라이드하여 이용한다.
	private class JTextFieldLimit extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int limit; // 제한할 길이를 받음
		
		private JTextFieldLimit(int limit) {//제한할 길이를 받아 생성자 호출
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

	// IdConfirm클래스 아이디 중복확인 이벤트 처리를 한다
	private class IdConfirm implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (id.getText().equals("admin"))// 아이디 검사
			{
				JOptionPane.showMessageDialog(null, "아이디로 \'admin\'을 사용할 수 없습니다.");
			} else if (id.getText().equals("")) {

				JOptionPane.showMessageDialog(null, "아이디를 입력해 주세요");
			} else {
				dupl_id = IdCheckProcess.checkId(id.getText());
				if (dupl_id == false) {
					JOptionPane.showMessageDialog(null, "사용 가능 한 아이디 입니다.");
					reConfirmId = id.getText();
				} else {
					JOptionPane.showMessageDialog(null, "사용할 수 없는 아이디 입니다.");
				}

			}

		}

	}
	
	// JoinProcess 클래스 시작
	private class JoinProcess implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			int input_age;
			try {
				input_age = Integer.parseInt(age.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "나이에 숫자를 입력해주세요");
				return;
			}

			if (dupl_id == false) // 아이디 중복검사 통과했고 아이디 중복검사 체크를 실행했으면
			{
				dupl_id = IdCheckProcess.checkId(id.getText());

				if (id.getText().equals("admin")) {
					JOptionPane.showMessageDialog(null,
							"아이디로 \'admin\'을 쓸수 없습니다.");
					dupl_id = true;
				} else if (id.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "아이디를 입력해주세요");
					dupl_id = true;
				} else if (dupl_id == true) {		// 중복 검사 눌러놓고 다른 ID로 가입하는 걸 방지
					JOptionPane.showMessageDialog(null, "중복검사를 해야합니다.");
				} else if (!reConfirmId.equals(id.getText())) {

					JOptionPane.showMessageDialog(null, "중복검사를 해야합니다.");

				} else {
					String encrypt_Pass = BCrypt.hashpw(pass.getText(),
							BCrypt.gensalt(12));
					JoinClientProcess.insertMember(id.getText(), encrypt_Pass,
							tel.getText(), loc.getText(), input_age);
					JOptionPane.showMessageDialog(null, "회원가입에 성공하셨습니다.");
					join.dispose();
				}

			} else {
				JOptionPane.showMessageDialog(null, "중복검사를 다시 해야합니다.");
			}

		}

	}

	// JoinProcess 클래스 종료

	// CloseProcess 클래스 시작
	private class CloseProcess implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			join.dispose();

		}

	}
	// CloseProcess 클래스 종료
}// JoinMember클래스 종료
