package com.lakomka.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Сущность ЮЛ Покупателя
 */
@Entity
public class JPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 12)
    private Long id;

    /**
     * Согласие на обработку ПД
     */
    @Column(name = "dp_agreement")
    private boolean dpAgreement = false;

    /**
     * Краткое наименование Покупателя
     */
    @Column(name = "name", length = 50)
    private String name;

    /**
     * Полное реестровое наименование Покупателя
     */
    @Column(name = "name_full")
    private String nameFull;

    /**
     * Полный реестровый юридический адрес Покупателя
     */
    @Column(name = "address")
    private String address;

    /**
     * ОГРН
     */
    @Column(name = "OGRN", length = 15)
    private String OGRN;

    /**
     * ИНН
     */
    @Column(name = "INN", length = 12, nullable = false)
    private String INN;

    /**
     * КПП
     */
    @Column(name = "KPP", length = 9)
    private String KPP;

    /**
     * Контактный телефон
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Электронная почта
     */
    @Column(name = "email", length = 50)
    private String email;

    /**
     * Контактное лицо
     */
    @Column(name = "contact")
    private String contact;

    /**
     * Должность контактного лица
     */
    @Column(name = "post", length = 50)
    private String post;

    /**
     * Адрес доставки
     */
    @Column(name = "address_delivery")
    private String addressDelivery;

    /**
     * Описание – карта доставки – особые метки или описания чтобы определить местоположение Покупателя
     */
    @Column(name = "map_delivery")
    private String mapDelivery;

    /**
     * Базовая цена
     */
    @Column(name = "base_price", length = 4, nullable = false)
    private String basePrice;

    /**
     * Количество дней отсрочки за поставленный товар, если 0 (ноль), то расчет за наличные в момент передачи товара
     */
    @Column(name = "day", length = 2)
    private String day;

    /**
     * Долг за поставленные товары
     */
    @Column(name = "rest", length = 12, nullable = false)
    private BigDecimal rest = new BigDecimal("0");

    /**
     * Просроченный долг за поставленные товары
     */
    @Column(name = "rest_time", length = 12, nullable = false)
    private BigDecimal restTime = new BigDecimal("0");

    //todo какой дефолт?
    /**
     * Вид оплаты (битовое поле) – 0 – нал; 1 – без нал
     */
    @Column(name = "pay_vid", nullable = false)
    private boolean payVid;

    //todo какой дефолт?
    /**
     * Признак печати предварительного счета (битовое поле) – 0- печать счета не требуется, 1 – печать счета требуется
     */
    @Column(name = "acc_print", nullable = false)
    private boolean accPrint;

    //todo какой дефолт?
    /**
     * Признак печати перечня сертификатов к поставляемым товаров – 0 – печать не требуется, 1- печать требуется
     */
    @Column(name = "sertif_print", nullable = false)
    private boolean sertifPrint;

    //todo какой дефолт?
    /**
     * Признак разрешения оформления возвратных документов
     */
    @Column(name = "vzr_doc", nullable = false)
    private boolean vzrDoc;

    //todo какой дефолт?
    /**
     * Признак обязательного наличия договора – 0 – договор не обязателен, 1 – договор обязателен
     */
    @Column(name = "dogovor", nullable = false)
    private boolean dogovor;

    /**
     * Атрибуты договора в одной строке – Покупателю для информации
     */
    @Column(name = "dogovor_alt")
    private String dogovorAlt;

    //todo какой дефолт?
    /**
     * Признак отправки документов по ЭДО – 0 – отправка документов по эдо не требуется, 1 – требуется отправка документов по эдо
     */
    @Column(name = "edo", nullable = false)
    private String edo;

    /**
     * Дата начала оформления документов по эдо
     */
    @Column(name = "edo_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date edoDate;

    /**
     * Примечание с нашей стороны
     */
    @Column(name = "prim")
    private String prim;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
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

    public String getEdo() {
        return edo;
    }

    public void setEdo(String edo) {
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

}
