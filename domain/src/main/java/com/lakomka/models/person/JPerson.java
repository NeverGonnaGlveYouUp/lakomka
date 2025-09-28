package com.lakomka.models.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.misc.Route;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность ЮЛ Покупателя
 */
@Table
@Entity
public class JPerson {

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
    @JoinColumn(name="route_id", nullable=false)
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
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    /**
     * Контактное лицо
     */
    @Column(name = "contact", nullable = false)
    private String contact;

    /**
     * Должность контактного лица
     */
    @Column(name = "post", length = 50, nullable = false)
    private String post;

    /**
     * Адрес доставки
     */
    @Column(name = "address_delivery", nullable = false)
    private String addressDelivery;

    /**
     * Описание – карта доставки – особые метки или описания чтобы определить местоположение Покупателя
     */
    @Column(name = "map_delivery", nullable = false)
    private String mapDelivery;

    /**
     * Базовая цена
     */
    @Column(name = "base_price", columnDefinition = "char(4)", nullable = false)
    private String basePrice;

    /**
     * Количество дней отсрочки за поставленный товар, если 0 (ноль), то расчет за наличные в момент передачи товара
     */
    @Column(name = "shipping_delay_days", precision = 2, nullable = false)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BasePerson getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(BasePerson basePerson) {
        this.basePerson = basePerson;
    }

    public boolean isDpAgreement() {
        return dpAgreement;
    }

    public void setDpAgreement(boolean dpAgreement) {
        this.dpAgreement = dpAgreement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameFull() {
        return nameFull;
    }

    public void setNameFull(String nameFull) {
        this.nameFull = nameFull;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOGRN() {
        return OGRN;
    }

    public void setOGRN(String OGRN) {
        this.OGRN = OGRN;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

    public String getKPP() {
        return KPP;
    }

    public void setKPP(String KPP) {
        this.KPP = KPP;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getAddressDelivery() {
        return addressDelivery;
    }

    public void setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
    }

    public String getMapDelivery() {
        return mapDelivery;
    }

    public void setMapDelivery(String mapDelivery) {
        this.mapDelivery = mapDelivery;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public BigDecimal getRest() {
        return rest;
    }

    public void setRest(BigDecimal rest) {
        this.rest = rest;
    }

    public BigDecimal getRestTime() {
        return restTime;
    }

    public void setRestTime(BigDecimal restTime) {
        this.restTime = restTime;
    }

    public boolean isPayVid() {
        return payVid;
    }

    public void setPayVid(boolean payVid) {
        this.payVid = payVid;
    }

    public boolean isAccPrint() {
        return accPrint;
    }

    public void setAccPrint(boolean accPrint) {
        this.accPrint = accPrint;
    }

    public boolean isSertifPrint() {
        return sertifPrint;
    }

    public void setSertifPrint(boolean sertifPrint) {
        this.sertifPrint = sertifPrint;
    }

    public boolean isVzrDoc() {
        return vzrDoc;
    }

    public void setVzrDoc(boolean vzrDoc) {
        this.vzrDoc = vzrDoc;
    }

    public boolean isDogovor() {
        return dogovor;
    }

    public void setDogovor(boolean dogovor) {
        this.dogovor = dogovor;
    }

    public String getDogovorAlt() {
        return dogovorAlt;
    }

    public void setDogovorAlt(String dogovorAlt) {
        this.dogovorAlt = dogovorAlt;
    }

    public boolean isEdo() {
        return edo;
    }

    public void setEdo(boolean edo) {
        this.edo = edo;
    }

    public Date getEdoDate() {
        return edoDate;
    }

    public void setEdoDate(Date edoDate) {
        this.edoDate = edoDate;
    }

    public String getPrim() {
        return prim;
    }

    public void setPrim(String prim) {
        this.prim = prim;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Integer getGlobalDiscount() {
        return globalDiscount;
    }

    public void setGlobalDiscount(Integer globalDiscount) {
        this.globalDiscount = globalDiscount;
    }
}
