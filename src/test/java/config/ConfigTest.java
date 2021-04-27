package config;

import config.yaml.CustomerConfig;
import config.yaml.Queue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import queues.QueueTypeEnum;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static queues.QueueTypeEnum.FIX;

public class ConfigTest {

    @Test
    public void testConfig() {
        CustomerConfig customerConfig = ConfigReader.getCustomerConfig();
        assertNotNull(customerConfig);
    }

    @Test
    public void testConfigYaml() {
        CustomerConfig customerConfig = new ConfigReader().getCustomerConfig("customer1.yaml");
        assertNotNull(customerConfig);
        assertEquals("customer1", customerConfig.getName());
        final Queue queue = customerConfig.getTaskmanager().getQueue();
        assertEquals(FIX, queue.getType());
        assertEquals(10, queue.getMaxRunningCapacity());
    }

    @Test
    public void testConfigProperties() throws IOException {
        String pomPropertyCustomer = ConfigReader.getCustomerPomProperty();
        assertNotNull(pomPropertyCustomer);
    }



}
