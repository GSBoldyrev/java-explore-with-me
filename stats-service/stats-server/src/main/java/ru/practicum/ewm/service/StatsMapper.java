package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;

public class StatsMapper {
    public static Hit fromHitDto(HitDto hitDto) {

        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .hitAt(LocalDateTime.now())
                .build();
    }

    public static HitDto toHitDto(Hit hit) {

        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .hitAt(hit.getHitAt())
                .build();
    }
}
