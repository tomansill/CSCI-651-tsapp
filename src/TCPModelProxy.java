/* @author Thomas Ansill
* CSCI-651-03
* Project 1
*/
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
/** The ModelProxy for TCP Socket. This object will act as proxy for a model. This proxy relays to the actual model
*  using TCP Socket.
*/
public class TCPModelProxy implements ModelListener{
    /** TCP Socket */
    private Socket socket;
    /** hostname used for TCP Socket */
    private String host;
    /** Port used for TCP Socket */
    private int port;
    /** Constructor for ModelProxy
    *  @param host String version of hostname, could be IP address or "localhost"
    *  @param port Port number to be used to connect the Model
    */
    public TCPModelProxy(String host, int port) throws Exception{
        this.socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        this.host = host;
        this.port = port;
    }
    /** Method when outside accesses the model's time
    *  TCP Stream will consist of "g"
    *  This method includes the time-out timer that will stop the listening if stream takes too long to arrive
    *  @param viewListener The requesting view - use this listener to respond the time to.
    */
    public void getTime(ViewListener listener) throws Exception{
        //Construct Threads and CountDownLatch
        final CountDownLatch cdl = new CountDownLatch(1); //Only require one of two threads to finish
        TimeoutThread timeout = new TimeoutThread(cdl, 10000L); //10 seconds
        ModelListenerThread.TCP mlt = new ModelListenerThread.TCP(true, cdl, socket); //"true" for getTime instead of setTime method
        mlt.start(); //Start the reading early
        //Write message in the stream and send out
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeByte('g');
        out.flush();
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
    *  TCP Stream will consist of "T (long)<time>"
    *  This method includes the time-out timer that will stop the listening if stream takes too long to arrive
    *  @param status true if setTime was successful, otherwise false if incorrect credentials
    *  @param hops Number of hops made when this message reaches to the view from the model
    */
    public void setTime(long time, String username, String password, ViewListener listener) throws Exception{
        //Construct Threads and CountDownLatch
        final CountDownLatch cdl = new CountDownLatch(1); //Only require one of two threads to finish
        TimeoutThread timeout = new TimeoutThread(cdl, 10000L); //10 seconds
        ModelListenerThread.TCP mlt = new ModelListenerThread.TCP(false, cdl, socket); //"true" for getTime instead of setTime method
        mlt.start(); //Start the reading early
        //Write message in the stream and send out
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeByte('T');
        out.writeLong(time);
        out.writeUTF(username);
        out.writeUTF(password);
        out.flush();
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
    /** Simple clean-up method that closes the socket if it is already open */
    public void cleanUp(){
        try{ socket.close(); }
        catch(Exception e){} //If throws exception, assume that the socket is already been closed.
    }
}
