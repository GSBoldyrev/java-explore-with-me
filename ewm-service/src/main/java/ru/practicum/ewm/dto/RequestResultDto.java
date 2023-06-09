package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResultDto {

    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
