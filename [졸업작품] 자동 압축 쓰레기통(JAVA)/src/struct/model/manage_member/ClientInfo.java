package struct.model.manage_member;

public class ClientInfo {

	private String id;
	private String tel;
	private String location;
	private String age;

	public ClientInfo(String id, String tel, String location, String age) {
		this.id = id;
		this.tel = tel;
		this.location = location;
		this.age = age;

	}

	public String getId() {
		return id;
	}

	public String getTel() {
		return tel;
	}

	public String getLocation() {
		return location;
	}

	public String getAge() {
		return age;
	}

}
