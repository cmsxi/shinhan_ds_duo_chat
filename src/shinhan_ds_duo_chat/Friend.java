package shinhan_ds_duo_chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import shinhan_ds_duo_chat.model.User;

public class Friend {
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private Session session;

	public Friend() {
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
		System.out.println("[친구목록]");
		System.out.println("0. 친구추가");
		try {
			String sql = "select userid , name , phoneNumber , password  from friend a join usertable b on a.userId2 = b.userId where userid1 = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, session.getUserId());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setUserId(rs.getString("userId"));
				user.setName(rs.getString("name"));
				user.setPhoneNumber(rs.getString("phoneNumber"));

				System.out.println(user.getName() + " " + user.getPhoneNumber());

			}
			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}

		// Menu();

		System.out.print("메뉴선택 : ");
		String menuNO = scanner.nextLine();
		System.out.println();

		switch (menuNO) {
		case "0" -> create();
		}
	}
	public void create() {
		
		
		try {
			String phoneNumber = null;
			System.out.println("[친구추가]");
			System.out.print("전화번호 : ");
			phoneNumber = scanner.nextLine();
			
			String sql = "INSERT INTO friend (userid1, userid2 ) select " +"'"+session.getUserId()+"'"+ " , userid from usertable where phonenumber = "+"'"+phoneNumber+"'";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			int result = pstmt.executeUpdate(sql);
			if(result == 0) {
				System.out.println("친구가 존재하지 않습니다.");
			} else {
				System.out.println("친구가 등록 되었습니다.");
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
		Friend friend = new Friend();
		friend.list();
	}
}
