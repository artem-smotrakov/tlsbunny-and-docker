package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.*;
import com.gypsyengineer.tlsbunny.tls13.connection.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.AllFailedCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.ExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.SocketException;

import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.ccsConfigs;

public class OpensslHttpsClientFuzzing {

    private static OpensslServer server;
    private static Config config = SystemPropertiesConfig.load();

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.start();
        Utils.waitServerStart(server);
    }

    @Test
    public void ccs() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(ccsConfigs(config))
                        .set(new HttpsClient()))
                .set(server)
                .run();
    }

    // TODO: check server logs for ASan findings
    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitServerStop(server);
    }
}
