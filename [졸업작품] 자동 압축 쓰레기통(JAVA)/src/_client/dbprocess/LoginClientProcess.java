package _client.dbprocess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import _client.db.connection.*;

public class LoginClientProcess {

	
	// 로그인 체크 메소드
	public static boolean loginMember(String id, String pass) {

		if(id.equals("admin")){
			return false;
		}
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		String hashPass = "hash_pass 초기값";
		boolean flag = false;
		DBConnectionMgr pool = null;

		try {
			pool = DBConnectionMgr.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			con = pool.getConnection();
			sql = "select password from member where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			// System.out.println("id 값은 " + id);
			rs = pstmt.executeQuery();
			rs.next();
			hashPass = rs.getString("password");
			// System.out.println(hashPass);

			if (BCrypt.checkpw(pass, hashPass))
				flag = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return flag;
	}
}
