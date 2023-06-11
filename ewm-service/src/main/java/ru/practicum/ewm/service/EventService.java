package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.error.BadRequestException;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.misc.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.practicum.ewm.misc.RequestStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final CategoryRepository catRepo;
    private final RequestRepository reqRepo;
    private final StatsClient statsClient;

    public EventFullDto addByUser(Long userId, NewEventDto newEventDto) {

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("incorrect event time");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = catRepo.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));

        Event event = EventMapper.fromEventDto(newEventDto);
        event.setCreated(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setInitiator(user);
        event.setCategory(category);
        event.setConfirmedRequests(0);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepo.save(event));
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    public EventFullDto getByIdByUser(Long userId, Long eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!user.equals(event.getInitiator())) {
            throw new NotFoundException("You can view only your events");
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Long views = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(views);

        return eventFullDto;
    }

    public List<EventFullDto> getAllByUser(Long userId, Integer from, Integer size) {

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        List<Event> events = eventRepo.findAllForInitiator(userId, PageRequest.of(from / size, size));

        return toFullDtoWithViews(events);
    }

    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserDto updateRequest) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!user.equals(event.getInitiator())) {
            throw new NotFoundException("You can update only your events");
        }
        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("you cannot update event, which is already published");
        }
        fillEvent(updateRequest.getAnnotation(),
                updateRequest.getCategory(),
                updateRequest.getDescription(),
                updateRequest.getEventDate(),
                updateRequest.getLocation(),
                updateRequest.getPaid(),
                updateRequest.getParticipantLimit(),
                updateRequest.getRequestModeration(),
                updateRequest.getTitle(),
                event,
                2);

        EventStateAction stateAction = updateRequest.getStateAction();
        if (stateAction == null) {
            event.setState(EventState.PENDING);
        }
        if (stateAction != null) {
            if (EventStateAction.CANCEL_REVIEW.equals(stateAction)) {
                event.setState(EventState.CANCELED);
            } else if (EventStateAction.SEND_TO_REVIEW.equals(stateAction)) {
                event.setState(EventState.PENDING);
            }
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepo.save(event));
        // Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    public List<RequestDto> getRequestsByUser(Long userId, Long eventId) {

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!eventRepo.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return reqRepo.findByEventInitiatorIdAndEventId(userId, eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public RequestResultDto updateRequestsByUser(Long userId, Long eventId, RequestStatusDto updateRequest) {

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        int partLimit = event.getParticipantLimit();
        int confRequests = event.getConfirmedRequests();
        log.info("ПРАВКА ТЕСТОВ. У СОБЫТИЯ ЛИМИТ УЧАСТНИКОВ {} И {} ПОДТВЕРЖДЕННЫХ ЗАЯВОК", partLimit, confRequests );

        if (partLimit == confRequests && partLimit != 0) {
            log.info("СЕЙЧАС БУДЕТ 409");
            throw new ConflictException("Participant limit for event reached");
        }

        log.info("ПРАВКА ТЕСТОВ. В ЗАЯВКЕ {} ЗАПРОСОВ", updateRequest.getRequests());
        log.info("ПРАВКА ТЕСТОВ. ЗАЯВКА НА {}", updateRequest.getStatus());
        List<Request> requests = reqRepo.findAllByParam(userId, eventId, updateRequest.getRequests());
        log.info("ПРАВКА ТЕСТОВ. НАЙДЕНО ЗАЯВОК {}", requests.size());

        for (Request r: requests) {
            if (!r.getStatus().equals(PENDING)) {
                throw new ConflictException("Request status should be Pending");
            }
        }

        List<Request> rejectedRequest = new ArrayList<>();
        List<Request> confirmedRequest = new ArrayList<>();
        RequestResultDto result = new RequestResultDto(null, null);
        switch (updateRequest.getStatus()) {
            case REJECTED:
                requests.forEach(request -> {
                    request.setStatus(REJECTED);
                    rejectedRequest.add(request);
                });
                log.info("ПРАВКА ТЕСТОВ. ОТМЕНЕНО ЗАЯВОК {}", rejectedRequest.size());
                reqRepo.saveAll(rejectedRequest);
                result.setConfirmedRequests(List.of());
                result.setRejectedRequests(rejectedRequest.isEmpty() ? List.of() : rejectedRequest
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()));

                return result;
            case CONFIRMED:
                requests.forEach(request -> {
                    if (partLimit - event.getConfirmedRequests() > 0
                            || partLimit == 0) {
                        request.setStatus(CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests()+1);
                        confirmedRequest.add(request);
                    } else {
                        request.setStatus(REJECTED);
                        rejectedRequest.add(request);
                    }
                });
                log.info("ПРАВКА ТЕСТОВ. ОТМЕНЕНО ЗАЯВОК {}", rejectedRequest.size());
                log.info("ПРАВКА ТЕСТОВ. ПОДТВЕРЖДЕНО ЗАЯВОК {}", confirmedRequest.size());
                reqRepo.saveAll(rejectedRequest);
                reqRepo.saveAll(confirmedRequest);

                eventRepo.save(event);

                result.setConfirmedRequests(confirmedRequest.isEmpty() ? List.of() : confirmedRequest
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()));
                result.setRejectedRequests(rejectedRequest.isEmpty() ? List.of() : rejectedRequest
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()));
        }

        return result;
    }

    public List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Integer from, Integer size) {

        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepo
                .findAllForAdminApi(users, states, categories, rangeStart, rangeEnd, page).stream()
                .collect(Collectors.toList());


        return toFullDtoWithViews(events);
    }

    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminDto updateRequest) {

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Incorrect event time");
        }

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("status of event is not PENDING");
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction() == EventStateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
            } else if (updateRequest.getStateAction() == EventStateAction.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            }
        }

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isAfter(event.getEventDate().minusHours(1))) {
            throw new BadRequestException("incorrect date");
        }

        fillEvent(updateRequest.getAnnotation(),
                updateRequest.getCategory(),
                updateRequest.getDescription(),
                updateRequest.getEventDate(),
                updateRequest.getLocation(),
                updateRequest.getPaid(),
                updateRequest.getParticipantLimit(),
                updateRequest.getRequestModeration(),
                updateRequest.getTitle(),
                event,
                1);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepo.save(event));
        // Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    public List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Boolean onlyAvailable, EventSort sort,
                                      Integer from, Integer size) {

        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepo
                .findAllForPublicApi(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page).stream()
                .collect(Collectors.toList());
        List<EventShortDto> eventsDto = toShortDtoWithViews(events);

        if (EventSort.VIEWS.equals(sort)) {
            eventsDto = eventsDto.stream()
                    .sorted((dto1, dto2) -> dto2.getViews().compareTo(dto1.getViews()))
                    .collect(Collectors.toList());
        }

        return eventsDto;
    }

    public EventFullDto getById(Long id) {

        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not yet published");
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(view);

        return eventFullDto;
    }

    public Map<Integer, Long> getViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(id -> String.format("/events/%s", id))
                .collect(Collectors.toUnmodifiableList());

        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();

        List<StatsDto> stats = statsClient.get(start, end, uris, false);

        return stats.stream()
                .filter(viewDto -> viewDto.getApp().equals("ewm-service"))
                .collect(Collectors.toMap(viewDto -> {
                                    Pattern pattern = Pattern.compile("/events/([0-9]*)");
                                    Matcher matcher = pattern.matcher(viewDto.getUri());
                                    return Integer.parseInt(matcher.group(1));
                                },
                                StatsDto::getHits));
    }

    public List<EventFullDto> toFullDtoWithViews(List<Event> events) {
        Map<Integer, Long> views = getViews(events);
        return events.stream()
                .map(event -> {
                    EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
                    eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));
                    return eventFullDto;
                }).collect(Collectors.toList());
    }

    public List<EventShortDto> toShortDtoWithViews(List<Event> events) {

        Map<Integer, Long> views = getViews(events);

        return events.stream()
                .map(event -> {
                    EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
                    eventShortDto.setViews(views.getOrDefault(event.getId(), 0L));
                    return eventShortDto;
                }).collect(Collectors.toList());
    }

    private void fillEvent(String annotation, Long categoryId, String description, LocalDateTime eventDate,
                           Location location, Boolean paid, Integer participantLimit, Boolean requestModeration,
                           String title, Event event, int limit) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (categoryId != null) {
            Category category = catRepo.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(limit))) {
            throw new BadRequestException("incorrect event time");
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }
    }
}
