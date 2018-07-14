package view;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public abstract class Trash_panAb extends JPanel{
	public JLabel[] label = new JLabel[4];	//0: 클라이언트의 연결번호, 1: 클라이언트 ID, 2: 클라이언트와 서버의 연결유지시간
	public boolean isChecked;
	public boolean isLogined;
	public boolean isTurned;
	public int num;//좌석번호를 혼자 가지고 있기 위한 넘버
	public Color tmp; //잠시 컬러를 가지고 있다가 다시 돌아갈때 준다. 
	public JPopupMenu pMenu;
	public JMenuItem autoOn, autoOff, trashChat, connectClose, doorClose;
	public abstract void turnOn();
	public abstract void turnOff();
	public abstract void checkOn();
	public abstract void checkOff();
	public abstract void greenTrash();
	public abstract void yellowTrash();
	public abstract void redTrash();
	public abstract void openRedOFF();
	public abstract void openYellowOFF();
	public abstract void openGreenOFF();
	public abstract void closeRedON();
	public abstract void closeYellowON();
	public abstract void closeGreenON();
	public abstract void closeRedOFF();
	public abstract void closeYellowOFF();
	public abstract void closeGreenOFF();
	public abstract void full();
	public int x,y;
	public String nickname;

}
