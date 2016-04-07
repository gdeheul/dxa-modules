package com.sdl.dxa.modules.degrees51.contextengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.modules.degrees51.cloud.Endpoints;
import com.sdl.dxa.modules.degrees51.cloud.MatchEntity;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.dd4t.core.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@Profile("51degrees.context.provider")
@Primary
public class Degrees51ClaimsProvider implements ContextClaimsProvider {

    @Autowired
    private Endpoints endpoints;

    @Autowired
    private ObjectMapper objectMapper;

    private boolean disabled;

    @Override
    public Map<String, Object> getContextClaims(String aspectName) throws DxaException {
        log.trace("51degrees.context.provider activated");

        MatchEntity match = match();

        return Collections.emptyMap();
    }

    @Override
    public String getDeviceFamily() {
        return null;
    }


    @SneakyThrows(URISyntaxException.class)
    private MatchEntity match() {
        URI uri = new URIBuilder(this.endpoints.match())
                .addParameter("user-agent", HttpUtils.getCurrentRequest().getHeader("user-agent"))
                .build();

        try {
            CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(uri));

            return objectMapper.readValue(response.getEntity().getContent(), MatchEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

}
