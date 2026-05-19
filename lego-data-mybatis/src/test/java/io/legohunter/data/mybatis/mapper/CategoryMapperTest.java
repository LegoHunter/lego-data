package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class CategoryMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByExternalServiceAndCategoryFindAllAndUpsert() {
        seedExternalCatalog();
        Category category = category(2, 101, "Parts", null);

        categoryMapper.insert(category);
        category.setCategoryName("LEGO Parts");
        categoryMapper.update(category);
        categoryMapper.upsert(category(2, 101, "Updated Parts", 5));

        assertThat(categoryMapper.findCategoryByExternalServiceAndCategoryId(2, 101))
                .hasValueSatisfying(found -> {
                    assertThat(found.getCategoryName()).isEqualTo("Updated Parts");
                    assertThat(found.getParentId()).isEqualTo(5);
                });
        assertThat(categoryMapper.findAll()).extracting(Category::getExternalCategoryId).containsExactlyInAnyOrder(5, 101);
    }
}
