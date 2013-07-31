package info.yourhomecloud.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import info.yourhomecloud.RootTest;

import java.util.Arrays;
import java.util.Map;

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
            Map<String, HostConfigurationBean> otherHostsMap = ConfigurationBean.getHostsMapFromHostsList(configuration.getConfigurationBean().getOtherHosts());
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
            Map<String, HostConfigurationBean> otherHostsMap = ConfigurationBean.getHostsMapFromHostsList(configuration.getConfigurationBean().getOtherHosts());
            HostConfigurationBean host1Found = otherHostsMap.get(key1);
            assertThat(host1Found, notNullValue());
            assertThat(host1Found.getLastUpdateDate(), is(host1.getLastUpdateDate()));

            HostConfigurationBean host2Found = otherHostsMap.get(key2);
            assertThat(host2Found, notNullValue());
            assertThat(host2Found.getLastUpdateDate(), is(host2.getLastUpdateDate()));
        }

    }

}
