package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.Category;
import net.lego.data.v1.mybatis.mapper.CategoryMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("categoryDaoV1")
@RequiredArgsConstructor
public class CategoryDao {
    private final CategoryMapperV1 categoryMapperV1;

    public List<Category> findAll() {
        return categoryMapperV1.findAll();
    }

    public Optional<Category> findCategoryById(final Long categoryId) {
        return categoryMapperV1.findCategoryById(categoryId);
    }
}
