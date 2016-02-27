import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	public static void main(String[] args) {
		int portNum = 6666;
		boolean listening = true;
		
		try {
			ServerSocket serverSocket = new ServerSocket(portNum);
			
			while (listening) {
				Socket clientSocket = serverSocket.accept();
				MultiServerThread newThread = new MultiServerThread(clientSocket); //Do not forget NEW !
				newThread.start();
			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
