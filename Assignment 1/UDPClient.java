/**
Aleksas Murauskas 260718389
Jacob McConnell

316 Assignment 1
Java Client TCP

*/
import java.io.*;
import java.net.*;

class UDPClient {

	public static void main(String argv[]) throws Exception{
		String sentence;
		String modifiedSentence;
		//Create Input Stream
		BufferedReader inFromUser =new BufferedReader(new InputStreamReader(System.in));
		//Create client socket
		DatagramSocket = new DatagramSocket();
		//Translate hostname to IP address using DNS
		InetAddress IPAddress =InetAddress.getByName("hostname");
		byte[] sendData =new byte[1024];
		byte[] recieveData = new byte[1024];

		String sentence = inFromUser.readLine();
		sendData =sentence.getBytes();
		//Create datagram with data-to-send, length, IP addr, port
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,9876);
		//Send datagram to server
		clientSocket.send(sendPacket);
		DatagramPacket recievePacket = new DatagramPacket(recieveData,recieveData.length);
		//Read datagram from server
		clientSocket.recieve(recievePacket);
		DatagramPacket recievePacket = new DatagramPacket(recieveData,recieveData.length);
		//Read datagram from server
		clientSocket.recieve(recievePacket);
		String modifiedSentence = new String(recievePacket.getData());
		System.out.println("FROM SERVER:"+modifiedSentence);
		clientSocket.close();
	}
}