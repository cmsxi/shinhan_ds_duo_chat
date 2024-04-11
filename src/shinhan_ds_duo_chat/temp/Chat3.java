package shinhan_ds_duo_chat.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shinhan_ds_duo_chat.model.ChatRoom;

public class Chat3 {
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private Connection conn;
	private static Session session;
	
	public static void main(String[] args) {
		session = new Session("b", "b", "010-1234-5678", "123");
		
		Chat3 chat = new Chat3();
		chat.list();
	}
	
	public Chat3() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");

			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "testuser", "test1234");
			System.out.println("DB 접속 성공");

		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	public void list() {
		System.out.println("[채팅방 목록]");
		try {
			// 현재 유저가 참여 중인 채팅방 목록 조회
			String sql = "SELECT r.roomID, r.roomName, r.recentMsg, r.recentTime "
					+ "FROM CHATROOM r JOIN USERJOINROOM u ON r.roomID = u.roomID WHERE u.userID = ?"
					+ "ORDER BY r.recentMsg DESC";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, session.getUserId());
			ResultSet rs = pstmt.executeQuery();

			// 채팅방 목록 출력
			int chatroomNum = 1;
			
			while (rs.next()) {
				ChatRoom chatroom = new ChatRoom();
				// 채팅방 정보 출력
				chatroom.setRoomID(rs.getString("roomID"));
				chatroom.setRoomName(rs.getString("roomName"));
				chatroom.setRecentMsg(rs.getString("recentMsg"));
				chatroom.setRecentTime(rs.getDate("recentTime"));
				// 메시지 시간 조금 더 자세하게 하기

				System.out.printf("(%d)\n방 이름: %s \n최근 메시지: %s\n%s\n\n", chatroomNum++, chatroom.getRoomName(), chatroom.getRecentMsg(), chatroom.getRecentTime());
			}
			
			// 참여 중 채팅방 없을 시 출력 
			if(chatroomNum == 1) {
				System.out.println("참여중인 채팅방이 없습니다.\n");
			}
			
			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		}
		menu();
	}
	
	public void menu() {
		System.out.println();
		System.out.println("[채팅방]");
		System.out.println("1. 채팅방 목록");
		System.out.println("2. 채팅방 생성");
		System.out.println("3. 채팅방 참여");
		System.out.println("(그 외 입력 시 뒤로가기)");
		System.out.print("실행할 메뉴를 선택해주세요: ");
		
		String menuNum = "";
		try {
			menuNum = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if("1".equals(menuNum)) {
			list();
		} else if("2".equals(menuNum)) {
			create();
		} else if("3".equals(menuNum)) {
			enter();
		} else {
			return;
		}
	}

	private void exit() {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	public void create() {
		try {
			System.out.println("[채팅방 생성]");
			System.out.print("방 이름을 입력해주세요: ");
			
			String roomName = "";
			try {
				roomName = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 채팅방 생성
			String sql = "INSERT INTO CHATROOM(roomID, roomName, recentmsg, recenttime) "
					+ "VALUES (SEQ_RID.nextval, ?, ?, SYSDATE)";
			PreparedStatement pstmt = conn.prepareStatement(sql, new String[] {"roomID"});
			pstmt.setString(1, roomName);
			pstmt.setString(2, "채팅방이 새로 생성되었습니다.");
			pstmt.executeUpdate();

//            int rows = pstmt.executeUpdate();
					
			// 생성된 채팅방 ID 가져오기
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			
			if (generatedKeys.next()) {
				String roomId = generatedKeys.getString(1);
							
				String initMsg = "INSERT INTO MESSAGE(MESSAGENUM, CONTENT, CHATTIME, USERID, ROOMID) VALUES (SEQ_MID.nextval, ?, CURRENT_TIMESTAMP, ?, ?)";
				PreparedStatement initMsgPstmt = conn.prepareStatement(sql);
				
				initMsgPstmt = conn.prepareStatement(initMsg);
				
				initMsgPstmt.setString(1, "(채팅방이 새로 생성되었습니다)");
				initMsgPstmt.setString(2, session.getUserId()); 
				initMsgPstmt.setString(3, roomId);
				initMsgPstmt.executeUpdate();
	            
				// 현재 로그인한 사용자(나 자신)를 채팅방에 추가
				String insertSelfSql = "INSERT INTO userJoinRoom(userID, roomID) VALUES (?, ?)";
				PreparedStatement pstmtSelf = conn.prepareStatement(insertSelfSql);
				pstmtSelf.setString(1, session.getUserId());
				pstmtSelf.setString(2, roomId);
				pstmtSelf.executeUpdate();

				System.out.print("초대할 친구의 이름을 쉼표로 구분하여 입력해주세요: ");
				
				String[] friendNames = {};
				try {
					friendNames = br.readLine().split(",\\s*");
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (String friendName : friendNames) {
					// 친구의 userID 가져오기
					String selectFriendSql = "SELECT userID FROM USERTABLE WHERE name = ?";
					PreparedStatement pstmtFriend = conn.prepareStatement(selectFriendSql);
					pstmtFriend.setString(1, friendName);
					ResultSet rs = pstmtFriend.executeQuery();

					if (rs.next()) {
						String friendId = rs.getString("userID");

						// 친구를 채팅방에 추가
						String insertFriendSql = "INSERT INTO userJoinRoom (userID, roomID) VALUES (?, ?)";
						PreparedStatement pstmtInsertFriend = conn.prepareStatement(insertFriendSql);
						pstmtInsertFriend.setString(1, friendId);
						pstmtInsertFriend.setString(2, roomId);
						pstmtInsertFriend.executeUpdate();

						System.out.println(friendName + "님을 초대했습니다.");
					} else {
						System.out.println(friendName + "님을 찾을 수 없습니다.");
						
					}
					
					menu();
				}
			} else {
				System.out.println("채팅방 생성에 실패하였습니다.");
				list();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		Chat chat = new Chat();
//		chat.list();
//		chat.create();
//		
//		String chatroom = scanner.nextLine();
//		chat.enter(chatroom);
//	}

	public void enter() {
		ChatRoom chatroom = new ChatRoom();
		
		System.out.print("참여하고자 하는 채팅방 이름을 입력해주세요: ");
		
//		String roomName = scanner.nextLine();
		String roomName = "";
		try {
			roomName = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		chatroom.setRoomName(roomName);
		
		try {
			String sql = "SELECT roomID FROM chatroom WHERE roomName = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, roomName);
			ResultSet rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				System.out.println("채팅방을 찾지 못했습니다.");
				return;
			}
			
			chatroom.setRoomID(rs.getString("roomID"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// 어 근데 서버가 chatroom 정보 가지고 잇으면 됨
		System.out.println("채팅방에 접속했습니다. 메시지를 입력하세요. (종료하려면 exit 입력)");
		
		// 메시지 수신
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int lastMsgNum = 0;
					// 초기 접속 시 메시지 출력
					String sql = "SELECT name, content, messagenum from "
							+ "(select * FROM testuser.MESSAGE m join USERTABLE u "
							+ "on m.userid = u.userid WHERE m.roomID = ? ORDER BY m.messagenum desc) "
							+ "where ROWNUM <= 10 ORDER BY messagenum";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					
					pstmt.setString(1, chatroom.getRoomID());
					ResultSet rs = pstmt.executeQuery();

					while (rs.next()) {
						String username = rs.getString("name");
						String content = rs.getString("content");
						
						if("(채팅방이 새로 생성되었습니다)".equals(content)) {
							System.out.println(content);
							continue;
						}
						System.out.println("\n" + username + ": " + content);
						
//						if (!username.equals(session.getName())) {
//							
//						}	
						lastMsgNum = rs.getInt("messagenum");
					}
					
					rs.close();
					pstmt.close();
					
					
					while (true/*!Thread.currentThread().isInterrupted()*/) {
						// 데이터베이스에서 최근 메시지 조회
						String sql2 = "SELECT name, content, messagenum from "
								+ "(select * FROM testuser.MESSAGE m join USERTABLE u "
								+ "on m.userid = u.userid WHERE m.roomID = ? ORDER BY m.messagenum desc) "
								+ "where ROWNUM = 1";
						pstmt = conn.prepareStatement(sql2);
						
						pstmt.setString(1, chatroom.getRoomID());
						rs = pstmt.executeQuery();
						
						while (rs.next()) {
							String username = rs.getString("name");
							String content = rs.getString("content");
//							System.out.println("chat1lastMsgNum:" + lastMsgNum);
//							System.out.println("chat1:messagenum" + rs.getInt("messagenum"));
							if (!username.equals(session.getName()) && lastMsgNum < rs.getInt("messagenum")) {
								System.out.println(username + ": " + content);
								lastMsgNum = rs.getInt("messagenum");
							}
						}
						

						rs.close();
						pstmt.close();

						// 일정 시간마다 메시지를 조회하기 위해 스레드를 잠시 멈춤
						Thread.sleep(200);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		
		
		// 메시지 전송, 출력 대기
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 이부분 돌아가다가 수신도 가능하게 바꾸기

		System.out.print("채팅에 참여하고자 하면 ENTER키를 눌러주세요");
//		System.out.print("입력> ");
		String input = " ";
        while (!(input.equals("exit"))) {
        	try {
    			input = br.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        	     	
//        	System.out.print("\n입력> ");
            try {
                if(input.equals("")) continue;
            	// 입력 메시지 데이터베이스에 저장
                String sql = "INSERT INTO message (messagenum, content, chatTime, userID, roomID) VALUES (SEQ_MID.nextval, ?, CURRENT_TIMESTAMP, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, input);
                pstmt.setString(2, session.getUserId()); 
                pstmt.setString(3, chatroom.getRoomID());

                int rows = pstmt.executeUpdate();

                if (rows <= 0) {
                   System.out.println("메시지 전송 실패.");
                }

                pstmt.close();
            } catch (SQLException e) {
                System.out.println("메시지 전송 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }
        
        System.out.println("채팅방에서 나갔습니다.");
		
	}

}
