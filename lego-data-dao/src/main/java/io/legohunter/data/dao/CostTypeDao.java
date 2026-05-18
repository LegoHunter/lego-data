package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.mybatis.mapper.CostTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CostTypeDao {
    private final CostTypeMapper costTypeMapper;

    public List<CostType> findAll() {
        return costTypeMapper.findAll();
    }

    public Optional<CostType> findCostTypeByCode(final String costTypeCode) {
        return costTypeMapper.findCostTypeByCode(costTypeCode);
    }

    public void insert(CostType costType) {
        costTypeMapper.insert(costType);
    }

    public void update(CostType costType) {
        costTypeMapper.update(costType);
    }
}