/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.network.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 *
 * @author beynet
 */
public class RmiProxy implements InvocationHandler {

    public RmiProxy(String hostKey,Object rmiObj) {
        this.hostKey = hostKey ;
        this.rmiObj = rmiObj;
    }

    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(rmiObj, args);
        } catch (Throwable e) {
            logger.error("error trapped",e);
            throw e;
        }
    }
    
    private String hostKey ;
    private Object rmiObj;
    private final static Logger logger = Logger.getLogger(RmiProxy.class);
}
