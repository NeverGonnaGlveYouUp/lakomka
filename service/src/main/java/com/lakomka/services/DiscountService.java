package com.lakomka.services;

import com.lakomka.dto.CartItemDto;
import com.lakomka.dto.ProductDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.BasePrice;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.misc.DiscountRepository;
import com.lakomka.repository.person.JPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountService {

    public final static BasePrice DEFAULT_BASE_PRICE = BasePrice.KONS;

    private final DiscountRepository discountRepository;
    private final JPersonRepository jPersonRepository;

    public record Discounts(BasePrice basePrice, Set<Discount> discounts) {
    }

    public Discounts getDiscounts(BasePerson user) {
        Optional<JPerson> optionalJPerson = Optional.ofNullable(user)
                .flatMap(u -> jPersonRepository.findById(u.getId()));
        Discounts discounts = new Discounts(
                optionalJPerson.map(JPerson::getBasePrice).orElse(DEFAULT_BASE_PRICE),
                getDiscountSet(optionalJPerson)
        );
        log.debug("getDiscounts: selected basePrice={}, num. of discounts={}",
                discounts.basePrice().name(), discounts.discounts().size()
        );
        return discounts;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Set<Discount> getDiscountSet(@NonNull Optional<JPerson> optionalJPerson) {
        return optionalJPerson
                .map(discountRepository::findAllByJPerson)
                .orElse(Set.of());
    }

    public BasePrice getBasePrice(BasePerson user) {
        return Optional.ofNullable(user)
                .flatMap(u -> jPersonRepository.findById(u.getId()))
                .map(JPerson::getBasePrice)
                .orElse(DEFAULT_BASE_PRICE);
    }

    public ProductDto apply(Product product, Discounts discounts) {
        ProductDto productDto = product.toProductDto(discounts.basePrice());

        discounts.discounts().stream()
                .filter(Discount::isDiscountActive)
                .filter(discount -> Objects.equals(discount.getProduct().getId(), product.getId()))
                // среди активных скидок для этого пользователя и продукта, если их несколько, выбираем свежее по startDate
                .min(Comparator.comparing(Discount::getDateStart))
                .map(Discount::applyDiscount)
                .ifPresent(productDto::setPrice);

        return productDto;
    }

    public CartItemDto apply(PersonCartItem cartItem) {
        Product product = cartItem.getProduct();
        Integer quantity = cartItem.getQuantity();
        return new CartItemDto(
                product.getId(),
                product.getName(),
                applyToPrice(cartItem)
                        .multiply(BigDecimal.valueOf(quantity))
                        .setScale(2, RoundingMode.HALF_UP)
                        .toPlainString(),
                quantity,
                product.getWeight() * quantity
        );
    }

    public BigDecimal applyToPrice(PersonCartItem cartItem) {
        Product product = cartItem.getProduct();
        Integer quantity = cartItem.getQuantity();
        BasePerson user = cartItem.getBasePerson();
        Discounts discounts = getDiscounts(user);
        Optional<Discount> optionalDiscount = discounts.discounts()
                .stream()
                .filter(Discount::isDiscountActive)
                .filter(discount -> Objects.equals(discount.getProduct().getId(), product.getId()))
                // среди активных скидок для этого пользователя и продукта, если их несколько, выбираем свежее по startDate
                .min(Comparator.comparing(Discount::getDateStart));
        return optionalDiscount
                .map(Discount::applyDiscount)
                .orElse(product.priceSelector(discounts.basePrice()));
    }

}
