package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dto.Category;
import io.legohunter.data.mybatis.mapper.CategoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryDao {
    private final CategoryMapper categoryMapper;

    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    public Optional<Category> findCategoryByExternalServiceAndCategoryId(final Integer externalServiceId, final Integer externalCategoryId) {
        return categoryMapper.findCategoryByExternalServiceAndCategoryId(externalServiceId, externalCategoryId);
    }

    public void insert(Category category) {
        try {
            categoryMapper.insert(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void update(Category category) {
        categoryMapper.update(category);
    }

    public void upsert(Category category) {
        categoryMapper.upsert(category);
    }
}