package com.lakomka.services;

import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProductXmlUpsert {

    private final ProductRepository productRepository;

    public Stat upsert(List<Product> validProducts) {

        // Собираем все коды для поиска
        List<String> codes = validProducts.stream()
                .map(Product::getArticle)
                .collect(Collectors.toList());

        // Находим существующие продукты одним запросом
        List<Product> existingProducts = productRepository.findByArticleInSafe(codes);
        Map<String, Product> existingProductsMap = existingProducts.stream()
                .collect(Collectors.toMap(Product::getArticle, Function.identity()));

        List<Product> productsToSave = new ArrayList<>();

        int updatedProducts = 0;
        int newProducts = 0;
        int notTouchedProducts = 0;
        for (Product newProduct : validProducts) {
            Product existingProduct = existingProductsMap.get(newProduct.getArticle());

            if (existingProduct == null) {
                // Новый продукт
                productsToSave.add(newProduct);
                newProducts++;
            } else {
                // Существующий продукт - проверяем изменения
                if (productsEqual(existingProduct, newProduct)) {
                    // нет изменений
                    notTouchedProducts++;
                } else {
                    // обновление полей
                    updateProductFields(existingProduct, newProduct);
                    productsToSave.add(existingProduct);
                    updatedProducts++;
                }
            }
        }

        // Сохраняем
        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
        }

        return new Stat(validProducts.size(), updatedProducts, newProducts, notTouchedProducts);
    }

    private boolean productsEqual(Product existing, Product newProduct) {
        return compareAndLogFields(existing, newProduct, false);
    }

    private void updateProductFields(Product existing, Product newProduct) {
        compareAndLogFields(existing, newProduct, true);

        // Обновляем только изменившиеся поля
        if (!Objects.equals(existing.getName(), newProduct.getName())) {
            existing.setName(newProduct.getName());
        }
        if (compareBigDecimal(existing.getPriceOpt1(), newProduct.getPriceOpt1()) != 0) {
            existing.setPriceOpt1(newProduct.getPriceOpt1());
        }
        if (compareBigDecimal(existing.getPriceOpt2(), newProduct.getPriceOpt2()) != 0) {
            existing.setPriceOpt2(newProduct.getPriceOpt2());
        }
        if (compareBigDecimal(existing.getPriceNal(), newProduct.getPriceNal()) != 0) {
            existing.setPriceNal(newProduct.getPriceNal());
        }
        if (compareBigDecimal(existing.getPriceKons(), newProduct.getPriceKons()) != 0) {
            existing.setPriceKons(newProduct.getPriceKons());
        }
        if (!Objects.equals(existing.getUnit(), newProduct.getUnit())) {
            existing.setUnit(newProduct.getUnit());
        }
        if (!Objects.equals(existing.getUnitVid(), newProduct.getUnitVid())) {
            existing.setUnitVid(newProduct.getUnitVid());
        }
        if (!Objects.equals(existing.getPackag(), newProduct.getPackag())) {
            existing.setPackag(newProduct.getPackag());
        }
        if (!Objects.equals(existing.getProductGroup(), newProduct.getProductGroup())) {
            existing.setProductGroup(newProduct.getProductGroup());
        }

    }

    private boolean compareAndLogFields(Product existing, Product newProduct, boolean performUpdate) {
        boolean allFieldsEqual = true;
        List<String> changedFields = new ArrayList<>();

        // Проверяем каждое поле отдельно
        if (!Objects.equals(existing.getName(), newProduct.getName())) {
            allFieldsEqual = false;
            changedFields.add(String.format("name: '%s' -> '%s'", existing.getName(), newProduct.getName()));
        }

        if (!Objects.equals(existing.getPriceOpt1(), newProduct.getPriceOpt1())) {
            allFieldsEqual = false;
            changedFields.add(String.format("price_Опт1: %s -> %s",
                    formatBigDecimal(existing.getPriceOpt1()), formatBigDecimal(newProduct.getPriceOpt1())));
        }

        if (!Objects.equals(existing.getPriceOpt2(), newProduct.getPriceOpt2())) {
            allFieldsEqual = false;
            changedFields.add(String.format("price_Опт2: %s -> %s",
                    formatBigDecimal(existing.getPriceOpt2()), formatBigDecimal(newProduct.getPriceOpt2())));
        }

        if (!Objects.equals(existing.getPriceNal(), newProduct.getPriceNal())) {
            allFieldsEqual = false;
            changedFields.add(String.format("price_Нал: %s -> %s",
                    formatBigDecimal(existing.getPriceNal()), formatBigDecimal(newProduct.getPriceNal())));
        }

        if (!Objects.equals(existing.getPriceKons(), newProduct.getPriceKons())) {
            allFieldsEqual = false;
            changedFields.add(String.format("price_Конс: %s -> %s",
                    formatBigDecimal(existing.getPriceKons()), formatBigDecimal(newProduct.getPriceKons())));
        }

        if (!Objects.equals(existing.getUnit(), newProduct.getUnit())) {
            allFieldsEqual = false;
            changedFields.add(String.format("unit: '%s' -> '%s'",
                    existing.getUnit(), newProduct.getUnit()));
        }

        if (!Objects.equals(existing.getUnitVid(), newProduct.getUnitVid())) {
            allFieldsEqual = false;
            changedFields.add(String.format("measure_unit: '%s' -> '%s'",
                    existing.getUnitVid(), newProduct.getUnitVid()));
        }

        if (!Objects.equals(existing.getPackag(), newProduct.getPackag())) {
            allFieldsEqual = false;
            changedFields.add(String.format("packaging: '%s' -> '%s'",
                    existing.getPackag(), newProduct.getPackag()));
        }

        if (!Objects.equals(existing.getProductGroup(), newProduct.getProductGroup())) {
            allFieldsEqual = false;
            changedFields.add(String.format("group: '%s' -> '%s'",
                    existing.getProductGroup(), newProduct.getProductGroup()));
        }

        // Логируем изменения, если они есть и если это режим обновления
        if (!changedFields.isEmpty() && performUpdate) {
            log.info("Product with code '{}' has changed fields: {}",
                    existing.getArticle(), String.join(", ", changedFields));
        }

        return allFieldsEqual;
    }

    // Метод для сравнения BigDecimal значений
    private int compareBigDecimal(BigDecimal bd1, BigDecimal bd2) {
        if (bd1 == null && bd2 == null) return 0;
        if (bd1 == null) return -1;
        if (bd2 == null) return 1;
        return bd1.compareTo(bd2);
    }

    // Метод для форматирования BigDecimal для логов
    private String formatBigDecimal(BigDecimal value) {
        if (value == null) return "null";
        return value.toPlainString();
    }

    public record Stat(int total, int updated, int newRecords, int notTouched) {
    }
}
