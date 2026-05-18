package io.legohunter.data.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CurrencyCode {
    ARS("Argentine Peso"),
    AUD("Australian Dollar"),
    BRL("Brazilian Real"),
    BGN("Bulgarian Lev"),
    CAD("Canadian Dollar"),
    CNY("Chinese Yuan"),
    CZK("Czech Koruna"),
    DKK("Danish Krone"),
    EUR("Euro"),
    GTQ("Guatemalan Quetzal"),
    HKD("Hong Kong Dollar"),
    HUF("Hungarian Forint"),
    INR("Indian Rupee"),
    IDR("Indonesian Rupiah"),
    ILS("Israeli New Shekel"),
    JPY("Japanese Yen"),
    MOP("Macau Pataca"),
    MYR("Malaysian Ringgit"),
    MXN("Mexican Peso"),
    NZD("New Zealand Dollar"),
    NOK("Norwegian Kroner"),
    PHP("Philippine Peso"),
    PLN("Polish Zloty"),
    GBP("Pound Sterling"),
    RON("Romanian New Lei"),
    RUB("Russian Rouble"),
    RSD("Serbian Dinar"),
    SGD("Singapore Dollar"),
    ZAR("South African Rand"),
    KRW("South Korean Won"),
    SEK("Swedish Krona"),
    CHF("Swiss Franc"),
    TWD("Taiwan New Dollar"),
    THB("Thai Baht"),
    TRY("Turkish Lira"),
    UAH("Ukraine Hryvnia"),
    USD("US Dollar");

    private final String countryName;

    public String getCountryName() {
        return countryName;
    }
}
