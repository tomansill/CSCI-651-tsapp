/* @author Thomas Ansill
 * CSCI-651-03 
 * Project 1
 */
/** ViewListener Interface - This interface shapes the implemented object to be a view
 */
public interface ViewListener{
    /** Method when model responds to the view with time
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
	public void sendTime(long time, int hops, long triptime) throws Exception;
    /** Method when model responds to the view with time
     *  @param time Time
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
	public void sendTime(long time, int hops, long triptime, String strRTT) throws Exception;
    /** Method when model responds to the view with status on setTime command
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     */
	public void timeChangeStatus(boolean status, int hops, long triptime) throws Exception;
    /** Method when model responds to the view with status on setTime command
     *  @param status true if setTime was successful, otherwise false if incorrect credentials
     *  @param hops Number of hops made when this message reaches to the view from the model
     *  @param triptime Time taken to travel to next server
     *  @param strRTT A full log of RTT
     */
	public void timeChangeStatus(boolean status, int hops, long triptime, String strRTT) throws Exception;
}