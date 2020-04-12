package com.jay.asset;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import javax.ws.rs.client.Client;

public class AssetClientApplication extends Application<AssetClientConfiguration> {
    @Override
    public void run(AssetClientConfiguration config, Environment environment) throws Exception {

        final Client client = new JerseyClientBuilder(environment)
                .using(config.getJerseyClientConfiguration())
                .build(getName());
//        environment.jersey().register(new ExternalServiceResource(client));
    }
}