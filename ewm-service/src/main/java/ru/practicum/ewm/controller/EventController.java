package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.misc.EventSort;
import ru.practicum.ewm.misc.EventState;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    private final StatsClient statsClient;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addByUser(@PathVariable Long userId,
                                  @RequestBody @Valid NewEventDto newEventDto) {

        return service.addByUser(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto GetByIdByUser(@PathVariable Long userId,
                                      @PathVariable Long eventId) {

        return service.getByIdByUser(userId, eventId);
    }

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllByUser(@PathVariable Long userId,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {

        return service.getAllByUser(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateByUser(@PathVariable(name = "userId") Long userId,
                                     @PathVariable(name = "eventId") Long eventId,
                                     @RequestBody(required = false) @Valid UpdateEventUserDto updates) {

        return service.updateByUser(userId, eventId, updates);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestsByUser(@PathVariable(name = "userId") Long userId,
                                              @PathVariable(name = "eventId") Long eventId) {

        return service.getRequestsByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestResultDto updateRequestsByUser(@PathVariable(name = "userId") Long userId,
                                                 @PathVariable(name = "eventId") Long eventId,
                                                 @RequestBody @Valid RequestStatusDto updates) {

        return service.updateRequestsByUser(userId, eventId, updates);
    }

    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                            @RequestParam(name = "states", required = false) List<EventState> states,
                                            @RequestParam(name = "categories", required = false) List<Long> categories,
                                            @RequestParam(name = "rangeStart", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {

        return service.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateByAdmin(@PathVariable(name = "eventId") Long eventId,
                                      @RequestBody(required = false)
                                      @Valid UpdateEventAdminDto updates) {

        return service.updateByAdmin(eventId, updates);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@RequestParam(name = "text", required = false) String text,
                                      @RequestParam(name = "categories", required = false) List<Long> categories,
                                      @RequestParam(name = "paid", required = false) Boolean paid,
                                      @RequestParam(name = "rangeStart", required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(name = "rangeEnd", required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(name = "sort", required = false) EventSort sort,
                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                      HttpServletRequest request) {

        statsClient.save(new HitDto(null,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));

        return service.getAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable(name = "id") Long id,
                                HttpServletRequest request) {

        statsClient.save(new HitDto(null,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));

        return service.getById(id);
    }
}
