import java.io.*;
import java.net.*;
import java.util.*;

class Game{
	public short num_game;
	public short nb_joueurs;
	public String state_game; // the state of the game ca be READY_TO_START  or NOT_READY_TO_START
	public Map<String, String> listPlayerID;  // state of the player can be READY or NOT_READY or LEFT_THE_GAME
	public Map<String, String> listPlayerPORT;  // state of the player can be READY or NOT_READY or LEFT_THE_GAME
	////// game height,weight
	public short height;
	public short width;

	public List<Player> listOfReadyPlayer;


	public String port;
	public String ip;

	public short nb_ghost;
	public Labyrinthe labyrinthe;

	Game(short num_game, short nb_joueurs){
		this.num_game = num_game;
		this.nb_joueurs = nb_joueurs;
		this.state_game = "NOT_READY_TO_START";
		this.listPlayerID = new HashMap<>();
		this.listPlayerPORT = new HashMap<>();
		this.height = (short)0;
		this.width = (short)0;

		this.port = "5445";
		this.ip   = "225.0.0."+(Short.toString((short)(num_game)));
		this.nb_ghost = 5; // pour tester



			
		this.listOfReadyPlayer = new ArrayList<Player>();

		this.labyrinthe =  new Labyrinthe(this.num_game,(short)5,this.ip,this.port);

	}
	void setStateGame(String state){  // READY_TO_START  or NOT_READY_TO_START
		this.state_game = state;  
	}
	synchronized void addNewPlayer(String id,String port){
		this.listPlayerID.put(id,"NOT_READY");
		this.listPlayerPORT.put(id,port);
		this.nb_joueurs++;
	}
	synchronized void removePlayer(String id){
		this.listPlayerID.put(id,"LEFT_THE_GAME");
		this.nb_joueurs--;
	}
	void ShowTheListOfPlayers(){
		for (Map.Entry<String, String> entry : this.listPlayerID.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + " " + value);
		}		
	}
	void startGame(){
		int counter = 0;
		for (Map.Entry<String, String> entry : this.listPlayerID.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if(value.equals("READY")){
				counter++;
			}
		}
		if(counter == this.nb_joueurs){
			this.state_game = "READY_TO_START";
			System.out.println("The game n : "+this.num_game+" is starting right now with "+this.nb_joueurs+" players !");
			// genereate laby...
			this.labyrinthe = new Labyrinthe(this.num_game,(short)5,this.ip,this.port);
			/*
			try{
			this.labyrinthe.dso = new DatagramSocket();
			this.labyrinthe.ia = new InetSocketAddress(this.labyrinthe.ip,Short.parseShort(this.labyrinthe.port));
			}catch(Exception e){}
			*/
			System.out.println("the socket of multid is ip :"+this.labyrinthe.ip+" and the port is : "+this.labyrinthe.port);
			this.labyrinthe.startMovingGhost();


		}else{
			System.out.println("There is still anothers players");
		}
	}
	void playerIsReady(String id){
		this.listPlayerID.put(id,"READY");
		// trigger the starting game
		this.startGame();
	}
    void addPlayerToTheReadyList(Player p){
		//this.listOfReadyPlayer.add(p);
		this.labyrinthe.addNewPlayer(p);
	}
}