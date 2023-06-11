package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.User;

public class UserMapper {

    public static User fromUserDto(NewUserDto newUser) {

        return new User(null, newUser.getEmail(), newUser.getName());
    }

    public static UserDto toUserDto(User user) {

        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto ToUserShortDto(User user) {

        return new UserShortDto(user.getId(), user.getName());
    }
}
