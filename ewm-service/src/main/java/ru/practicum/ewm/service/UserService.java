package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.UserMapper.fromUserDto;
import static ru.practicum.ewm.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public UserDto add(NewUserDto newUser) {
        User user = repository.save(fromUserDto(newUser));

        return toUserDto(user);
    }

    public void delete(Long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        repository.deleteById(userId);
    }

    public List<UserDto> getByIds(Long[] ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids == null) {
            return repository.findAll(page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        return repository.findAllByIdIn(Arrays.asList(ids), page).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
