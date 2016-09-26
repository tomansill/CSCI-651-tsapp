/* @author Thomas Ansill
 * CSCI-651-03
 * Project 1
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
/** The ViewProxy for UDP Datagram. This object will act as proxy for a view. This proxy relays to the actual view
 *  using UDP datagram.
 */
public class UDPViewProxy extends Thread implements ViewListener{
    /** UDP Datagram mailbox */
	private DatagramSocket mailbox;
    /** UDP Datagram packet that was received by the server */
	private DatagramPacket packet;
    /** ModelListener, could be Model itself or a ModelProxy */
	private ModelListener modelListener;
    /** Constructor for ViewProxy
     *  @param mailbox defined UDP datagram mailbox for sending messages
     *  @param packet Packet caught by the server
     *  @param modelListener ModelListener, could be Model itself or a ModelProxy
     */
	public UDPViewProxy(DatagramSocket mailbox, DatagramPacket packet, ModelListener modelListener) throws Exception{
        this.mailbox = mailbox;
		this.packet = packet;
		this.modelListener = modelListener;
	}
    /** Method when model responds to the view with time
     *  UDP Datagram will consist of "t (long)<time> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
	public void sendTime(long time, int hops, long triptime) throws Exception{
        this.sendTime(time, hops, triptime, "");
    }
    /** Method when model responds to the view with time
     *  UDP Datagram will consist of "t (long)<time> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
	public void sendTime(long time, int hops, long triptime, String strRTT) throws Exception{
		try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeByte('t');
			out.writeLong(time);
            out.writeInt(hops);
            out.writeLong(triptime);
            out.writeUTF(strRTT);
            out.close();
            byte[] payload = baos.toByteArray();
            mailbox.send(new DatagramPacket(payload, payload.length, packet.getSocketAddress()));
            System.out.println("\t<< Sending time " + time);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
	}
    /** Method when model responds to the view with status on setTime command
     *  UDP Datagram will consist of "s (boolean)<status> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
	public void timeChangeStatus(boolean status, int hops, long triptime) throws Exception{
        this.timeChangeStatus(status, hops, triptime, "");
    }
    /** Method when model responds to the view with status on setTime command
     *  UDP Datagram will consist of "s (boolean)<status> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
	public void timeChangeStatus(boolean status, int hops, long triptime, String strRTT) throws Exception{
		try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeByte('s');
			out.writeBoolean(status);
            out.writeInt(hops);
            out.writeLong(triptime);
            out.writeUTF(strRTT);
            out.close();
            byte[] payload = baos.toByteArray();
            mailbox.send(new DatagramPacket(payload, payload.length, packet.getSocketAddress()));
            System.out.println("\t<< Sending status " + status);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
	}
    /** Thread's run method, will be run as a separate thread to allow server to serve multiple datagrams concurrently.
     *  This thread processes the datagram packet and act on appropraite method.
     */
	public void run(){
        try{
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            byte b = in.readByte();
            System.out.print("\rIncoming UDP address: " + this.packet.getSocketAddress() + "\t");
            System.out.print(">> " + (char)b);
            switch(b){
                // Format: g 
                case 'g':	modelListener.getTime(this);
                            break;
                // Format T <long> <String> <String>
                case 'T':	long time = in.readLong();
                            System.out.print(" " + time);
                            String username = in.readUTF();
                            System.out.print(" " + username);
                            String password = in.readUTF();
                            System.out.print(" " + password);
                            modelListener.setTime(time, username, password, this);
                            break;
                default:	System.err.println("ERROR: Incorrect message formatting!");
                            break;
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
	}
}
