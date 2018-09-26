package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

public class FuzzyHttpsClient implements Client {

    private Config mainConfig;
    private FuzzerConfig[] fuzzerConfigs;
    private Output output;

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            new FuzzyHttpsClient()
                    .set(output)
                    .connect();
        }
    }

    public FuzzyHttpsClient() {
        mainConfig = SystemPropertiesConfig.load();
        fuzzerConfigs = FuzzyClient.noClientAuthConfigs(mainConfig);
    }

    @Override
    public Config config() {
        return mainConfig;
    }

    public FuzzyHttpsClient set(FuzzerConfig... fuzzerConfigs) {
        this.fuzzerConfigs = fuzzerConfigs;
        return this;
    }

    @Override
    public Client set(Config mainConfig) {
        this.mainConfig = mainConfig;
        return this;
    }

    @Override
    public Client set(Output output) {
        this.output = output;
        return this;
    }

    @Override
    public Client connect() throws Exception {
        try (Client client = new HttpsClient()) {
            client.set(output).set(mainConfig).set(no_checks);

            new Runner()
                    .set(mainConfig)
                    .set(output)
                    .set(FuzzyClient.fuzzerFactory)
                    .set(client)
                    .set(fuzzerConfigs)
                    .set(new NoAlertAnalyzer())
                    .submit();
        }

        return this;
    }

    @Override
    public Client set(Check... check) {
        // TODO: we need to be able to set checks
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public Client set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public Engine engine() {
        // TODO: return engines
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public void close() {
        if (output != null) {
            output.flush();
        }
    }
}
