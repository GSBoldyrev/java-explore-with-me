package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.service.EndpointHitService;

@RestController
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
public class EndpointHitController {

    private final EndpointHitService service;

    @PostMapping
    public EndpointHitDto addHit(@RequestBody EndpointHitDto hit) {
        log.info("Посещение сервиса {} пользователем {}",
                hit.getApp(),
                hit.getIp());

        return service.add(hit);
    }
}
