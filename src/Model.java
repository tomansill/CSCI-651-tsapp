/* @author Thomas Ansill
 * CSCI-651-03
 * Project 1
 */
 /** The actual Model, not a proxy. */
public class Model implements ModelListener{
    /** The authorized username */
	private String username;
    /** The password */
	private String password;
    /** Current time */
	private long time;
    /** Variable to be used to keep track of time with custom times */
    //private long serverStartTime = System.currentTimeMillis();
    /** Flag if custom time is used */
    //public boolean customTime = false;
    /** Default constructor */
	public Model(){
        this.username = null;
        this.password = null;
		time = System.currentTimeMillis();
	}
    /** Constructor with custom time
     *  @param time The time in unix epoch time
     */
	public Model(long time){
        this.username = null;
        this.password = null;
		this.time = time;
	}
    /** Constructor with username and password, no custom time 
     *  @param username The authorized username
     *  @param password The password
     */
	public Model(String username, String password){
		this.time = System.currentTimeMillis();
		this.username = username;
		this.password = password;
	}
    /** Constructor with username, password and custom time 
     *  @param time The time in unix epoch time
     *  @param username The authorized username
     *  @param password The password
     */
	public Model(long time, String username, String password){
		this.username = username;
		this.password = password;
		this.time = time;
	}
    /** Method when outside accesses the model's time
     *  @param viewListener The requesting view - use this listener to respond the time to.
     */
	public void getTime(ViewListener listener) throws Exception{
        listener.sendTime(this.time, 0, System.currentTimeMillis());
	}
    /** Method when outside sets the model's time using username and password
     *  @param time The new time in unix epoch time
     *  @param username the username
     *  @param password the password
     *  @param viewListener The requesting view - use this listener to respond the status of change to.
     */
	public void setTime(long time, String username, String password, ViewListener listener) throws Exception{
		if(this.username != null && this.username.equals(username) && this.password != null && this.password.equals(password)){
			synchronized(this){ this.time = time; }
			listener.timeChangeStatus(true, 0, System.currentTimeMillis());
		}else listener.timeChangeStatus(false, 0, System.currentTimeMillis());
	}
}
