package com.gypsyengineer.tlsbunny.jsse;

import com.gypsyengineer.tlsbunny.BaseTest;
import com.gypsyengineer.tlsbunny.tls13.client.common.Client;
import com.gypsyengineer.tlsbunny.tls13.client.common.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.NoExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Ignore;
import org.junit.Test;

public class BasicTest extends BaseTest {

    /**
     * The test is excluded because it fails with the following exception on JSSE side.
     * See https://bugs.openjdk.java.net/browse/JDK-8210334 for details.
     *
     * javax.net.ssl.SSLHandshakeException: pre_shared_key key extension is offered without a psk_key_exchange_modes extension
     * 	at java.base/sun.security.ssl.Alert.createSSLException(Alert.java:128)
     * 	at java.base/sun.security.ssl.Alert.createSSLException(Alert.java:117)
     * 	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:308)
     * 	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:264)
     * 	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:255)
     * 	at java.base/sun.security.ssl.PskKeyExchangeModesExtension$PskKeyExchangeModesOnTradeAbsence.absent(PskKeyExchangeModesExtension.java:327)
     * 	at java.base/sun.security.ssl.SSLExtension.absentOnTrade(SSLExtension.java:572)
     * 	at java.base/sun.security.ssl.SSLExtensions.consumeOnTrade(SSLExtensions.java:180)
     * 	at java.base/sun.security.ssl.ServerHello$T13ServerHelloProducer.produce(ServerHello.java:522)
     * 	at java.base/sun.security.ssl.SSLHandshake.produce(SSLHandshake.java:436)
     * 	at java.base/sun.security.ssl.ClientHello$T13ClientHelloConsumer.goServerHello(ClientHello.java:1189)
     * 	at java.base/sun.security.ssl.ClientHello$T13ClientHelloConsumer.consume(ClientHello.java:1125)
     * 	at java.base/sun.security.ssl.ClientHello$ClientHelloConsumer.onClientHello(ClientHello.java:831)
     * 	at java.base/sun.security.ssl.ClientHello$ClientHelloConsumer.consume(ClientHello.java:792)
     * 	at java.base/sun.security.ssl.SSLHandshake.consume(SSLHandshake.java:392)
     * 	at java.base/sun.security.ssl.HandshakeContext.dispatch(HandshakeContext.java:444)
     * 	at java.base/sun.security.ssl.HandshakeContext.dispatch(HandshakeContext.java:421)
     * 	at java.base/sun.security.ssl.TransportContext.dispatch(TransportContext.java:178)
     * 	at java.base/sun.security.ssl.SSLTransport.decode(SSLTransport.java:164)
     * 	at java.base/sun.security.ssl.SSLSocketImpl.decode(SSLSocketImpl.java:1152)
     * 	at java.base/sun.security.ssl.SSLSocketImpl.readHandshakeRecord(SSLSocketImpl.java:1063)
     * 	at java.base/sun.security.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:402)
     * 	at java.base/sun.security.ssl.SSLSocketImpl.ensureNegotiated(SSLSocketImpl.java:716)
     * 	at java.base/sun.security.ssl.SSLSocketImpl$AppInputStream.read(SSLSocketImpl.java:799)
     * 	at java.base/java.io.BufferedInputStream.fill(BufferedInputStream.java:252)
     * 	at java.base/java.io.BufferedInputStream.read1(BufferedInputStream.java:292)
     * 	at java.base/java.io.BufferedInputStream.read(BufferedInputStream.java:351)
     * 	at java.base/java.io.FilterInputStream.read(FilterInputStream.java:107)
     * 	at com.gypsyengineer.tlsbunny.jsse.SimpleEchoServer.run(SimpleEchoServer.java:72)
     * 	at java.base/java.lang.Thread.run(Thread.java:834)
     */
    @Test
    @Ignore
    public void httpsClient() throws Exception {
        Config serverConfig = SystemPropertiesConfig.load();
        serverConfig.serverCertificate(serverCertificatePath);
        serverConfig.serverKey(serverKeyPath);

        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Client client = new HttpsClient()
                .version(ProtocolVersion.TLSv13)
                .set(StructFactory.getDefault())
                .set(clientOutput);

        SimpleEchoServer server = SimpleEchoServer.create()
                .set(serverConfig)
                .set(serverOutput);

        try (client; server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load();
            clientConfig.port(server.port());

            client.set(clientConfig).set(clientOutput);

            client.connect()
                    .run(new NoAlertCheck())
                    .run(new SuccessCheck())
                    .run(new NoExceptionCheck())
                    .apply(new NoAlertAnalyzer());
        }
    }

    // standalone interface
    public static void main(String[] args) throws Exception {
        BasicTest test = new BasicTest();
        test.httpsClient();
    }
}
