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
			
			Message msgFromClient = new Message(0000);
			Message msgToClient = new Message(0000);
			
			try {
				if ((msgFromClient = (Message) in.readObject()) != null) {
					msgToClient = cmdHandler(msgFromClient);
					out.writeObject(msgToClient);
//					if((msgToClient.getCommand() != null) && msgToClient.getCommand().equalsIgnoreCase("terminate")){
//						break;
//					}
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
		
		String dirName = "/tmp/user/java/bin";
		int cursorLoc = msgIn.getCursorLoc();	
		msgOut.setCursorLoc(cursorLoc);	//keep the cursor location updated
		int fileSize = getFileSize(dirName + "/" + fileName);
		
		if(cmd.equalsIgnoreCase("create")) {
			File d = new File(dirName);
			d.mkdirs();
			try {
				File file = new File(dirName + "/" + fileName);
				file.createNewFile();
				msgOut.setData("\"" + fileName + "\"" + " was created!");
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println("No file is created!");
				msgOut.setData("Create file " + "\"" + fileName + "\"" + "failed!");
			}
		} else if(cmd.equalsIgnoreCase("delete")) {
			if(isFileExist(dirName + "/" + fileName)){
				try {
					File file = new File(dirName + "/" + fileName);
					file.delete();
					msgOut.setData("\"" + fileName + "\"" + " is deleted!");
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("No file is deleted!");
					msgOut.setData("Delete file " + "\"" + fileName + "\"" + "failed!");
				}					
			} else {
				msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
			}
		
		} else if(cmd.equalsIgnoreCase("write")) {
			if(isFileExist(dirName + "/" + fileName)){
				try {
					//using FileOutputStream(File file, boolean append) 
					//different constructor works!
					OutputStream outStream = new FileOutputStream(dirName + "/" + fileName, true);
					outStream.write(data.getBytes());
					outStream.close();
					msgOut.setData("A string is written in " + fileName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Cannot find the file!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Cannot read the input data!");
				}				
			} else {
				msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
			}

			
		} else if(cmd.equalsIgnoreCase("seek")) {
			if(isFileExist(dirName + "/" + fileName)) {
				int cursor = Integer.parseInt(msgIn.getData());
				if (cursor > fileSize) {
					msgOut.setData("Cursor exceeds the file length!");
				} else {
					cursorLoc = cursor;
					msgOut.setCursorLoc(cursorLoc);
					msgOut.setData("Cursor is moved to index " + cursorLoc + " in " + fileName);
				}
			} else {
				msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
			}
			
		} else if(cmd.equalsIgnoreCase("read")) {
			if(isFileExist(dirName + "/" + fileName)) {
				try {
					InputStream inStream = new FileInputStream(dirName + "/" + fileName);
					fileSize = inStream.available();
					int reqLength = Integer.parseInt(data);
					
					if((reqLength + cursorLoc) <= fileSize) {
						char [] out = new char [reqLength];
						int index = 0;
//						System.out.println("cursorLoc is " + cursorLoc);
						for (int i = 0; i < fileSize; i ++) {
							if ((i >= cursorLoc) && (i < cursorLoc + reqLength)){
								out[index++] = (char)inStream.read();
//								System.out.println("i is " + i);
//								System.out.println("out[" + index + "] :" + out[index - 1]);		
							} else {
								inStream.read();	//to read next byte
							}
						}
						msgOut.setData(String.valueOf(out));
					} else {
						msgOut.setData("Read length is greater than the file length!");
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("File does not exit!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Cannot cast the data (String) to integer!");
				}	
			} else {
				msgOut.setData("failure -- \"" + fileName + "\" does not exist!");
			}	
			
		} else if (cmd.equalsIgnoreCase("terminate")) {
			msgOut.setData("The Session is going to be closed!");
			msgOut.setCommand("terminate");
		} else {
			msgOut.setData("Command does not exist!");
		}
		
		return msgOut;
	}
	
	private static int getFileSize(String filename) {
		File file = new File(filename);
		if(file.exists() && file.isFile()) {
			return (int)file.length();
		} else {
			return 0;
		}
	}
	
	private static boolean isFileExist(String filename) {
		File file = new File(filename);
		
		if(file.exists()) 
			return true;
		else 
			return false;
	}
}
