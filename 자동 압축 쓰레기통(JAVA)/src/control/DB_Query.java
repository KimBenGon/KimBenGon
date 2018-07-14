package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import db.connection.BCrypt;
import db.connection.DBConnectionMgr;

public class DB_Query {
	DBConnectionMgr pool;

	public DB_Query() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
	}
	
	// 아이디 비교 메소드 시작
	public static boolean checkId(String id) {
		DBConnectionMgr pool = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		boolean flag = false;

		try {
			pool = DBConnectionMgr.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			con = pool.getConnection();
			sql = "select id from member where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			flag = pstmt.executeQuery().next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return flag;
	}

	// 아이디 비교 함수 종료
	// 로그인 함수
	public static boolean loginMember(String id, String pass) {

		if(!(id.equals("admin"))){
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
			rs = pstmt.executeQuery();
			rs.next();
			hashPass = rs.getString("password");
			
			//암호화된 PW로 비교
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