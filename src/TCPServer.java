/* @author Thomas Ansill
 * CSCI-651-03
 * Project 1
 */
import java.net.Socket;
import java.net.ServerSocket;
 /** The TCP ServerSocket server */
public class TCPServer extends Thread{
    /** TCP Server Socket */
	private ServerSocket serversocket;
    /** TCP Server Socket port */
	private int port;
    /** The ModelListener, could be a model or modelproxy */
	private ModelListener model;
    /** Constructor for TCPServer
     *  @param port Port to be used for server socket
     *  @param model The ModelListener, could be a model or modelproxy
     */
	public TCPServer(int port, ModelListener model) throws Exception{
		this.port = port;
		this.serversocket = new ServerSocket(port);
		this.model = model;
	}
    /** The run method, will always waiting and processing sockets. When a socket is connected,
     *  new thread will be created to process and respond to the socket.
     */
	public void run(){
        System.out.println("\rTCP server is up and running!");
		try{
			while(true){
				System.out.print("\rRunning...                                                                                     ");
				//Get TCP socket and get a worker to process the socket then get another socket
				Socket socket = serversocket.accept();
				new TCPViewProxy(socket, model).start();
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}finally{
            try{serversocket.close();}
            catch(Exception e){}
		}
	}
}
