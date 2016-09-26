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
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
/** ModelListenerThread is a class that handles all reading fromf network streams. It has two subclasses, TCP and UDP. */
public abstract class ModelListenerThread extends Thread{
    /** Switch for deciding between getTime or setTime methods */
    public boolean gettime;
    /** getTime return for parent to retrieve data */
    private long time = 0;
    /** Hops return for parent to retrieve data */
    private int hops = 0;
    /** triptime return for parent to retrieve data */
    private long triptime = 0;
    /** string RTT return for parent to retrieve data */
    private String strRTT = "";
    /** setTime status return for parent to retrieve data */
    private boolean status = false;
    /** Flag for parent to monitor to see if thread is successful or not */
    public boolean failed = false;
    /** Flag for parent to monitor to see if thread had finished its task */
    private boolean finished = false;
    /** CountDownLatch to let parent know when thread is finished */
    private CountDownLatch cdl;
    /** Constructor
    * @param gettime Decides which method to use between getTime or setTime... (pretty lousy programming style though)
    * @param listener Viewlistener to convey the received message to
    */
    public ModelListenerThread(boolean gettime, CountDownLatch cdl){
        this.gettime = gettime;
        this.cdl = cdl;
    }
    /** Accessor for parent to retrieve the variable and check if thread had finished its task
    *  @return finished status of thread's completion
    */
    public boolean isFinished(){ return finished; }
    /** Accessor for parent to retrieve the variable and check if thread is successful or not
    *  @return failed status of thread's completion
    */
    public boolean isFailed(){ return failed; }
    /** Accessor for parent to retrieve the variable
    *  @return hops number of hops
    */
    public int getHops(){ return hops; }
    /** Accessor for parent to retrieve the variabl
    *  @return hops number of hops
    */
    public long getTripTime(){ return triptime; }
    /** Accessor for parent to retrieve the variabl
    *  @return hops number of hops
    */
    public String getRTT(){ return strRTT; }
    /** Accessor for parent to retrieve the variabl
    *  @return status status of setTime
    */
    public boolean getStatus(){ return status; }
    /** Accessor for parent to retrieve the variabl
    *  @return time the time of getTime
    */
    public long getTime(){ return time; }
    /** This is for getTime()
    *   @param in DataInputStream for packet
    */
    public void runGetTime(DataInputStream in) throws Exception{
        byte type = in.readByte();
        if(type == 't'){
            time = in.readLong();
            hops = in.readInt();
            triptime = in.readLong();
            strRTT = in.readUTF();
            hops++;
            strRTT += "Hop #" + hops + " delay: " + (System.currentTimeMillis() - triptime) + " milliseconds.\n";
            triptime = System.currentTimeMillis();
            finished = true;
            cdl.countDown();
        }else{
            System.err.println("ERROR: Cannot read getTime, incorrect formatting!");
            failed = true;
        }
    }
    /** This is for setTime()
    *   @param in DataInputStream for packet
    */
    public void runSetTime(DataInputStream in) throws Exception{
        byte type = in.readByte();
        if(type == 's'){
            status = in.readBoolean();
            hops = in.readInt();
            triptime = in.readLong();
            strRTT = in.readUTF();
            hops++;
            strRTT += "Hop #" + hops + " delay: " + (System.currentTimeMillis() - triptime) + " milliseconds.\n";
            triptime = System.currentTimeMillis();
            finished = true;
            cdl.countDown();
        }else{
            System.err.println("ERROR: Cannot read setTime, incorrect formatting!");
            failed = true;
        }
    }
    /** Thread's run method */
    public abstract void run();
    /** Subclass of ModelListenerThread that handles all TCP internal */
    public static class TCP extends ModelListenerThread{
        /** TCP socket */
        private Socket socket;
        /** Constructor for TCP ModelListenerThread
         *  @param gettime Flag to use getTime method, true to use getTime(), false to use setTime()
         *  @param cdl CountDownLatch for thread control
         *  @param socket TCP socket
         */
        public TCP(boolean gettime, CountDownLatch cdl, Socket socket){
            super(gettime, cdl);
            this.socket = socket;
        }
        /** Thread's run method */
        public void run(){
            try{
                //Get the stream
                DataInputStream in = new DataInputStream(socket.getInputStream());
                if(gettime) runGetTime(in);
                else runSetTime(in);
            }catch(Exception e){
                failed = true;
            }
        }
    }
    /** Subclass of ModelListenerThread that handles all UDP internal */
    public static class UDP extends ModelListenerThread{
        DatagramSocket mailbox;
        /** Constructor for TCP ModelListenerThread
         *  @param gettime Flag to use getTime method, true to use getTime(), false to use setTime()
         *  @param cdl CountDownLatch for thread control
         *  @param mailbox UDP DatagramSocket mailbox
         */
        public UDP(boolean gettime, CountDownLatch cdl, DatagramSocket mailbox){
            super(gettime, cdl);
            this.mailbox = mailbox;
        }
        /** Thread's run method */
        public void run(){
            byte[] payload = new byte[1024];
            try{
                //Gets the datagram
                DatagramPacket packet = new DatagramPacket(payload, payload.length);
                mailbox.receive(packet);
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload, 0, packet.getLength()));
                if(gettime) runGetTime(in);
                else runSetTime(in);
            }catch(Exception e){
                failed = true;
            }
        }
    }
}
