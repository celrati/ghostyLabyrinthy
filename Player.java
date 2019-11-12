import java.io.*;
import java.net.*;
import java.util.*;

class Player{
	public String id;
	public String port;
	public short num_game;
	public String state;
	public short x;
	public short y;
	public short score;
	public String ip;

	Player(){
		this.id = "";
		this.port = "";
		this.num_game = -1;
		this.state = "NOT_STARTING";
		this.ip = "";
	}
	void setX(short x){
		this.x = x;
	}
	void setY(short y){
		this.y = y;
	}
	void setSCore(short s){
		this.score = s;
	}
}