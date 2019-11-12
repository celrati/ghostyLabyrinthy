import java.io.*;
import java.net.*;
import java.util.*;

class ThreadGhostMoving implements Runnable{
	public List<Ghost> listGhost;
	public Labyrinthe laby;

	public ThreadGhostMoving(List<Ghost> l,Labyrinthe laby){
		this.listGhost = l;
		this.laby = laby;
	}
	public void run(){
		int c = 0;
		while(true){
			try{
				Thread.sleep(1000);
				for(int i=0; i< this.listGhost.size() ; i++){
					if(this.listGhost.get(i).state == "ALIVE"){
						if(this.listGhost.get(i).y == this.laby.width -1){
							this.listGhost.get(i).y = 0;

						}else{
							this.listGhost.get(i).y++;
						}

						//System.out.println("new pos of ghost num "+i+" is x = "+this.listGhost.get(i).x+" y = "+this.listGhost.get(i).y);
						byte[] ms = new byte[15];
						ms[0] = (byte)'F';
						ms[1] = (byte)'A';
						ms[2] = (byte)'N';
						ms[3] = (byte)'T';
						ms[4] = (byte)' ';
						String new_x = "";
						String new_y = "";

						String x = Short.toString(listGhost.get(i).x);
						String y = Short.toString(listGhost.get(i).y);

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
						ms[5] = (byte)new_x.charAt(0);
						ms[6] = (byte)new_x.charAt(1);
						ms[7] = (byte)new_x.charAt(2);
						ms[8] = (byte)' ';
						ms[9] = (byte)new_y.charAt(0);
						ms[10] = (byte)new_y.charAt(1);
						ms[11] = (byte)new_y.charAt(2);
						ms[12] = (byte)'+';
						ms[13] = (byte)'+';
						ms[13] = (byte)'+';
						try{
							DatagramPacket paquet = new DatagramPacket(ms,ms.length,this.laby.ia);
							this.laby.dso.send(paquet);
						}catch(Exception e){

						}


					}
					
				}
				// apply the changes to the file Graphics/laby
				 FileWriter fileWriter = new FileWriter("Graphics/laby");
   				 PrintWriter printWriter = new PrintWriter(fileWriter);
   				 String a1 = "$"+Short.toString(laby.height)+" "+Short.toString(laby.width)+"\n";
   				 printWriter.print(a1);
   				 printWriter.print("###\n");
   				 for(int i=0;i<laby.listPlayer.size();i++){
   				 	Player p = laby.listPlayer.get(i);
   				 	printWriter.print(p.id+":"+p.ip+":"+p.port+":"+Short.toString(p.score)+"\n");
   				 }
   				 printWriter.print("###\n");
   				 for(int i=0;i<laby.laby.length;i++){
   				 	String line = "";
   				 	for(int j=0;j<laby.laby[i].length() ;j++){
   				 		// test if there is a ghost or player in this pos
   				 		int cc = 0;
   				 		for(int x = 0 ; x <laby.listGhost.size() ;x++){
   				 			if(laby.listGhost.get(x).x == i && laby.listGhost.get(x).y == j && 
   				 				laby.listGhost.get(x).state != "DEAD"){
   				 				line += '2';
   				 				cc = 1;
   				 				break;

   				 			}
   				 		}
   				 		if(cc == 1) continue;
   				 		for(int x = 0 ; x <laby.listPlayer.size() ;x++){
   				 			if(laby.listPlayer.get(x).x == i && laby.listPlayer.get(x).y == j){
   				 				line += '3';
   				 				cc = 1;
   				 				break;

   				 			}
   				 		}
   				 		if(cc == 1) continue;
   				 		line += laby.laby[i].charAt(j);


   				 	}
   				 	printWriter.write(line+"\n");
   				 }

   				 printWriter.close();
			}catch(Exception e){

			}



			if(c == 1) break;
		}
	}



}