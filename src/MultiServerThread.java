import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MultiServerThread extends Thread{
	private static final String [] serverIPAddr = {"127.0.0.1", "dc24.utdallas.edu", "dc25.utdallas.edu", "dc26.utdallas.edu"};
	private static final int serverPortNum = 6666;
	
	private Socket clientSocket = null;
	
	public MultiServerThread(Socket acceptSocket) {
		// TODO Auto-generated constructor stub
		this.clientSocket = acceptSocket;
		System.out.println("Connected...");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub		
		try {
			ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			
			Message msgIn = new Message();	
			Message msgOut = new Message();	//The serverID will be obtained from msgFromClient
			
			try {
				if ((msgIn = (Message) in.readObject()) != null) {	//**Here may come problem, due to the input does not meet the required format!
					msgOut = cmdHandler(msgIn);	
					out.writeObject(msgOut);						
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.err.println("what the hell is going on?");
			}
			
			in.close();
			out.close();			
			clientSocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private static Message cmdHandler (Message msgIn) {	//why need static?
		Message msgOut = new Message();
		
		String cmd = msgIn.getCommand();
		String fileName = msgIn.getFileName();
		String data = msgIn.getData();
		//int serverID = msgIn.getServerID();		
		
		String dirName = "/tmp/user/java/bin";	//to be replaced
		String fileDir = dirName + "/" + fileName;
		int cursorLoc = msgIn.getCursorLoc();	
		msgOut.setCursorLoc(cursorLoc);	//Output message also need to set cursorLoc whenever respond! 
		
		if(cmd.equalsIgnoreCase("create")) {
			if (msgIn.isServer2Server() == false) {
				//Server received a message from client
				if (createFile(fileDir)) {
					forwardMessage(msgIn);
					msgOut.setData("\"" + fileName + "\"" + " was created!");
				} else {
					msgOut.setData("Create file " + "\"" + fileName + "\"" + "failed!");
				}
			} else {
				//Server received a synchronizing request message from another server
				if (createFile(fileDir)) {
					msgOut.setCommand("success");
				} else {
					msgOut.setCommand("failure");
				}				
			}			
		} else if(cmd.equalsIgnoreCase("delete")) {
			if (msgIn.isServer2Server() == false) {
				//Server received a message from client
				if (deleteFile(fileDir)) {
					forwardMessage(msgIn);
					msgOut.setData("\"" + fileName + "\"" + " is deleted!");
				} else {
					msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
				}
			} else {
				//Server received a synchronizing request message from another server
				if (deleteFile(fileDir)) {
					msgOut.setCommand("success");
				} else {
					msgOut.setCommand("failure");
				}				
			}			
		} else if(cmd.equalsIgnoreCase("write")) {
			if (msgIn.isServer2Server() == false) {
				//Server received a message from client
				if (writeFile(fileDir, data)) {
					forwardMessage(msgIn);
					msgOut.setData("A string is written in " + fileName);
				} else {
					msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
				}
			} else {
				//Server received a synchronizing request message from another server
				if (writeFile(fileDir, data)) {
					msgOut.setCommand("success");
				} else {
					msgOut.setCommand("failure");
				}				
			}					
		} else if(cmd.equalsIgnoreCase("seek")) {
			File file = new File(fileDir);
			
			if (file.exists()) {
				int cursor = seekFile(file, data);
				if (cursor != -1) {
					msgOut.setCursorLoc(cursor);
					msgOut.setData("Cursor is moved to index " + cursor + " in " + fileName);
				} else {
					msgOut.setData("The cursor exceeds file \"" + fileName + "\"'s lengths!");
				}					
			} else {
				msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
			}
		} else if(cmd.equalsIgnoreCase("read")) {
			File file = new File(fileDir);
			
			if (file.exists()) {
				String read = readFile(file, data, cursorLoc);
				msgOut.setData(read);
			} else {
				msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
			}
		} else if (cmd.equalsIgnoreCase("terminate")) {
			msgOut.setData("The Session is going to be closed!");
			msgOut.setCommand("terminate");
		} else {
			msgOut.setData("Command does not exist! Try again!");
		}
		
		return msgOut;
	}
	
	private static boolean createFile (String fileDir) {
		File file = new File(fileDir);
		System.out.println(fileDir);
		
		//If file does not exist
		if (file.exists() == false) {
			File parentFile = file.getParentFile();			
			//Create dir if the dir does not exist.
			if (parentFile.exists() == false) {
				parentFile.mkdirs();
			}			
			try {
				file.createNewFile();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Cannot create file!");
			}			
		}
		
		//If not created successfully, then return FALSE
		return false;	
	}
	
	private static boolean deleteFile (String fileDir) {
		File file = new File(fileDir);
		
		if (file.exists()) {
			file.delete();
			return true;
		} 
		
		return false;
	}
	
	private static boolean writeFile (String fileDir, String data) {		
		File file = new File(fileDir);
		
		if (file.exists()) {
			try {
				//Set <append> to TRUE
				OutputStream outStream = new FileOutputStream(file, true);
				outStream.write(data.getBytes());
				outStream.close();
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Cannot find the file...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Cannot write the data in...");
			}
		}
		
		return false;
	}
	
	private static int seekFile (File file, String data) {
		int cursorLoc = Integer.parseInt(data);
		int fileLength = (int) file.length();
			
		if (cursorLoc <= fileLength)
			return cursorLoc;
		else
			return -1;	//If seek file failed (exceeds the file length), return -1
	}
	
	private static String readFile (File file, String data, int cursorLoc) {
		int reqLength = Integer.parseInt(data);
		int fileLength = (int) file.length();
		
		if ((reqLength + cursorLoc) <= fileLength) {
			InputStream inStream;
			try {
				inStream = new FileInputStream(file);
				char [] out = new char [reqLength];
				int index = 0;
				for (int i = 0; i < fileLength; i++) {
					if ((i >= cursorLoc) && (i < cursorLoc + reqLength)) {
						out[index ++] = (char) inStream.read();
					} else {
						inStream.read();	//Move on to next byte
					}
				}
				inStream.close();
				return String.valueOf(out);	//Cast char into String
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return "Reading exceeds the maximum file length [" + fileLength + "]!";
		}
		
		//There is some problem about this final returned value
		return null;
	}
	
	private static void forwardMessage (Message msgIn) {	//perhaps I can return a <foward succeed/fail> info?
		int serverID = msgIn.getServerID();
		int serverAmount = 4;
		
		for (int sID = 1; sID < serverAmount; sID ++) {
			if (sID != serverID) {
				try {
					Socket s2sSocket = new Socket(serverIPAddr[sID], serverPortNum);
					ObjectOutputStream out = new ObjectOutputStream(s2sSocket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(s2sSocket.getInputStream());
					
					Message msgS2SOut = msgIn;
					msgS2SOut.setServer2Server(true);
					msgS2SOut.setClientID(serverID);
					msgS2SOut.setSeverID(sID);
					out.writeObject(msgS2SOut);
					
					Message msgS2SIn;
					String reply;
					//I think in.readObject() will wait until it reads an object
					if ((msgS2SIn = (Message) in.readObject()) != null) {	
						if ((reply = msgS2SIn.getCommand()) != null && reply.equals("success")) {
							System.out.println("server[" + sID + "] synchronized the command!");
						} else {
							System.out.println("server[" + sID + "] failed to synchronize the command!");
						}
					}
					in.close();
					out.close();
					s2sSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Cannot connect to server[" + sID + "]");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Cannot read from server[" + sID + "]");
				}
			}
		}
	}
}
