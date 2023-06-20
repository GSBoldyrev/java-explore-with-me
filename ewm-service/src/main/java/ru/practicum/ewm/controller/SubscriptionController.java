package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.SubscriptionDto;
import ru.practicum.ewm.dto.UserFullDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class SubscriptionController {

    private final SubscriptionService service;

    @PatchMapping("/{userId}/subscriptions/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserShortDto updateProfile(@PathVariable Long userId,
                                      @RequestParam Boolean profile) {

        return service.updateProfile(userId, profile);
    }

    @PostMapping("/{userId}/subscriptions/{followerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserShortDto> addSubscription(@PathVariable Long userId,
                                       @PathVariable Long followerId) {

        return service.addSubscription(userId, followerId);
    }

    @PatchMapping("/{userId}/subscriptions/cancel/{followerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserShortDto> cancelSubscription(@PathVariable Long userId,
                                          @PathVariable Long followerId) {

        return service.cancelSubscription(userId, followerId);
    }

    @GetMapping("/{userId}/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserFullDto> getFollowers(@PathVariable Long userId,
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {

        return service.getFollowers(userId, from, size);
    }

    @GetMapping("/{userId}/subscriptions/events")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getAllSubscriptions(@PathVariable Long userId) {

        return service.getAllSubscriptions(userId);
    }

    @GetMapping("/{userId}/subscriptions/events/{followerId}")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionDto getSubscription(@PathVariable Long userId,
                                           @PathVariable Long followerId) {

        return service.getSubscription(userId, followerId);
    }
}
