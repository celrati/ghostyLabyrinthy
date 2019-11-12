import java.io.*;
import java.net.*;
import java.util.*;

class Client{
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		try{
			//////////////////////////////////////////////
			/*
			System.out.print("chose an id maximun 8 char : ");
			String id = sc.nextLine();
			System.out.print("chose a port with 4 chars : ");
			String port = sc.nextLine();
			*/
			/////////////////////////////////////////////////////

			Socket socket=new Socket("localhost",4444); 
			DataInputStream d = new DataInputStream(socket.getInputStream());


			byte[] ms = new byte[64];
			d.read(ms);
			processMessageMSG1(ms,socket);
			String stateGame = "";
			int cc =0;

			String ip_machine = InetAddress.getLocalHost().getHostAddress();
			String port_machine ="";
			

			while(true){
				ThreadState threadstate = new ThreadState();
				stateGame = "";
			while(true){  // before the game
				String s = sc.nextLine();

				if(s.charAt(0) == 'N' && s.charAt(1) == 'E' && s.charAt(2) == 'W'){
					//[NEW␣id␣port***]
					stateGame = "RUN";
					String port = "";
					String id = "";
					int i;
					for(i=4; i<s.length(); i++){
						if(s.charAt(i) == ' '){
							break;
						}
						id += s.charAt(i);
					}
					for(int j = i+1 ; j<s.length() ; j++){
						if(s.charAt(j) == '*'){
							break;
						}
						port += s.charAt(j);
					}
					port_machine=port;
					//System.out.println("id is "+id);
					//System.out.println("port is "+port);
					sendMSG2(socket,id,port);


				}else
				if(s.charAt(0) == 'R' && s.charAt(1) == 'E' && s.charAt(2) == 'G'){
					//[REG␣id␣port␣m***]
					String port = "";
					String id = "";
					String m_1 = "";
					int i;
					for(i=4; i<s.length(); i++){
						if(s.charAt(i) == ' '){
							break;
						}
						id += s.charAt(i);
					}
					int j;
					for(j = i+1 ; j<s.length() ; j++){
						if(s.charAt(j) == ' '){
							break;
						}
						port += s.charAt(j);
					}
					for(int k = j+1 ; k<s.length(); k++){
						if(s.charAt(k) == '*'){
							break;
						}
						m_1 += s.charAt(k);
					}
					port_machine=port;
					//System.out.println("id is "+id);
					//System.out.println("port is "+port);
					short m_2 = Short.parseShort(m_1);
					sendMSG3(socket,id,port,m_2,stateGame);
				}else
				if(s.charAt(0) == 'U' && s.charAt(1) == 'N' && s.charAt(2) == 'R' && s.charAt(3) == 'E'
					&& s.charAt(4) == 'G'){
					sendMSG4(socket);
			}else
			if(s.charAt(0) == 'S' && s.charAt(1) == 'I' && s.charAt(2) == 'Z' && s.charAt(3) == 'E' &&
				s.charAt(4) == '?'){
				int i;
			String m_1= "";
			for( i=6; i < s.length() ; i++){
				if(s.charAt(i) == '*'){
					break;
				} 
				m_1 += s.charAt(i);
			}
			short m_2 = Short.parseShort(m_1);
			sendMSG5(socket,m_2);
		}else
		if(s.charAt(0) == 'L' && s.charAt(1) == 'I' && s.charAt(2) == 'S' && s.charAt(3) == 'T'
			&& s.charAt(4) == '?'){
			int i;
		String m_1= "";
		for( i=6; i < s.length() ; i++){
			if(s.charAt(i) == '*'){
				break;
			} 
			m_1 += s.charAt(i);
		}
		short m_2 = Short.parseShort(m_1);
		sendMSG6(socket,m_2);					
	}else
	if(s.charAt(0) == 'G' && s.charAt(1) == 'A' && s.charAt(2) == 'M' && s.charAt(3) == 'E'
		&& s.charAt(4) == 'S'){
		sendMSG7(socket);					
}else
if(s.charAt(0) == 'S' && s.charAt(1) == 'T' && s.charAt(2) == 'A' && s.charAt(3) == 'R'
	&& s.charAt(4) == 'T'){
	sendMSG8(socket);
break;
	//if(stateGame == "RUN") break;
	//else System.out.println("you are not in a game !!!");
		
}
	if(s.substring(0,5).equals("RIGHT")){
		System.out.println("[ERROR:321]sending right");
		sendNewDirection(socket,"RIGHT",s.substring(6,9));
	}
	if(s.substring(0,4).equals("LEFT")){
		System.out.println("[ERROR:321]sending left");
		sendNewDirection(socket,"LEFT",s.substring(5,8));
	}
	if(s.substring(0,4).equals("DOWN")){
		System.out.println("[ERROR:321]sending down");
		sendNewDirection(socket,"DOWN",s.substring(5,8));
	}
	if(s.substring(0,2).equals("UP")){
		System.out.println("[ERROR:321]sending up");
		sendNewDirection(socket,"UP",s.substring(3,6));
	}
	if(s.substring(0,4).equals("QUIT")){
		System.out.println("[ERROR:321]sending to the server request to quit from the game...");
		sendQuitMsg(socket);
		threadstate.a = 1;
	}
	if(s.substring(0,6).equals("GLIST?")){
		System.out.println("[ERROR:321]sending to the server request to get the players in this game ....");
		sendGLISTmsg(socket);
	}
	if(s.substring(0,4).equals("ALL?")){
		System.out.println("[ERROR:321]sending a message to all players...");
		String mess = "";
		for(int i =5; i < s.length(); i++){
			if(s.charAt(i) == '*') break;
			mess += s.charAt(i);
		}
		sendAllMsg(socket,mess);
	}
	if(s.substring(0,5).equals("SEND?")){
		System.out.println("[ERROR:321]sending a private message !!!");
		sendPrivateMessage(socket,s);
	}





}



			// the game is starting

processWelcomeMSG(socket,threadstate,ip_machine,port_machine);
processWelcomePos(socket);

			// now the game begin..



while(true){
	String st = sc.nextLine();
	try{	
		Thread.sleep(100);
	}catch(Exception e){

	}
				//System.out.println(st.substring(0,5));
	if(st.substring(0,5).equals("RIGHT")){
		System.out.println("sending right");
		sendNewDirection(socket,"RIGHT",st.substring(6,9));
	}
	if(st.substring(0,4).equals("LEFT")){
		System.out.println("sending left");
		sendNewDirection(socket,"LEFT",st.substring(5,8));
	}
	if(st.substring(0,4).equals("DOWN")){
		System.out.println("sending down");
		sendNewDirection(socket,"DOWN",st.substring(5,8));
	}
	if(st.substring(0,2).equals("UP")){
		System.out.println("sending up");
		sendNewDirection(socket,"UP",st.substring(3,6));
	}
	if(st.substring(0,4).equals("QUIT")){
		System.out.println("sending to the server request to quit from the game...");
		sendQuitMsg(socket);
		threadstate.a = 1;
		break;
	}
	if(st.substring(0,6).equals("GLIST?")){
		System.out.println("sending to the server request to get the players in this game ....");
		sendGLISTmsg(socket);
	}
	if(st.substring(0,4).equals("ALL?")){
		System.out.println("sending a message to all players...");
		String mess = "";
		for(int i =5; i < st.length(); i++){
			if(st.charAt(i) == '*') break;
			mess += st.charAt(i);
		}
		sendAllMsg(socket,mess);
	}
	if(st.substring(0,5).equals("SEND?")){
		System.out.println("snding a private message !!!");
		sendPrivateMessage(socket,st);
	}
	if(threadstate.stateGame == "END") {
		System.out.println("GAME over");
		break;
	}

	try{
		Thread.sleep(10);
	}catch(Exception e){}


}

if(cc == 1) break;
}


}catch(Exception e){
	System.out.println(e);
	e.printStackTrace();			
}	

}
public static void sendPrivateMessage(Socket s,String mes){
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] m = mes.getBytes();
		d1.write(m);

		byte[] ms1 = new byte[16];
		d.read(ms1);
		for(int i=0;i<ms1.length ;i++){
			System.out.print((char)ms1[i]);
		}
		System.out.println("");
	}catch(Exception e){

	}

}
public static void sendAllMsg(Socket s,String m){
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] ms = new byte[256];
		ms[0] = (byte)'A';
		ms[1] = (byte)'L';
		ms[2] = (byte)'L';
		ms[3] = (byte)'?';
		ms[4] = (byte)' ';
		int i;
		for(i=0;i<m.length();i++){
			ms[5 + i] = (byte)m.charAt(i);
		}
		i += 5;
		ms[i++] = (byte)'*';
		ms[i++] = (byte)'*';
		ms[i++] = (byte)'*';
		d1.write(ms);
		byte[] ms1 = new byte[7];
		d.read(ms1);


		for(i =0; i < 7 ;i++){
			System.out.print((char)ms1[i]);
		}
		System.out.println("");

	}catch(Exception e){

	}
}
public static void sendGLISTmsg(Socket s){
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] ms = new byte[8];
		ms[0] = (byte)'G';
		ms[1] = (byte)'L';
		ms[2] = (byte)'I';
		ms[3] = (byte)'S';
		ms[4] = (byte)'T';
		ms[5] = (byte)'*';
		ms[6] = (byte)'*';
		ms[7] = (byte)'*';
		d1.write(ms);
		byte[] ms1 = new byte[12];
		d.read(ms1);
		if(ms1[0] ==(byte)'B'){
			System.out.println("BYE***");
			return;
		}
		short ss = (short)(((ms1[8]&0xFF)<<8) | (ms1[7]&0xFF));
		System.out.println("There is :"+ss+" players playing right now !!!!");
		for(short i =0; i<ss ;i++){
				// get the players now
			byte[] ms2 = new byte[64];
			d.read(ms2);
			for(int j =0;j<ms2.length; j++){
				System.out.print((char)ms2[j]);
			}
			System.out.println("");

		}
			// get the response...

	}catch(Exception e){

	}
}
public static void sendQuitMsg(Socket s){
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] ms = new byte[7];
		ms[0] = (byte)'Q';
		ms[1] = (byte)'U';
		ms[2] = (byte)'I';
		ms[3] = (byte)'T';
		ms[4] = (byte)'*';
		ms[5] = (byte)'*';
		ms[6] = (byte)'*';
		d1.write(ms);
		byte[] ms1 = new byte[6];
		d.read(ms1);
		for(int i=0;i<6;i++){
			System.out.print((char)ms1[i]);
		}
		System.out.println("");

	}catch(Exception e){

	}
}
public static void sendNewDirection(Socket s,String dir,String x){
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] MSG =new byte[64];
		
		if(dir == "LEFT"){
			byte[] ms = new byte[11];
			ms[0] = (byte)'L';
			ms[1] = (byte)'E';
			ms[2] = (byte)'F';
			ms[3] = (byte)'T';
			ms[4] = (byte)' ';
			ms[5] = (byte)x.charAt(0);
			ms[6] = (byte)x.charAt(1);
			ms[7] = (byte)x.charAt(2);
			ms[8] = (byte)'*';
			ms[9] = (byte)'*';
			ms[10] = (byte)'*';
			d1.write(ms);
			d.read(MSG);
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'V'){
				System.out.print("MOV x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("");
			}else 
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'F'){
				System.out.print("MOF x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("  p= ");
				for(int i=12;i<16;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println(" ");
			}else{
				System.out.println("BYE***");
			}
		}
		if(dir == "RIGHT"){
			byte[] ms = new byte[12];
			ms[0] = (byte)'R';
			ms[1] = (byte)'I';
			ms[2] = (byte)'G';
			ms[3] = (byte)'H';
			ms[4] = (byte)'T';
			ms[5] = (byte)' ';
			ms[6] = (byte)x.charAt(0);
			ms[7] = (byte)x.charAt(1);
			ms[8] = (byte)x.charAt(2);
			ms[9] = (byte)'*';
			ms[10] = (byte)'*';
			ms[11] = (byte)'*';
			d1.write(ms);
			System.out.println("sending the pos to server !!");
			d.read(MSG);
			System.out.println("received the mov message !!");
			
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'V'){
				System.out.print("MOV x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("");
			}else 
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'F'){
				System.out.print("MOF x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  p= ");
				for(int i=12;i<16;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println(" ");
			}else{
				System.out.println("BYE***");
			}
		}
		if(dir == "UP"){
			byte[] ms = new byte[9];
			ms[0] = (byte)'U';
			ms[1] = (byte)'P';
			ms[2] = (byte)' ';
			ms[3] = (byte)x.charAt(0);
			ms[4] = (byte)x.charAt(1);
			ms[5] = (byte)x.charAt(2);
			ms[6] = (byte)'*';
			ms[7] = (byte)'*';
			ms[8] = (byte)'*';
			d1.write(ms);
			d.read(MSG);
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'V'){
				System.out.print("MOV x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("");
			}else
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'F'){
				System.out.print("MOF x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("  p= ");
				for(int i=12;i<16;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println(" ");
			}
			else{
				System.out.println("BYE***");
			}
		}
		if(dir == "DOWN"){
			byte[] ms = new byte[11];
			ms[0] = (byte)'D';
			ms[1] = (byte)'O';
			ms[2] = (byte)'W';
			ms[3] = (byte)'N';
			ms[4] = (byte)' ';
			ms[5] = (byte)x.charAt(0);
			ms[6] = (byte)x.charAt(1);
			ms[7] = (byte)x.charAt(2);
			ms[8] = (byte)'*';
			ms[9] = (byte)'*';
			ms[10] = (byte)'*';
			d1.write(ms);
			d.read(MSG);
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'V'){
				System.out.print("MOV x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("");
			}else
			if(MSG[0] == (byte)'M' && MSG[1] == (byte)'O' && MSG[2] == (byte)'F'){
				System.out.print("MOF x=");
				for(int i=4;i<7;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.print("  y=");
				for(int i=8;i<11;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println("  p= ");
				for(int i=12;i<16;i++){
					System.out.print((char)MSG[i]);
				}
				System.out.println(" ");
			}
			else{
				System.out.println("BYE***");
			}
		}
	}catch(Exception e){

	}


}
public static void processWelcomePos(Socket s){
	try{
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] MSG =new byte[64];
		d.read(MSG);
		System.out.print("POS id:");
		int i;
		for(i=4;i<MSG.length;i++){
				//System.out.print("@"+(char)MSG[i]+"@");
			if(MSG[i] == (byte)' ') break;
			System.out.print((char)MSG[i]);
		}
		System.out.println("");
		System.out.print("x :");
		int j;
		for(j=i+1;j<MSG.length;j++){
			if(MSG[j] == (byte)' ') break;
			System.out.print((char)MSG[j]);
		}
		int k;
		System.out.println("");
		System.out.print("y :");
		for(k=j+1;k<MSG.length;k++){
			if(MSG[k] == (byte)'*') break;
			System.out.print((char)MSG[k]);
		}
		System.out.println("");

	}catch(Exception e){

	}
}
public static void processMessageMSG1(byte[] MSG,Socket s){
	System.out.print("GAMES ");
	short nb_games = 0;
	nb_games = (short)(((nb_games | MSG[7]) << 8) | (MSG[6]));
	System.out.println(nb_games);
			// now we re going to receive a nb_games message
	for(int i=0; i < nb_games ; i++){
				// we re going to receive a 13 bytes
		byte[] message = new byte[13];
		try{
			DataInputStream d = new DataInputStream(s.getInputStream());
			d.read(message);
			System.out.print("GAME ");
			short m_1=0,s_1=0;
			m_1 = (short)(((message[6]&0xFF)<<8) | (message[5]&0xFF));
			s_1 = (short)(((message[9]&0xFF)<<8) | (message[8]&0xFF));
			System.out.println(m_1+" "+s_1+"***");
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}	
}
public static void sendMSG2(Socket s,String id,String port){
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		String msg = "NEW "+id+" "+port+"***";
		d1.write(msg.getBytes());
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] MSG =new byte[64];
		d.read(MSG);
		if(MSG[0] == (byte)'R' && MSG[1] == (byte)'E' && MSG[2] == (byte)'G' && MSG[3] == (byte)'O' && MSG[4] == (byte)'K'){
			System.out.print("REGOK ");
			int m_1=0;
			m_1 = (short)(((MSG[7]&0xFF)<<8) | (MSG[6]&0xFF));

			System.out.println(m_1+"***");
		}else if(MSG[0] == (byte)'R' && MSG[1] == (byte)'E' && MSG[2] == (byte)'G' && MSG[3] == (byte)'N' && MSG[4] == (byte)'O'){
			System.out.println("REGNO***");
		}



	}catch(Exception e){
		System.out.println(e);
		e.printStackTrace();			
	}
}
public static void sendMSG3(Socket s, String id, String port,short m,String stateGame){
	try{
		DataOutputStream d = new DataOutputStream(s.getOutputStream());
		String msg = "REG "+id+" "+port+" ";
		byte[] MSG3 = new byte[23];
		int i;
		for(i=0;i<msg.length() ;i++){
			MSG3[i] = (byte)(msg.charAt(i));
		}
		byte b1 = (byte)(m);
		byte b2 = (byte)(m >> 8);
		if(isTheMachineIsWorkingWithBigEndian()){
			MSG3[i] = b1;
			MSG3[i+1] = b2;
		}else{
			MSG3[i] = b2;
			MSG3[i+1] = b1;
		}


		MSG3[i+2] = (byte)'*';
		MSG3[i+3] = (byte)'*';
		MSG3[i+4] = (byte)'*';
		d.write(MSG3);
		DataInputStream d1 = new DataInputStream(s.getInputStream());
		byte[] MSG =new byte[64];
		d1.read(MSG);
		if(MSG[0] == (byte)'R' && MSG[1] == (byte)'E' && MSG[2] == (byte)'G' && MSG[3] == (byte)'O' && MSG[4] == (byte)'K'){
			System.out.print("REGOK ");
			stateGame ="RUN";
			int m_1=0;
			m_1 = (short)(((MSG[7]&0xFF)<<8) | (MSG[6]&0xFF));
			System.out.println(m_1+"***");
		}else if(MSG[0] == (byte)'R' && MSG[1] == (byte)'E' && MSG[2] == (byte)'G' && MSG[3] == (byte)'N' && MSG[4] == (byte)'O'){
			System.out.println("REGNO***");
		}

	}catch(Exception e){
		System.out.println(e);
		e.printStackTrace();			
	}
}
	/*
	public static void processMessageMSG4(byte[] MSG,Socket s){
		if(MSG[0] == (byte)'R' && MSG[1] == (byte)'E' && MSG[2] == (byte)'G' && MSG[3] == (byte)'O' && MSG[4] == (byte)'K'){
			System.out.print("REGOK ");
			int m_1=0;
			m_1 = (short)(((MSG[7]&0xFF)<<8) | (MSG[6]&0xFF));
			System.out.print(m_1+"***");
		}else if(MSG[0] == (byte)'R' && MSG[1] == (byte)'E' && MSG[2] == (byte)'G' && MSG[3] == (byte)'N' && MSG[4] == (byte)'O'){
			System.out.print("REGNO***");
		}
	}
	*/
	public static void sendMSG4(Socket s){
		// send the [UNREG***]
		try{
			DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
			String msg = "UNREG***";
			d1.write(msg.getBytes());
			// get the response
			DataInputStream d = new DataInputStream(s.getInputStream());
			byte[] ms =new byte[64];
			d.read(ms);
			if(ms[0] == (byte)'D' && ms[1] == (byte)'U' && ms[2] == (byte)'N' && ms[3] == (byte)'N' && ms[4] == (byte)'O' 
				&& ms[5] == (byte)'*' && ms[6] == (byte)'*' && ms[7] == (byte)'*'){
				// PROCESS THE DUNNO
				System.out.println("DUNNO !!!!!");


		}else if(ms[0] == (byte)'U' && ms[1] == (byte)'N' && ms[2] == (byte)'R' && ms[3] == (byte)'E' && ms[4] == (byte)'G' 
			&& ms[5] == (byte)'O' && ms[6] == (byte)'K' && ms[7] == (byte)' ' &&
			ms[10] == (byte)'*' && ms[11] == (byte)'*' && ms[12] == (byte)'*' ){
				// PROCESS THE UNREGOK
			int m=0;
			m = (short)(((ms[9]&0xFF)<<8) | (ms[8]&0xFF));
			System.out.println("UNREGOK "+m+"***");

		}



	}catch(Exception e){
		System.out.println(e);
		e.printStackTrace();			
	}		
}
public static void sendMSG5(Socket s,short m){
		// send the [SIZE?␣m***]
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		byte[] msg = new byte[11];
		msg[0] = (byte)'S';
		msg[1] = (byte)'I';
		msg[2] = (byte)'Z';
		msg[3] = (byte)'E';
		msg[4] = (byte)'?';
		msg[5] = (byte)' ';
		byte b1 = (byte)(m);
		byte b2 = (byte)(m >> 8);
		if(isTheMachineIsWorkingWithBigEndian()){
			msg[6] = b1;
			msg[7] = b2;
		}else{
			msg[6] = b2;
			msg[7] = b1;
		}
		msg[8] = (byte)'*';
		msg[9] = (byte)'*';
		msg[10] = (byte)'*';
		d1.write(msg);

		DataInputStream d = new DataInputStream(s.getInputStream());
			// receive the answer;
		byte[] ms = new byte[17];
		d.read(ms);
		if(ms[0] == (byte)'D' && ms[1] == (byte)'U' && ms[2] == (byte)'N' && ms[3] == (byte)'N' && ms[4] == (byte)'O' 
				&& ms[5] == (byte)'*' && ms[6] == (byte)'*' && ms[7] == (byte)'*'){// process the dunno
			System.out.println("DUNNO");

	}else{
		short w_1 = 0;
		short h_1 = 0;
		short m_1 = 0;
		m_1 = (short)(((ms[7]&0xFF)<<8) | (ms[6]&0xFF));
		h_1 = (short)(((ms[10]&0xFF)<<8) | (ms[9]&0xFF));
		w_1 = (short)(((ms[13]&0xFF)<<8) | (ms[12]&0xFF));
		System.out.println("SIZE! "+m_1+" "+h_1+" "+w_1+"***");
	}


	

}catch(Exception e){
	System.out.println(e);
	e.printStackTrace();			
}
}
public static void sendMSG6(Socket s, short m){
		// send the [LIST?␣m***]
	try{
		DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
		byte[] ms1 = new byte[11];
		ms1[0] = (byte)'L';
		ms1[1] = (byte)'I';
		ms1[2] = (byte)'S';
		ms1[3] = (byte)'T';
		ms1[4] = (byte)'?';
		ms1[5] = (byte)' ';
		byte b1 = (byte)(m);
		byte b2 = (byte)(m >> 8);
		if(isTheMachineIsWorkingWithBigEndian()){
			ms1[6] = b1;
			ms1[7] = b2;
		}else{
			ms1[6] = b2;
			ms1[7] = b1;
		}

		ms1[8] = (byte)'*';
		ms1[9] = (byte)'*';
		ms1[10] = (byte)'*';
		d1.write(ms1);
			// get the response now
		DataInputStream d = new DataInputStream(s.getInputStream());
		byte[] ms = new byte[14];
		d.read(ms);

		if(ms[0] == (byte)'D' && ms[1] == (byte)'U' && ms[2] == (byte)'N' && ms[3] == (byte)'N' && ms[4] == (byte)'O' 
				&& ms[5] == (byte)'*' && ms[6] == (byte)'*' && ms[7] == (byte)'*'){// process the dunno
			System.out.println("DUNNO");

	}else{
				// get the reel response
				// m
		short s_1 = 0;
		short m_1 = 0;
		m_1 = (short)(((ms[7]&0xFF)<<8) | (ms[6]&0xFF));
		s_1 = (short)(((ms[10]&0xFF)<<8) | (ms[9]&0xFF));
		System.out.println("there is "+s_1+" player in the game n "+m_1);
		for(int i=0; i<s_1 ; i++){
					// get every player right now
			byte[] ms2 = new byte[18];
			d.read(ms2);
			String ss = new String(ms2);
			System.out.println(ss);
		}

	}



}catch(Exception e){
	System.out.println(e);
	e.printStackTrace();		
}
}

public static void sendMSG7(Socket s){
		try{ // [GAMES?***]
			DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
			byte[] ms = new byte[9];	
			ms[0] = (byte)'G';
			ms[1] = (byte)'A';
			ms[2] = (byte)'M';
			ms[3] = (byte)'E';
			ms[4] = (byte)'S';
			ms[5] = (byte)'?';
			ms[6] = (byte)'*';
			ms[7] = (byte)'*';
			ms[8] = (byte)'*';
			d1.write(ms);
			// get the response
			DataInputStream d = new DataInputStream(s.getInputStream());
			byte[] ms1 = new byte[11];
			d.read(ms1);

			short n_1 = (short)(((ms1[7]&0xFF)<<8) | (ms1[6]&0xFF));
			// get the n_1 games
			for(short i=0 ;i< n_1 ; i++){
				byte[] ms2 = new byte[13];
				d.read(ms2);
				// showing the the games
				System.out.print("GAME ");
				short m_1 = (short)(((ms2[6]&0xFF)<<8) | (ms2[5]&0xFF));
				short s_1 = (short)(((ms2[9]&0xFF)<<8) | (ms2[8]&0xFF));
				System.out.println(m_1+" "+s_1+"***");
			}


		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
	}
	public static void sendMSG8(Socket s){
		try{
			DataOutputStream d1 = new DataOutputStream(s.getOutputStream());
			byte[] ms = new byte[8];
			ms[0] = (byte)'S';
			ms[1] = (byte)'T';
			ms[2] = (byte)'A';
			ms[3] = (byte)'R';
			ms[4] = (byte)'T';
			ms[5] = (byte)'*';
			ms[6] = (byte)'*';
			ms[7] = (byte)'*';
			d1.write(ms);


		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
	}
	public static void processWelcomeMSG(Socket s,ThreadState threadstate,String ip_machine,String port_machine){
		try{
			DataInputStream d = new DataInputStream(s.getInputStream());
			byte[] ms = new byte[43];
			d.read(ms);

			//System.out.println(ms);
			short m,h,w,f;
			String ip = "",port = "";
			m = (short)(((ms[9]&0xFF)<<8) | (ms[8]&0xFF));
			h = (short)(((ms[12]&0xFF)<<8) | (ms[11]&0xFF));
			w = (short)(((ms[15]&0xFF)<<8) | (ms[14]&0xFF));
			f = (short)(((ms[18]&0xFF)<<8) | (ms[17]&0xFF));
			for(int i=20; i < 20 + 15 ; i++){
				ip += (char)ms[i];
			}
			for(int i=36; i < 36 + 4 ; i++){
				port += (char)ms[i];
			}
			System.out.println("welcome to party n: "+m+" height ="+h+" width ="+w+" nb of ghosts ="+f);
			System.out.println("adress ip of multid-iffusion is : "+ip);
			System.out.println("port of multid-iffusion is : "+port);	

			ThreadUDPMessageListener  tuml = new ThreadUDPMessageListener(ip_machine,port_machine);


			ThreadMultiDiffusionListener tmdl = new ThreadMultiDiffusionListener(ip,Short.parseShort(port),threadstate);
			Thread t = new Thread(tmdl);
			Thread t1 = new Thread(tuml);
			t1.start();
			
			t.start();

		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
	}
	public static boolean isTheMachineIsWorkingWithBigEndian(){
		short n = 1;
		if((byte)n == 1) return true;
		return false;
	}



}