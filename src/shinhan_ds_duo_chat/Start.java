package shinhan_ds_duo_chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import shinhan_ds_duo_chat.model.User;

public class Start {
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private static Session session = null;

	public Start() {
		try {


			Class.forName("oracle.jdbc.OracleDriver");

			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "testuser", "test1234");
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	public void logo() {
		System.out.println("            _                       _              _             _   \r\n"
				+ "           (_)                     ( )            | |           | |  \r\n"
				+ " _ __ ___   _  _ __    __ _  _   _ |/  ___    ___ | |__    __ _ | |_ \r\n"
				+ "| '_ ` _ \\ | || '_ \\  / _` || | | |   / __|  / __|| '_ \\  / _` || __|\r\n"
				+ "| | | | | || || | | || (_| || |_| |   \\__ \\ | (__ | | | || (_| || |_ \r\n"
				+ "|_| |_| |_||_||_| |_| \\__, | \\__,_|   |___/  \\___||_| |_| \\__,_| \\__|\r\n"
				+ "                       __/ |                                         \r\n"
				+ "                      |___/                                          ");
		
		
	}

	public Session list() {
		System.out.println();
		System.out.println("[시작페이지]");
		System.out.println("1. 로그인");
		System.out.println("2. 회원가입");

		System.out.print("메뉴선택 : ");
		String menuNO = scanner.nextLine();
		System.out.println();

		switch (menuNO) {
		case "1":
			return login();
		case "2":
			signUp();
			break;
		}
		return null;
	}

	public Session login() {
		String id = null;
		String password = null;
		System.out.println("[로그인]");
		System.out.print("id : ");
		id = scanner.nextLine();
		System.out.print("password : ");
		password = scanner.nextLine();

		try {
			String sql = "select userid , name , phoneNumber , password  from usertable where userid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				User user = new User();
				user.setUserId(rs.getString("userId"));
				user.setName(rs.getString("name"));
				user.setPhoneNumber(rs.getString("phoneNumber"));
				if (password.equals(rs.getString("password"))) {
					System.out.println("로그인 성공");
					Session session = new Session(user.getUserId() , user.getName() , user.getPhoneNumber() , user.getPassword());
					return session;
				} else {
					System.out.println("회원정보가 일치하지 않습니다.");
				}
			} else {
				System.out.println("회원정보가 일치하지 않습니다.");
			}
			
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		return null;
	}

	public void signUp() {
		String name = null;
		String phoneNumber = null;
		String id = null;
		String password = null;
		String yn = null;
		System.out.println("[회원가입]");
		System.out.print("id : ");
		id = scanner.nextLine();
		System.out.print("password : ");
		password = scanner.nextLine();
		System.out.print("이름 : ");
		name = scanner.nextLine();
		System.out.print("전화번호 : ");
		phoneNumber = scanner.nextLine();

		System.out.print("해당정보로 가입 하시겠습니까? Y/N: ");
		yn = scanner.nextLine();

		if ("Y".equals(yn)) {
			try {
				String sql = "INSERT INTO usertable (userid, name , phonenumber , password ) values(" + "'" + id + "'"
						+ " ," + "'" + name + "'" + "," + "'" + phoneNumber + "'" + "," + "'" + password + "'" + ")";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				int result = pstmt.executeUpdate(sql);
				if (result != 0) {
					System.out.println("가입되었습니다.");
				} else {
					System.out.println("해당 정보로 가입할수 없습니다.");
				}
			
				pstmt.close();
				
			} catch (Exception e) {
				System.out.println("해당 정보로 가입할수 없습니다.");
			}
		}

	}

	public String menu() {
		System.out.println();
		System.out.println("[메뉴]");
		System.out.println("1. 친구목록");
		System.out.println("2. 대화목록");
		System.out.println("3. 내정보");
		System.out.print("메뉴선택 : ");
		String menuNO = scanner.nextLine();
		return menuNO;
	}


	public void exit() {
		System.exit(0);
	}
}
