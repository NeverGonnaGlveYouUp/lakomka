package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for xml export for {@link com.lakomka.models.person.JPerson}
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JpersonXmlDto {

    private Long shopId;

    private Long officeId;

    /**
     * Маршрут
     */
    private String routeDays;

    private String discounts;

    /**
     * Согласие на обработку ПД
     */
    private boolean dpAgreement = false;

    /**
     * Краткое наименование Покупателя
     */
    private String name;

    /**
     * Полное реестровое наименование Покупателя
     */
    private String nameFull;

    /**
     * Полный реестровый юридический адрес Покупателя
     */
    private String address;

    /**
     * ОГРН
     */
    private String OGRN;

    /**
     * ИНН
     */
    private String INN;

    /**
     * КПП
     */
    private String KPP;

    /**
     * Контактный телефон
     */
    private String phone;

    /**
     * Электронная почта
     */
    private String email;

    /**
     * Контактное лицо
     */
    private String contact;

    /**
     * Должность контактного лица
     */
    private String post;

    /**
     * Адрес доставки
     */
    private String addressDelivery;

    /**
     * Описание – карта доставки – особые метки или описания чтобы определить местоположение Покупателя
     */
    private String mapDelivery;

    /**
     * Базовая цена
     */
    private String basePrice;

    /**
     * Количество дней отсрочки за поставленный товар, если 0 (ноль), то расчет за наличные в момент передачи товара
     */
    private Integer shippingDelayDays;

    /**
     * Долг за поставленные товары
     */
    private BigDecimal rest;

    /**
     * Просроченный долг за поставленные товары
     */
    private BigDecimal restTime;

    /**
     * Вид оплаты (битовое поле) – 0 – нал; 1 – без нал
     */
    private boolean payVid;

    /**
     * Признак печати предварительного счета (битовое поле) – 0- печать счета не требуется, 1 – печать счета требуется
     */
    private boolean accPrint;

    /**
     * Признак печати перечня сертификатов к поставляемым товаров – 0 – печать не требуется, 1- печать требуется
     */
    private boolean sertifPrint;

    /**
     * Признак разрешения оформления возвратных документов
     */
    private boolean vzrDoc;

    /**
     * Признак обязательного наличия договора – 0 – договор не обязателен, 1 – договор обязателен
     */
    private boolean dogovor;

    /**
     * Атрибуты договора в одной строке – Покупателю для информации
     */
    private String dogovorAlt;

    /**
     * Признак отправки документов по ЭДО – 0 – отправка документов по эдо не требуется, 1 – требуется отправка документов по эдо
     */
    private boolean edo;

    /**
     * Дата начала оформления документов по эдо
     */
    private String edoDate;

    /**
     * Примечание с нашей стороны
     */
    private String prim;

    /**
     * Скидка для всего ассортимента товаров
     */
    private Integer globalDiscount; // todo не используется в расчете скидок!

}
