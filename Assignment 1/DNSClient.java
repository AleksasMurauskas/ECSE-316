
import java.io.*;
import java.net.*;

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


		//Set up 
		DatagramPacket sendPacket =null;
		DatagramPacket recievePacket = null;
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

	}
}