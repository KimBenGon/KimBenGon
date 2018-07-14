package interfaceMonitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import view.Manage;
import control.TrashHost;
import inquiry.manage_member.ManageClient;
import control.TrashControl_Start;

/*
 * 관리자가 로그인에 성공하면 보여지는 클라이언트 관제 화면임
 * 
 * 기능
 * 1. 개별의 클라이언트 최대 50개까지 컨트롤 가능
 * 2. 우측하단에 라이브 시간 표시
 * 3. 우측하단 모니터를 누르면 클라이언트 전체 및 부분 제어가 가능함
 * 4. 우측하단 돋보기를 누르면 클라이언트 ID 관리화면으로 넘어감(ID추가, ID삭제, ID조회, ID검색)
 *    - 관리자 ID는 보여주지 않으며 PW도 볼 수없다.
 */
public class ManageMonitor extends Manage implements ActionListener {

	private static final long serialVersionUID = 1L;
	TrashControl_Start vcm;

	JPanel panel, pan_navi, pan_clock;
	public JButton bt[] = new JButton[2]; 	// 추가기능 버튼 2개(전체 및 부분제어, 클라이언트 ID 관리)
	public JPanel seat50;					// 50개 패널을 담기 위함!
	int pX, pY;								// 마우스 리스너를 위함
	int x = 0, y = 0; 						// 좌표 계속 움직이게 해주는 x, y
	int sx = 77, sy = 0;					// 마우스 리스너를 위함

	JPopupMenu popup;						// 마우스 우클릭 구현
	JMenuItem totalAutoOn, totalAutoOff, partAutoOn, partAutoOff, compulsionOpenClose;
	JPanel pan_imgClock;
	Image image, image2, image3;
	Image img;

	public ManageMonitor() {
		vcm =  TrashControl_Start.getInstance("매니지프레임HUD");
		// 프레임 초기 설정
		setSize(1600, 900);
		setTitle("쓰레기통 관리 프로그램");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setBackground(Color.BLACK);

		// 프레임 화면 중앙 배열
		Dimension frameSize = this.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		// 가장 큰 JLayer패널= 레이어를 순서대로 올려줌
		JLayeredPane lpane = new JLayeredPane();
		lpane.setBounds(0, 0, 1600, 900);
		lpane.setLayout(null);

		// 배경패널
		panel = new MyPanel("img/mainHud_back.png");
		panel.setLayout(null);
		panel.setBounds(0, -30, 1600, 900);
		
		
		// 시계패널
		pan_imgClock = new MyPanel2();
		pan_imgClock.setLayout(null);
		pan_imgClock.setBounds(15, 20, 179, 149);
		pan_imgClock.setOpaque(false);

		// 시계글씨패널
		pan_clock = new MyClock();
		pan_clock.setBounds(1500, 800, 100, 100);
		//pan_clock.setBounds(80, 53, 100, 100);
		pan_clock.setBackground(Color.BLUE);
		pan_clock.setOpaque(false);					// 패널의 배경을 투명하게, 그래야 뒷 배경이 보임

		// 클라이언트 전체 및 부분 제어, ID관리 버튼 패널, 1200 828
		pan_navi = new JPanel();
		pan_navi.setLayout(null);
		pan_navi.setBounds(1200, 798, 400, 70);
		pan_navi.setOpaque(false);
		
		/*
		 * 우측하단 서버 기능 아이콘 추가 지점, 91 95
		 * 현재는 두 개의 제어를 지원하지만 추가적으로 기능을 넣고 싶다면 아래 루프를 수정하면 됨
		 * 그런데 기능 하나 넣을 떄마다 내가 죽기 때문에 안 넣을거임 ㅎㅎ
		 * 4개 넣으려다 말았음
		 */
		int temp[] = { 91, 95, 80, 79 };
		for (int i = 0; i < 2; i++) {
			bt[i] = new JButton(new ImageIcon("img/bt_navi_" + i + ".png"));
			bt[i].setBorderPainted(false);
			bt[i].setFocusPainted(false);
			bt[i].setContentAreaFilled(false);
			bt[i].addActionListener(this);
			bt[i].setBounds(x + 95, -2, temp[i], 60);
			x += temp[i];
			pan_navi.add(bt[i]);
		}

		/*
		 * 클라이언트 이미 패널 시작 지점, 165 79
		 * 한 줄에 10개, 세로 5개로 총 50개의 클라이언트를 보여줌
		 */
		seat50 = new JPanel();
		seat50.setLayout(null);
		seat50.setOpaque(false);
		seat50.setBounds(165, 79, 1368, 686);
		x = 0;
		y = 0;
		for (int i = 0; i < 50; i++) {
			pan[i] = new interfaceMonitor.TrashImage(i);
			if (i % 10 == 0 && i != 0) {
				x = 0;
				y += 140;
			}
			// System.out.print("x : " + x + " y :" + y + " ");
			pan[i].setBounds(x, y, 99, 99);
			pan[i].x = x + 165;
			pan[i].y = y + 79 + 30;
			x += 135;
		}

		// 드래그 패널
		SelectPanel sPanel = new SelectPanel();
		sPanel.setBounds(0, -30, 1600, 900);
		sPanel.setForeground(new Color(36, 205, 198));
		sPanel.setOpaque(false);

		// 마지막 붙이기
		lpane.add(panel, new Integer(0), 0);

		lpane.add(pan_imgClock, new Integer(4), 0);
		lpane.add(pan_clock, new Integer(5), 0); // 시계패널은 우측하단
		lpane.add(pan_navi, new Integer(2), 0);

		///lpane.add(star, new Integer(3), 0);
		lpane.add(seat50, new Integer(2), 0);
		lpane.add(sPanel, new Integer(0), 0);

		getContentPane().add(lpane);
		setVisible(true);

		// 클라이언트 액션
		Thread th = new MyThread(1);
		th.start();

		
		/*
		 * 우측 하단 아이콘 클릭시 나오는 팝업창 구현
		 */
		popup = new JPopupMenu();
		totalAutoOn = new JMenuItem("전체 자동모드 ON");
		totalAutoOff = new JMenuItem("전체 자동모드 OFF");
		partAutoOn = new JMenuItem("단체 자동모드 ON");
		partAutoOff = new JMenuItem("단체 자동모드 OFF");
		compulsionOpenClose = new JMenuItem("전체 투입구 여닫기");
		totalAutoOn.addActionListener(this);
		totalAutoOff.addActionListener(this);
		partAutoOn.addActionListener(this);
		partAutoOff.addActionListener(this);
		compulsionOpenClose.addActionListener(this);
		popup.add(totalAutoOn);
		popup.add(totalAutoOff);
		popup.add(partAutoOn);
		popup.add(partAutoOff);
		popup.add(compulsionOpenClose);
	//170501 여기까지 진행완료
	}
	
	//코드 테스트를 위해 생성
	public static void test(){
		TrashHost host = new TrashHost();
		host.startFromFrame();
	}
	public static void main(String[] args) {
		test();
	}

	// 이미지 그리기 위한 마이패널
	@SuppressWarnings("serial")
	class MyPanel extends JPanel {
		Image image;

		MyPanel(String img) {
			image = Toolkit.getDefaultToolkit().createImage(img);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image, 0, 0, this);
			}
		}

		public void update(Graphics g) {
			paintComponent(g);
		}

	}// 마이패널 종료

	public void reimg() {
		repaint();
	}

	@SuppressWarnings("serial")
	class MyPanel2 extends JPanel {

		int i=2;
		//시계이미지쓰레드
		MyPanel2() {
			///image = Toolkit.getDefaultToolkit().createImage("img/cl1.png");
			///image2 = Toolkit.getDefaultToolkit().createImage("img/cl2.png");
			///image3 = Toolkit.getDefaultToolkit().createImage("img/cl3.png");
			///img = image;
			
			Thread thread = new ClockRoThread();
			thread.start();

		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null) {
				g.drawImage(img, 0, 0, this);
			}
		}

		class ClockRoThread extends Thread {
			public void run() {

				try {
					while (true) {
						Thread.sleep(10000);
						switch (i) {
						case 1:
							img = image;
							i = 2;
							pan_imgClock.repaint();
							break;
						case 2:
							img = image2;
							i = 3;
							pan_imgClock.repaint();
							break;
						case 3:
							img = image3;
							i = 1;
							pan_imgClock.repaint();
							break;
						}

					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	// 마우스 드래그 구현
	@SuppressWarnings("serial")
	class SelectPanel extends JPanel implements MouseMotionListener,MouseListener {
		int x, y, pX, pY, iX, iY;

		SelectPanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
			setFocusable(true);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.draw3DRect(x, y, pX - x, pY - y, false);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			pX = e.getX();
			pY = e.getY();
			repaint();
		}

		public void mousePressed(MouseEvent e) {
			x = e.getX();
			iX = e.getX();
			y = e.getY();
			iY = e.getY();
			pX = e.getX();
			pY = e.getY();
			// System.out.println("x:" + x + " y:" + y);
		}

		public void mouseReleased(MouseEvent e) {
			for (int i = 0; i < 50; i++) {
				if (x < pan[i].x && pan[i].x < pX && y < pan[i].y
						&& pan[i].y < pY)
					pan[i].checkOn();
			}

			x = 0;
			y = 0;
			pX = 0;
			pY = 0;
			repaint();
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	/* 클라이언트를 표시해주는 스레드 
	 * 1: 50개의 클라이언트 이미지를 표시해준다.
	 *  - 굳이 딜레이를 줄 필요는 없지만 한번에 보여지면 뭔가 프로그램이 허접해보여서 일부러 딜레이줌ㅋㅋ
	 * 2: 단체 ON, OFF 모드를 제어할 때 순서대로 보여지도록 적용함
	 *  - 마찬가지로 한번에 표시해주면 허접해 보여서 눈에 보이게 딜레이를 줬음
	 */
	class MyThread extends Thread {
		int i;

		MyThread(int i) {
			this.i = i;
		}

		public void run() {
			Set<Integer> hs = null;
			if (i == 1) {
				hs = new LinkedHashSet<Integer>();
				for (; hs.size() < 50;) {
					int x = (int) ((Math.random() * 50));
					hs.add(x);
				}
			} else {
				hs = new HashSet<Integer>();
				for (int a = 0; a < 50; a++)
					hs.add(a);
			}
			try {
				int tmp=0;
				for (Integer s : hs) {

					if (i == 1)
						Thread.sleep(50);
					else {
						Thread.sleep(25);
					}

					
					switch (i) {
					case 1:
						tmp++;
						if(tmp>30)
							Thread.sleep(s*10 -(s*5));
						if(tmp==50)
						{
							Thread.sleep(1000);
							
							System.out.println("50번째");
						}
							
						seat50.add(pan[s]);
						
						break;
					case 2:		//전체 자동모드 ON
						for(int i = 0; i < 50; i++){
							if(TrashHost.userConnectCheck[i] == 1){
								vcm.controlToClient(i, 1);
							}
							pan[i].checkOff();
						}
						break;
					case 3:		//전체 자동모드 OFF	
						for(int i = 0; i < 50; i++){
							if(TrashHost.userConnectCheck[i] == 1){

								vcm.controlToClient(i, 2);
							}
							pan[i].checkOff();
						}
						break;
					case 4:		//부분 자동모드 ON 
						for (int i = 0; i < 50; i++) {
							if (pan[i].isChecked == true) {
								if(TrashHost.userConnectCheck[i] == 1){
									vcm.controlToClient(i, 1);
								}
								pan[i].checkOff();
							}
						}
						break;
					case 5:		//부분 자동모드 OFF
						for (int i = 0; i < 50; i++) {
							if (pan[i].isChecked == true) {
								if(TrashHost.userConnectCheck[i] == 1){
									vcm.controlToClient(i, 2);
									pan[i].checkOff();
								}
								pan[i].checkOff();
							}
						}
						break;
					case 6:
						for (int i = 0; i < 50; i++) {
							if (pan[i].isChecked == true) {
								vcm.controlToClient(i, 4);
							}
						}
					}
					repaint();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	/*
	 * 시계에 표시될 폰트와 색깔을 설정하고 배치
	 */
	@SuppressWarnings("serial")
	class MyClock extends JPanel {
		Calendar ctoday = Calendar.getInstance();
		int i = ctoday.get(Calendar.AM_PM);
		String[] ampm = { "AM", "PM" };
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		String time = sdf.format(today);
		JLabel timeLabel;
		JLabel ampmLabel;

		public MyClock() {
			this.setLayout(null);

			timeLabel = new JLabel(time);
			timeLabel.setBounds(0, 0, 100, 20);
			timeLabel.setForeground(new Color(36, 205, 198));
			timeLabel.setForeground(new Color(255, 255, 255));
			timeLabel.setFont(new Font("배달의민족 한나", Font.BOLD, 12));
			ampmLabel = new JLabel(ampm[i]);
			ampmLabel.setBounds(15, 20, 100, 30);
			ampmLabel.setForeground(new Color(255, 255, 255));
			ampmLabel.setFont(new Font("배달의민족 한나", Font.BOLD, 12));

			add(timeLabel, BorderLayout.NORTH);
			add(ampmLabel, BorderLayout.CENTER);
			Thread thread = new MyClockThread();
			thread.start();
		}

		class MyClockThread extends Thread {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					today = new Date();
					time = sdf.format(today);
					timeLabel.setText(time);
				}

			}
		}
	}

	@Override
	// 우측하단 서버 기능 버튼들의 액션 구현
	public void actionPerformed(ActionEvent e) {
		// 팝업메뉴나오게
		if (e.getSource() == bt[0]) {
			popup.show(ManageMonitor.this, 1253, 751);
			// 전체 자동모드 ON
		} else if (e.getSource() == totalAutoOn) {
			System.out.println("전체 자동모드 ON");
			Thread seatThread = new MyThread(2);
			seatThread.start();
			// 전체 자동모드 OFF
		} else if (e.getSource() == totalAutoOff) {
			System.out.println("전체 자동모드 OFF");
			Thread seatThread = new MyThread(3);
			seatThread.start();
			// 부분 자동모드 ON
		} else if (e.getSource() == partAutoOn) {
			System.out.println("부분 자동모드 ON");
			Thread seatThread = new MyThread(4);
			seatThread.start();
			// 부분 자동모드 OFF
		} else if (e.getSource() == partAutoOff) {
			System.out.println("부분 자동모드 OFF");
			Thread seatThread = new MyThread(5);
			seatThread.start();
			// 전체 강제 여닫기
		} else if (e.getSource() == compulsionOpenClose) {
			System.out.println("단체 강제 여닫기");
			Thread seatThread = new MyThread(6);
			seatThread.start();

			// 클라이언트 ID 관리
		} else if (e.getSource() == bt[1]) {
			new ManageClient();
		}
	}
	
	public void img(String s) {
		try {
			img = ImageIO.read(new File("img/" + s + ".png"));
		} catch (IOException e) {
			System.out.println("이미지 불러오기 실패!");
			System.exit(0);
		}
		repaint();
	}
}