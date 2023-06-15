package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @Email
    @NotNull
    @Size(min = 6, max = 254)
    String email;
    @Size(min = 2, max = 250)
    @NotBlank
    String name;
}
