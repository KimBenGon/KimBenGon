package interfaceMonitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import view.Trash_panAb;
import control.TrashControl_Start;
import control.TrashHost;

/*
 * 클라이언트의 이미지를 출력함
 * 클라이언트 이미지를 우클릭하면 제어 기능이 5가지가 나옴
 */
public class TrashImage extends Trash_panAb implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	TrashControl_Start vcm = TrashControl_Start.getInstance("시트팬");
	BufferedImage img = null;
	JLayeredPane lpane;
	JPanel panel3;

	/*
	 * 클라이언트 아이콘 색
	 * 1. 빨강: 쓰레기 적재량 및 무게 경고
	 * 2. 노랑: 쓰레기 적재량 유의
	 * 3. 초록: 쓰레기 적재량 및 무게 양호
	 * 4. 검정: 클라이언트 연결 요청 대기 중
	 * 5. 하양: 클라이언트 모드를 바꿔줄 때 생기는 딜레이 시간 동안 "변경중"이라는 아이콘을 시각적으로 표시 
	 * 
	 * 우클릭
	 * 1. 자동모드ON: ON모드 시, 센서값에 따른 상태값이 서버로 전송되고 클라이언트는 자체적으로 압축 및 개폐 동작을 함
	 * 2. 자동모드OFF: OFF모드 시, 센서값에 따른 상태값은 정상적으로 서버로 보내지지만 클라이언트 자체적으로 압축 및 개폐 동작은 하지 않음
	 * 3. 메세지 연결: 클라이언트와의 채팅, 클라이언트로 점검나간 A/S기사와 원할한 통신을 위해 구현
	 * 4. 투입구 여닫기: 쓰레기통의 투입구를 강제로 열거나 닫도록 하는 버튼
	 * 5. 연결해제: 클라이언트는 강제 종료를 하지 않는 한, 혼자서 종료를 할 수 없으며 서버에서 연결해제를 해주어야 종료되도록 구현
	 */
	public TrashImage(int i) {
		num = i;
		isChecked = false;
		img("black");
		setLayout(null);

		// 가장 큰 패널
		lpane = new JLayeredPane();
		lpane.setBounds(0, 0, 1600, 900);
		lpane.setLayout(null);
		lpane.setOpaque(false);
		
		// 클라이언트 이미지 패널
		JPanel panel = new InnerPanel();
		panel.setBounds(0, 0, 99, 99);
		panel.setOpaque(false);
		
		// 클라이언트 ID, 연결시간 표시 패널, 아이콘 상단부에 배치 
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		panel2.setBounds(0, 0, 99, 99);
		
		
		//클라이언트 이미지 파일 상단에 연결번호와 로그인 ID, 그 밑에 연결시간을 표시해줌 
		int y = 4;
		for (int a = 0; a < 4; a++) {
			
			if (a == 0) label[a] = new JLabel("");
			else label[a] = new JLabel("");

			label[a].setBounds(16, y, 80, 15);
			y += 16;
			label[a].setForeground(new Color(255, 255, 255));
			label[a].setFont(new Font("배달의민족 한나", 1, 12));
			panel2.add(label[a]);
		}
		panel2.setOpaque(false);

		// 체크 표시 패널, 클라이언트를 좌클릭하면 선택이 됨
		panel3 = new CheckPanel();
		panel3.setLayout(null);
		panel3.setBounds(0, 0, 99, 99);
		panel3.setOpaque(false);
		
		// 패널 붙이기
		lpane.add(panel, new Integer(0), 0);
		lpane.add(panel2, new Integer(1), 0);
		add(lpane);
		setVisible(true);
		setOpaque(false);
		setFocusable(true);
		addMouseListener(this);
		
		
		/*
		 * 아이콘 우클릭 시 액션 구현
		 * 1. 자동모드ON
		 * 2. 자동모드OFF
		 * 3. 메세지 보내기
		 * 4. 투입구 여닫기
		 * 5. 연결해제
		 * 
		 * 위 총 5가지의 기능을 가지고 있음.
		 */
		pMenu = new JPopupMenu();
		autoOn = new JMenuItem("자동동작ON");
		autoOn.addActionListener(this);
		autoOff = new JMenuItem("자동동작OFF");
		autoOff.addActionListener(this);
		trashChat = new JMenuItem("메세지 보내기");
		trashChat.addActionListener(this);
		doorClose = new JMenuItem("투입구 여닫기");
		doorClose.addActionListener(this);
		connectClose = new JMenuItem("연결해제");
		connectClose.addActionListener(this);
		
		pMenu.add(autoOn);
		pMenu.add(autoOff);
		pMenu.add(trashChat);
		pMenu.add(doorClose);
		pMenu.add(connectClose);
		
		// 패널에 마우스 리스너를 붙인다. JPopupMenu는 이런식으로 구현을 해야 한다..
		addMouseListener(new MousePopupListener());
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle("시트 패널");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(99, 144);

		JPanel panel = new TrashImage(1);
		f.add(panel);

		f.setVisible(true);
	}

	// 이미지를 받아오는 메소드
	public void img(String s) {
		try {
			img = ImageIO.read(new File("img/" + s + ".png"));
		} catch (IOException e) {
			System.out.println("이미지 불러오기 실패!");
			System.exit(0);
		}
		repaint();
	}

	/*
	 * 클라이언트의 상태 표시를 해주는 메소드들이다.
	 * 총 16개의 메소드가 구성되어 있다.
	 * trunOn: 클라이언트의 연결이 처음 시작 됐을 때 보여주는 이미지
	 *       - 이후 바로 클라이언트 상태값에 맞는 아이콘을 표시해 줌
	 *       
	 * turnOff: 클라이언트의 연결이 해제됐으면 다시 검정아이콘으로 바꿔줌
	 * 
	 * checkOn: 클라이언트 좌클릭시 표시되는 아이콘
	 * 
	 * checkOff: 클라이언트가 체크되어 있을 때 다시 좌클릭을 하면 체크 아이콘이 사라짐
	 * 
	 * 나머지 12개: 클라이언트는 총 12개의 상태값을 보내기 때문에 12개의 메소드를 구성했다.
	 */
	public void turnOn() {
		img("green");
		isTurned = true;
	}

	public void turnOff() {
		img("black");
		isTurned = false;
	}
	
	public void greenTrash(){
		img("GreenOpenON");
		isTurned = true;
	}
	
	public void yellowTrash(){
		img("YellowOpenON");
		isTurned = true;
	}
	
	public void redTrash(){
		img("RedOpenON");
		isTurned = true;
	}
	
	public void openRedOFF(){
		img("RedOpenOFF");
		isTurned = true;
	}
	public void openYellowOFF(){
		img("YellowOpenOFF");
		isTurned = true;
	}
	public void openGreenOFF(){
		img("GreenOpenOFF");
		isTurned = true;
	}
	public void closeRedON(){
		img("RedCloseON");
		isTurned = true;
	}
	public void closeYellowON(){
		img("YellowCloseON");
		isTurned = true;
	}
	public void closeGreenON(){
		img("GreenCloseON");
		isTurned = true;
	}
	public void closeRedOFF(){
		img("RedCloseOFF");
		isTurned = true;
	}
	public void closeYellowOFF(){
		img("YellowCloseOFF");
		isTurned = true;
	}
	public void closeGreenOFF(){
		img("GreenCloseOFF");
		isTurned = true;
	}
	public void full(){
		img("full.png");
		isTurned = true;
	}
	
	public void checkOn() {
		lpane.add(panel3, new Integer(2), 0);
		this.isChecked = true;
		repaint();
	}

	public void checkOff() {
		lpane.remove(panel3);
		this.isChecked = false;
		repaint();
	}

	// 클라이언트 이미지를 불러오는 패널
	class InnerPanel extends JPanel {
		private static final long serialVersionUID = 1547128190348749556L;

		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(img, 0, 0, null);
		}
	}

	// 클라이언트를 좌클릭시 선택을 표시하는 select 버튼을 표시
	@SuppressWarnings("serial")
	class CheckPanel extends JPanel {
		BufferedImage img_c;

		CheckPanel() {
			try {
				img_c = ImageIO.read(new File("img/check.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(img_c, 0, 0, null);
		}
	}

	//마우스 및 버튼 액션 처리
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (this.isChecked == false) {
			checkOn();

		} else if (this.isChecked == true) {
			checkOff();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@SuppressWarnings("static-access")
	@Override
	public void mousePressed(MouseEvent me) {
		if (me.getModifiers() == me.BUTTON3_MASK)
			pMenu.show(this, me.getX(), me.getY());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	// 서버가 제어할 수 있는 기능들에 알맞는 액션처리를 해주는 메소드 
	@Override
	public void actionPerformed(ActionEvent e) {
		if(TrashHost.userConnectCheck[num] == 0)
			return;
		if (e.getSource() == autoOn) {
			img("Applying");
			vcm.controlToClient(num, 1);
		} else if (e.getSource() == trashChat) {
			vcm.messageFromPC(num, "채팅을 시작합니다\n");
		} else if (e.getSource() == autoOff) {
			img("Applying");
			vcm.controlToClient(num, 2);
		} else if (e.getSource() == doorClose){
			vcm.controlToClient(num, 4);
		} else if (e.getSource() == connectClose){
			vcm.groupOff(1, num);
		}
	}

	class MousePopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			checkPopup(e);
		}

		
		//우클릭시 5가지의 기능을 보여주는 팝업창
		private void checkPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				pMenu.show(TrashImage.this, e.getX(), e.getY());
			}
		}
	}


}
