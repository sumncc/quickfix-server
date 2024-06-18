package org.raj.quickfixj.springboot.client;

import org.raj.quickfixj.springboot.server.BootifiedServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class BootifiedClientTest {

    @Test
    void testMain() throws IOException {
        BootifiedServer.main(new String[]{});
        Assertions.assertTrue(true);
    }
}