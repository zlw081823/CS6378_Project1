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
	private boolean server2server;
	
	public Message() {
		// TODO Auto-generated constructor stub
		this.command = null;
		this.fileName = null;
		this.data = null;
		this.clientID = 0;
		this.serverID = 0;
		this.cursorLoc = 0;
		this.server2server = false;	//By default, the massage is sent between client to server
	}
	
	public Message(int clientID, int serverID) {
		// TODO Auto-generated constructor stub
		this.command = null;
		this.data = null;
		this.fileName = null;
		this.clientID = clientID;
		this.serverID = serverID;
		this.cursorLoc = 0;
		this.server2server = false;
		
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
					input = in.split(" ", 3);	//If there's less than 3 divided string, then the total length of the string would be less than 2
					//example: "linwei z" will result in [input.length = 2]
					//So now I can choose my command based on the length of the string
					if (input.length == 1) {
						this.command = input[0];
					} else if (input.length == 2) {
						this.command = input[0];
						this.fileName = input[1];
					} else {
						this.command = input[0];
						this.fileName = input[1];
						this.data = input[2];
					}
//					this.command = input[0];
//					if (!this.command.equalsIgnoreCase("terminate")){
//						//problem - what if enter something else? ex, shutup?
//						this.fileName = input[1];
//						if ((!this.command.equalsIgnoreCase("create") && (!this.command.equalsIgnoreCase("delete"))))
//							this.data = input[2];
//					}

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
	
//	get/set serverID
	public void setSeverID (int serverID) {
		this.serverID = serverID;
	}
	
	public int getServerID () {
		return this.serverID;
	}
	
//	get/set cursor location
	public void setCursorLoc (int cursorLoc) {
		this.cursorLoc = cursorLoc;
	}
	
	public int getCursorLoc () {
		return this.cursorLoc;
	}
	
//	get/set flag_massage sent from client to server
	public void setServer2Server (boolean server2server) {
		this.server2server = server2server;
	}
	
	public boolean isServer2Server () {
		return this.server2server;
	}
}
