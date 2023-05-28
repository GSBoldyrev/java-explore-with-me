package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;

@Service
public class EndPointHitClient extends BaseClient {

    @Autowired
    public EndPointHitClient() {
        super();
    }

    public ResponseEntity<Object> addHit(EndpointHitDto hit) {

        return post("", hit);
    }
}
