package io.bootique.jersey.client;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DefaultHttpClientFactoryTest {

    private ClientConfig config;
    private ClientRequestFilter mockAuth1;
    private ClientRequestFilter mockAuth2;

    @Before
    public void before() {
        config = new ClientConfig();
        mockAuth1 = mock(ClientRequestFilter.class);
        mockAuth2 = mock(ClientRequestFilter.class);
    }

    @Test
    public void testNewClient() {

        config.property("x", "y");

        DefaultHttpClientFactory factory = new DefaultHttpClientFactory(config, null, Collections.emptyMap(), Collections.emptyMap());
        Client c = factory.newClient();
        assertNotNull(c);

        assertEquals("y", c.getConfiguration().getProperty("x"));
    }

    @Test
    public void testNewClient_NewInstanceEveryTime() {

        DefaultHttpClientFactory factory = new DefaultHttpClientFactory(
                config,
                null,
                Collections.emptyMap(),
                Collections.emptyMap());

        Client c1 = factory.newClient();
        Client c2 = factory.newClient();
        assertNotSame(c1, c2);
    }

    @Test
    public void testNewClientBuilder_Auth() {

        config.property("a", "b");

        Map<String, ClientRequestFilter> authFilters = new HashMap<>();
        authFilters.put("one", mockAuth1);
        authFilters.put("two", mockAuth2);

        DefaultHttpClientFactory factory = new DefaultHttpClientFactory(config, null, authFilters, Collections.emptyMap());
        Client c = factory.newBuilder().auth("one").build();
        assertNotNull(c);

        assertEquals("b", c.getConfiguration().getProperty("a"));
        assertTrue(c.getConfiguration().isRegistered(mockAuth1));
        assertFalse(c.getConfiguration().isRegistered(mockAuth2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewClient_Auth_BadAuth() {

        config.property("a", "b");

        Map<String, ClientRequestFilter> authFilters = new HashMap<>();
        authFilters.put("one", mockAuth1);
        authFilters.put("two", mockAuth2);

        DefaultHttpClientFactory factory = new DefaultHttpClientFactory(config, null, authFilters, Collections.emptyMap());
        factory.newBuilder().auth("three");
    }
}
