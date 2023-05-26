package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitMapper {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static EndpointHit toEndpointHit(EndpointHitDto hit) {

        return new EndpointHit(null, hit.getApp(), hit.getUri(), hit.getIp(), fromString(hit.getTimestamp()));
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit hit) {
        return new EndpointHitDto(hit.getApp(), hit.getUri(), hit.getIp(), toString(hit.getMoment()));
    }

    public static String toString(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(localDateTime);
    }

    public static LocalDateTime fromString(String localDateTimeAsString) {
        return LocalDateTime.from(DateTimeFormatter.ofPattern(
                DATE_TIME_PATTERN).parse(
                localDateTimeAsString));
    }
}
