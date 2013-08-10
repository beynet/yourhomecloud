/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.hosts;

import info.yourhomecloud.hosts.impl.RMITargetHost;
import java.io.IOException;

/**
 *
 * @author beynet
 */
public class TargetHostBuilder {
    /**
     * create a new TargetHost that will be reached by RMI
     * @param host : hostName or IP address the target host is exporting its RMI objects
     * @param port : the TCP port the target host is listening for incoming RMI requests
     * @return a targethost recheable by RMI
     * @throws IOException : if an error occured during stub obtention
     */
    public static TargetHost createRMITargetHost(String hostKey,String host,int port) throws IOException {
        return new RMITargetHost(hostKey,host, port);
    }
    
}
