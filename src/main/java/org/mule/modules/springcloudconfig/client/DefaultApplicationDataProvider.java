package org.mule.modules.springcloudconfig.client;

import org.mule.modules.springcloudconfig.ApplicationDataProvider;
import org.mule.modules.springcloudconfig.ConfigurationNotFoundException;
import org.mule.modules.springcloudconfig.config.ConnectorConfig;
import org.mule.modules.springcloudconfig.model.ApplicationConfiguration;
import org.mule.modules.springcloudconfig.model.ApplicationDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Map;

public class DefaultApplicationDataProvider implements ApplicationDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationDataProvider.class);


    private final ConnectorConfig config;
    private final Client restClient;

    public DefaultApplicationDataProvider(ConnectorConfig config, Client restClient) {
        this.config = config;
        this.restClient = restClient;
    }

    @Override
    public Map<String, Object> loadApplication(String name, String version, String environment) {

        WebTarget target = restClient.target(config.getConfigServerBaseUrl())
                .path(name)
                .path(version)
                .path(environment);

        //read it as a java map
        Map<String, Object> result = target.request().accept(MediaType.APPLICATION_JSON).get(Map.class);

        logger.debug("Got settings from cloud config server: {}", result);


        return result;
    }

    @Override
    public InputStream loadDocument(ApplicationDocument doc, ApplicationConfiguration app) throws ConfigurationNotFoundException {

        WebTarget target = restClient.target(config.getConfigServerBaseUrl())
                .path(app.getName())
                .path(app.getVersion())
                .path(app.getEnvironment())
                .path("dynamic")
                .path(doc.getName());

        return target.request().accept(doc.getContentType()).get(InputStream.class);
    }
}
