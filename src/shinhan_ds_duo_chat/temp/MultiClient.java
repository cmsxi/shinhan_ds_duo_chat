package shinhan_ds_duo_chat.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MultiClient {
	public static void main(String[] args) {
		MultiClient multiClient = new MultiClient();
		multiClient.start();
	}

	public void start() {
		Socket socket = null;
		BufferedReader in = null;
		try {
			socket = new Socket("localhost", 8000);
			System.out.println("[서버와 연결되었습니다]");

			String name = "user" + (int) (Math.random() * 10);
			Thread sendThread = new SendThread(socket, name);
			sendThread.start();

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (in != null) {
				String inputMsg = in.readLine();
				if (("[" + name + "]님이 나가셨습니다").equals(inputMsg))
					break;
				System.out.println("From:" + inputMsg);
			}
		} catch (IOException e) {
			System.out.println("[서버 접속끊김]");
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[서버 연결종료]");

	}
}
