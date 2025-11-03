package com.lakomka.services;

import com.lakomka.dto.ProductDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.BasePrice;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.Product;
import com.lakomka.repository.misc.DiscountRepository;
import com.lakomka.repository.person.JPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountService {

    private final static BasePrice DEFAULT_BASE_PRICE = BasePrice.KONS;

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
                // среди активных скидок для пользователя\продукта, выбираем одну любую (т.к. опции объединения нет?)
                .findFirst()
                .map(Discount::applyDiscount)
                .ifPresent(productDto::setPrice);

        return productDto;
    }

}
