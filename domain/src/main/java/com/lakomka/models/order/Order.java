package com.lakomka.models.order;

import com.lakomka.dto.OrderDto;
import com.lakomka.dto.OrderXmlDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.util.DateFormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static com.lakomka.util.DateFormatUtil.DEFAULT_FORMATTER;
import static com.lakomka.util.DateFormatUtil.SHORT_DATE_FORMATTER;

@Setter
@Getter
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
     * Покупатель. Для анонима\гостя проставится системный пользователь
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
    private Double sumWeight;

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

    /**
     * Экспортирован в файл с таким именем
     * при нескольких экспортах одного заказа остается последнее имя
     */
    @Column(name = "exported_file")
    private String exportedFileName;

    /**
     * Для анонимного заказа - номер сессии
     */
    @Column(name = "guest", length = 40)
    private String guest = "";

    public OrderDto toOrderDTO() {
        return new OrderDto(
                this.id,
                this.dateDelivery.compareTo(new Date()) > 0 ? "доставлен" : "не доставлен",
                DateFormatUtil.formatDate(
                        this.datePay,
                        SHORT_DATE_FORMATTER),
                DateFormatUtil.formatDate(
                        this.dateDelivery,
                        SHORT_DATE_FORMATTER),
                this.sumOrder
        );
    }

    public OrderXmlDto toOrderXmlDTO() {
        OrderDto im = this.toOrderDTO();
        return new OrderXmlDto(
                im.id().toString(),

                this.basePerson.getLogin(), // SystemUser for guest order

                this.contact,
                this.telephone,
                this.email,
                this.prim,
                this.adressDelivery,

                im.datePay(),
                im.dateDelivery(),
                DateFormatUtil.formatDate(Date.from(this.dateTimeOrder), DEFAULT_FORMATTER),

                im.sumOrder(),
                this.sumWeight,

                this.bitAccPrint,
                this.bitSertifPrint
        );

    }
}
