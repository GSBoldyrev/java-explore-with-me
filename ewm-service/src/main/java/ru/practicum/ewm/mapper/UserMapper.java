package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserFullDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User fromUserDto(NewUserDto newUser) {

        return new User(null, newUser.getEmail(), newUser.getName(), false, null);
    }

    public static UserDto toUserDto(User user) {

        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {

        return new UserShortDto(user.getId(), user.getName(), user.getPrivateProfile());
    }

    public static UserFullDto toUserFullDto(User user) {

        if (user.getPrivateProfile()) {
            return new UserFullDto(user.getId(), user.getName(), user.getPrivateProfile(), null);
        }

        List<UserShortDto> followers = user.getSubscribedOn().stream()
                .map(UserMapper::toUserShortDto)
                .collect(Collectors.toList());

        return new UserFullDto(user.getId(), user.getName(), user.getPrivateProfile(), followers);
    }
}
