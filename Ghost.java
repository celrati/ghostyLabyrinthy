import java.io.*;
import java.net.*;
import java.util.*;

class Ghost{
	public short x;
	public short y;
	public String state; // alive or dead

	Ghost(short x,short y){
		this.x = x;
		this.y = y;
		this.state = "ALIVE";
	}
	public void  killGhost(){
		this.state = "DEAD";
	}

}