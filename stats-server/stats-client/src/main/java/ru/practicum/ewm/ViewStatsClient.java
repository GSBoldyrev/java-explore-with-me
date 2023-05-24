package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class ViewStatsClient extends BaseClient {

    private static final String API_PREFIX = "/stats";

    @Autowired
    public ViewStatsClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getNotSpecifiedStats(String start, String end, Boolean unique) {
        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "unique", unique
        );

        return get("?start={start}&end={end}&unique={unique}", params);
    }

    public ResponseEntity<Object> getSpecifiedStats(String start, String end, String[] uris, Boolean unique) {
        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );

        return get("?start={start}&end={end}&uris={uris}&unique={unique}", params);
    }
}
