package control.manage_member.dbprocess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import db.connection.DBConnectionMgr;
import struct.model.manage_member.ClientInfo;

public class ReadClientProcess {

	// DB에 있는 회원들의 정보를 읽어옴(전체)
	public static void readMember(ArrayList<ClientInfo> list) {

		ResultSet rs = null;
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

			int num = 0;
			// id, tel, location, age
			con = pool.getConnection();
			sql = "select id,tel,location,age from member where id<>'admin'";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString("id");
				String tel = rs.getString("tel");
				String location = rs.getString("location"); //).toString();
				String age = new Integer(rs.getInt("age")).toString();
				list.add(new ClientInfo(id, tel, location, age));
				num++;

			}
			if (num == 0) {
				JOptionPane.showMessageDialog(null, "조회할 데이터가 존재 하지 않습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			pool.freeConnection(con, pstmt);
		}

	}
	
	
	// 확인하고 싶은 특정 ID를 검색하는 메소드
	public static ClientInfo readPerson(String id) {

		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		DBConnectionMgr pool = null;
		ClientInfo memberInfo=null;
		
		try {
			pool = DBConnectionMgr.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			int num = 0;
			// id, tel, location,age
			con = pool.getConnection();
			sql = "select id,tel,location,age from member where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String id2 = rs.getString("id");
				String tel = rs.getString("tel");
				String location = rs.getString("location"); //).toString();
				String age = new Integer(rs.getInt("age")).toString();
				memberInfo = new ClientInfo(id2, tel, location, age);
				num++;
			}
			
			if (num == 0) {
				JOptionPane.showMessageDialog(null, "조회할 데이터가 존재 하지 않습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		
		return memberInfo;
	}
}
