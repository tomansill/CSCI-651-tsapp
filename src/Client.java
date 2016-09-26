/* @author Thomas Ansill
 * CSCI-651-01 
 * Project 1
 */
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
/** The actual View, not a proxy. This object will run as soon as it is created and do setTime and getTime operations 
 *  then report the results with RTT and hops.
 */
public class Client implements ViewListener{
    /** Flag for displaying UTC */
    private boolean displayUTC = false;
    /** Timer variable used for calculating RTT */
    private long timer;
    /** The ModelListener, could be model itself or a proxy, most likely a proxy though. */
    private ModelListener modelProxy;
    /** The constructor. Will immediately start the operation as soon as it is initinated. 
     *  @param destination Hostname String of the destination. It could be IP address or "localhost"
     *  @param port The destination port
     *  @param useUDP flag for use UDP datagram
     *  @param numberOfQueries Number of queries against the model
     *  @param username Username for setTime
     *  @param password Password for setTime
     *  @param time setTime time
     */
    public Client(String destination, int port, boolean displayUTC, boolean useUDP, int numberOfQueries, String username, String password, long time){
        this.displayUTC = displayUTC;
        if(useUDP){
            try{
                modelProxy = new UDPModelProxy(destination, port);
                startOperation(username, password, numberOfQueries, time);
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }else{
            try{
                TCPModelProxy tcpModel = new TCPModelProxy(destination, port);
                modelProxy = tcpModel; //Don't really want to modify the interface just for TCP
                startOperation(username, password, numberOfQueries, time);
                tcpModel.cleanUp(); 
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
		}
        
    }
    /** The operation. It will first do setTime if username, password, and time is defined, 
     *  then do a number of queries of getTime against the model
     *  @param username The username for setTime
     *  @param password The password for setTime
     *  @param numberOfQueries number of getTime queries
     *  @param time The time for setTime
     */
    public void startOperation(String username, String password, int numberOfQueries, long time){
        //Send time change if applicable
        if(username != null && password != null){
            System.out.print("Sending setTime request...");
            startTimer();
            try{
                modelProxy.setTime(time, username, password, this);
            }catch(Exception e){
                System.err.println("\nERROR: Failed to set time: " + e.getMessage());
            }
        }
        //Get time
        for(int i = 0; i < numberOfQueries; i++){
            if(numberOfQueries != 1) System.out.print((i+1) + ": ");
            System.out.print("Sending getTime request...");
            startTimer();
            try{
                modelProxy.getTime(this);
            }catch(Exception e){
                System.err.println("\nERROR: Failed to get time: " + e.getMessage());
            }
        }
    }
    /** Starts the timer for RTT */
    public void startTimer(){
        timer = System.currentTimeMillis();
    }
    /** Display the RTT with timer
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT full log of RTT
     */
    private void displayRTT(int hops, long triptime, String strRTT){
        System.out.println("\nThe round-trip time is " + (System.currentTimeMillis() - timer) + " milliseconds.\t" + hops + " hops.");
        strRTT += "Client delay: " + (System.currentTimeMillis() - triptime) + " milliseconds.\n";
        System.out.println(strRTT);
    }
    /** Method when model responds to the view with time
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
    public void sendTime(long time, int hops, long triptime) throws Exception{
        this.sendTime(time, hops, triptime);
    }
    /** Method when model responds to the view with time
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
    public void sendTime(long time, int hops, long triptime, String strRTT) throws Exception{
        if(displayUTC) System.out.print("\r" + new Date(time));
        else System.out.println("\rReceived Time: " + time + " milliseconds");
        displayRTT(hops, triptime, strRTT);
    }
    /** Method when model responds to the view with status on setTime command
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
    public void timeChangeStatus(boolean status, int hops, long triptime) throws Exception{
        this.timeChangeStatus(status, hops, triptime, "");
    }
    /** Method when model responds to the view with status on setTime command
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
    public void timeChangeStatus(boolean status, int hops, long triptime, String strRTT) throws Exception{
        if(status) System.out.print("Time change successful");
        else System.out.print("Time change unsuccessful");
        displayRTT(hops, triptime, strRTT);
    }
}
