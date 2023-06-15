package ru.practicum.ewm.dto;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class Location {

    Float lat;
    Float lon;
}
