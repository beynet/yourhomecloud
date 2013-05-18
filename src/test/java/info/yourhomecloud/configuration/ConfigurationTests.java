package info.yourhomecloud.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

public class ConfigurationTests {

    @Test
    public void localHostKeyNotNull() {
        Configuration configuration = Configuration.getConfiguration(Paths.get("/tmp"));
        assertThat(configuration.getCurrentHostKey(), notNullValue());
    }
    
    @Test
    public void updateHosts() {
        Configuration configuration = Configuration.getConfiguration(Paths.get("/tmp"));
        configuration.clearOtherHostsConfiguration();
        String key1 = "abcd";
        String key2 = "abcde";

        HostConfigurationBean host1 = new HostConfigurationBean();
        host1.setLastUpdateDate(Long.valueOf(1));
        host1.setHostKey(key1);

        HostConfigurationBean host2 = new HostConfigurationBean();
        host2.setLastUpdateDate(Long.valueOf(1));
        host2.setHostKey(key2);

        configuration.updateOtherHostsConfiguration(Arrays.asList(host1,host2));

        {
            Map<String, HostConfigurationBean> otherHostsMap = ConfigurationBean.getOtherHostsMap(configuration.getConfigurationBean().getOtherHosts());
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

        configuration.updateOtherHostsConfiguration(Arrays.asList(host1,host2));

        //check that conf is updated as expected
        {
            Map<String, HostConfigurationBean> otherHostsMap = ConfigurationBean.getOtherHostsMap(configuration.getConfigurationBean().getOtherHosts());
            HostConfigurationBean host1Found = otherHostsMap.get(key1);
            assertThat(host1Found, notNullValue());
            assertThat(host1Found.getLastUpdateDate(), is(host1.getLastUpdateDate()));

            HostConfigurationBean host2Found = otherHostsMap.get(key2);
            assertThat(host2Found, notNullValue());
            assertThat(host2Found.getLastUpdateDate(), is(host2.getLastUpdateDate()));
        }


    }

    @Test
    public void longT() {
        Long t1 = Long.valueOf(3);
        Long t2 = null;
        System.out.println(t1.compareTo(t2));
    }

}
