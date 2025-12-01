package com.lakomka.models.misc;

import com.lakomka.dto.DiscountXmlDto;
import com.lakomka.models.person.BasePrice;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.Product;
import com.lakomka.util.DateFormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Table
@Entity
@Setter
@Getter
@Slf4j
public class Discount {

    /**
     * id общий для офиса и магазина
     */
    @Id
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "j_person_id", nullable = false)
    private JPerson jPerson;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Признак скидки/наценки – 0 скидка, 1- наценка
     */
    @Column(name = "bit_discount")
    private boolean bitDiscount = false;

    /**
     * Величина сктдки/наценки. В процентах
     */
    @Column(name = "rest_time", length = 12, nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * Базовая цена - PriceOpt1 или PriceOpt2 или PriceNal или PriceKons
     * Если Discount=0 это означает применение BasePrice иной отличной от BasePrice которая в карточке Покупателя
     */
    @Column(name = "base_price", columnDefinition = "char(4)", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BasePrice basePrice;

    /**
     * Признак запрета отгрузки – 0 разрешен, 1 - запрещен
     */
    @Column(name = "bit_stop")
    private boolean bitStop = false;

    /**
     * Дата начала действия правила
     */
    @Column(name = "discount_start", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStart;

    /**
     * Дата окончания действия правила
     */
    @Column(name = "discount_end", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnd;

    public BigDecimal applyDiscount() {

        BigDecimal priced;
        if (BigDecimal.ZERO.compareTo(this.discount) == 0) {
            // базовая цена берется не из профиля пользователя, а из этой скидки.
            // ну и сама скидка не применяется, потому что отсутствует
            priced = this.product.priceSelector(this.basePrice);
            log.debug("Применена базовая цена {}:{} из скидки id={}, пользователя {} к продукту id={}, art={}",
                    this.basePrice.name(),
                    priced.setScale(2, RoundingMode.HALF_UP),
                    this.getId(),
                    this.jPerson.getBasePerson().getLogin(),
                    this.product.getId(),
                    this.product.getArticle());
        } else {
            // базовая цена берется из профиля пользователя
            BasePrice userBasePrice = this.jPerson.getBasePrice();
            // берем цену независимо от активности скидки по датам
            priced = this.product.priceSelector(userBasePrice);
            if (isDiscountActive()) {
                // даты действия скидки активны
                BigDecimal applied = applyPercentage(priced, this.discount);
                if (this.bitDiscount) {
                    // наценка
                    priced = priced.add(applied);
                } else {
                    // скидка
                    priced = priced.add(applied.negate());
                }
                log.debug("Применена скидка {}{} {}:{} id={}, пользователя {} к продукту id={}, art={}",
                        this.bitDiscount ? "+" : "-",
                        this.discount,
                        userBasePrice.name(),
                        priced.setScale(2, RoundingMode.HALF_UP),
                        this.getId(),
                        this.jPerson.getBasePerson().getLogin(),
                        this.product.getId(),
                        this.product.getArticle());
            } else {
                log.debug("Применена базовая {}:{} цена пользователя {} к продукту id={}, art={}",
                        userBasePrice.name(),
                        priced.setScale(2, RoundingMode.HALF_UP),
                        this.jPerson.getBasePerson().getLogin(),
                        this.product.getId(),
                        this.product.getArticle());
            }
        }

        return priced.setScale(2, RoundingMode.HALF_UP);

    }

    /**
     * Applies a percentage to a BigDecimal value
     *
     * @param value      the original BigDecimal value
     * @param percentage the percentage to apply (e.g., 20 for 20%)
     * @return the result after applying the percentage
     */
    public static BigDecimal applyPercentage(BigDecimal value, BigDecimal percentage) {
        // Multiply the value by the percentage
        BigDecimal result = value.multiply(percentage);
        // Divide by 100 to get the actual percentage value
        return result.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    /**
     * Checks if the current date is within the discount period
     *
     * @return true if current date is between dateStart and dateEnd, false otherwise
     */
    public boolean isDiscountActive() {
        LocalDate now = LocalDate.now();
        LocalDate start = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return !now.isBefore(start) && !now.isAfter(end);
    }

    public DiscountXmlDto toDiscountXmlDto() {
        DiscountXmlDto dto = new DiscountXmlDto();
        dto.setBitDiscount(this.bitDiscount);
        dto.setBasePrice(this.basePrice.name());
        dto.setDiscount(this.discount);
        dto.setBitStop(this.bitStop);
        dto.setDateStart(DateFormatUtil.formatDate(this.dateStart, DateFormatUtil.SHORT_DATE_FORMATTER));
        dto.setDateEnd(DateFormatUtil.formatDate(this.dateEnd, DateFormatUtil.SHORT_DATE_FORMATTER));
        dto.setJPersonShopId(this.jPerson.getId());
        dto.setJPersonOfficeId(this.jPerson.getOfficeId());
        dto.setId(this.id);
        dto.setProductId(this.product.getId());
        return dto;
    }
}
