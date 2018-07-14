package struct.model;

@SuppressWarnings("serial")
public class Client implements java.io.Serializable{
	int num;
	String nick;
	String hour;
	
	public Client(int num, String nick, String hour) {
		this.num = num;
		this.nick = nick;
		this.hour = hour;
	}

	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String toString() {
		return "num :" + num + " , nick :" + nick + " , hour :" + hour;
	}
}