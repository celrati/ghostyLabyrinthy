import java.io.*;
import java.net.*;
import java.util.*;

class ThreadUDPMessageListener implements Runnable{
	public String ip;
	public String port;

	ThreadUDPMessageListener(String ip, String port){
		this.ip = ip;
		this.port = port;
	}
	public void run(){
		try{

			DatagramSocket dso = new DatagramSocket(Integer.parseInt(port));
			System.out.println("Thread of receiving udp message from players is ON  :");
			System.out.println("my ip address is : "+ip);
			System.out.println("my port  is : "+port);

			byte[] data = new byte[256];
			DatagramPacket paquet= new DatagramPacket(data,data.length);
			while(true){
				dso.receive(paquet);
				String s = new String(paquet.getData(),0,paquet.getLength() );
				System.out.println("you received new message !!!!");
				System.out.println(s);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}