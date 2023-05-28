package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.ViewStatsService;

import java.util.List;

@RestController
@RequestMapping(path = "/stats")
@RequiredArgsConstructor
@Slf4j
public class ViewStatsController {

    private final ViewStatsService service;

    @GetMapping
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) String[] uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {

        return service.getStats(start, end, uris, unique);
    }
}
