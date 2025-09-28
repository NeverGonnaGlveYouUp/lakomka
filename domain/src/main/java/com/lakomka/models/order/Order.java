package com.lakomka.models.order;

import com.lakomka.models.person.BasePerson;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Table(name = "orders")
@Entity
public class Order {

    /**
     * Номер заказа и ПК
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    /**
     * Покупатель
     */
    @ManyToOne
    @JoinColumn(name = "base_person", nullable = false)
    private BasePerson basePerson;

    /**
     * Дата-Время заказа
     */
    @CreationTimestamp
    @Column(name = "date_time_order", nullable = false)
    private Instant dateTimeOrder;

    /**
     * Общая сумма заказа
     */
    @Column(name = "sum_order", nullable = false, precision = 10, scale = 2)
    private BigDecimal sumOrder;

    /**
     * Общий вес заказа
     */
    @Column(name = "sum_weight", nullable = false, precision = 12)
    private Integer sumWeight;

    /**
     * Адрес доставки
     */
    @Column(name = "adress_delivery", nullable = false)
    private String adressDelivery;

    /**
     * Желаемая дата доставки (больше, чем дата оформления заказа) Если не указана – то DateDelivery=Дата оформления заказа + 1 день (иначе – на следующий день)
     */
    @Column(name = "date_delivery", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateDelivery;

    /**
     * Признак печати предварительного счета (битовое поле) – 0- печать счета не требуется, 1 – печать счета требуется
     */
    @Column(name = "bit_acc_print", nullable = false)
    private boolean bitAccPrint = false;

    /**
     * Признак печати перечня сертификатов к поставляемым товаров – 0 – печать не требуется, 1- печать требуется
     */
    @Column(name = "bit_sertif_print", nullable = false)
    private boolean bitSertifPrint = false;

    /**
     * Дата оплаты – если наличные, то DatePay= DateDelivery, если б/нал и в карточке стоит отсрочка, то DatePay= DateDelivery+Количество дней отсрочки
     */
    @Column(name = "date_pay", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date datePay;

    /**
     * Электронная почта
     */
    @Column(name = "email", nullable = false, length = 50)
    private String email = "";

    /**
     * Телефон
     */
    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone = "";

    /**
     * Контактное лицо
     */
    @Column(name = "contact", nullable = false)
    private String contact = "";

    /**
     * Примечание к заказу
     */
    @Column(name = "prim", nullable = false)
    private String prim = "";

    public String getPrim() {
        return prim;
    }

    public void setPrim(String prim) {
        this.prim = prim;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDatePay() {
        return datePay;
    }

    public void setDatePay(Date datePay) {
        this.datePay = datePay;
    }

    public boolean isBitSertifPrint() {
        return bitSertifPrint;
    }

    public void setBitSertifPrint(boolean bitSertifPrint) {
        this.bitSertifPrint = bitSertifPrint;
    }

    public boolean isBitAccPrint() {
        return bitAccPrint;
    }

    public void setBitAccPrint(boolean bitAccPrint) {
        this.bitAccPrint = bitAccPrint;
    }

    public Date getDateDelivery() {
        return dateDelivery;
    }

    public void setDateDelivery(Date dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public String getAdressDelivery() {
        return adressDelivery;
    }

    public void setAdressDelivery(String adressDelivery) {
        this.adressDelivery = adressDelivery;
    }

    public Integer getSumWeight() {
        return sumWeight;
    }

    public void setSumWeight(Integer sumWeight) {
        this.sumWeight = sumWeight;
    }

    public BigDecimal getSumOrder() {
        return sumOrder;
    }

    public void setSumOrder(BigDecimal sumOrder) {
        this.sumOrder = sumOrder;
    }

    public Instant getDateTimeOrder() {
        return dateTimeOrder;
    }

    public void setDateTimeOrder(Instant dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }

    public BasePerson getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(BasePerson basePerson) {
        this.basePerson = basePerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
