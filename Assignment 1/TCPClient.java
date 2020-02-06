/**
Aleksas Murauskas 260718389
Jacob McConnell

316 Assignment 1
Java Client TCP

*/
import java.io.*;
import java.net.*;

class TCPClient {

	public static void main(String argv[]) throws Exception{
		String sentence;
		String modifiedSentence;
		//Create Input Stream
		BufferedReader inFromUser =new BufferedReader(new InputStreamReader(System.in));
		//Create client socket, connect to server
		Socket clientSocket = new Socket("hostname",6789);
		//Create output stream attached to socket
		DataOutgputStream outToServer=new DataOutgputStream(clientSocket.getOutputStream());
		//Create input stream attached to socket
		BufferedReader inFromServer = new BufferedReader(new inputStreamReader(clientSocket.getInputStream()));
		sentence =inFromUser.readLine();
		//send line to server
		outToServer.writeBytes(sentence+'\n');
		//read line from server
		modifiedSentence=inFromServer.readLine();
		System.out.println("FROM SERVER:"+modifiedSentence);
		clientSocket.close;
	}
}