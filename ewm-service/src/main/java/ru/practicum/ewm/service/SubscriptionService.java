package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.SubscriptionDto;
import ru.practicum.ewm.dto.UserFullDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.UserMapper.toUserShortDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    public UserShortDto updateProfile(Long userId, Boolean profile) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        if (user.getPrivateProfile().equals(profile)) {
            throw new ConflictException("Your profile is already set to selected value");
        }
        user.setPrivateProfile(profile);

        return toUserShortDto(userRepo.save(user));
    }

    public List<UserShortDto> addSubscription(Long userId, Long followerId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        if (follower.getPrivateProfile()) {
            throw new ConflictException("User has closed account, you can not follow him");
        }
        List<User> followers;
        if (user.getSubscribedOn() == null) {
            followers = new ArrayList<>();
        } else {
            followers = user.getSubscribedOn();
        }
        followers.add(follower);
        user.setSubscribedOn(followers);
        userRepo.save(user);

        return followers.stream()
                .map(UserMapper::toUserShortDto)
                .collect(Collectors.toList());
    }

    public List<UserShortDto> cancelSubscription(Long userId, Long followerId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<User> followers = user.getSubscribedOn();
        followers.remove(follower);
        user.setSubscribedOn(followers);
        userRepo.save(user);

        return followers.stream()
                .map(UserMapper::toUserShortDto)
                .collect(Collectors.toList());
    }

    public Page<UserFullDto> getFollowers(Long userId, int from, int size) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (user.getSubscribedOn() == null) {
            throw new ConflictException("You do not have followers yet!");
        }

        List<User> followers = user.getSubscribedOn();

        Pageable pageRequest = PageRequest.of(from / size, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), followers.size());

        List<UserFullDto> pageContent = followers.subList(start, end).stream()
                .map(UserMapper::toUserFullDto).collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageRequest, followers.size());
    }

    public List<SubscriptionDto> getAllSubscriptions(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<SubscriptionDto> result = new ArrayList<>();
        List<UserShortDto> followers = user.getSubscribedOn().stream()
                .map(UserMapper::toUserShortDto)
                .collect(Collectors.toList());
        for (UserShortDto u: followers) {
            List<EventShortDto> events = eventRepo.findAllForInitiator(u.getId()).stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
            result.add(new SubscriptionDto(u, events));
        }

        return result;
    }

    public SubscriptionDto getSubscription(Long userId, Long followerId) {

        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        UserShortDto follower = userRepo.findById(followerId)
                .map(UserMapper::toUserShortDto)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<EventShortDto> events = eventRepo.findAllForInitiator(followerId).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        return new SubscriptionDto(follower, events);
    }
}
