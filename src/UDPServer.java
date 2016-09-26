/* @author Thomas Ansill
 * CSCI-651-03
 * Project 1
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
 /** The UDP Datagram server */
public class UDPServer extends Thread{
    /** UDP Datagram mailbox */
	private DatagramSocket mailbox;
    /** UDP Datagram mailbox port */
	private int port;
    /** The ModelListener, could be a model or modelproxy */
	private ModelListener model;
    /** Constructor for UDPServer
     *  @param port Port to be used for datagram mailbox
     *  @param model The ModelListener, could be a model or modelproxy
     */
	public UDPServer(int port, ModelListener model) throws Exception{
		this.port = port;
		this.mailbox = new DatagramSocket(port);
		this.model = model;
	}
    /** The run method, will always waiting and processing datagram packets. When a packet is received,
     *  new thread will be created to process and respond to the packet.
     */
	public void run(){
        System.out.println("\rUDP server is up and running!");
		byte[] payload = new byte[1024];
		try{
			while(true){
				//Get packet and get a worker to process the packet then get another packet
				System.out.print("\rRunning...                                                                              ");
				DatagramPacket packet = new DatagramPacket(payload, payload.length);
				mailbox.receive(packet);
				new UDPViewProxy(mailbox, packet, model).start();
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}finally{
			mailbox.close();
		}
	}
}
