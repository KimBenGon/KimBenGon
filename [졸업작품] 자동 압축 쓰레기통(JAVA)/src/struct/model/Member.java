package struct.model;
/* 
 * 
 * DB 테이블과 똑같이 작성해야함
 * 클라이언의 정보는 순번(num), 아이디(id), 이름(name), 전화번호(tel), 비밀번호(password), 나이(age) 로 이루어 짐
 */

//num, id, password, tel, location, age
@SuppressWarnings("unused")
public class Member {
	private int num;
	private String id;
	private String password;
	private String tel;
	private String location;
	private int age;
	

	// get, set 메소드
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void main(String[] args) {
	
	}

}
