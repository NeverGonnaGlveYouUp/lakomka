package com.lakomka.models.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.dto.JpersonXmlDto;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.misc.Route;
import com.lakomka.util.DateFormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сущность ЮЛ Покупателя
 */
@Table
@Entity
@Getter
@Setter
public class JPerson {

    public JPerson() {
    }

    public JPerson(RegistrationDto registrationDto) {
        this.phone = registrationDto.getPhone();
        this.KPP = registrationDto.getKpp();
        this.INN = registrationDto.getInn();
        this.OGRN = registrationDto.getOgrn();
        this.address = registrationDto.getJurAddress();
        this.addressDelivery = registrationDto.getDeliveryAddress();
        this.nameFull = registrationDto.getNameFull();
        this.name = registrationDto.getName();
        this.dpAgreement = registrationDto.isDpAgreement();
        this.contact = registrationDto.getContact();
        this.rest = new BigDecimal("0");
        this.restTime = new BigDecimal("0");
        this.basePrice = BasePrice.KONS;
    }

    @Id
    @Column(name = "base_person_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "base_person_id")
    private BasePerson basePerson;

    /**
     * Маршрут
     */
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @JsonIgnore
    @OneToMany(mappedBy = "jPerson")
    private Set<Discount> discounts = new HashSet<>();

    /**
     * Согласие на обработку ПД
     */
    @Column(name = "dp_agreement", nullable = false)
    private boolean dpAgreement = false;

    /**
     * Краткое наименование Покупателя
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * Полное реестровое наименование Покупателя
     */
    @Column(name = "name_full", nullable = false)
    private String nameFull;

    /**
     * Полный реестровый юридический адрес Покупателя
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * ОГРН
     */
    @Column(name = "OGRN", columnDefinition = "char(15)", nullable = false)
    private String OGRN;

    /**
     * ИНН
     */
    @Column(name = "INN", columnDefinition = "char(12)", nullable = false)
    private String INN;

    /**
     * КПП
     */
    @Column(name = "KPP", columnDefinition = "char(9)", nullable = false)
    private String KPP;

    /**
     * Контактный телефон
     */
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    /**
     * Электронная почта
     */
    @Column(name = "email", length = 50)
    private String email;

    /**
     * Контактное лицо
     */
    @Column(name = "contact", nullable = false)
    private String contact;

    /**
     * Должность контактного лица
     */
    @Column(name = "post", length = 50)
    private String post;

    /**
     * Адрес доставки
     */
    @Column(name = "address_delivery", nullable = false)
    private String addressDelivery;

    /**
     * Описание – карта доставки – особые метки или описания чтобы определить местоположение Покупателя
     */
    @Column(name = "map_delivery")
    private String mapDelivery;

    /**
     * Базовая цена
     */
    @Column(name = "base_price", columnDefinition = "char(4)", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BasePrice basePrice;

    /**
     * Количество дней отсрочки за поставленный товар, если 0 (ноль), то расчет за наличные в момент передачи товара
     */
    @Column(name = "shipping_delay_days", precision = 2)
    private Integer day;

    /**
     * Долг за поставленные товары
     */
    @Column(name = "rest", nullable = false)
    private BigDecimal rest;

    /**
     * Просроченный долг за поставленные товары
     */
    @Column(name = "rest_time")
    private BigDecimal restTime;

    /**
     * Вид оплаты (битовое поле) – 0 – нал; 1 – без нал
     */
    @Column(name = "pay_vid")
    private boolean payVid = false;

    /**
     * Признак печати предварительного счета (битовое поле) – 0- печать счета не требуется, 1 – печать счета требуется
     */
    @Column(name = "acc_print")
    private boolean accPrint = false;

    /**
     * Признак печати перечня сертификатов к поставляемым товаров – 0 – печать не требуется, 1- печать требуется
     */
    @Column(name = "sertif_print")
    private boolean sertifPrint = false;

    /**
     * Признак разрешения оформления возвратных документов
     */
    @Column(name = "vzr_doc")
    private boolean vzrDoc = false;

    /**
     * Признак обязательного наличия договора – 0 – договор не обязателен, 1 – договор обязателен
     */
    @Column(name = "dogovor")
    private boolean dogovor = false;

    /**
     * Атрибуты договора в одной строке – Покупателю для информации
     */
    @Column(name = "dogovor_alt")
    private String dogovorAlt;

    /**
     * Признак отправки документов по ЭДО – 0 – отправка документов по эдо не требуется, 1 – требуется отправка документов по эдо
     */
    @Column(name = "edo")
    private boolean edo = false;

    /**
     * Дата начала оформления документов по эдо
     */
    @Column(name = "edo_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date edoDate;

    /**
     * Примечание с нашей стороны
     */
    @Column(name = "prim")
    private String prim;

    /**
     * Скидка для всего ассортимента товаров
     */
    @Column(name = "global_discount")
    private Integer globalDiscount = 0;

    public String getINN() {
        return INN.trim();
    }

    public String getKPP() {
        return KPP.trim();
    }

    public String getOGRN() {
        return OGRN.trim();
    }

    public JpersonXmlDto toJpersonXmlDto() {
        JpersonXmlDto dto = new JpersonXmlDto();

        dto.setShopId(this.id);
        dto.setOfficeId(null); // todo

        dto.setAccPrint(this.accPrint);
        dto.setAddress(this.address);
        dto.setAddressDelivery(this.addressDelivery);
        dto.setBasePrice(this.basePrice.name());
        dto.setContact(this.contact);
        dto.setDiscounts( // todo
                Optional.ofNullable(this.discounts)
                        .map(set -> set.stream()
                                .map(Discount::getId)
                                .map(id -> Long.toString(id))
                                .sorted()
                                .collect(Collectors.joining(",")))
                        .orElse(null)
        );
        dto.setDogovor(this.dogovor);
        dto.setDogovorAlt(this.dogovorAlt);
        dto.setDpAgreement(this.dpAgreement);
        dto.setEdo(this.edo);
        dto.setEdoDate(
                Optional.ofNullable(this.edoDate)
                        .map(d -> DateFormatUtil.formatDate(d, DateFormatUtil.SHORT_DATE_FORMATTER))
                        .orElse(null)
        );
        dto.setEmail(this.email);
        dto.setGlobalDiscount(this.globalDiscount);
        dto.setINN(this.getINN());
        dto.setKPP(this.getKPP());
        dto.setMapDelivery(this.mapDelivery);
        dto.setName(this.name);
        dto.setNameFull(this.nameFull);
        dto.setOGRN(this.getOGRN());
        dto.setPayVid(this.payVid);
        dto.setPhone(this.phone);
        dto.setPost(this.post);
        dto.setPrim(this.prim);
        dto.setRest(this.rest);
        dto.setRestTime(this.restTime);
        dto.setRouteDays( // todo
                Optional.ofNullable(this.route)
                        .map(Route::getRouteString)
                        .orElse(null)
        );
        dto.setSertifPrint(this.sertifPrint);
        dto.setShippingDelayDays(this.day);
        dto.setVzrDoc(this.vzrDoc);
        return dto;
    }

}
