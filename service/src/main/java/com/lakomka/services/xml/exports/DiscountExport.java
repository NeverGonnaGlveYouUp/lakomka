package com.lakomka.services.xml.exports;

import com.lakomka.dto.DiscountXmlDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.services.S3Service;
import com.lakomka.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class DiscountExport extends AbstractXmlExport<Discount, Object, DiscountXmlDto, Object> {

    @Autowired
    public DiscountExport(S3Service s3Service, FileUtil fileUtil) {
        super(s3Service, fileUtil);
    }

    public String safeExportXml(List<Discount> discountList) {
        return super.safeExportXml(
                null,
                discountList,
                "discounts",
                "ds",
                Function.identity(),
                Discount::toDiscountXmlDto,
                this::partOfFileName);
    }

    /**
     * примеры:
     * <p>
     * если один контрагент
     * ds_inn[9876543210]_2025-11-30-12-12-12.xml
     * <p>
     * если список контрагентов
     * ds_list[3]_2025-11-30-12-12-12.xml
     *
     * @param discountList - список скидок
     * @return - имя файла
     */
    private String partOfFileName(List<Discount> discountList) {
        if (discountList.size() == 1) {
            return discountList.stream().findFirst()
                    .map(discount -> discount.getJPerson().getINN())
                    .map(inn -> "inn[" + inn + "]")
                    .orElse("inn[]");
        } else if (discountList.isEmpty()) {
            return "";
        } else {
            return "list[" + discountList.size() + "]";
        }
    }

}