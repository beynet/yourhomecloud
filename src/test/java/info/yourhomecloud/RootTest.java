package info.yourhomecloud;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class RootTest {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
    }
}
