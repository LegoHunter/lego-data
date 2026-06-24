package io.legohunter.data.validation;

import io.legohunter.data.dao.ConditionDao;
import io.legohunter.data.dao.CostTypeDao;
import io.legohunter.data.dao.ExternalCatalogItemDao;
import io.legohunter.data.dao.ExternalServiceDao;
import io.legohunter.data.dao.TransactionPlatformDao;
import io.legohunter.data.dao.TransactionTypeDao;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidatorCoverageTest {

    @Test
    void itemNumberValidatorAcceptsNullExistingItemsAndRejectsMissingItems() {
        ExternalCatalogItemDao catalogItemDao = mock(ExternalCatalogItemDao.class);
        ExternalServiceDao externalServiceDao = mock(ExternalServiceDao.class);
        ConstraintValidatorContext context = mockContext();
        ItemNumberExists annotation = mock(ItemNumberExists.class);
        ExternalService bricklink = new ExternalService();
        bricklink.setExternalServiceId(1);
        bricklink.setServiceCode("BRICKLINK");
        ExternalCatalogItem catalogItem = ExternalCatalogItem.builder()
                .externalCatalogItemId(10)
                .externalItemKey("6390-1")
                .build();
        ItemNumberValidator validator = new ItemNumberValidator(catalogItemDao, externalServiceDao);

        when(annotation.externalService()).thenReturn("BRICKLINK");
        validator.initialize(annotation);
        assertThat(validator.isValid(null, context)).isTrue();

        when(externalServiceDao.findByServiceCode("BRICKLINK")).thenReturn(Optional.of(bricklink));
        when(catalogItemDao.findByExternalServiceIdAndExternalItemKey(1, "6390-1")).thenReturn(Optional.of(catalogItem));
        assertThat(validator.isValid("6390-1", context)).isTrue();

        when(catalogItemDao.findByExternalServiceIdAndExternalItemKey(1, "missing")).thenReturn(Optional.empty());
        assertThat(validator.isValid("missing", context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Item Number was not found in external service BRICKLINK");
    }

    @Test
    void conditionCodeValidatorAcceptsNullExistingCodesAndRejectsMissingCodes() {
        ConditionDao dao = mock(ConditionDao.class);
        ConstraintValidatorContext context = mockContext();
        Condition good = Condition.builder().conditionCode("G").build();
        ConditionCodeValidator validator = new ConditionCodeValidator(dao);

        when(dao.findAll()).thenReturn(List.of(good));
        validator.initialize(mock(ConditionCodeExists.class));
        assertThat(validator.isValid(null, context)).isTrue();

        when(dao.findByConditionCode("G")).thenReturn(Optional.of(good));
        assertThat(validator.isValid("G", context)).isTrue();

        when(dao.findByConditionCode("BAD")).thenReturn(Optional.empty());
        assertThat(validator.isValid("BAD", context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Must be one of [G]");
    }

    @Test
    void costTypeValidatorAcceptsExistingCodesAndRejectsMissingCodes() {
        CostTypeDao dao = mock(CostTypeDao.class);
        ConstraintValidatorContext context = mockContext();
        CostType shipping = CostType.builder().costTypeCode("SHIP").build();
        CostTypeValidator validator = new CostTypeValidator(dao);

        when(dao.findAll()).thenReturn(List.of(shipping));
        validator.initialize(mock(CostTypeExists.class));

        when(dao.findCostTypeByCode("SHIP")).thenReturn(Optional.of(shipping));
        assertThat(validator.isValid("SHIP", context)).isTrue();

        when(dao.findCostTypeByCode("BAD")).thenReturn(Optional.empty());
        assertThat(validator.isValid("BAD", context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Cost Type Code [BAD] is invalid. Must be one of [SHIP]");
    }

    @Test
    void transactionPlatformValidatorAcceptsNullExistingNamesAndRejectsMissingNames() {
        TransactionPlatformDao dao = mock(TransactionPlatformDao.class);
        ConstraintValidatorContext context = mockContext();
        TransactionPlatform bricklink = TransactionPlatform.builder().transactionPlatformName("BrickLink").build();
        TransactionPlatformValidator validator = new TransactionPlatformValidator(dao);

        when(dao.findAll()).thenReturn(List.of(bricklink));
        validator.initialize(mock(TransactionPlatformExists.class));
        assertThat(validator.isValid(null, context)).isTrue();

        when(dao.findTransactionPlatformByName("BrickLink")).thenReturn(Optional.of(bricklink));
        assertThat(validator.isValid("BrickLink", context)).isTrue();

        when(dao.findTransactionPlatformByName("Bad")).thenReturn(Optional.empty());
        assertThat(validator.isValid("Bad", context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Transaction Platform [Bad] is invalid. Must be one of [BrickLink]");
    }

    @Test
    void transactionTypeValidatorAcceptsNullExistingCodesAndRejectsMissingCodes() {
        TransactionTypeDao dao = mock(TransactionTypeDao.class);
        ConstraintValidatorContext context = mockContext();
        TransactionType sale = TransactionType.builder().transactionTypeCode("SALE").build();
        TransactionTypeValidator validator = new TransactionTypeValidator(dao);

        when(dao.findAll()).thenReturn(List.of(sale));
        validator.initialize(mock(TransactionTypeExists.class));
        assertThat(validator.isValid(null, context)).isTrue();

        when(dao.findTransactionTypeByCode("SALE")).thenReturn(Optional.of(sale));
        assertThat(validator.isValid("SALE", context)).isTrue();

        when(dao.findTransactionTypeByCode("BAD")).thenReturn(Optional.empty());
        assertThat(validator.isValid("BAD", context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Transaction Type [BAD] is invalid. Must be one of [SALE]");
    }

    private ConstraintValidatorContext mockContext() {
        return mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);
    }
}
