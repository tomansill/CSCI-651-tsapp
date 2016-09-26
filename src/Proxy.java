/* @author Thomas Ansill
 * CSCI-651-03 
 * Project 1
 */
 /** The actual Proxy */
public class Proxy{
    /** The constructor for Proxy, will immediately create servers once the object is created
     *  @param hostname The hostname for Model server, could be IP address or "localhost"
     *  @param portUDP the UDP port on proxy server
     *  @param portTCP the TCP port on proxy server
     *  @param type Configuration of proxy. type = 0 means use both UDP and TCP to communicate with the model server
     *      type = 1 means only use UDP to communicate with the model server 
     *      type = 2 means only use TCP to communicate with the model server 
     *  @param proxyUDP the UDP port for model server
     *  @param proxyTCP the TCP port for model server
     */
    public Proxy(String hostname, int portUDP, int portTCP, int type, int proxyUDP, int proxyTCP){
        try{
            //TCP
            if(type != 2) new TCPServer(portTCP, new TCPModelProxy(hostname, proxyTCP)).start();
            else new TCPServer(portTCP, new UDPModelProxy(hostname, proxyUDP)).start();
            //UDP
            if(type != 1) new UDPServer(portUDP, new UDPModelProxy(hostname, proxyUDP)).run();
            else new UDPServer(portUDP, new TCPModelProxy(hostname, proxyTCP)).run();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}