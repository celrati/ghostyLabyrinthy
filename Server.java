import java.io.*;
import java.net.*;
import java.util.*;

class Server{
	public static void main(String[] args){
		try{
			ServerSocket server=new ServerSocket(4444); // the socket of the server
			System.out.println(" ¤ Server On v0.1 by Charif and tuan ¤");
			List<Game> gameList = new ArrayList<Game>();
			
			gameList.add(new Game((short)0,(short)0));
			gameList.get(0).addNewPlayer("luffy","123");
			gameList.get(0).addNewPlayer("zoro","234");
			gameList.get(0).addNewPlayer("sanji","345");
			
			while(true){
				Socket socket=server.accept();
				System.out.println("A new Player is Online !");
				ServiceServer service =new ServiceServer(socket,gameList);

				Thread t = new Thread(service);
				t.start();
				//System.out.println("hello");
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
	}
}
