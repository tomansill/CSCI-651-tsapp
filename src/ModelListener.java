/* @author Thomas Ansill
 * CSCI-651-03
 * Project 1
 */
/** ModelListener Interface - This interface shapes the implemented object to be a model
 */
public interface ModelListener{
    /** Method when outside accesses the model's time
     *  @param viewListener The requesting view - use this listener to respond the time to.
     */
	public void getTime(ViewListener viewListener) throws Exception;
    /** Method when outside sets the model's time using username and password
     *  @param time The new time
     *  @param username the username
     *  @param password the password
     *  @param viewListener The requesting view - use this listener to respond the status of change to.
     */
	public void setTime(long time, String username, String password, ViewListener viewListener) throws Exception;
}
