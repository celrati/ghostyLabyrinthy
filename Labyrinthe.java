import java.io.*;
import java.net.*;
import java.util.*;

class Labyrinthe{
	public short num_game;
	public short width;
	public short height;
	public short nb_ghost;
	public String ip;
	public String port;
	public String[] laby;
	public List<Player> listPlayer;
	public List<Ghost> listGhost;

	public String stateGame;

	public DatagramSocket dso;
	public InetSocketAddress ia;

	public Labyrinthe(short n,short f,String ip,String port){
		this.num_game = n;
		this.nb_ghost = f ;
		this.ip = ip;
		this.port = port;
		this.listPlayer = new ArrayList<Player>();
		this.listGhost = new ArrayList<Ghost>();
		this.stateGame = "RUN";




		////////////////////////////
		try{
			/*
			this.dso = new DatagramSocket();
			this.ia = new InetSocketAddress(ip,Short.parseShort(port));
			System.out.println("the socket of multid is ip :"+ip+" and the port is : "+port);
			*/
			File file =
			new File("labyrintheEx/1");
			Scanner sc = new Scanner(file);
			String[] h_w = sc.nextLine().split(" ");
			this.height = Short.parseShort(h_w[0]);
			this.width = Short.parseShort(h_w[1]);
			this.laby = new String[this.height];
			//System.out.println("width :"+this.width);
			//System.out.println("height :"+this.height);
			int i= 0;
			this.dso = new DatagramSocket();
			this.ia = new InetSocketAddress(this.ip,Short.parseShort(this.port));
			while(sc.hasNextLine()){
				this.laby[i++] = sc.nextLine();
				//System.out.println(this.laby[i-1]);
			}
		}catch(Exception e){}

	}
	public void sendAllMessage(byte[] mess){
		try{
		DatagramPacket paquet = new DatagramPacket(mess,mess.length,this.ia);
		this.dso.send(paquet);
		}catch(Exception e){

		}

	}
	public void checkIfAllTheGhostsAreDead(){
		try{
			int c = 0;
		for(int i=0; i < listGhost.size() ;i++){
			if(listGhost.get(i).state == "DEAD") c++;
		}
		String id = listPlayer.get(0).id;
		short score = listPlayer.get(0).score;
		if( c == listGhost.size()){ // the gameis ended!! send the multicaste END
			// get the id score of the winner
			for(int i=0;i<listPlayer.size() ;i++){
				if(score <= listPlayer.get(i).score){
					id = listPlayer.get(i).id;
					score = listPlayer.get(i).score;
				}
			}	
			// send the message
			String pp = Short.toString(score);
			String new_p = "";
									if(pp.length() == 1){
							new_p = "000"+pp;
						}
						if(pp.length() == 2){
							new_p = "00"+pp;
						}
						if(pp.length() == 3){
							new_p = "0"+pp;
						}
								String ms = "END "+id+" "+new_p+"+++";
		byte[] ms1 =ms.getBytes();
		DatagramPacket paquet = new DatagramPacket(ms1,ms1.length,this.ia);
		this.dso.send(paquet);
		this.stateGame = "END";
		}

		}catch(Exception e){

		}
		
	}
	synchronized public void addNewPlayer(Player p){
		
		int nombreAleatoireH = 0 + (int)(Math.random() * ((this.height - 0)));
		int nombreAleatoireW = 0 + (int)(Math.random() * ((this.width - 0)));
		int c= 0;
		while(this.laby[nombreAleatoireH].charAt(nombreAleatoireW) != '1' || c == 0){
			nombreAleatoireH = 0 + (int)(Math.random() * ((this.height - 0)));
			nombreAleatoireW = 0 + (int)(Math.random() * ((this.width - 0)));
			int k =0;
			for(int i=0; i<this.listPlayer.size();i++){
				if(listPlayer.get(i).x != (short)nombreAleatoireH && 
					listPlayer.get(i).y != (short)nombreAleatoireW
					){
					k++;
				}
			}
			if(k == this.listPlayer.size()){
				c = 1;
			}
		}
		p.x = (short)nombreAleatoireH;
		p.y = (short)nombreAleatoireW;
		this.listPlayer.add(p);
	}
	public void movePlayer(Player p,String dir,String steps,Socket s){
		try{
		short steps_1 = Short.parseShort(steps);
		int i = 0;
		DataOutputStream d = new DataOutputStream(s.getOutputStream());
		int is_ghost_down = 0;
		while(i < steps_1){
			if(dir.equals("RIGHT") && p.y < this.width){
				if(p.y >= this.width - 1) break;
				if(this.laby[p.x].charAt(p.y +1) == '1' ){
					p.y++;
					System.out.println("moving right");
					int yes_k = 0;
					for(int j=0;j<this.listGhost.size();j++){
						if(this.listGhost.get(j).x == p.x && this.listGhost.get(j).y == p.y && this.listGhost.get(j).state != "DEAD" ){
							System.out.println("WELL DONE a ghost captured by player : "+p.id);

							this.listGhost.get(j).state = "DEAD";
							yes_k = 1;



						}
					}

					if(yes_k == 1){
						p.score++;
						for(int x =0;x<listPlayer.size();x++){
							if(listPlayer.get(x).id == p.id){
								listPlayer.get(x).score = p.score;
							}
						}
						String new_x = "";
						String new_y = "";
						String new_p = "";

						String x = Short.toString(p.x);
						String y = Short.toString(p.y);
						String pp = Short.toString(p.score);

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

						if(pp.length() == 1){
							new_p = "000"+pp;
						}
						if(pp.length() == 2){
							new_p = "00"+pp;
						}
						if(pp.length() == 3){
							new_p = "0"+pp;
						}
							String scormsg = "SCOR "+p.id+" ";
							scormsg += new_p;
							scormsg += " "+new_x+" "+new_y+" +++";
							byte[] m = scormsg.getBytes();
							
							DatagramPacket paquet = new DatagramPacket(m,m.length,this.ia);
			   				this.dso.send(paquet);
					}
				}else{
					System.out.println("stop moving!!");
					break;

				}

			}
			if(dir.equals("LEFT")){
				if(p.y <= 0 ) break;
				if(this.laby[p.x].charAt(p.y - 1) == '1'){
					p.y--;
					System.out.println("moving left");
					int yes_k = 0;
					for(int j=0;j<this.listGhost.size();j++){
						if(this.listGhost.get(j).x == p.x && this.listGhost.get(j).y == p.y && this.listGhost.get(j).state != "DEAD"){
							System.out.println("WELL DONE a ghost captured by player : "+p.id);

							this.listGhost.get(j).state = "DEAD";
							yes_k = 1;

						}
					}
					if(yes_k == 1){
						p.score++;
												for(int x =0;x<listPlayer.size();x++){
							if(listPlayer.get(x).id == p.id){
								listPlayer.get(x).score = p.score;
							}
						}
												String new_x = "";
						String new_y = "";
						String new_p = "";

						String x = Short.toString(p.x);
						String y = Short.toString(p.y);
						String pp = Short.toString(p.score);

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

						if(pp.length() == 1){
							new_p = "000"+pp;
						}
						if(pp.length() == 2){
							new_p = "00"+pp;
						}
						if(pp.length() == 3){
							new_p = "0"+pp;
						}
							String scormsg = "SCOR "+p.id+" ";
							scormsg += new_p;
							scormsg += " "+new_x+" "+new_y+" +++";
							byte[] m = scormsg.getBytes();
							
							DatagramPacket paquet = new DatagramPacket(m,m.length,this.ia);
			   				this.dso.send(paquet);
					}
				}else{
					System.out.println("stop moving!!");
					break;
				}				
			}
			if(dir.equals("UP")){
				if(p.x <= 0 ) break;
				if(this.laby[p.x - 1].charAt(p.y) == '1'){
					System.out.println("moving up");
					int yes_k = 0;
					for(int j=0;j<this.listGhost.size();j++){
						if(this.listGhost.get(j).x == p.x && this.listGhost.get(j).y == p.y && this.listGhost.get(j).state != "DEAD"){
							System.out.println("WELL DONE a ghost captured by player : "+p.id);
							
							this.listGhost.get(j).state = "DEAD";
							yes_k = 1;

						}
					}
					if(yes_k == 1){
						p.score++;
												for(int x =0;x<listPlayer.size();x++){
							if(listPlayer.get(x).id == p.id){
								listPlayer.get(x).score = p.score;
							}
						}
												String new_x = "";
						String new_y = "";
						String new_p = "";

						String x = Short.toString(p.x);
						String y = Short.toString(p.y);
						String pp = Short.toString(p.score);

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

						if(pp.length() == 1){
							new_p = "000"+pp;
						}
						if(pp.length() == 2){
							new_p = "00"+pp;
						}
						if(pp.length() == 3){
							new_p = "0"+pp;
						}
							String scormsg = "SCOR "+p.id+" ";
							scormsg += new_p;
							scormsg += " "+new_x+" "+new_y+" +++";
							byte[] m = scormsg.getBytes();
							
							DatagramPacket paquet = new DatagramPacket(m,m.length,this.ia);
			   				this.dso.send(paquet);
					}
					p.x--;
				}else{
					System.out.println("stop moving!!");
					break;
				}				
			}
			if(dir.equals("DOWN")){
				if(p.x >= this.height -1 ) break;
				if(this.laby[p.x + 1].charAt(p.y) == '1'){
					System.out.println("moving down");
					int yes_k = 0;
					for(int j=0;j<this.listGhost.size();j++){
						if(this.listGhost.get(j).x == p.x && this.listGhost.get(j).y == p.y && this.listGhost.get(j).state != "DEAD"){
							System.out.println("WELL DONE a ghost captured by player : "+p.id);
							yes_k = 1;
							
							this.listGhost.get(j).state = "DEAD";
							yes_k = 1;

						}
					}
					if(yes_k == 1){
						p.score++;
												for(int x =0;x<listPlayer.size();x++){
							if(listPlayer.get(x).id == p.id){
								listPlayer.get(x).score = p.score;
							}
						}
												String new_x = "";
						String new_y = "";
						String new_p = "";

						String x = Short.toString(p.x);
						String y = Short.toString(p.y);
						String pp = Short.toString(p.score);

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

						if(pp.length() == 1){
							new_p = "000"+pp;
						}
						if(pp.length() == 2){
							new_p = "00"+pp;
						}
						if(pp.length() == 3){
							new_p = "0"+pp;
						}
							String scormsg = "SCOR "+p.id+" ";
							scormsg += new_p;
							scormsg += " "+new_x+" "+new_y+" +++";
							byte[] m = scormsg.getBytes();
							
							DatagramPacket paquet = new DatagramPacket(m,m.length,this.ia);
			   				this.dso.send(paquet);
					}
					p.x++;
				}else{
					System.out.println("stop moving!!");
					break;
				}				
			}
			i++;				
			}
			System.out.println("stop moving!! and trying to send the new pos");
			if(is_ghost_down == 0){
				//MOV
				System.out.println("the server gonna try to send [MOV....]");
				byte[] ms = new byte[14];
				ms[0] = (byte)'M';
				ms[1] = (byte)'O';
				ms[2] = (byte)'V';
				ms[3] = (byte)' ';
				String x = Short.toString(p.x);
				String y = Short.toString(p.y);
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
				System.out.println("testing: new pos x ="+new_x);
				System.out.println("testing: new pos y ="+new_y);

				ms[4] = (byte)new_x.charAt(0);
				ms[5] = (byte)new_x.charAt(1);
				ms[6] = (byte)new_x.charAt(2);
				ms[7] = (byte)' ';
				System.out.println("¤");
				ms[8] = (byte)new_y.charAt(0);
				ms[9] = (byte)new_y.charAt(1);
				ms[10] = (byte)new_y.charAt(2);
				ms[11] = (byte)'*';
				ms[12] = (byte)'*';
				ms[13] = (byte)'*';
				//Thread.sleep(100);
				System.out.println("before !!  sending the  [MOV....] !!");
				d.write(ms);
				System.out.println("sending the  [MOV....] !!");



			}else{
				//MOF
				System.out.println("the server gonna try to send [MOF....]");
				byte[] ms = new byte[19];
				ms[0] = (byte)'M';
				ms[1] = (byte)'O';
				ms[2] = (byte)'F';
				ms[3] = (byte)' ';
				String x = Short.toString(p.x);
				String y = Short.toString(p.y);
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
				System.out.println("testing: new pos x ="+new_x);
				System.out.println("testing: new pos y ="+new_y);

				ms[4] = (byte)new_x.charAt(0);
				ms[5] = (byte)new_x.charAt(1);
				ms[6] = (byte)new_x.charAt(2);
				ms[7] = (byte)' ';
				ms[8] = (byte)new_y.charAt(0);
				ms[9] = (byte)new_y.charAt(1);
				ms[10] = (byte)new_y.charAt(2);
				ms[11] = (byte)' ';
				String p_1 = Short.toString(p.score);
				String p_2 = "";
				if(p_1.length() == 1){
					p_2 = "000"+p_1;
				}
				if(p_1.length() == 2){
					p_2 = "00"+p_1;
				}
				if(p_1.length() == 3){
					p_2 = "0"+p_1;
				}
				ms[12] = (byte)p_2.charAt(0);
				ms[13] = (byte)p_2.charAt(1);
				ms[14] = (byte)p_2.charAt(2);
				ms[15] = (byte)p_2.charAt(3);
				ms[16] = (byte)'*';
				ms[17] = (byte)'*';
				ms[18] = (byte)'*';
				d.write(ms);
			}
			checkIfAllTheGhostsAreDead();

		}catch(Exception e){

		}

			
			
		

	}
	public  void startMovingGhost(){
		
		for(int ii=0;ii<this.nb_ghost;ii++){
					int nombreAleatoireH = 0 + (int)(Math.random() * ((this.height - 0)));
		int nombreAleatoireW = 0 + (int)(Math.random() * ((this.width - 0)));
		int c= 0;
		while(this.laby[nombreAleatoireH].charAt(nombreAleatoireW) != '1' || c == 0){
			nombreAleatoireH = 0 + (int)(Math.random() * ((this.height - 0)));
			nombreAleatoireW = 0 + (int)(Math.random() * ((this.width - 0)));
			int k =0;
			for(int i=0; i<this.listGhost.size();i++){
				if(listGhost.get(i).x != (short)nombreAleatoireH && 
					listGhost.get(i).y != (short)nombreAleatoireW
					){
					k++;
				}
			}
			if(k == this.listGhost.size()){
				c = 1;
			}
		}

		short x = (short)nombreAleatoireH;
		short y = (short)nombreAleatoireW;
		Ghost g = new Ghost(x,y);
		this.listGhost.add(g);
		System.out.println("new ghost added");
		}

		///////
		/*
		Ghost g1 = new Ghost((short)0,(short)1);
		Ghost g2 = new Ghost((short)3,(short)6);
		Ghost g3 = new Ghost((short)6,(short)7);
		this.listGhost.add(g1);
		this.listGhost.add(g2);
		this.listGhost.add(g3);
		*/
		ThreadGhostMoving tgm = new ThreadGhostMoving(this.listGhost,this);
		Thread t = new Thread(tgm);
		t.start();

	}
	/*
	public static void main(String[] args){
		Labyrinthe l = null;
		System.out.println("hi");
		l = new Labyrinthe((short)0,(short)2,"127.0.1.2,","1234");
		Player p1 = new Player();
		Player p2 = new Player();
		l.addNewPlayer(p1);
		l.addNewPlayer(p2);
		System.out.println(p1.x);
		System.out.println(p1.y);
		System.out.println(p2.x);
		System.out.println(p2.y);


	}
	*/







}









