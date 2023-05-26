package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.EndpointHitRepository;

@Service
@RequiredArgsConstructor
public class EndpointHitService {

    private final EndpointHitRepository repository;

    public EndpointHitDto add(EndpointHitDto hit) {
        EndpointHit receivedHit = EndpointHitMapper.toEndpointHit(hit);
        EndpointHit savedHit = repository.save(receivedHit);

        return EndpointHitMapper.toEndpointHitDto(savedHit);
    }
}
