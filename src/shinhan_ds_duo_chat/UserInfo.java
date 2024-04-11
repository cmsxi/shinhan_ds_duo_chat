package shinhan_ds_duo_chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class UserInfo {
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private Session session;

	public UserInfo() {
		try {

			// session setting
			session = new Session("1234", "최민서", "010-1234-1234", "1234");

			Class.forName("oracle.jdbc.OracleDriver");

			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "scott", "tiger");
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	public void list() {
		System.out.println();
		System.out.println("[내정보]");
		System.out.println("이름 : "+session.getName());
		System.out.println("전화번호 : " + session.getPhoneNumber());
		System.out.println("0. 뒤로가기");
		System.out.println("1. 회원정보수정");
		System.out.println("2. 회원탈퇴");

		System.out.print("메뉴선택 : ");
		String menuNO = scanner.nextLine();
		System.out.println();

		switch (menuNO) {
		case "0" -> nothing();
		case "1" -> update();
		case "2" -> delete();
		}
	}
	public void nothing() {
		
	}
	
	
	
	public void update() {
		
		
		try {
			String name = null;
			String phoneNumber = null;
			System.out.println("[회원정보수정]");
			System.out.print("이름 : ");
			name = scanner.nextLine();
			System.out.print("전화번호 : ");
			phoneNumber = scanner.nextLine();
			
			String sql = "update usertable set name = "+"'"+name+"'"+" , phoneNumber = "+"'"+phoneNumber+"'"+" where userid = "+"'"+session.getUserId()+"'";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			int result = pstmt.executeUpdate(sql);
			if(result == 0) {
				System.out.println("세션정보가 잘못되었습니다.");
			} else {
				System.out.println("수정되었습니다.");
				
				session.setName(name);
				session.setPhoneNumber(phoneNumber);
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
	
		list();
		
		
	}
	
	public void delete() {
		
		
		try {
			String yn = null;
			System.out.println("[탈퇴]");
			System.out.print("Y/N : ");
			yn = scanner.nextLine();
			if("Y".equals(yn)) {
				String sql = "DELETE FROM usertable WHERE userid = "+"'"+session.getUserId()+"'";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				int result = pstmt.executeUpdate(sql);
				if(result == 0) {
					System.out.println("세션정보가 잘못되었습니다.");
				} else {
					System.out.println("탈퇴되었습니다.");
					exit();
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
	
		list();
	}
	
	public void exit() {
		System.exit(0);
	}

	public static void main(String[] args) {
		UserInfo userInfo = new UserInfo();
		userInfo.list();
	}
}
