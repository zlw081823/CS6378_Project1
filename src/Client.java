import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
	private static final String [] serverIPAddr = {"127.0.0.1", "10.176.66.80", "10.176.66.81", "10.176.66.80"};
	private static final int [] serverPortNum = {6666, 6667, 6668, 6669};
	
	public static void main(String[] args) {
		int clientID = 1234;
		int serverID = 0;
		String hostname = serverIPAddr[0];	//Server IP address
		int portNum = serverPortNum[0];	//Server port number
		int cursorLoc = 0;
		
		while (true) {
			serverID = serverSelect();
			hostname = serverIPAddr[serverID - 1];
			portNum = serverPortNum[serverID - 1];
			
			try {
				Socket clientSocket = new Socket(hostname, portNum);
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				
				Message msgOut = new Message(clientID, serverID);
				msgOut.setCursorLoc(cursorLoc);
				out.writeObject(msgOut);
				
				Message msgIn;
				String cmdFromServer;	//Use server's command to communicate 
				if ((msgIn = (Message) in.readObject()) != null) {
					System.out.println("Output: Server[" + serverID + "]:: " + msgIn.getData());
					cursorLoc = msgIn.getCursorLoc();
					if ((cmdFromServer = msgIn.getCommand()) != null && cmdFromServer.equalsIgnoreCase("terminate")){
						//make sure the input command is not null!
						in.close();
						out.close();
						clientSocket.close();
						break;	
					}
				}
				
				//end of one communication session
				in.close();
				out.close();
				clientSocket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
	private static int serverSelect () {
		//Returns a pseudorandom int value between the specified origin (inclusive) and the specified bound (exclusive).
		//range from 1 to 3
//		return ThreadLocalRandom.current().nextInt(1, 4);
		return 1;	//Just for test
	}
}
