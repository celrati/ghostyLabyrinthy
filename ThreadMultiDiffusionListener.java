import java.io.*;
import java.net.*;
import java.util.*;


class ThreadMultiDiffusionListener implements Runnable{
	public String ip;
	public short port;
	public ThreadState th;

	public ThreadMultiDiffusionListener(String ip,short port, ThreadState th){
		this.ip = ip;
		this.port = port;

		this.th = th;
	}
	public void run(){
		try{	
			MulticastSocket mso = new MulticastSocket(this.port);
			String new_ip = "";
			for(int i=0;i<this.ip.length();i++){
				if(this.ip.charAt(i) == '#') break;
				new_ip += this.ip.charAt(i);
			}
			System.out.println("trying to join the ip :"+new_ip+" and port :"+this.port);
			//Thread.sleep(100);

			mso.joinGroup(InetAddress.getByName(this.ip));

			System.out.println("###--");
			byte[] data = new byte[64];
			DatagramPacket paquet = new DatagramPacket(data,data.length);
			System.out.println("The thread of multidiffusion is now running ...");
			while(true){
				if(this.th.a == 1) break;
				mso.receive(paquet);
				// processing the message
				String st = new String(paquet.getData(),0,paquet.getLength());
				if(st.charAt(0) != 'F') System.out.println(st);
				
				if(st.substring(0,3).equals("END")){
					th.stateGame = "END";
					System.out.println("The game is finished thanks for playing goodbye !!");
				}
			}
		}catch(Exception e){

		}
	}
}