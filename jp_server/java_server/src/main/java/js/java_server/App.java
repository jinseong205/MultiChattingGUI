package js.java_server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


class ServerThread extends Thread{

	Socket socket;
    HashMap<String, PrintWriter> user_info;

    BufferedReader input;
    PrintWriter print_out;
    String user_id;
    String user_pw;
    InetAddress ip;
  
  
	public ServerThread(Socket socket, HashMap<String,PrintWriter> user_info){
		this.socket = socket;
		this.user_info = user_info;

	
    //db connect
   	MongoClient mongoClient = MongoClients.create();
   	MongoDatabase database = mongoClient.getDatabase("jp");
   	MongoCollection<Document> collection = database.getCollection("user");   	    	 
   	
   	//1st : login
   	try {
          
   		  input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
          print_out = new PrintWriter( socket.getOutputStream() );

          user_id = input.readLine();
          user_pw = input.readLine();
          System.out.println("user id : " + user_id  + " userpw : " + user_pw);
          input.readLine();
          
          ip = socket.getInetAddress();
          
         
          synchronized(user_info) {   
              user_info.put( user_id, print_out );
          }
                   
          	if(login(collection,user_id,user_pw)) {
          		System.out.println( user_id + "  connected	from" + ip);
          		print_out.println("***login success***");
          		print_out.flush();

          		broadcast("* " +user_id + "  connected" +" *" );
          	}
          	else{
          		print_out.println("***login fail***");
          		print_out.flush();
 
                user_info.remove( user_id );
        		socket.close();
				return;
          	}
          	mongoClient.close();
   		}
      catch (Exception e ) {
          e.printStackTrace();
      }
	}
	
	
	public void run(){
	       
		String receiveData;
        
		try
        {
            while( (receiveData = input.readLine()) != null ) {
               
            	if( receiveData.equals( "/quit" ) ) {
                    synchronized( user_info ) {
                        user_info.remove( user_id );
                        broadcast("* " + user_id + "  disconnected" +" *"  );
                        System.out.println("* " + user_id + "  disconnected" +" *"  );
                    }
                    break;
                }
            	else if( receiveData.indexOf( "/to" ) >= 0 ) {
                    sendMsg( receiveData );
                }
                else{
                    System.out.println(user_id + " >> " + receiveData );
                    broadcast( user_id + " >> " + receiveData );
                }              

            }
 
        }
 
        catch (Exception e ) {
            e.printStackTrace();
        }
 
        finally {
            synchronized(user_info) {
                user_info.remove( user_id );
            }
            try
            {
                if( socket != null ) {
                    input.close();
                    print_out.close();
                    socket.close();
                }
            }
            catch ( Exception e) {}
        }
	}
	
	
    public void broadcast(String message){
        synchronized(user_info ) {
            try{
                for( PrintWriter print_out : user_info.values( )){
                  print_out.println( message );
                  print_out.flush();
                }
            }catch(Exception e){ }
        }
    }
	
    public void sendMsg(String message){
        int begin = message.indexOf(" ") + 1;
        int end   = message.indexOf(" ", begin);
 
        if(end != -1){
            String id = message.substring(begin, end);
            String msg = message.substring(end+1);
            PrintWriter print_out = user_info.get(id);
 
            try{
                if(print_out != null){
                    print_out.println( "("+ user_id + ")" + msg );
                    print_out.flush();
                }
 
            }catch(Exception e)
            {             
            }
        }
    }
	
	static boolean login(MongoCollection<Document> collection, String id, String pw){

		 MongoCursor<Document> cursor = collection.find(new Document("id",id).append("pw", pw)).iterator();
		 
		 if(cursor.hasNext()) {
			 	System.out.println("***login success***");
			 	cursor.close();
			 	 return true;
				}
		 else {
			 System.out.println("***login fail***");
			 cursor.close();
			 return false;
		 }
	} 
}

//create sever socket & create thread
public class App {
	public static void main(String[] args) {
		
		ServerSocket server = null;
		Socket socket =null;
		
		HashMap<String, PrintWriter> user_info;
		
		try{
			server= new ServerSocket(9100);
	        user_info = new HashMap<String, PrintWriter>();
			while(true){
				System.out.println("Waiting for client to connect..");
				socket = server.accept();
				new ServerThread(socket,user_info).start();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}

