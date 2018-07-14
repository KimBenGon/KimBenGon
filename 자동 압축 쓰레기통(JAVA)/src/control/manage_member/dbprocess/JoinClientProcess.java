package control.manage_member.dbprocess;

import java.sql.Connection;
import java.sql.PreparedStatement;

import _client.db.connection.*;

//DB에 있는 클라이언트 ID 삭제 클래스 시작
public class JoinClientProcess {

	// DB에 신규 클라이언트 추가 메소드
	public static int insertMember(String id, String pass, String tel, String loc, int age) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		int flag = 0; // 0은 실패, 1=회원가입 성공, 2=아이디가 중복됨
		DBConnectionMgr pool = null;

		try {
			pool = DBConnectionMgr.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			// num, id, password, tel, location, age
			con = pool.getConnection();
			sql = "insert member(id, password, tel, location, age)"
					+ "values(?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pass);
			pstmt.setString(3, tel);
			pstmt.setString(4, loc);
			pstmt.setInt(5, age);

			if (pstmt.executeUpdate() == 1)
				flag = 1;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

}
