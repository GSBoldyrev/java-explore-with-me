package ru.practicum.ewm.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {

    private String app;
    private String uri;
    private Long hits;
}
