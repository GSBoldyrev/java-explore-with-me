package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.error.BadRequestException;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.misc.RequestStatus;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.misc.EventState.PUBLISHED;
import static ru.practicum.ewm.misc.RequestStatus.CANCELED;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository reqRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    public RequestDto add(long userId, long eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        int confRequests = event.getConfirmedRequests();
        int partLimit = event.getParticipantLimit();

        if (reqRepo.existsByRequester(user)) {
            throw new ConflictException("Request from user with id=" + userId + "already exist");
        }
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("You can not send request for your event");
        }
        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictException("You can send request only for published event");
        }
        if (confRequests == partLimit && partLimit != 0) {
            throw new ConflictException("Participant limit already reached");
        }

        RequestStatus status = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(confRequests + 1);
            eventRepo.save(event);
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .status(status)
                .requester(user)
                .event(event)
                .build();

        return RequestMapper.toRequestDto(reqRepo.save(request));
    }

    public RequestDto cancel(long userId, long requestId) {

        Request reqToUpdate = reqRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        if (reqToUpdate.getRequester().getId() != userId) {
            throw new BadRequestException("You can cancel only your request");
        }
        reqToUpdate.setStatus(CANCELED);

        return RequestMapper.toRequestDto(reqRepo.save(reqToUpdate));
    }

    public List<RequestDto> getAll(long userId) {
        List<Request> requests = reqRepo.findAllByRequester(userId);

        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
