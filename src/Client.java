import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
	private static final String [] serverIPAddr = {"127.0.0.1", "dc24.utdallas.edu", "dc25.utdallas.edu", "dc26.utdallas.edu"};
	
	public static void main(String[] args) {
		int clientID = 1234;
		int serverID = 0;
		String hostname = serverIPAddr[0];	//Default Server IP address - Localhost
		int portNum = 6666;	//Default Server port number - 6666
		int cursorLoc = 0;
		
		while (true) {
			serverID = serverSelect();
			hostname = serverIPAddr[serverID];
			System.out.println("Try to connect to Server[" + serverID + "] ...");
			try {
				Socket clientSocket = new Socket(hostname, portNum);
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				
				Message msgOut = new Message(clientID, serverID);	
				msgOut.setSeverID(serverID);
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
		return 0;	//For test on local host
	}
}
