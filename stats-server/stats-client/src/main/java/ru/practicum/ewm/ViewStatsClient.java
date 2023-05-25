package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ViewStatsClient extends BaseClient {

    @Autowired
    public ViewStatsClient() {
        super();
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
