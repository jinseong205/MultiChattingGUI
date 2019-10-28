package java_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

//write thread
class WriteThread{
	Socket socket;
	MainGUI mg;
	String str;
	String user_id,user_pw;
	public WriteThread(MainGUI mg) {
		this.mg  = mg;
		this.socket= mg.socket;
	}
	
	public void sendMsg() {
		//create buffer to recive data from keyboard
		BufferedReader write_buf=new BufferedReader(new InputStreamReader(System.in));
		PrintWriter print_out=null;
		try{
			//create stream object to send data to server
			print_out=new PrintWriter(socket.getOutputStream(),true);
			
			if(mg.isFirst==true){
				InetAddress iaddr=socket.getLocalAddress();				
				String ip = iaddr.getHostAddress();				
				getID();
				getPW();
				
				print_out.println(user_id);
				print_out.println(user_pw);				
				str = "["+user_id+"] login("+ip+")"; 
			}
			else if(mg.isLast==true){
				print_out.println("/quit");
			}
			else{
				str= mg.txt_field.getText();			
				print_out.println(str);
			}
			
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(write_buf!=null) write_buf.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}	
	public void getID(){		
		user_id = LoginGUI.getID(); 
	}
	public void getPW(){		
		user_pw = LoginGUI.getPW(); 
	}
}

class ReadThread extends Thread{
	Socket socket;
	MainGUI mg;
	public ReadThread(Socket socket, MainGUI mg) {
		this.mg = mg;
		this.socket=socket;
	}
	public void run() {
		BufferedReader read_buf=null;
		try{
			//create buffer to recive data from server
			read_buf=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				//read string from socket
				String str=read_buf.readLine();
				if(str==null){
					System.out.println("disconnected");
					break;
				}
				//print msg
				mg.txt_area.append(str+"\n");
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(read_buf!=null)read_buf.close();
				if(socket!=null) socket.close();
			}catch(IOException ie){}
		}
	}
}
public class MultiChatClient {
	public static void main(String[] args) {
		Socket socket=null;
		MainGUI cf;
		try{
			socket=new Socket("xxx.xxx.xxx.xxx",9100);
			System.out.println("connected!");
			cf = new MainGUI(socket);
			new ReadThread(socket, cf).start();
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}












