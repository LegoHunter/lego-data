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

`current_schema.ddl` is single-sourced from `lego-data-mybatis/src/test/resources`. The MyBatis module publishes its test resources through a Maven `tests` classifier artifact, and the DAO module consumes that artifact as a test-scoped `test-jar` dependency so DAO and mapper integration tests run against the same H2 schema file.

## Inventory Read And Correction Data Support

Phase 2 Inventory Read, Search, and Correction workflows add reusable data-layer support for `lego-data-service`:

- `ItemInventorySearchCriteria` models optional search filters for owned inventory.
- `ItemInventoryDao.search(...)` and `ItemInventoryDao.countSearch(...)` delegate to dynamic MyBatis search queries.
- `ItemInventoryMapper.search(...)` supports filters for catalog item number, description, box number, inventory state, sale intent, active flag, physical item facts, condition codes, and transaction date range.
- `TransactionCostDao` now exposes row-scoped transaction-item cost operations: find by id, find by transaction item and cost type, update, and delete.
- Transaction and transaction-item cost replacement helpers delete existing rows even when the replacement list is empty, which keeps explicit empty replacement semantics deterministic.

Business rules remain in `lego-data-service`; DAOs and mappers stay focused on persistence operations.

## BrickLink Inventory Color Policy

The shared `BricklinkInventoryColorPolicy` keeps BrickLink listing-create color semantics consistent across
services. BrickLink SET catalog items (`S` or `SET`) use color id `0` (`Not Applicable`) when the local color is
missing, and reject any nonzero color. Color-specific item types require a positive color id. The policy returns an
effective color id for valid inputs and stable error codes for invalid inputs so callers can fail closed before an
HTTP mutation.

## Environments and BrickLink Stockrooms

`LegoEnvironment` (`io.legohunter.data.enums`) is the one list of LCMS runtime environments: `local`, `sandbox`,
`dev` and `prod`. For each it records the configuration code, whether it is PROD, the BrickLink stockroom it owns
(`stockroom()`: `C` for SANDBOX and local, `B` for DEV, `A` for PROD), and whether it forces every lot into that
stockroom (`forcesStockroom()`). All environments share one BrickLink account, so every environment except PROD forces
each lot it creates or updates into its own stockroom, hidden from buyers. PROD's lots are public by default; it owns
stockroom `A` only for deliberately hiding a particular lot, and never forces lots into it. `BricklinkStockroom` is
the `A`/`B`/`C` enum.

The shared `BricklinkStockroomPolicy` turns those facts into startup checks. Given a service's configured
environment code, production flag and non-prod stockroom, it lists every violation. It rejects an unknown
environment code (so a typo cannot borrow another environment's stockroom), a production flag that disagrees with
the environment, a missing stockroom, a value other than `A`/`B`/`C`, PROD's stockroom `A`, and another environment's
stockroom. lego-data-ingress and lego-data-service both run it at startup and refuse to start on any violation.
Adding an environment means adding a `LegoEnvironment` constant.

Each environment also names the database schema it must be connected to (`databaseSchema()`): `lego_sandbox`,
`lego_dev` and `lego_prod`. LOCAL names none, because a developer run may use any schema except `lego_prod`, including
the in-memory H2 test database. The shared `EnvironmentDatabasePolicy` (`io.legohunter.data.environment`) compares a
service's configured environment code and production flag with the schema its data source reports, ignoring case.
It rejects an unknown environment code, a production flag that disagrees with the environment, a schema it cannot
determine, an environment connected to another environment's schema, and any non-PROD environment connected to
`lego_prod`. Both services run it at startup, so a PROD configuration cannot run against a lower-environment
database, and a lower-environment configuration cannot run against PROD's.
