package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.model.Request;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {

        return RequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .created(request.getCreated())
                .build();
    }
}

