package com.lakomka.services.xml.imports;

import com.lakomka.dto.DiscountXmlDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.person.BasePrice;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.Product;
import com.lakomka.repository.misc.DiscountRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.util.DateFormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DiscountXmlUpsert {

    private final DiscountRepository discountRepository;
    private final JPersonRepository jPersonRepository;
    private final ProductRepository productRepository;

    public Stat upsert(List<DiscountXmlDto> discountXmlDtos) {
        int created = 0;
        int updated = 0;
        int unchanged = 0;
        int deleted = 0;

        for (DiscountXmlDto dto : discountXmlDtos) {
            try {

                Optional<JPerson> jPersonOptional = jPersonRepository.findById(dto.getJPersonShopId());
                if (jPersonOptional.isEmpty()) {
                    log.warn("For discount Id: {} not found JPerson Id: {}. Counting as unchanged.",
                            dto.getId(),
                            dto.getJPersonShopId());
                    unchanged++;
                    continue;
                }

                Optional<Product> optionalProduct = productRepository.findById(dto.getProductId());
                if (optionalProduct.isEmpty()) {
                    log.warn("For discount Id: {} not found Product Id: {}. Counting as unchanged.",
                            dto.getId(),
                            dto.getProductId());
                    unchanged++;
                    continue;
                }

                Optional<Discount> optionalDiscount = discountRepository.findById(dto.getId());
                if (optionalDiscount.isEmpty()) {
                    // Create new Discount
                    Discount newDiscount = createNewDiscount(dto, jPersonOptional.get(), optionalProduct.get());
                    discountRepository.save(newDiscount);
                    created++;
                    log.info("Discount Id/jpId/prId '{}/{}/{}' created",
                            newDiscount.getId(),
                            newDiscount.getJPerson().getId(),
                            newDiscount.getProduct().getId()
                    );
                } else {
                    // Update existing Discount
                    Discount existingDiscount = optionalDiscount.get();
                    if (dto.isBitDelete()) {
                        discountRepository.delete(existingDiscount);
                        deleted++;
                        log.info("Discount Id/jpId/prId '{}/{}/{}' deleted",
                                existingDiscount.getId(),
                                existingDiscount.getJPerson().getId(),
                                existingDiscount.getProduct().getId()
                        );
                    } else if (updateDiscountIfNeeded(existingDiscount, dto, jPersonOptional.get(), optionalProduct.get())) {
                        updated++;
                        discountRepository.save(existingDiscount);
                    } else {
                        unchanged++;
                    }
                }
            } catch (Exception e) {
                log.error("Error processing DiscountXmlDto with Id: {}, JPersonOfficeId: {}. JPersonShopId: {}" +
                                "Counting as unchanged.",
                        dto.getId(),
                        dto.getJPersonOfficeId(),
                        dto.getJPersonShopId(),
                        e);
                unchanged++;
            }
        }

        return new Stat(discountXmlDtos.size(), updated, created, unchanged, deleted);
    }

    private Discount createNewDiscount(DiscountXmlDto dto, JPerson jPerson, Product product) {
        Discount discount = new Discount();

        discount.setId(dto.getId());
        discount.setJPerson(jPerson);
        discount.setProduct(product);
        discount.setDiscount(dto.getDiscount().setScale(2, RoundingMode.HALF_UP));
        discount.setBasePrice(BasePrice.valueOf(dto.getBasePrice()));
        discount.setBitDiscount(dto.isBitDiscount());
        discount.setBitStop(dto.isBitStop());
        discount.setDateEnd(DateFormatUtil.parseDate(dto.getDateEnd()));
        discount.setDateStart(DateFormatUtil.parseDate(dto.getDateStart()));

        return discount;
    }

    // Update only fields that have changed
    private boolean updateDiscountIfNeeded(Discount existing, DiscountXmlDto dto, JPerson jPerson, Product product) {

        List<String> changedFields = new ArrayList<>();

        if (dto.getJPersonOfficeId() != null && !dto.getJPersonOfficeId().equals(jPerson.getOfficeId())) {
            changedFields.add(String.format("JPersonOfficeId detected as changed: '%s' -> '%s', but ignored",
                    jPerson.getOfficeId(), dto.getJPersonOfficeId()));
            // todo выпилить JPersonOffice из DiscountXmlDto?
        }
        if (dto.getDiscount() != null && dto.getDiscount().compareTo(existing.getDiscount()) != 0) {
            changedFields.add(String.format("Discount: '%s' -> '%s'", existing.getDiscount(), dto.getDiscount()));
            existing.setDiscount(dto.getDiscount());
        }
        if (dto.getProductId() != null && !dto.getProductId().equals(existing.getProduct().getId())) {
            changedFields.add(String.format("Product: '%s' -> '%s'", existing.getProduct().getId(), dto.getProductId()));
            existing.setProduct(product);
        }
        String existingDateStart = existing.getDateStart() == null
                ? null
                : DateFormatUtil.formatDate(existing.getDateStart(), DateFormatUtil.SHORT_DATE_FORMATTER);
        if (dto.getDateStart() != null && !dto.getDateStart().equals(existingDateStart)) {
            Date newDateStart = DateFormatUtil.parseDate(dto.getDateStart());
            String formatDate = DateFormatUtil.formatDate(newDateStart, DateFormatUtil.SHORT_DATE_FORMATTER);
            changedFields.add(String.format("DateStart: '%s' -> '%s'", existingDateStart, formatDate));
            existing.setDateStart(newDateStart);
        }
        String existingDateEnd = existing.getDateEnd() == null
                ? null
                : DateFormatUtil.formatDate(existing.getDateEnd(), DateFormatUtil.SHORT_DATE_FORMATTER);
        if (dto.getDateEnd() != null && !dto.getDateEnd().equals(existingDateEnd)) {
            Date newDateEnd = DateFormatUtil.parseDate(dto.getDateEnd());
            String formatDate = DateFormatUtil.formatDate(newDateEnd, DateFormatUtil.SHORT_DATE_FORMATTER);
            changedFields.add(String.format("DateEnd: '%s' -> '%s'", existingDateEnd, formatDate));
            existing.setDateEnd(newDateEnd);
        }
        if (dto.getBasePrice() != null && !dto.getBasePrice().equals(existing.getBasePrice().name())) {
            changedFields.add(String.format("BasePrice: '%s' -> '%s'", existing.getBasePrice().name(), dto.getBasePrice()));
            existing.setBasePrice(BasePrice.valueOf(dto.getBasePrice()));
        }
        if (dto.isBitDiscount() != existing.isBitDiscount()) {
            changedFields.add(String.format("BitDiscount: '%s' -> '%s'", existing.isBitDiscount(), dto.isBitDiscount()));
            existing.setBitDiscount(dto.isBitDiscount());
        }
        if (dto.isBitStop() != existing.isBitStop()) {
            changedFields.add(String.format("BitStop: '%s' -> '%s'", existing.isBitStop(), dto.isBitStop()));
            existing.setBitStop(dto.isBitStop());
        }

        if (!changedFields.isEmpty()) {
            log.info("Discount Id/jpId/prId '{}/{}/{}' has changed fields: {}",
                    existing.getId(),
                    existing.getJPerson().getId(),
                    existing.getProduct().getId(),
                    String.join(", ", changedFields));
            return true;
        }

        return false;
    }
}