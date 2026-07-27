## Lego Data module

Provides data layer to lego database.

## Inventory Intake Data Support

Phase 1 Direct REST Inventory Intake requires payment persistence as part of a complete LEGO Item Acquisition transaction.

The current data layer includes:

- `Payment` DTO.
- `PaymentMapper` and XML result map.
- `PaymentDao`.
- Transaction-scoped payment replacement through `PaymentDao.setTransactionPayments(...)`.
- Transaction read-back helpers for payments, transaction costs, transaction items, and transaction item costs.
- Lookup-backed validators for party ids and payment platform names.

Money-bearing payment fields use `BigDecimal`. Existing transaction cost and transaction item cost DTOs still use their legacy numeric types and should be migrated separately if broader money-type cleanup is desired.

`transactions.transaction_date` and `payment.payment_date` are date-only fields. The DTOs use `LocalDate`, the mappers bind them with `jdbcType=DATE`, and H2 test schemas define both columns as `DATE`.

`current_schema.ddl` is single-sourced from `lego-data-mybatis/src/test/resources`. The DAO module copies that schema into its test classpath through its Maven `testResources` configuration so DAO and mapper integration tests run against the same H2 schema file.
