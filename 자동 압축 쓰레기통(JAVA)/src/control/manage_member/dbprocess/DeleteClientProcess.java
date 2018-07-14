package control.manage_member.dbprocess;

import java.sql.Connection;
import java.sql.PreparedStatement;

import db.connection.DBConnectionMgr;

public class DeleteClientProcess {

	// DB에 있는 클라이언트 ID 삭제 메소드
	public static void delMember(String id) {

		
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		DBConnectionMgr pool = null;

		try {
			pool = DBConnectionMgr.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			con = pool.getConnection();
			sql = "delete from member where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			pool.freeConnection(con, pstmt);
		}

	}
}
