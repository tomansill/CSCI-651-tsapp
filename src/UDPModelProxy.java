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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

/** The ModelProxy for UDP Datagram. This object will act as proxy for a model. This proxy relays to the actual model
 *  using UDP datagram.
 */
public class UDPModelProxy implements ModelListener{
    /** UDP Datagram mailbox */
	private DatagramSocket mailbox;
    /** Port used for UDP Datagram mailbox */
    private int port;
    /** Destination address to send datagrams to */
	private SocketAddress destination;
    /** Constructor for ModelProxy
     *  @param host String version of hostname, could be IP address or "localhost"
     *  @param port Port number to be used to connect the Model
     */
    public UDPModelProxy(String host, int port) throws Exception{
        this.destination = new InetSocketAddress(host, port);
        //int random = (int)((Math.random()*100)+1); //These lets me to run UDP on localhost
        //this.port = port + random; //change later
		this.mailbox = new DatagramSocket(this.port);
	}
    /** Method when outside accesses the model's time
     *  UDP Datagram will consist of "g"
     *  This method includes the time-out timer that will stop the listening if datagram takes too long to arrive
     *  @param viewListener The requesting view - use this listener to respond the time to.
     */
	public void getTime(ViewListener listener) throws Exception{
        //Construct Threads and CountDownLatch
        final CountDownLatch cdl = new CountDownLatch(1); //Only require one of two threads to finish
        TimeoutThread timeout = new TimeoutThread(cdl, 10000L); //10 seconds
        ModelListenerThread.UDP mlt = new ModelListenerThread.UDP(true, cdl, mailbox); //"true" for getTime instead of setTime method
        mlt.start(); //Start the reading early
        //Write message in the stream and send out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeByte('g');
        out.close();
        byte[] payload = baos.toByteArray();
        mailbox.send(new DatagramPacket(payload, payload.length, destination));
        //Begin the timeout and await for a response
        timeout.start();
        cdl.await(); //Wait for either one of Timeout or ModelListenerThread to finish
        if(!mlt.isFinished()){ //Check if ModelListenerThread has finished processing the message
            mlt.interrupt(); //Stop Thread from listening
            System.err.println("\ngetTime request timed out!");
        }else if(mlt.isFailed()) System.err.println("\ngetTime request failed!");
        else{
            listener.sendTime(mlt.getTime(), mlt.getHops(), mlt.getTripTime(), mlt.getRTT());
            timeout.interrupt(); //Close the Timeout Thread
        }
	}
    /** Method when model responds to the view with status on setTime command
     *  UDP Datagram will consist of "T (long)<time>"
     *  This method includes the time-out timer that will stop the listening if datagram takes too long to arrive
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     */
	public void setTime(long time, String username, String password, ViewListener listener) throws Exception{
        //Construct Threads and CountDownLatch
        final CountDownLatch cdl = new CountDownLatch(1); //Only require one of two threads to finish
        TimeoutThread timeout = new TimeoutThread(cdl, 10000L); //10 seconds
        ModelListenerThread.UDP mlt = new ModelListenerThread.UDP(false, cdl, mailbox); //"true" for getTime instead of setTime method
        mlt.start(); //Start the reading early
        //Write message in the stream and send out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeByte('T');
        out.writeLong(time);
        out.writeUTF(username);
        out.writeUTF(password);
        out.close();
        byte[] payload = baos.toByteArray();
        mailbox.send(new DatagramPacket(payload, payload.length, destination));
        //Begin the timeout and await for a response
        timeout.start();
        cdl.await(); //Wait for either one of Timeout or ModelListenerThread to finish
        if(!mlt.isFinished()){ //Check if ModelListenerThread has finished processing the message
            mlt.interrupt(); //Stop Thread from listening
            System.err.println("\nsetTime request timed out!");
        }else if(mlt.isFailed()) System.err.println("\nsetTime request failed!");
        else{
            listener.timeChangeStatus(mlt.getStatus(), mlt.getHops(), mlt.getTripTime(), mlt.getRTT());
            timeout.interrupt(); //Close the Timeout Thread
        }
	}

    /** Forces the mailbox to stop listening */
    private void resetSocket() throws Exception{
        mailbox.close();
        mailbox = new DatagramSocket(port);
    }
}
