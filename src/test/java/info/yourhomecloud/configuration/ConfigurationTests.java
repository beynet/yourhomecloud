package info.yourhomecloud.configuration;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import info.yourhomecloud.RootTest;

import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;

import org.junit.Test;

public class ConfigurationTests extends RootTest {

    @Test
    public void localHostKeyNotNull() {
        Configuration configuration = Configuration.getConfiguration();
        assertThat(configuration.getCurrentHostKey(), notNullValue());
    }
    
    @Test
    public void updateHosts() {
        Configuration configuration = Configuration.getConfiguration();
        configuration.clearOtherHostsConfiguration();
        String key1 = "abcd";
        String key2 = "abcde";

        HostConfigurationBean host1 = new HostConfigurationBean();
        host1.setLastUpdateDate(Long.valueOf(1));
        host1.setHostKey(key1);

        HostConfigurationBean host2 = new HostConfigurationBean();
        host2.setLastUpdateDate(Long.valueOf(1));
        host2.setHostKey(key2);
        
        configuration.updateOtherHostsConfiguration(Arrays.asList(host1,host2),host1);

        {
            Map<String, HostConfigurationBean> otherHostsMap = ConfigurationBean.getHostsMapFromHostsList(configuration.getOtherHostsSnapshot());
            HostConfigurationBean host1Found = otherHostsMap.get(key1);
            assertThat(host1Found, notNullValue());
            assertThat(host1Found.getLastUpdateDate(), is(host1.getLastUpdateDate()));

            HostConfigurationBean host2Found = otherHostsMap.get(key2);
            assertThat(host2Found, notNullValue());
            assertThat(host2Found.getLastUpdateDate(), is(host2.getLastUpdateDate()));
        }

        // update hosts
        host1 = new HostConfigurationBean();
        host1.setLastUpdateDate(Long.valueOf(2));
        host1.setHostKey(key1);

        host2 = new HostConfigurationBean();
        host2.setLastUpdateDate(Long.valueOf(2));
        host2.setHostKey(key2);

        configuration.updateOtherHostsConfiguration(Arrays.asList(host1,host2),host1);

        //check that conf is updated as expected
        {
            Map<String, HostConfigurationBean> otherHostsMap = ConfigurationBean.getHostsMapFromHostsList(configuration.getOtherHostsSnapshot());
            HostConfigurationBean host1Found = otherHostsMap.get(key1);
            assertNotNull(host1Found);
            assertThat(host1Found.getLastUpdateDate(), is(host1.getLastUpdateDate()));

            HostConfigurationBean host2Found = otherHostsMap.get(key2);
            assertNotNull(host2Found);
            assertThat(host2Found.getLastUpdateDate(), is(host2.getLastUpdateDate()));
        }

    }
    
    @Test
    public void cloneHostConfigurationBean() {
        HostConfigurationBean host = new HostConfigurationBean();
        host.setCurrentRMIAddress("127.0.0.1");
        host.setCurrentRMIPort(3248);
        host.setHostKey("key1");
        host.setHostName("hostname");
        host.setLastUpdateDate(System.currentTimeMillis());
        host.setNetworkInterface("eth0");
        host.getDirectoriesToBeSaved().add("/tmp");
        host.getDirectoriesToBeSaved().add("/etc");
        
        HostConfigurationBean host2 = host.clone();
        Assert.assertNotNull(host2);
        assertFalse(host2==host);
        assertTrue(host2.equals(host));
        
        assertEquals(host2.getCurrentRMIAddress(), host.getCurrentRMIAddress());
        assertEquals(host2.getCurrentRMIPort(), host.getCurrentRMIPort());
        assertEquals(host2.getDirectoriesToBeSaved(), host.getDirectoriesToBeSaved());
        assertEquals(host2.getHostKey(), host.getHostKey());
        assertEquals(host2.getHostName(), host.getHostName());
        assertEquals(host2.getLastUpdateDate(), host.getLastUpdateDate());
        assertEquals(host2.getNetworkInterface(), host.getNetworkInterface());
        
    }

}
