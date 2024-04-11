package shinhan_ds_duo_chat.temp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {

	public static void main(String[] args) {
		MultiServer multiServer = new MultiServer();
		multiServer.start();
	}
	
	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(8000);
			while (true) {
				System.out.println("[클라이언트 연결대기중]");
				socket = serverSocket.accept();
				
				// client가 접속할때마다 새로운 스레드 생성
				ReceiveThread receiveThread = new ReceiveThread(socket);	
				receiveThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket!=null) {
				try {
					serverSocket.close();
					System.out.println("[서버종료]");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("[서버소켓통신에러]");
				}
			}
		}
	}

}
