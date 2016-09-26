/* @author Thomas Ansill
* CSCI-651-03
* Project 1
*/
import java.util.concurrent.CountDownLatch;
/** A simple Timeout timer with CountDownLatch */
public class TimeoutThread extends Thread{
    /** CountDownLatch to call countDown() */
    private CountDownLatch cdl;
    /** Variable length of count-down time in milliseconds */
    private long timeoutlength;
    /** Constructor for TimeoutThread
     *  @param cdl CountDownLatch
     *  @param timeoutlength Length of count down time in milliseconds
     */
    public TimeoutThread(CountDownLatch cdl, long timeoutlength){
        this.cdl = cdl;
        this.timeoutlength = timeoutlength;
    }
    /** Thread run() method */
    public void run(){
        try{
            Thread.sleep(timeoutlength);
        }catch(Exception e){
            //Do nothing
        }
        cdl.countDown();
    }
}
