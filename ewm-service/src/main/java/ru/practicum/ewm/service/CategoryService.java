package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.ewm.mapper.CategoryMapper.fromCategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository catRepo;
    private final EventRepository eventRepo;

    public CategoryDto add(NewCategoryDto newCategory) {
        Category category = catRepo.save(fromCategoryDto(newCategory));

        return toCategoryDto(category);
    }

    public CategoryDto update(long catId, NewCategoryDto category) {
        Category catToUpdate = catRepo.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        catToUpdate.setName(category.getName());

        return toCategoryDto(catRepo.save(catToUpdate));
    }

    public void delete(long catId) {
        if (!catRepo.existsById(catId)) {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }
        if (!eventRepo.existsByCategoryId(catId)) {
            catRepo.deleteById(catId);
        } else {
            throw new ConflictException("The category is not empty");
        }
    }

    public List<CategoryDto> getAll(int from, int size) {

        return catRepo.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(long catId) {
        Category category = catRepo.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        return toCategoryDto(category);
    }
}
