import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public class Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String command;
	private String data;
	private String fileName;
	private int clientID;
	private int serverID;
	private int cursorLoc;
	
	public Message() {
		// TODO Auto-generated constructor stub
		this.command = null;
		this.fileName = null;
		this.data = null;
		this.clientID = 0000;
		this.serverID = 0000;
		this.cursorLoc = 0;
	}
	
	public Message(int serverID) {
		// TODO Auto-generated constructor stub
		this.command = null;
		this.fileName = null;
		this.data = null;
		this.clientID = 0000;
		this.serverID = serverID;
		this.cursorLoc = 0;
	}
	
	public Message(int clientID, int serverID) {
		// TODO Auto-generated constructor stub
		this.command = null;
		this.data = null;
		this.fileName = null;
		this.clientID = clientID;
		this.serverID = serverID;
		this.cursorLoc = 0;
		
		inputSelection();
	}
	
	private void inputSelection () {
		System.out.println("Please enter your command: ...");
		System.out.println("1. Create <filename>");
		System.out.println("2. Seek <filename> <index>");
		System.out.println("3. Read <filename> [<length>]");
		System.out.println("4. Write <filename> <String>");
		System.out.println("5. Delete <filename>");
		System.out.println("6. Terminate");
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String in;
		String[] input = new String[3];
		while (true) {
			try {
				if ((in = stdIn.readLine()) != null) {
					input = in.split(" ", 3);
					this.command = input[0];
					if (!this.command.equalsIgnoreCase("terminate")){
						//problem - what if enter something else? eg, shutup?
						this.fileName = input[1];
						if ((!this.command.equalsIgnoreCase("create") && (!this.command.equalsIgnoreCase("delete"))))
							this.data = input[2];
					}

					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Create Message failed!");
			}
		}
	}
//	set/get command
	public void setCommand (String command) {
		this.command = command;
	}
	
	public String getCommand () {
		return this.command;
	}

//	get/set file name
	public void setFileName (String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName () {
		return this.fileName;
	}
	
//	get/set data
	public void setData (String data) {
		this.data = data;
	}
	
	public String getData () {
		return this.data;
	}
	
//	get/set clientID
	public void setClientID (int clientID) {
		this.clientID = clientID;
	}
	
	public int getClientID () {
		return this.clientID;
	}
	
//	get/set clientID
	public void setCursorLoc (int cursorLoc) {
		this.cursorLoc = cursorLoc;
	}
	
	public int getCursorLoc () {
		return this.cursorLoc;
	}
}
