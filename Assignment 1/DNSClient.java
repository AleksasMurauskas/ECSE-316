
import java.io.*;
import java.net.*;
import java.util.Random;
import java.nio.ByteBuffer;


public class DNSClient{

	public static void main(String args[]) throws Exception{
		//First Parse Input following standard below
		//java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name
		//default values are as follows since these inputs are optional 
		int timeout = 5; //Optional
		int maxRetries= 3; //Optional 
		int portVal =53; //Optional 
		String qType ="A"; //Baseline unless changed by mx or ns 

		//needed values 
		String server="";
		int server_pos= args.length-2;
		String name="";
		int name_pos = args.length-1;

		//Begin Parsing Input 
		for(int x=0; x<args.length;x++){ //Loop through all STDIN tokens
			if(args[x].equals("-t")){ //If this appears the next value should hold a timeout timer
				timeout=Integer.parseInt(args[x+1]);
			}
			else if(args[x].equals("-r")){ //If this appears the next value should hold a maximum number of retries 
				maxRetries=Integer.parseInt(args[x+1]);
			}
			else if(args[x].equals("-p")){
				portVal= Integer.parseInt(args[x+1]);
			}
			else if(args[x].equals("-mx")||args[x].equals("-MX")){ //If this appears the Query should be type -MX (Mail Server)
				qType="MX";
			}
			else if(args[x].equals("-ns")||args[x].equals("-NS")){ //If this appears the Query should be type -MX (Name Server)
				qType="NS";
			}
			else if(x==server_pos){//Find Server
				server=args[x];
			}
			else if(x==name_pos){//Find name 
				name=args[x];
			}
		}
		//Parse complete 

		//Now parse server name 
		byte[] ipAddress = new byte[4];
		if(!server.substring(0,1).equals("@")){//Check the first symbol is a @
			System.out.println("Input Error: IP Address has incorrect syntax, missing the @ symbol");
			System.exit(1);
		}
		server = server.substring(1); //Removes the @ from the server
		String[] strIpAddress = server.split("\\.",4); 
		if(strIpAddress.length!=4){
			System.out.println("Input Error: IP Address has incorrect syntax, server have the correct length");
			System.exit(1);
		}

		for(int x=0; x<strIpAddress.length;x++){
			int address_part=0;
			try{
				address_part =Integer.parseInt(strIpAddress[x]); //Parse the 
			} catch(Exception e){
				System.out.println("Input Error: IP Address has incorrect syntax,cannot be parsed as an integer");
				System.exit(1);
			}
			if(address_part<0||address_part>255){ //make sure the number is within the range of a byte
				System.out.println("Input Error: IP Address has incorrect syntax, Part of address out of range");
				System.exit(1);
			}
			try{
				ipAddress[x]= (byte) address_part;//convert the the 
			} catch(Exception e){
				System.out.println("Input Error: IP Address has incorrect syntax, cannot be parsed to an integer");
				System.exit(1);
			}
		}

		//calculate the domain length 
		int domainNameLength=0;
		String[] tokenizedName =name.split("\\.");//Split the name up into tokens to find length
		for(int x=0;x< tokenizedName.length; x++){
			domainNameLength+= tokenizedName[x].length()+1;
		}


		//Set up header
		ByteBuffer header =ByteBuffer.allocate(12);
		header = createHeader(header);

		//Set up the question
		ByteBuffer question =ByteBuffer.allocate(domainNameLength+5);
		question=createQuestion(question);


		ByteBuffer query = ByteBuffer.allocate(12 + domainNameLength + 5);
		query = createQuery(header,question,query);

		//Set up 
		DatagramPacket outgoingPacket =null;
		DatagramPacket incomingPacket = null;

		InetAddress foundAddress =InetAddress.getByAddress(ipAddress);//Find net address
		DatagramSocket clientSocket = new DatagramSocket(); //Create the socket to begin interaction

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sendData=query.array();




		//Begin attempts to send and recieve packets
		//Notify User attempts began
		System.out.println("DNSClient sending request for "+name);
		System.out.println("Server: " + server);
		System.out.println("Request Type: " + qtype);

		long startTime=0, endTime=0;

		int attempt;

		for(attempt=0;attempt<maxRetries;attempt++){
			outgoingPacket= new DatagramPacket(sendData,sendData.length,foundAddress,portVal);
			clientSocket.send(outgoingPacket);
			incomingPacket= new DatagramPacket(receiveData, receiveData.length);
			try{
				startTime=System.currentTimeMillis();
				clientSocket.receive(incomingPacket);
				endTime=System.currentTimeMillis();
			} catch(Exception e){
				continue;
			}
			if(incomingPacket!=null){
				break;
			}
			if(attempt ==maxRetries){
				System.out.println("Communication ERROR: Maximum number of retries " + maxRetries + " reached");
				System.exit(1);
			}
		}
		clientSocket.close();


		byte[] dataRecieved = receivePacket.getData();
		int[] rID = new int[2];
		rID[0] = dataRecieved[0] & 0xff; //response id first half
		rID[1] = dataRecieved[1] & 0xff; //response id second half
		int QR =((dataRecieved[2]>>7)&1)&0xff; //QR bit found
		int AA =((dataRecieved[2]>>2)&1)&0xff; //AA bit found
		int TC=((dataRecieved[2]>>1)&1)&0xff;//TC bit found
		int RD=((dataRecieved[2]>>0)&1)&0xff;//RD bit found
		int RA =((dataRecieved[3]>>7)&1)&0xff;//RA bit found
		int RCODE = dataRecieved[3] & 0x0f; //RCODE byte found
		int QDCOUNT=(short) ((dataRecieved[4] << 8) | (dataRecieved[5] & 0xFF)); // 2 bytes that make up QDCOUNT found
		int ANCOUNT=(short) ((dataRecieved[6] << 8) | (dataRecieved[7] & 0xFF)); // 2 bytes that make up ANCOUNT found
		int NSCOUNT=(short) ((dataRecieved[8] << 8) | (dataRecieved[9] & 0xFF)); // 2 bytes that make up ANCOUNT found
		int NSCOUNT=(short) ((dataRecieved[10] << 8) | (dataRecieved[11] & 0xFF)); // 2 bytes that make up ANCOUNT found
		//Check header values to see if packet is valid 
		if (QR != 1) {
			System.out.println("Packet ERROR: Recieved packet is not a response.");
			System.exit(1);
		}
		if(RA != 1){
			System.out.println("Packet ERROR: Created Server does not support recursion"); 
		}
		boolean auth=false;
		if (AA == 1) {//Checks if packet is authoratative 
			auth = true;
		}
		String authorization;
		if(auth){
			authorization="auth";
		}
		else{
			authorization="nonauth";
		}
		//Check for Error Codes
		//RCODE =0 then there were no errors found
		if(RCODE==5){
			System.out.println("Refused: the name server refuses to perform the requested operation for policy reasons");
			System.exit(1);
		}
		else if(RCODE==1){
			System.out.println("Format ERROR: The name server was unable to interpret the query");
			System.exit(1);
		}
		else if(RCODE==2){
			System.out.println("Server FAILURE: the name server was unable to process this query due to a problem with the name server");
			System.exit(1);
		}
		else if(RCODE==3){
			System.out.println("Name ERROR: meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist");
			System.exit(1);
		}
		else if(RCODE==4){
			System.out.println("Not Implemented: the name server does not support the requested kind of query");
			System.exit(1);
		}

		System.out.println("Response packet received after " + ((endTime - startTime) / 1000.0) + " seconds and "+attempt+" retries");
		
	}

	public ByteBuffer createHeader(ByteBuffer header){
		byte[] qID = new byte[2]; //Create a randomized 
		new Random().nextBytes(qID);
		header.put(qID); //Randomly generated Id number
		header.put((byte) 0x01); // header line 2 (QR, OPcode, AA, TC, RD, RA, Z, RCODE)
		header.put((byte) 0x00); // buffer line 2
		header.put((byte) 0x00); // buffer qd
		header.put((byte) 0x01); // qd set to 1
		return header;
	}

	public ByteBuffer createQuestion(ByteBuffer question, String[] tokenizedName, String type){
		//First we create QName
		for(int x; x<tokenizedName.length;x++){
			question.put((byte) tokenizedName[x].length());
			for(int y=0; y<tokenizedName[x].length;y++){
				question.put((byte) ((int)tokenizedName[x].charAt(y)));
			}
		}
		question.put((byte) 0x00); //Termination bit for QName
		question.put((byte) 0x00); //Buffer before QType

		//Create QType
		if(type.equals("A")){ //Record the DNS type
			question.put((byte) 0x0001);
		}
		else if(type.equals("MX")){
			question.put((byte) 0x000f);
		}
		else{
			question.put((byte) 0x0002);
		}
		question.put((byte) 0x0000); //buffer
		question.put((byte) 0x0001); 
		return question;
	}

	public ByteBuffer createQuery(ByteBuffer header, ByteBuffer question, ByteBuffer query){
		//Build the query with the header and question
		query.put(header.array()); 
		query.put(question.array());
		return query;
	}
}