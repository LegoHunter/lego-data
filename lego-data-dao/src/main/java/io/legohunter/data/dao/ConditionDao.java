package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.mybatis.mapper.ConditionMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConditionDao {
    private final ConditionMapper conditionMapper;

    public List<Condition> findAll() {
        return conditionMapper.findAll();
    }

    public Optional<Condition> findConditionById(final Integer conditionId) {
        return conditionMapper.findConditionById(conditionId);
    }

    public Optional<Condition> findByConditionCode(final String conditionCode) {
        return conditionMapper.findByConditionCode(conditionCode);
    }

    public void insert(Condition condition) {
        conditionMapper.insert(condition);
    }

    public void update(Condition condition) {
        conditionMapper.update(condition);
    }
}