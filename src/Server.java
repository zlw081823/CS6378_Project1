import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
