import java.io.*;
import java.net.*;
import java.util.*;

class ServiceServer implements Runnable{
	public Socket socket;
	public List<Game> gamelist;
	public Player player;

	public ServiceServer(Socket s,List<Game> gamelist){
		this.socket = s;
		this.gamelist = gamelist;
		this.player = new Player();
	}
	public static boolean isTheMachineIsWorkingWithBigEndian(){
		short n = 1;
		if((byte)n == 1) return true;
		return false;
	}
	public void run(){
		try{
			int c = 0;
			String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
			player.ip = ip;
			System.out.println("client with IP :"+ip+" is now connect");
			sendMSG1(this.socket,(short)this.gamelist.size(), this.gamelist);
			while(true){
				while(true){
					DataInputStream d = new DataInputStream(socket.getInputStream());
					byte[] message = new byte[64];

					if( d.read(message) == -1 ){
						System.out.println("the client close his terminal");
						break;
					}
					processMSG(socket,this.gamelist,this.player,message);
					if(player.state == "STARTING") break;
				}
			// to the next step of the game


			//while(true){
				System.out.println("Now the player :"+player.id+" isready to start the game : "+player.num_game);
				System.out.print("waiting the others players to start ... ");

				while(!gamelist.get(player.num_game).state_game.equals("READY_TO_START")){
					System.out.println("WAITING PLAYERS !!!!! to start plz wait");
				}
				System.out.println("Okay everyone is ready to start");
				System.out.println("Starting the game...");
				try{
					Thread.sleep(10);
				}catch(Exception e){
					System.out.println(e);
					e.printStackTrace();			
				}
				gamelist.get(player.num_game).labyrinthe.stateGame = "RUN";
				gamelist.get(player.num_game).addPlayerToTheReadyList(player);

				try{
					Thread.sleep(1);
				}catch(Exception e){}

				sendWelcomeMSG(socket,gamelist,player);
				// the num of game is in player var
				// we have to init the laby
				// pos the players in the laby
				// fantom  int he laby
				// gamelist.get(player.num_game) // to get the game
				// player.x y score
				// send the pos xy
				sendPosWelcome(socket,gamelist,player);
				player.score = 0;

				// now the game begin..
				//System.out.println("aa");
				
				while(true){
					//System.out.println("bb");
					
					//System.out.println("cc");
					processMSGGAME(socket,gamelist,player);
					if(player.state == "LEFT_GAME") break;
					if(this.gamelist.get(player.num_game).labyrinthe.stateGame == "END"){
						System.out.println("GAME OVER !! restarting...");
						break;
					}
				}

				if(c == 1) break;
			}


			//}

			socket.close();
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
	public static void processMSGGAME(Socket s,List<Game> gamelist,Player p){
		try{
			DataInputStream d = new DataInputStream(s.getInputStream());
			DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
			byte[] ms= new byte[64];
			d.read(ms);
			
			if(ms[0] == (byte)'R' && ms[1] == (byte)'I' && ms[2] == (byte)'G' && ms[3] == (byte)'H' && ms[4] == (byte)'T'){
				System.out.println("player want to go right");
				String p_s ="";
				for(int i=6; i< 9;i++){
					p_s += (char)ms[i];
				}
				System.out.println("processing now the right mov!!");
				
				gamelist.get(p.num_game).labyrinthe.movePlayer(p,"RIGHT",p_s,s);
			}
			if(ms[0] == (byte)'L' && ms[1] == (byte)'E' && ms[2] == (byte)'F' && ms[3] == (byte)'T'){
				System.out.println("player want to go left");
				String p_s ="";
				for(int i=5; i< 8;i++){
					p_s += (char)ms[i];
				}
				gamelist.get(p.num_game).labyrinthe.movePlayer(p,"LEFT",p_s,s);
			}
			if(ms[0] == (byte)'D' && ms[1] == (byte)'O' && ms[2] == (byte)'W' && ms[3] == (byte)'N'){
				System.out.println("player want to go down");
				String p_s ="";
				for(int i=5; i< 8;i++){
					p_s += (char)ms[i];
				}
				gamelist.get(p.num_game).labyrinthe.movePlayer(p,"DOWN",p_s,s);
			}
			if(ms[0] == (byte)'U' && ms[1] == (byte)'P'){
				System.out.println("player want to go up");
				String p_s ="";
				for(int i=3; i< 6;i++){
					p_s += (char)ms[i];
				}
				gamelist.get(p.num_game).labyrinthe.movePlayer(p,"UP",p_s,s);
			}
			if(ms[0] == (byte)'Q' && ms[1] == (byte)'U' && ms[2] == (byte)'I' && ms[3]== (byte)'T'){
				System.out.println("THE player :"+p.id+" want to quit the game :( !!");
				p.state = "LEFT_GAME";
				byte[] m = new byte[6];
				m[0] = (byte)'B';
				m[1] = (byte)'Y';
				m[2] = (byte)'E';
				m[3] = (byte)'*';
				m[4] = (byte)'*';
				m[5] = (byte)'*';
				d1.write(m);
				System.out.println(" done THE player :"+p.id+"is out :( :( goodbye dear player");
			} 
			if(ms[0] == (byte)'G' && ms[1] == (byte)'L' && ms[2] == (byte)'I' && ms[3] == (byte)'S' && ms[4] == (byte)'T'){
				short ss = 0;
				for(int i=0;i<gamelist.get(p.num_game).labyrinthe.listPlayer.size() ; i++){
					if(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).state != "LEFT_GAME") ss++;
				}
				byte[] mss = new byte[12];
				mss[0] = (byte)'G';
				mss[1] = (byte)'L';
				mss[2] = (byte)'I';
				mss[3] = (byte)'S';
				mss[4] = (byte)'T';
				mss[5] = (byte)'!';
				mss[6] = (byte)' ';
				byte b1 = (byte)ss;
				byte b2 = (byte)(ss >> 8);
				if(isTheMachineIsWorkingWithBigEndian()){
					mss[7] = b1;
					mss[8] = b2;
				}else{
					mss[7] = b2;
					mss[8] = b1;
				}
				mss[9] = (byte)'*';
				mss[10] = (byte)'*';
				mss[11] = (byte)'*';
				d1.write(mss);
				// now send sthe list of players
				for(int i=0;i<gamelist.get(p.num_game).labyrinthe.listPlayer.size() ; i++){
					byte[] m = new byte[64];
					if(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).state != "LEFT_GAME"){
						m[0] = (byte)'G';
						m[1] = (byte)'P';
						m[2] = (byte)'L';
						m[3] = (byte)'A';
						m[4] = (byte)'Y';
						m[5] = (byte)'E';
						m[6] = (byte)'R';
						m[7] = (byte)' ';
						int j;
						for(j=0;j<gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).id.length() ;  j++){
							m[8 + j] = (byte)gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).id.charAt(j);
							System.out.print(m[8+j]);
						}
						j += 9;
						String x_1 = Short.toString(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).x);
						String y_1 = Short.toString(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).y);
						String p_1 = Short.toString(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).score);

						System.out.println(x_1);
						System.out.println(y_1);
						System.out.println(p_1);

						String new_x = "";
						String new_y = "";
						String new_p = "";

						if(x_1.length() == 1){
							new_x = "00"+x_1;
						}
						if(x_1.length() == 2){
							new_x = "0"+x_1;
						}						
						if(y_1.length() == 1){
							new_y = "00"+y_1;
						}
						if(y_1.length() == 2){
							new_y = "0"+y_1;
						}	

						if(p_1.length() == 1){
							new_p = "000"+p_1;
						}
						if(p_1.length() == 2){
							new_p = "00"+p_1;
						}
						if(p_1.length() == 3){
							new_p = "0"+p_1;
						}
						System.out.println(new_x);
						System.out.println(new_y);
						System.out.println(new_p);

						m[j++] = (byte)' ';
						m[j++] = (byte)new_x.charAt(0);
						m[j++] = (byte)new_x.charAt(1);
						m[j++] = (byte)new_x.charAt(2);
						m[j++] = (byte)' ';
						m[j++] = (byte)new_y.charAt(0);
						m[j++] = (byte)new_y.charAt(1);
						m[j++] = (byte)new_y.charAt(2);
						m[j++] = (byte)' ';
						m[j++] = (byte)new_p.charAt(0);
						m[j++] = (byte)new_p.charAt(1);
						m[j++] = (byte)new_p.charAt(2);
						m[j++] = (byte)new_p.charAt(3);
						m[j++] = (byte)'*';
						m[j++] = (byte)'*';
						m[j++] = (byte)'*';
						d1.write(m);
					}
				}

			}if(ms[0] == (byte)'A' && ms[1] == (byte)'L' && ms[2] == (byte)'L' && ms[3] == (byte)'?'){
				System.out.println("the player "+p.id+" want to send a message to all players !!");
				byte[] ms1 = new byte[256];
				ms1[0] = (byte)'M';
				ms1[1] = (byte)'E';
				ms1[2] = (byte)'S';
				ms1[3] = (byte)'A';
				ms1[4] = (byte)' ';
				int i;
				for(i=0;i<p.id.length() ;i++){
					ms1[5 + i] = (byte)p.id.charAt(i);
				}
				
				String mess = "";
				for(int x = 5; x < ms.length ; x++){
					if(ms[x] == (byte)'*')break;
					mess += (char)ms[x];
				}
				i += 5;
				ms1[i++] = (byte)' ';
				int j;
				for(j=0;j<mess.length();j++){
					ms1[i++] = (byte)mess.charAt(j);
				}
				ms1[i++] = (byte)'+';
				ms1[i++] = (byte)'+';
				ms1[i++] = (byte)'+';
				byte[] bymsg = new byte[7];
				bymsg[0] = (byte)'A';
				bymsg[1] = (byte)'L';
				bymsg[2] = (byte)'L';
				bymsg[3] = (byte)'!';
				bymsg[4] = (byte)'*';
				bymsg[5] = (byte)'*';
				bymsg[6] = (byte)'*';
				d1.write(bymsg);


				gamelist.get(p.num_game).labyrinthe.sendAllMessage(ms1);

			}
			if(ms[0] == (byte)'S' && ms[1] == (byte)'E' && ms[2] == (byte)'N'&&  ms[3] == (byte)'D' && ms[4] == (byte)'?'){
				String ip_of_id = "";
				String port_of_id = "";
				int ok = 0;
				String id = "";
				String message = "";

				int i;
				for(i =6;i<ms.length;i++){
					if(ms[i] == (byte)' ') break;
					id += (char)ms[i];
				}
				for(int j= i+1; j<ms.length ;j++){
						if(ms[j] == (byte)'*') break;
					message += (char)ms[j];
				}

				System.out.println("The player "+p.id+" want to send a message to player "+id);
				System.out.println("the message is :"+message);

				for(i=0;i<gamelist.get(p.num_game).labyrinthe.listPlayer.size();i++){
					System.out.println(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).id);

					if(gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).id.equals(id)){
						System.out.print("found the player"+id);
						ok = 1;
						ip_of_id = gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).ip;
						port_of_id = gamelist.get(p.num_game).labyrinthe.listPlayer.get(i).port;
						break;

					}
				}
				if(ok == 1){
					// send the message
					String m = "MESP "+p.id+" "+message+"+++";
					String m1 = "SEND!***";
					byte[] MS1 = m1.getBytes();
					d1.write(MS1);
					byte[] MS2 = m.getBytes();
					//
					DatagramSocket dso = new DatagramSocket();
					DatagramPacket paquet = new DatagramPacket(MS2,MS2.length,InetAddress.getByName(ip_of_id),Short.parseShort(port_of_id));
					dso.send(paquet);
				}else{
					// send nosend
					String m1 = "NOSEND***";
					byte[] m = m1.getBytes();
					d1.write(m);

				}
			}

		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
	}
	public static void sendPosWelcome(Socket s,List<Game> gamelist,Player player){
		// xy on 3byte chaine
		// [POS␣id␣x␣y***]
		try{
			DataOutputStream d = new DataOutputStream(s.getOutputStream());
			byte[] MSG = new byte[23];
			MSG[0] = (byte)'P';
			MSG[1] = (byte)'O';
			MSG[2] = (byte)'S';
			MSG[3] = (byte)' ';
			int i;
			for(i=0;i<player.id.length();i++){
				MSG[4+i] = (byte)player.id.charAt(i);
				System.out.println((char)MSG[4+i]);
			}
			i = (i++) + 4;
			MSG[i++] = (byte)' ';
			String x = Short.toString(player.x);
			String y = Short.toString(player.y);
			String new_x = "";
			String new_y = "";

			if(x.length() == 1){
				new_x = "00"+x;
			}
			if(x.length() == 2){
				new_x = "0"+x;
			}
			if(y.length() == 1){
				new_y = "00"+y;
			}
			if(y.length() == 2){
				new_y = "0"+y;
			}
			MSG[i++] = (byte)new_x.charAt(0);
			MSG[i++] = (byte)new_x.charAt(1);
			MSG[i++] = (byte)new_x.charAt(2);
			MSG[i++] = (byte)' ';
			MSG[i++] = (byte)new_y.charAt(0);
			MSG[i++] = (byte)new_y.charAt(1);
			MSG[i++] = (byte)new_y.charAt(2);
			MSG[i++] = (byte)'*';
			MSG[i++] = (byte)'*';
			MSG[i++] = (byte)'*';
			d.write(MSG);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();	
		}

		
	}
	public static void sendMSG1(Socket s,short n, List<Game> gamelist){
	 // MSG is in 11 bytes
		byte[] MSG1 = new byte[11];
		// [GAMES n***]
		MSG1[0] = (byte)'G'; 
		MSG1[1] = (byte)'A'; 
		MSG1[2] = (byte)'M'; 
		MSG1[3] = (byte)'E'; 
		MSG1[4] = (byte)'S'; 
		MSG1[5] = (byte)' ';
		////////////////////// we do the little endiant thing here
		byte b1 = (byte)n;
		byte b2 = (byte)(n >> 8);
		if(isTheMachineIsWorkingWithBigEndian()){
			MSG1[6] = b1;
			MSG1[7] = b2;
		}else{
			MSG1[6] = b2;
			MSG1[7] = b1;
		}

		////////////////////////////////////////////////////
		MSG1[8] =  (byte)'*';
		MSG1[9] =  (byte)'*';
		MSG1[10] = (byte)'*';
		///////////////// we send the bytes
		try{
			DataOutputStream d = new DataOutputStream(s.getOutputStream());
			d.write(MSG1);

			System.out.println("Sending list of games to a client ...");
			for(int i=0; i<n; i++){
				byte[] sub_MSG1 = new byte[13];
				sub_MSG1[0] = (byte)'G';
				sub_MSG1[1] = (byte)'A';
				sub_MSG1[2] = (byte)'M';
				sub_MSG1[3] = (byte)'E';
				sub_MSG1[4] = (byte)' ';
				byte b3 = (byte)(gamelist.get(i).num_game);
				byte b4 = (byte)(gamelist.get(i).num_game >> 8 );
				if(isTheMachineIsWorkingWithBigEndian()){
					sub_MSG1[5] = b1;
					sub_MSG1[6] = b2;
				}else{
					sub_MSG1[5] = b2;
					sub_MSG1[6] = b1;
				}

				sub_MSG1[5] = b3;
				sub_MSG1[6] = b4;
				sub_MSG1[7] = (byte)' ';
				byte b5 = (byte)(gamelist.get(i).nb_joueurs);
				byte b6 = (byte)(gamelist.get(i).nb_joueurs >> 8);
				if(isTheMachineIsWorkingWithBigEndian()){
					sub_MSG1[5] = b5;
					sub_MSG1[6] = b6;
				}else{
					sub_MSG1[5] = b6;
					sub_MSG1[6] = b5;
				}

				sub_MSG1[10] = (byte)'*';
				sub_MSG1[11] = (byte)'*';
				sub_MSG1[12] = (byte)'*';

				try{
					Thread.sleep(1);
				}catch(Exception e){
					System.out.println(e);
					e.printStackTrace();	
				}

				d.write(sub_MSG1);
				System.out.println("send");
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}

	}
	public static void sendWelcomeMSG(Socket s,List<Game> gamelist,Player player){
		//[W E L C O M E ␣ m ␣ h ␣ w ␣ f ␣ ip ␣ port ***]
		try{
			DataOutputStream d = new DataOutputStream(s.getOutputStream());
			byte[] MSGW = new byte[43];
			MSGW[0] = (byte)'W';
			MSGW[1] = (byte)'E';
			MSGW[2] = (byte)'L';
			MSGW[3] = (byte)'C';
			MSGW[4] = (byte)'O';
			MSGW[5] = (byte)'M';
			MSGW[6] = (byte)'E';
			MSGW[7] = (byte)' ';
			byte b1 = (byte)(player.num_game);
			byte b2 = (byte)(player.num_game >> 8);
			if(isTheMachineIsWorkingWithBigEndian()){
				MSGW[8] = b1;
				MSGW[9] = b2;
			}else{
				MSGW[8] = b2;
				MSGW[9] = b1;
			}
			MSGW[10] = (byte)' ';
			byte b3 = (byte)(gamelist.get(player.num_game).labyrinthe.height );
			byte b4 = (byte)(gamelist.get(player.num_game).labyrinthe.height >> 8);	
			if(isTheMachineIsWorkingWithBigEndian()){
				MSGW[11] = b3;
				MSGW[12] = b4;
			}else{
				MSGW[11] = b4;
				MSGW[12] = b3;
			}
			MSGW[13] = (byte)' ';
			byte b5 = (byte)(gamelist.get(player.num_game).labyrinthe.width );
			byte b6 = (byte)(gamelist.get(player.num_game).labyrinthe.width >> 8);
			if(isTheMachineIsWorkingWithBigEndian()){
				MSGW[14] = b5;
				MSGW[15] = b6;
			}else{
				MSGW[14] = b6;
				MSGW[15] = b5;
			}	
			MSGW[16] = (byte)' ';
			byte b7 = (byte)(gamelist.get(player.num_game).labyrinthe.nb_ghost );
			byte b8 = (byte)(gamelist.get(player.num_game).labyrinthe.nb_ghost >> 8);
			if(isTheMachineIsWorkingWithBigEndian()){
				MSGW[17] = b7;
				MSGW[18] = b8;
			}else{
				MSGW[17] = b8;
				MSGW[18] = b7;
			}	
			MSGW[19] = (byte)' ';	
			String ip = gamelist.get(player.num_game).labyrinthe.ip;
			for(int i=gamelist.get(player.num_game).ip.length();i<15;i++){
				ip += '#';
				
			}
			for(int i=0;i<15;i++){
				MSGW[20 + i] = (byte)ip.charAt(i);				
			}
			for(int i=0;i<4;i++){
				MSGW[36 + i]= (byte)gamelist.get(player.num_game).labyrinthe.port.charAt(i);
			}
			MSGW[40] = (byte)'*';
			MSGW[41] = (byte)'*';
			MSGW[42] = (byte)'*';
			d.write(MSGW);
			System.out.println("sending the welcome to player :"+player.id);








		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}



	}
	public static void processMSG(Socket s,List<Game> gamelist,Player player,byte[] message){
		try{
			
			if(message[0] == (byte)'R' && message[1] == (byte)'I' && message[2] == (byte)'G' && message[3] == (byte)'H' && message[4] == (byte)'T'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'L' && message[1] == (byte)'E' && message[2] == (byte)'F' && message[3] == (byte)'T'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'D' && message[1] == (byte)'O' && message[2] == (byte)'W' && message[3] == (byte)'N'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'U' && message[1] == (byte)'P'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'Q' && message[1] == (byte)'U' && message[2] == (byte)'I' && message[3] == (byte)'T'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'G' && message[1] == (byte)'L' && message[2] == (byte)'I' && message[3] == (byte)'S'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'A' && message[1] == (byte)'L' && message[2] == (byte)'L' && message[3] == (byte)'?'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to so move !");
			}
			if(message[0] == (byte)'S' && message[1] == (byte)'E' && message[2] == (byte)'N' && message[3] == (byte)'S'){
				// impossible wesned the bye***
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG = "BYE***".getBytes();
				d1.write(MSG);
				System.out.println("impossible to send private message !");
			}


	


			if(message[0] == (byte)'N' && message[1] == (byte)'E' && message[2] == (byte)'W'){
				String id ="";
				String port = "";
				int i;
				for(i=4;i<message.length; i++){
					if(message[i] == (byte)' ') break;
					id += (char)message[i];
				}
				for(int j=i+1;j<message.length;j++){
					if(message[j] == (byte)'*') break;
					port += (char)message[j];
				}
				System.out.println("player with the id "+id+" and port :"+port+" want to create a new party" );
				///////////////////////create a new game
				player.id = id;
				player.port = port;
				player.num_game = (short)(gamelist.size());

				Game g1 = new Game( (short)(gamelist.size()) , (short)0 );
				g1.addNewPlayer(id,port);
				gamelist.add(g1);



				//g1.ShowTheListOfPlayers();
				/////////////////////// sending the REGOK 
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] MSG4 = new byte[11];
				MSG4[0] = (byte)'R';
				MSG4[1] = (byte)'E';
				MSG4[2] = (byte)'G';
				MSG4[3] = (byte)'O';
				MSG4[4] = (byte)'K';
				MSG4[5] = (byte)' ';
				byte b1 = (byte)((short)(gamelist.size() -1));
				byte b2 = (byte)((short)((gamelist.size() - 1) >> 8));
				if(isTheMachineIsWorkingWithBigEndian()){
					MSG4[6] = b1;
					MSG4[7] = b2;
				}else{
					MSG4[6] = b2;
					MSG4[7] = b1;
				}
				MSG4[8] = (byte)'*';
				MSG4[9] = (byte)'*';
				MSG4[10] = (byte)'*';
				d1.write(MSG4);
				System.out.println("sending REGOK to : "+id);

			}else if(message[0] == (byte)'R' && message[1] == (byte)'E' && message[2] == (byte)'G'){
				System.out.println("REG reveive");
				String id ="";
				String port = "";
				int i;
				for(i=4;i<message.length; i++){
					if(message[i] == (byte)' ') break;
					id += (char)message[i];
				}
				int j;
				for(j=i+1;j<message.length;j++){
					if(message[j] == (byte)' ') break;
					port += (char)message[j];
				}
				short m_1= 0;
				m_1 = (short)(((message[j+2]&0xFF)<<8) | (message[j+1]&0xFF));
				System.out.println("player with the id "+id+" and port :"+port+" want to join party num :"+m_1);
				// check is there is the game n
				int c =0;
				for(i=0; i< gamelist.size() ; i++){
					if(gamelist.get(i).num_game == m_1 && gamelist.get(i).state_game.equals("NOT_READY_TO_START") ){
						player.id = id;
						player.port = port;
						player.num_game = m_1;
						gamelist.get(i).addNewPlayer(id,port);
						c=1;
						break;
					}
				}
				if(c==1){
					DataOutputStream d1 = new DataOutputStream(s.getOutputStream());

					byte[] MSG4 = new byte[11];
					MSG4[0] = (byte)'R';
					MSG4[1] = (byte)'E';
					MSG4[2] = (byte)'G';
					MSG4[3] = (byte)'O';
					MSG4[4] = (byte)'K';
					MSG4[5] = (byte)' ';
					byte b1 = (byte)((short)(m_1 ));
					byte b2 = (byte)((short)(m_1 >> 8));
					if(isTheMachineIsWorkingWithBigEndian()){
						MSG4[6] = b1;
						MSG4[7] = b2;
					}else{
						MSG4[6] = b2;
						MSG4[7] = b1;
					}
					MSG4[8] = (byte)'*';
					MSG4[9] = (byte)'*';
					MSG4[10] = (byte)'*';
					d1.write(MSG4);

				}else{
					DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
					byte[] MSG4 = new byte[8];
					MSG4[0] = (byte)'R';
					MSG4[1] = (byte)'E';
					MSG4[2] = (byte)'G';
					MSG4[3] = (byte)'N';
					MSG4[4] = (byte)'O';
					MSG4[5] = (byte)'*';
					MSG4[6] = (byte)'*';
					MSG4[7] = (byte)'*';
					d1.write(MSG4);
				// REGNO***
				}
			}else if(message[0] == (byte)'U' && message[1] == (byte)'N' && message[2] == (byte)'R' 
				&& message[3] == (byte)'E' && message[4] == (byte)'G' && message[5] == (byte)'*'
				&& message[6] == (byte)'*' && message[7] == (byte)'*'  ){
				System.out.println("the player want to unreg");
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				try{
					Thread.sleep(1);
				}catch(Exception e){

				}
				if(player.id =="" || player.port == "" || player.num_game == -1  ){
					// send dunno
					byte[] MSG6 = new byte[8];
					MSG6[0] = (byte)'D';
					MSG6[1] = (byte)'U';
					MSG6[2] = (byte)'N';
					MSG6[3] = (byte)'N';
					MSG6[4] = (byte)'O';
					MSG6[5] = (byte)'*';
					MSG6[6] = (byte)'*';
					MSG6[7] = (byte)'*';
					d1.write(MSG6);
					System.out.println("The client want to unreg from a game and he's not in it !!");

				}else{
					// send [UNREGOK␣m***]
					byte[] MSG7 = new byte[13];
					MSG7[0] = (byte)'U';
					MSG7[1] = (byte)'N';
					MSG7[2] = (byte)'R';
					MSG7[3] = (byte)'E';
					MSG7[4] = (byte)'G';
					MSG7[5] = (byte)'O';
					MSG7[6] = (byte)'K';
					MSG7[7] = (byte)' ';
					byte b1 = (byte)((short)(player.num_game ));
					byte b2 = (byte)((short)(player.num_game >> 8));
					if(isTheMachineIsWorkingWithBigEndian()){
						MSG7[8] = b1;
						MSG7[9] = b2;
					}else{
						MSG7[8] = b2;
						MSG7[9] = b1;
					}
					
					MSG7[10] = (byte)'*';
					MSG7[11] = (byte)'*';
					MSG7[12] = (byte)'*';
					d1.write(MSG7);
					System.out.println("player id : "+player.id+" left the game n :"+player.num_game);
					gamelist.get(player.num_game).removePlayer(player.id);
					player.num_game = -1;

				}


			}else if(message[0] ==(byte)'S' && message[1] ==(byte)'I' && message[2] ==(byte)'Z' && message[3] ==(byte)'E' &&
				message[4] ==(byte)'?' && message[5] ==(byte)' ' && message[8] ==(byte)'*' && message[9] ==(byte)'*' &&
				message[10] ==(byte)'*'){
				// the player want to know the size of the game 
				int m_1 = 0;
				m_1 = (short)(((message[7]&0xFF)<<8) | (message[6]&0xFF));
				System.out.println("the player want to know the size of the game n :"+m_1);
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());


				if(m_1 < 0 || m_1 >= gamelist.size()){
					// sned dunno
					byte[] ms = new byte[8];
					ms[0] = (byte)'D';
					ms[1] = (byte)'U';
					ms[2] = (byte)'N';
					ms[3] = (byte)'N';
					ms[4] = (byte)'O';
					ms[5] = (byte)'*';
					ms[6] = (byte)'*';
					ms[7] = (byte)'*';
					d1.write(ms);
				}else{
					short w = 50;
					short h = 10;

					byte[] ms = new byte[17];
					ms[0] = (byte)'S';
					ms[1] = (byte)'I';
					ms[2] = (byte)'Z';
					ms[3] = (byte)'E';
					ms[4] = (byte)'!';
					ms[5] = (byte)' ';
					byte b1 = (byte)((short)(m_1 ));
					byte b2 = (byte)((short)(m_1 >> 8));
					if(isTheMachineIsWorkingWithBigEndian()){
						ms[6] = b1;
						ms[7] = b2;
					}else{
						ms[6] = b2;
						ms[7] = b1;
					}
					ms[8] = (byte)' ';
					byte b3 = (byte)((short)(h ));
					byte b4 = (byte)((short)(h >> 8));	
					if(isTheMachineIsWorkingWithBigEndian()){
						ms[9] = b3;
						ms[10] = b4;
					}else{
						ms[9] = b4;
						ms[10] = b3;
					}			

					ms[11] = (byte)' ';
					byte b5 = (byte)((short)(w ));
					byte b6 = (byte)((short)(w >> 8));
					if(isTheMachineIsWorkingWithBigEndian()){
						ms[12] = b5;
						ms[13] = b6;
					}else{
						ms[12] = b6;
						ms[13] = b5;
					}
					
					ms[14] = (byte)'*';
					ms[15] = (byte)'*';
					ms[16] = (byte)'*';
					d1.write(ms); // send the size response	
				}




			}else if(message[0] == (byte)'L' && message[1] == (byte)'I' && message[2] == (byte)'S' && message[3] == (byte)'T'
				&& message[4] == (byte)'?' && message[5] == (byte)' ' && message[8] == (byte)'*' &&  message[9] == (byte)'*'
				&&  message[10] == (byte)'*'){
				// now send to the client [LIST!␣m␣s***] and s fois [PLAYER␣id***]
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				int m_1 = 0;
				m_1 = (short)(((message[7]&0xFF)<<8) | (message[6]&0xFF));

				if(m_1 < 0 || m_1 >= gamelist.size()){
					byte[] ms = new byte[8];
					ms[0] = (byte)'D';
					ms[1] = (byte)'U';
					ms[2] = (byte)'N';
					ms[3] = (byte)'N';
					ms[4] = (byte)'O';
					ms[5] = (byte)'*';
					ms[6] = (byte)'*';
					ms[7] = (byte)'*';
					d1.write(ms);
					System.out.println("there is no game : "+m_1 );
				}else{
					// now send the [LIST!␣m␣s***]
					byte[] ms = new  byte[14];
					System.out.println("sending list of players of game :"+m_1 );
					ms[0] = (byte)'L';
					ms[1] = (byte)'I';
					ms[2] = (byte)'S';
					ms[3] = (byte)'T';
					ms[4] = (byte)'!';
					ms[5] = (byte)' ';
					byte b1 = (byte)((short)(m_1 ));
					byte b2 = (byte)((short)(m_1 >> 8));
					if(isTheMachineIsWorkingWithBigEndian()){
						ms[6] = b1;
						ms[7] = b2;
					}else{
						ms[6] = b2;
						ms[7] = b1;
					}

					ms[8] = (byte)' ';
					byte b3 = (byte)((short)(gamelist.get(m_1).nb_joueurs ));
					byte b4 = (byte)((short)(gamelist.get(m_1).nb_joueurs >> 8));	
					if(isTheMachineIsWorkingWithBigEndian()){
						ms[9] = b3;
						ms[10] = b4;
					}else{
						ms[9] = b4;
						ms[10] = b3;
					}

					ms[11] = (byte)'*';
					ms[12] = (byte)'*';
					ms[13] = (byte)'*';	
					d1.write(ms);
					//now send the s players [PLAYER␣id***]
					for (Map.Entry<String, String> entry : gamelist.get(m_1).listPlayerID.entrySet()) {
						String key = entry.getKey();
						int l_1 = key.length();
						byte[] ms1 = new byte[18];
						ms1[0] = (byte)'P';
						ms1[1] = (byte)'L';
						ms1[2] = (byte)'A';
						ms1[3] = (byte)'Y';
						ms1[4] = (byte)'E';
						ms1[5] = (byte)'R';
						ms1[6] = (byte)' ';
						int i;
						for(i=1;i<=l_1;i++){
							ms1[6+i] = (byte)key.charAt(i-1);
						}
						ms1[6 + i] = (byte)'*';
						ms1[6 + i + 1] = (byte)'*';
						ms1[6 + i + 2] = (byte)'*';
						d1.write(ms1);


						
					}						





				}

			}else if(message[0] == (byte)'G' && message[1] == (byte)'A' && message[2] == (byte)'M' && message[3] == (byte)'E' 
				&& message[4] == (byte)'S' && message[5] == (byte)'?' && message[6] == (byte)'*' && message[7] == (byte)'*' 
				&& message[8] == (byte)'*'){

				short n_1 = 0;
				for(int i=0; i<gamelist.size() ;i++){
					if(gamelist.get(i).state_game.equals("NOT_READY_TO_START")){
						n_1++;
					}	
				}
				// sending the [GAMES␣n***] 
				System.out.println("Sending list of not starting games...");
				DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
				byte[] ms = new byte[11];
				ms[0] = (byte)'G';
				ms[1] = (byte)'A';
				ms[2] = (byte)'M';
				ms[3] = (byte)'E';
				ms[4] = (byte)'S';
				ms[5] = (byte)' ';
				byte b1 = (byte)((short)(n_1 ));
				byte b2 = (byte)((short)(n_1 >> 8));
				if(isTheMachineIsWorkingWithBigEndian()){
					ms[6] = b1;
					ms[7] = b2;
				}else{
					ms[6] = b2;
					ms[7] = b1;
				}

				ms[8] = (byte)'*';
				ms[9] = (byte)'*';
				ms[10] = (byte)'*';
				d1.write(ms);
				// now sending the n_1 games
				for(int i=0; i < gamelist.size() ; i++){
					if(gamelist.get(i).state_game.equals("NOT_READY_TO_START")){
						short m_1 = gamelist.get(i).num_game;
						short s_1 = gamelist.get(i).nb_joueurs;
						byte[] ms2 = new byte[13];
						ms2[0] = (byte)'G';
						ms2[1] = (byte)'A';
						ms2[2] = (byte)'M';
						ms2[3] = (byte)'E';
						ms2[4] = (byte)' ';
						b1 = (byte)((short)(m_1 ));
						b2 = (byte)((short)(m_1 >> 8));
						
						if(isTheMachineIsWorkingWithBigEndian()){
							ms2[5] = b1;
							ms2[6] = b2;
						}else{
							ms2[5] = b2;
							ms2[6] = b1;
						}

						ms2[7] = (byte)' ';
						byte b3 = (byte)((short)(s_1 ));
						byte b4 = (byte)((short)(s_1 >> 8));
						if(isTheMachineIsWorkingWithBigEndian()){
							ms2[8] = b3;
							ms2[9] = b4;
						}else{
							ms2[8] = b4;
							ms2[9] = b3;
						}

						ms2[10] = (byte)'*';
						ms2[11] = (byte)'*';
						ms2[12] = (byte)'*';
						d1.write(ms2);
						
					}
				}



			}else if(message[0] == (byte)'S' && message[1] == (byte)'T' &&  message[2] == (byte)'A' &&
				message[3] == (byte)'R' &&  message[4] == (byte)'T' && message[5] == (byte)'*' && 
				message[6] == (byte)'*' && message[7] == (byte)'*'   ){

				System.out.println("A client :  want to start a game ");
				if(player.num_game == -1){
					System.out.print("impossible to start a game because the client is not reg in it !!");

				}else{
					System.out.print("the client :"+player.id+" want to start the game :"+player.num_game +"  !!");
					gamelist.get(player.num_game).playerIsReady(player.id);
					player.state = "STARTING";


				}

			}else{
				System.out.println("ERROR");
				return;
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}

	}

}