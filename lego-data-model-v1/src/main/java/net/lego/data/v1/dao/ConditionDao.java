package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.Condition;
import net.lego.data.v1.mybatis.mapper.ConditionMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("conditionDaoV1")
@RequiredArgsConstructor
public class ConditionDao {
    private final ConditionMapperV1 conditionMapperV1;

    public List<Condition> findAll() {
        return conditionMapperV1.findAll();
    }

    public Optional<Condition> findConditionById(final Long conditionId) {
        return conditionMapperV1.findConditionById(conditionId);
    }
}
