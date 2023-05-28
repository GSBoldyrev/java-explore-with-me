package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ViewStatsDto {

    String app;
    String uri;
    Long hits;
}
