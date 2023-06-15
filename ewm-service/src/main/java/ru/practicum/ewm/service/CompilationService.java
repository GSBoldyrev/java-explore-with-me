package ru.practicum.ewm.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compRepo;
    private final EventRepository eventRepo;
    private final EventService eventService;

    public CompilationDto add(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() != null) {
            events = eventRepo.findAllByIdIn(newCompilationDto.getEvents());
        }

        List<EventShortDto> eventsDto = eventService.toShortDtoWithViews(events);

        Compilation compilation = CompilationMapper.fromCompilationDto(newCompilationDto);
        compilation.setEvents(events);
        compRepo.save(compilation);

        return CompilationMapper.toCompilationDto(compilation, eventsDto);
    }

    public CompilationDto update(Long compId, UpdateCompilationDto updateRequest) {
        Compilation compilation = compRepo.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));
        List<EventShortDto> eventsDto = eventService.toShortDtoWithViews(compilation.getEvents());

        if (updateRequest == null) {
            return CompilationMapper.toCompilationDto(compilation, eventsDto);
        }
        if (updateRequest.getEvents() != null) {
            compilation.setEvents(eventRepo.findAllByIdIn(updateRequest.getEvents()));
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null && !updateRequest.getTitle().isBlank()) {
            compilation.setTitle(updateRequest.getTitle());
        }

        compRepo.save(compilation);

        return CompilationMapper.toCompilationDto(compilation, eventsDto);
    }

    public void delete(Long compId) {

        if (!compRepo.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " not found");
        }

        compRepo.deleteById(compId);
    }

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compRepo.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            compilations = compRepo.findAllByPinned(pinned, PageRequest.of(from / size, size));
        }

        return compilations.stream().map(compilation -> {
                    List<EventShortDto> eventsDto = eventService
                            .toShortDtoWithViews(compilation.getEvents());
                    return CompilationMapper.toCompilationDto(compilation, eventsDto);
                })
                .collect(Collectors.toList());
    }

    public CompilationDto getById(Long compId) {
        Compilation compilation = compRepo.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));
        List<EventShortDto> eventsDto = eventService
                .toShortDtoWithViews(compilation.getEvents());

        return CompilationMapper.toCompilationDto(compilation, eventsDto);
    }
}