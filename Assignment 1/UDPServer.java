/**
Aleksas Murauskas 260718389
Jacob McConnell

316 Assignment 1
Java Server UDP

*/
import java.io.*;
import java.net.*;

class UDPServer{
	public static void public static void main(String[] args) {
		//Create datagram socket at Port 9876
		DatagramSocket serverSocket =new DatagramSocket(9876);
		byte[] sendData =new byte[1024];
		byte[] recieveData = new byte[1024];
		while(true){
			//Create space for recieved datagram 
			DatagramPacket recievePacket=new DatagramPacket(recieveData, recieveData.length);
			//Recieve Datagram
			serverSocket.recieve(recievePacket);
			String sentence = new String(recievePacket.getData());
			//Get IP address port # of sender
			InetAddress IPAddress = recievePacket.getAddress();
			int port = recievePacket.getPort();
			String capitaizedSentence =sentence.toUpperCase();
			sendData = capitaizedSentence.getBytes();
			//Create datagram to send to client 
			DatagramPacket sendPacket =new DatagramPacket(sendData,sendData.length,IPAddress,port);
			//Write out datagram socket
			serverSocket.sendSocket(sendPacket);
		}
		//End of while loop, loop back and wait for another datagram
	}
}