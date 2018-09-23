package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import static com.gypsyengineer.tlsbunny.tls13.client.CommonFuzzer.*;

public class FuzzyHttpsClient implements Client {

    private Config config = SystemPropertiesConfig.load();
    private Output output = new Output();
    private FuzzerConfig[] fuzzerConfigs = noClientAuthConfigs();
    private Client client = new HttpsClient();

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            new FuzzyHttpsClient()
                    .set(output)
                    .connect();
        }
    }

    @Override
    public Config config() {
        return config;
    }

    public FuzzyHttpsClient set(FuzzerConfig... fuzzerConfigs) {
        this.fuzzerConfigs = fuzzerConfigs;
        return this;
    }

    public FuzzyHttpsClient set(Client client) {
        this.client = client;
        return this;
    }

    @Override
    public Client set(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public Client set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public Client set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Client set(Check... check) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public Client connect() throws Exception {
        client.set(output).set(config);

        new Runner()
                .set(config)
                .set(output)
                .add(fuzzerFactory, combine(fuzzerConfigs, client))
                .set(new NoAlertAnalyzer())
                .submit();

        return this;
    }

    @Override
    public Engine engine() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
