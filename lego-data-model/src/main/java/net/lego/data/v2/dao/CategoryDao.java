package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lego.data.v2.dto.Category;
import net.lego.data.v2.mybatis.mapper.CategoryMapper;
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

    public Optional<Category> findCategoryById(final Integer categoryId) {
        return categoryMapper.findCategoryById(categoryId);
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
}
