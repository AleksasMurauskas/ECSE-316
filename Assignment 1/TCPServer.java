/**
Aleksas Murauskas 260718389
Jacob McConnell

316 Assignment 1
Java Server TCP

*/
import java.io.*;
import java.net.*;

class TCPServer{
	public void main(String argv[]) throws Exception{
		String clientSentence;
		String capitalizedSentence;
		//Create welcoming socket at port 6789
		while(true){
			// Wait on wlecoming socket for contact by client
			Socket connectionSocket=welcomeSocket.accept();
			//Create Input stream, attached to socket
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			//Create output Stream, attached to socket
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			//read in line from socket 
			clientSentence=inFromClient.readLine();
			capitalizedSentence =clientSentence.toUpperCase() + '\n';
			//Write out line to socket
			outToClient.writeBytes(capitalizedSentence);
		}
		//loop to wait for a new socket connection
	}
}