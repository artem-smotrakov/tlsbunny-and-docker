package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.client.*;
import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingMainServerFlight;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.server.HttpsServer;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv13;
import static com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme.ecdsa_secp256r1_sha256;
import static org.junit.Assert.*;

public class BasicTest {

    private static final long delay = 1000; // in millis
    private static final String serverCertificatePath = "certs/server_cert.der";
    private static final String serverKeyPath = "certs/server_key.pkcs8";
    private static StructFactory factory = StructFactory.getDefault();

    @Test
    public void httpsClientWithSecp256r1() throws Exception {
        test(new HttpsClient(), NamedGroup.secp256r1, 1);
    }

    @Test
    public void httpsClientWithFFDHE2048() throws Exception {
        test(new HttpsClient(), NamedGroup.ffdhe2048, 1);
    }

    @Test
    public void anotherHttpsClientWithSecp256r1() throws Exception {
        test(new AnotherHttpsClient(), NamedGroup.secp256r1, 1);
    }

    @Test
    public void anotherHttpsClientWithFFDHE2048() throws Exception {
        test(new AnotherHttpsClient(), NamedGroup.ffdhe2048, 1);
    }

    @Test
    public void manyGroupsInClientHello() throws Exception {
        // we use only 3000 groups here because a higher number results to multiple TLSPlaintext
        // but the server currently can't assemble them
        test(new ManyGroupsInClientHello().numberOfGroups(3000), NamedGroup.secp256r1, 1);
    }

    @Test
    public void strictValidation() throws Exception {
        test(new ECDHEStrictValidation().connections(10), NamedGroup.secp256r1, 10);
    }

    private static void test(Client client, NamedGroup group, int n) throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config serverConfig = SystemPropertiesConfig.load();
        serverConfig.serverCertificate(serverCertificatePath);
        serverConfig.serverKey(serverKeyPath);

        /*
        TODO use HttpsServer
        HttpsServer server = httpsServer()
                .set(factory)
                .set(group)
                .set(serverConfig)
                .set(serverOutput)
                .maxConnections(n);
                */

        SingleThreadServer server = new SingleThreadServer()
                .set(new EngineFactoryImpl()
                        .set(Negotiator.create(group, factory))
                        .set(serverConfig)
                        .set(serverOutput))
                .set(serverConfig)
                .set(serverOutput)
                .maxConnections(n);

        try (server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load().port(server.port());
            client.set(Negotiator.create(group, factory))
                    .set(clientConfig).set(clientOutput);

            try (client) {
                client.connect()
                        .engines()[0]
                        .apply(new NoAlertAnalyzer());
            }
        }

        boolean success = checkContexts(
                client.engines()[0].context(),
                server.recentEngine().context(),
                clientOutput);

        assertTrue("something went wrong!", success);
    }

    private static boolean checkContexts(
            Context clientContext, Context serverContext, Output output) {

        output.info("check client and server contexts");
        assertNotNull("client context should not be null", clientContext);
        assertNotNull("server context should not be null", serverContext);

        assertArrayEquals("contexts: dh_shared_secret are not equal",
                clientContext.dh_shared_secret,
                serverContext.dh_shared_secret);

        assertArrayEquals("contexts: early_secret are not equal",
                clientContext.early_secret,
                serverContext.early_secret);

        assertArrayEquals("contexts: binder_key are not equal",
                clientContext.binder_key,
                serverContext.binder_key);

        assertArrayEquals("contexts: client_early_traffic_secret are not equal",
                clientContext.client_early_traffic_secret,
                serverContext.client_early_traffic_secret);

        assertArrayEquals("contexts: early_exporter_master_secret are not equal",
                clientContext.early_exporter_master_secret,
                serverContext.early_exporter_master_secret);

        assertArrayEquals("contexts: handshake_secret_salt are not equal",
                clientContext.handshake_secret_salt,
                serverContext.handshake_secret_salt);

        assertArrayEquals("contexts: handshake_secret are not equal",
                clientContext.handshake_secret,
                serverContext.handshake_secret);

        assertArrayEquals("contexts: client_handshake_traffic_secret are not equal",
                clientContext.client_handshake_traffic_secret,
                serverContext.client_handshake_traffic_secret);

        assertArrayEquals("contexts: server_handshake_traffic_secret are not equal",
                clientContext.server_handshake_traffic_secret,
                serverContext.server_handshake_traffic_secret);

        assertArrayEquals("contexts: master_secret are not equal",
                clientContext.master_secret,
                serverContext.master_secret);

        assertArrayEquals("contexts: client_application_traffic_secret_0 are not equal",
                clientContext.client_application_traffic_secret_0,
                serverContext.client_application_traffic_secret_0);

        assertArrayEquals("contexts: server_application_traffic_secret_0 are not equal",
                clientContext.server_application_traffic_secret_0,
                serverContext.server_application_traffic_secret_0);

        assertArrayEquals("contexts: exporter_master_secret are not equal",
                clientContext.exporter_master_secret,
                serverContext.exporter_master_secret);

        assertArrayEquals("contexts: resumption_master_secret are not equal",
                clientContext.resumption_master_secret,
                serverContext.resumption_master_secret);

        assertArrayEquals("contexts: client_handshake_write_key are not equal",
                clientContext.client_handshake_write_key,
                serverContext.client_handshake_write_key);

        assertArrayEquals("contexts: client_handshake_write_iv are not equal",
                clientContext.client_handshake_write_iv,
                serverContext.client_handshake_write_iv);

        assertArrayEquals("contexts: server_handshake_write_key are not equal",
                clientContext.server_handshake_write_key,
                serverContext.server_handshake_write_key);

        assertArrayEquals("contexts: server_handshake_write_iv are not equal",
                clientContext.server_handshake_write_iv,
                serverContext.server_handshake_write_iv);

        assertArrayEquals("contexts: client_application_write_key are not equal",
                clientContext.client_application_write_key,
                serverContext.client_application_write_key);

        assertArrayEquals("contexts: client_application_write_iv are not equal",
                clientContext.client_application_write_iv,
                serverContext.client_application_write_iv);

        assertArrayEquals("contexts: server_application_write_key are not equal",
                clientContext.server_application_write_key,
                serverContext.server_application_write_key);

        assertArrayEquals("contexts: server_application_write_iv are not equal",
                clientContext.server_application_write_iv,
                serverContext.server_application_write_iv);

        return true;
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        private Negotiator negotiator;
        private Config config;

        public EngineFactoryImpl set(Config config) {
            this.config = config;
            return this;
        }

        public EngineFactoryImpl set(Negotiator negotiator) {
            this.negotiator = negotiator;
            return this;
        }

        @Override
        protected Engine createImpl() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)
                    .set(negotiator)
                    .set(negotiator.group())

                    // receive ClientHello
                    .receive(new IncomingData())
                    .run(new ProcessingTLSPlaintext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(client_hello)
                            .updateContext(Context.Element.first_client_hello))
                    .run(new ProcessingClientHello())

                    // receive CCS
                    .receive(new IncomingChangeCipherSpec())

                    // send messages
                    .send(new OutgoingMainServerFlight()
                            .apply(config))

                    // receive Finished
                    .receive(new IncomingData())
                    .run(new ProcessingHandshakeTLSCiphertext()
                            .expect(handshake))
                    .run(new ProcessingHandshake()
                            .expect(finished))
                    .run(new ProcessingFinished(Side.server))

                    .run(new ComputingApplicationTrafficKeys()
                            .server())

                    // receive application data
                    .receive(new IncomingData())
                    .run(new ProcessingApplicationDataTLSCiphertext()
                            .expect(application_data))
                    .run(new PrintingData())

                    // send application data
                    .run(new PreparingHttpResponse())
                    .run(new WrappingApplicationDataIntoTLSCiphertext())
                    .send(new OutgoingData());
        }
    }
}
