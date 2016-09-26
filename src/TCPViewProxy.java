/* @author Thomas Ansill
 * CSCI-651-03
 * Project 1
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketAddress;
/** The ViewProxy for TCP Socket. This object will act as proxy for a view. This proxy relays to the actual view
 *  using TCP socket.
 */
public class TCPViewProxy extends Thread implements ViewListener{
    /** TCP Socket */
	private Socket socket;
    /** ModelListener, could be Model itself or a ModelProxy */
	private ModelListener modelListener;
    /** Constructor for ViewProxy
     *  @param socket Existing TCP socket for reading and sending messages
     *  @param modelListener ModelListener, could be Model itself or a ModelProxy
     */
	public TCPViewProxy(Socket socket, ModelListener modelListener) throws Exception{
		this.socket = socket;
		this.modelListener = modelListener;
	}
    /** Thread's run method, will be run as a separate thread to allow server to serve multiple sockets concurrently.
     *  This thread processes the socket stream and act on appropraite method.
     */
	public void run(){
        try{
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while(true){
                byte b = in.readByte();
                System.out.print("\rIncoming TCP address: " + this.socket.getLocalSocketAddress() + "\t");
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
            }
        }catch(EOFException e){
            //System.out.println("Socket closed");
        }catch(Exception e){
            System.err.println(e);
        }
	}
    /** Method when model responds to the view with time
     *  TCP Stream will consist of "t (long)<time> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
	public void sendTime(long time, int hops, long triptime) throws Exception{
        this.sendTime(time, hops, triptime, "");
    }
    /** Method when model responds to the view with time
     *  TCP Stream will consist of "t (long)<time> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
	public void sendTime(long time, int hops, long triptime, String strRTT) throws Exception{
		try{
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeByte('t');
			out.writeLong(time);
            out.writeInt(hops);
            out.writeLong(triptime);
            out.writeUTF(strRTT);
            out.flush();
            System.out.println("\t<< Sending time " + time);
            //System.out.print("Running...                                                        ");
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
	}
    /** Method when model responds to the view with status on setTime command
     *  TCP Stream will consist of "s (boolean)<status> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
	public void timeChangeStatus(boolean status, int hops, long triptime) throws Exception{
        this.timeChangeStatus(status, hops, triptime, "");
    }
    /** Method when model responds to the view with status on setTime command
     *  TCP Stream will consist of "s (boolean)<status> (int)<hops> (long)<triptime> (String)<strRTT>"
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
	public void timeChangeStatus(boolean status, int hops, long triptime, String strRTT) throws Exception{
		try{
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeByte('s');
			out.writeBoolean(status);
            out.writeInt(hops);
            out.writeLong(triptime);
            out.writeUTF(strRTT);
            out.flush();
            System.out.println("\t<< Sending status " + status);
            System.out.print("Running...");
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
	}

}
