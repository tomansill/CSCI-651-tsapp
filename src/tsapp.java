/* @author Thomas Ansill
 * CSCI-651-03 
 * Project 1
 */
/** The main class - used only for parsing the arguments and running the appropraite actions 
 *  Usage: tsapp -[c,s,p] [options] [server address] port [2nd port]
 *  Warning: The programming style is pretty horrible in here. I'm writing this like C, no OOP here. 
 */
public class tsapp{
    /** The username */
	private static String username = null;
    /** The password */
	private static String password = null;
    /** Flag for UDP */
	private static boolean useUDP = false;
    /** Flag for TCP */
	private static boolean useTCP = false;
    /** Flag used to indicate an incorrect or leading argument parts */
	private static boolean uncaught = true;
    /** The time in unix epoch */
    private static long time = -1;
    /** the port in UDP */
	private static int portUDP = -1;
    /** the port in TCP */
	private static int portTCP = -1;
    /** The common commandline argument error */
	private static void invalidArgumentError(){
        //Thread.dumpStack();
		System.out.println("Usage: tsapp -[c,s,p] [options] [server address] port [2nd port]");
		System.exit(0);
	}
    /** Function for attempting to read the argument and find any matching flags for -T
     *  Sets customTime if matches
     *  @param args Entire commandline arguments
     *  @param i Element of the commandline argument to focus only
     *  @return New position of the pointer on the commandline arguments
     */
    private static int attemptTime(String[] args, int i){
        if(uncaught && args[i].equals("-T")){
            i++;
            if(i >= args.length) invalidArgumentError();
            if(time == -1){
                try{
                    time = Long.parseLong(args[i]);
                    if(time < 1){
                        System.err.println("The value for -T option cannot be less than 1!");
                        System.exit(0);
                    }
                }catch(Exception e){
                    System.err.println("The value for -T option must be an integer!");
                    System.exit(0);
                }
            }else invalidArgumentError();
            uncaught = false;
        }
		return i;
    }
    /** Function for attempting to read the argument and find any matching flags 
     *  Sets username if matches
     *  @param args Entire commandline arguments
     *  @param i Element of the commandline argument to focus only
     *  @return New position of the pointer on the commandline arguments
     */
	private static int attemptUsername(String[] args, int i){
		if(uncaught && args[i].equals("--user")){
			i++;
			if(i >= args.length) invalidArgumentError();
			if(username == null) username = args[i];
			else invalidArgumentError();
			uncaught = false;
		}
		return i;
	}
    /** Function for attempting to read the argument and find any matching flags 
     *  Sets password if matches
     *  @param args Entire commandline arguments
     *  @param i Element of the commandline argument to focus only
     *  @return New position of the pointer on the commandline arguments
     */
	private static int attemptPassword(String[] args, int i){
		if(uncaught && args[i].equals("--pass")){
			i++;
			if(i >= args.length) invalidArgumentError();
			if(username != null && password == null) password = args[i];
			else invalidArgumentError();
			uncaught = false;
		}
		return i;
	}
    /** Function for attempting to read the argument and find any matching flags
     *  Sets useUDP if matches 
     *  @param args Entire commandline arguments
     *  @param i Element of the commandline argument to focus only
     *  @return New position of the pointer on the commandline arguments
     */
	private static int attemptUDP(String[] args, int i){
		if(uncaught && args[i].equals("-u")){
			if(!useUDP && !useTCP) useUDP = true;
			else invalidArgumentError();
			uncaught = false;
		}
		return i;
	}
    /** Function for attempting to read the argument and find any matching flags
     *  Sets useTCP if matches 
     *  @param args Entire commandline arguments
     *  @param i Element of the commandline argument to focus only
     *  @return New position of the pointer on the commandline arguments
     */
	private static int attemptTCP(String[] args, int i){
		if(uncaught && args[i].equals("-t")){
			if(!useTCP && !useUDP) useTCP = true;
			else invalidArgumentError();
			uncaught = false;
		}
		return i;
	}
    /** Function for attempting to read the argument and find any matching flags
     *  gets and sets UDP and TCP ports if matches. Assumes UDP goes first. 
     *  @param args Entire commandline arguments
     *  @param i Element of the commandline argument to focus only
     *  @return New position of the pointer on the commandline arguments
     */
	private static int attemptUDPandTCPport(String[] args, int i){
		if(uncaught && args[i].matches("^\\d+$")){
			if(portUDP != -1 && portTCP != -1) invalidArgumentError(); //Another number? cannot be correct.
			//Check if integer
			int port = -1;
			try{
				port = Integer.parseInt(args[i]);
			}catch(Exception e){} //Shouldn't happen
			if(port < 0){
				System.err.println("The port number cannot be negative!");
				System.exit(0);
			}
			if(portUDP == -1) portUDP = port;
			else portTCP = port;	
			uncaught = false;
		}
		return i;
	}
    /** The main method
     *  @param args Commandline arguments 
     */
	public static void main(String[] args){
		username = null;
		password = null;
		useUDP = false;
		useTCP = false;
		uncaught = true;
		portUDP = -1;
		portTCP = -1;
		if(args.length > 1){
			if(args[0].equals("-c")){
				//Client
				String serveraddress = args[1];
				boolean displayUTC = false;
				int numberOfQueries = -1;
				int port = -1;
				for(int i = 2; i < args.length; i++){
					i = attemptUDP(args, i);
					i = attemptTCP(args, i);
					i = attemptUsername(args, i);
					i = attemptPassword(args, i);
                    i = attemptTime(args, i);
					if(uncaught && args[i].equals("-n")){
						i++;
						if(i >= args.length) invalidArgumentError();
						if(numberOfQueries == -1){
							try{
								numberOfQueries = Integer.parseInt(args[i]);
								if(numberOfQueries < 1){
									System.err.println("The value for -n option cannot be less than 1!");
									System.exit(0);
								}
							}catch(Exception e){
								System.err.println("The value for -n option must be an integer!");
								System.exit(0);
							}
						}else invalidArgumentError();
						uncaught = false;
					}
					//Display UTC
					if(uncaught && args[i].equals("-z")){
						if(!displayUTC) displayUTC = true;
						else invalidArgumentError();
						uncaught = false;
					}
					//Client port
					if(uncaught && args[i].matches("^\\d+$")){
						if(port != -1) invalidArgumentError(); //Another number? cannot be correct.
						//Check if integer
						try{
							port = Integer.parseInt(args[i]);
						}catch(Exception e){} //Shouldn't happen
						if(port < 0){
							System.err.println("The port number cannot be negative!");
							System.exit(0);
						}
						uncaught = false;
					}
					//If uncaught then this piece of argument is invalid
					if(uncaught){
						invalidArgumentError();
					}else uncaught = true;
				} 
				if(numberOfQueries == -1) numberOfQueries = 1;
				if(port == -1) invalidArgumentError();
                if(!useUDP && !useTCP) useUDP = true; //Defaults to UDP
                if(username != null && password == null) invalidArgumentError();
                if(username == null && password != null) invalidArgumentError();
                if(username != null && password != null && time == -1) invalidArgumentError();
                if((username == null || password == null) && time != -1) invalidArgumentError();
                /* //Use this for input debugging
				System.out.println("Client input information - server: " + serveraddress 
					+ " port: " + port 
					+ " useUTC? " + displayUTC 
					+ " useUDP? " + useUDP 
					+ " useTCP? " + useTCP
					+ " number of queries: " + numberOfQueries
					+ " username: " + username
					+ " password: " + password
					+ " time: " + 0);*/
                    
                //Start the client
                Client client = new Client(serveraddress, port, displayUTC, useUDP, numberOfQueries, username, password, time);
			}else if(args[0].equals("-s")){
				//Server
				for(int i = 1; i < args.length; i++){
					i = attemptUsername(args, i);
					i = attemptPassword(args, i);
					i = attemptUDPandTCPport(args, i);
                    i = attemptTime(args, i);
					//If uncaught then this piece of argument is invalid
					if(uncaught){
						invalidArgumentError();
					}else uncaught = true;
				}
				if(portUDP == -1 || portTCP == -1) invalidArgumentError();
                if(portUDP == portTCP) invalidArgumentError();
                if(username != null && password == null) invalidArgumentError();
                if(username == null && password != null) invalidArgumentError();
                /* //Use this for input debugging
				System.out.println("Server input information - UDP port: " + portUDP
					+ " TCP port: " + portTCP
					+ " username: " + username
					+ " password: " + password
					+ " time: " + time);*/

				//All seems good, so go ahead and create UDP and TCP servers
                Model model;
                if(time != -1 && username != null && password != null) model = new Model(time, username, password);
                else if(time != -1) model = new Model(time);
				else model = new Model(username, password);	
                
                try{    
                    new TCPServer(portTCP, model).start(); 
                    new UDPServer(portUDP, model).run(); //run instead of start to not start another thread 
                }catch(Exception e){
                    System.err.println(e.getMessage());
                }
			}else if(args[0].equals("-p")){
				//Proxy
				String serveraddress = args[1];
                int proxyUDP = -1;
                int proxyTCP = -1;
				for(int i = 2; i < args.length; i++){
                    i = attemptUDPandTCPport(args, i);
                    i = attemptUDP(args, i);
					i = attemptTCP(args, i);
                    //--proxy-udp
                    if(uncaught && args[i].equals("--proxy-udp")){
						i++;
						if(i >= args.length) invalidArgumentError();
						if(proxyUDP == -1){
							try{
								proxyUDP = Integer.parseInt(args[i]);
								if(proxyUDP < 1){
									System.err.println("The value for --proxy-udp option cannot be less than 1!");
									System.exit(0);
								}
							}catch(Exception e){
								System.err.println("The value for --proxy-udp option must be an integer!");
								System.exit(0);
							}
						}else invalidArgumentError();
						uncaught = false;
					}
                    //--proxy-udp
                    if(uncaught && args[i].equals("--proxy-tcp")){
						i++;
						if(i >= args.length) invalidArgumentError();
						if(proxyTCP == -1){
							try{
								proxyTCP = Integer.parseInt(args[i]);
								if(proxyTCP < 1){
									System.err.println("The value for --proxy-udp option cannot be less than 1!");
									System.exit(0);
								}
							}catch(Exception e){
								System.err.println("The value for --proxy-udp option must be an integer!");
								System.exit(0);
							}
						}else invalidArgumentError();
						uncaught = false;
                        
					}
                    if(uncaught){
						invalidArgumentError();
					}else uncaught = true;
                }
                if(portUDP == -1 || portTCP == -1) invalidArgumentError();
                if(portUDP == portTCP) invalidArgumentError();
                if(useUDP && proxyUDP == -1) invalidArgumentError();
                if(useTCP && proxyTCP == -1) invalidArgumentError();
                if(!useUDP && !useTCP && proxyTCP == proxyUDP) invalidArgumentError();
                /*//Use this for input debugging
                System.out.println("Proxy input information - Server host name: " + serveraddress 
                    + " UDP port: " + portUDP
                    + " TCP port: " + portTCP
                    + " useUDP: " + useUDP
                    + " useTCP: " + useTCP
                    + " proxyUDP: " + proxyUDP
                    + " proxyTCP: " + proxyTCP);
                */    
                int type = 0;
                if(useUDP) type = 2;
                else if(useTCP) type = 1;
                new Proxy(serveraddress, portUDP, portTCP, type, proxyUDP, proxyTCP);
			}else invalidArgumentError();
		}else invalidArgumentError();
	}
}
