package com.lakomka.services.xml.exports;

import com.lakomka.dto.DiscountXmlDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.services.S3Service;
import com.lakomka.util.DateFormatUtil;
import com.lakomka.utils.FileUtil;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import static com.lakomka.util.DateFormatUtil.WITH_SECONDS_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountExport {

    private final S3Service s3Service;
    private final FileUtil fileUtil;

    public String safeExportXml(List<Discount> discountList) {
        try {
            // Create XML content
            String xmlContent = createXmlContent(discountList);

            // Upload to S3
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = DateFormatUtil.formatDate(now, WITH_SECONDS_FORMATTER);
            String fileName = "discounts/ds_" + partOfFileName(discountList) + "_" + formattedDateTime + ".xml";
            MultipartFile file = fileUtil.createMultipartFile(fileName, xmlContent.getBytes());
            s3Service.uploadFile(file, false);

            log.info("Successfully Export XML: discounts to S3 as {}", fileName);

            return fileName;
        } catch (Throwable t) {
            log.error("Error Export XML: discounts. {}", t.getMessage(), t);
            return null;
        }
    }

    /**
     * примеры:
     * <p>
     * если один
     * ds_inn[9876543210]_2025-11-22-12-44-05.xml
     * <p>
     * если список
     * ds_list[3]_2025-11-22-12-44-35.xml
     *
     * @param discountList - список контрагентов
     * @return - имя файла
     */
    private String partOfFileName(List<Discount> discountList) {
        if (discountList.size() == 1) {
            return discountList.stream().findFirst()
                    .map(d -> d.getJPerson().getINN())
                    .map(inn -> "inn[" + inn + "]")
                    .orElse("inn[]");
        } else if (discountList.isEmpty()) {
            return "";
        } else {
            return "list[" + discountList.size() + "]";
        }
    }

    private String createXmlContent(List<Discount> discountList) throws JAXBException {

        // Create root element with order and items
        DiscountXmlWrapper wrapper = new DiscountXmlWrapper();
        wrapper.setDiscounts(discountList.stream()
                .map(Discount::toDiscountXmlDto)
                .toList());

        // Marshal to XML
        JAXBContext context = JAXBContext.newInstance(DiscountXmlWrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(wrapper, writer);

        return writer.toString();

    }

    // Wrapper class to hold  in XML structure
    @Setter
    @XmlRootElement(name = "discounts_export")
    private static class DiscountXmlWrapper {

        private List<DiscountXmlDto> discounts;

        @XmlElement(name = "discount_item")
        public List<DiscountXmlDto> getDiscounts() {
            return discounts;
        }

    }

}
