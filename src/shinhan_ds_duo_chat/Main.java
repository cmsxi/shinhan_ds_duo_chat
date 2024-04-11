package shinhan_ds_duo_chat;

public class Main {
<<<<<<< HEAD

	public static void main(String[] args) {
		// TODO Auto-generated method stub
=======
	static Session session = null;

	static Friend friend = null;
	static Start start = new Start();
	static UserInfo userInfo = null;

	public static void main(String[] args) {
		try {
			start.logo();
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				session = start.list();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (session != null) {
				friend = new Friend(session);
				userInfo = new UserInfo(session);
				break;
			}
		}

		while (true) {
			String menuNo = start.menu();
			switch (menuNo) {
			case "1" -> friend.list();
			case "3" -> userInfo.list();
			}

		}
>>>>>>> 286be918e7e8eabe7ef5747a797c46b45d0c2061

	}

}
