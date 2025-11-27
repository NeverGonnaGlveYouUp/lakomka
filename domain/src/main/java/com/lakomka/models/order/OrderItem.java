package com.lakomka.models.order;

import com.lakomka.dto.OrderItemDto;
import com.lakomka.dto.OrderItemXmlDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Table
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    /**
     * Уникальный индекс заказа
     */
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    /**
     * Уникальный индекс покупателя
     */
    @ManyToOne
    @JoinColumn(name = "base_person_id", referencedColumnName = "id")
    private BasePerson basePerson;

    /**
     * Уникальный индекс товара
     */
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    /**
     * Единиц измерения количества товара
     */
    @Column(name = "unit", length = 20, nullable = false)
    private String unit;

    /**
     * Норма упаковки товара
     */
    @Column(name = "packag", length = 50, nullable = false)
    private String packag;

    /**
     * Бит отгрузки нормами упаковок – 0 – Quantity это штуки или килограммы, 1 – Quantity это упаковки
     */
    @Column(name = "bit_packag", nullable = false)
    private boolean bitPackag = false;

    /**
     * Количество товара
     */
    @Column(name = "quantity", precision = 12, nullable = false)
    private Integer quantity;

    /**
     * Цена товара
     */
    @Column(name = "price", nullable = false, length = 10, scale = 2)
    private BigDecimal price;

    /**
     * Вес товара или упаковки в зависимости от состояния bitPackag
     */
    @Column(name = "weight_packag", precision = 12, nullable = false)
    private Double weightPackag;

    public OrderItemXmlDto toOrderItemsXmlDto() {
        return new OrderItemXmlDto(
                this.product.getId().toString(),
                this.product.getArticle(),
                this.unit,
                this.packag,
                this.price.toPlainString(),
                this.quantity,
                this.weightPackag,
                this.bitPackag
        );
    }

    public OrderItemDto toOrderItemDto(){
        return new OrderItemDto(
                this.product.getName(),
                this.unit,
                this.packag,
                this.bitPackag,
                this.quantity,
                this.price,
                this.weightPackag
        );
    }

}
