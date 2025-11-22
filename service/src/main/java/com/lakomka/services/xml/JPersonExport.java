package com.lakomka.services.xml;

import com.lakomka.dto.JpersonXmlDto;
import com.lakomka.models.person.JPerson;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.services.S3Service;
import com.lakomka.util.DateFormatUtil;
import com.lakomka.utils.FileUtil;
import com.lakomka.utils.SessionUtil;
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
public class JPersonExport {

    private final S3Service s3Service;
    private final FileUtil fileUtil;

    public String safeExportXml(List<JPerson> jPersonList) {
        try {
            // Create XML content
            String xmlContent = createXmlContent(jPersonList);

            // Upload to S3
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = DateFormatUtil.formatDate(now, WITH_SECONDS_FORMATTER);
            String fileName = "jpersons/jp_" + partOfFileName(jPersonList) + "_" + formattedDateTime + ".xml";
            MultipartFile file = fileUtil.createMultipartFile(fileName, xmlContent.getBytes());
            s3Service.uploadFile(file, false);

            log.info("Successfully Export XML: jpersons to S3 as {}", fileName);

            return fileName;
        } catch (Throwable t) {
            log.error("Error Export XML: jpersons. {}", t.getMessage(), t);
            return null;
        }
    }

    /**
     * примеры:
     *
     * если один контрагент
     * jp_inn[9876543210]_2025-11-22-12-44-05.xml
     *
     * если список контрагентов
     * jp_list[3]_2025-11-22-12-44-35.xml
     *
     * @param jPersonList - список контрагентов
     * @return - имя файла
     */
    private String partOfFileName(List<JPerson> jPersonList) {
        if (jPersonList.size() == 1) {
            return jPersonList.stream().findFirst()
                    .map(JPerson::getINN)
                    .map(inn -> "inn[" + inn + "]")
                    .orElse("inn[]");
        } else if (jPersonList.isEmpty()) {
            return "";
        } else {
            return "list[" + jPersonList.size() + "]";
        }
    }

    private String createXmlContent(List<JPerson> jPersonList) throws JAXBException {

        // Create root element with order and items
        JpersonXmlWrapper wrapper = new JpersonXmlWrapper();
        wrapper.setJpersons(jPersonList.stream()
                .map(JPerson::toJpersonXmlDto)
                .toList());

        // Marshal to XML
        JAXBContext context = JAXBContext.newInstance(JpersonXmlWrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(wrapper, writer);

        return writer.toString();

    }

    // Wrapper class to hold jpersons in XML structure
    @Setter
    @XmlRootElement(name = "jpersons_export")
    private static class JpersonXmlWrapper {

        private List<JpersonXmlDto> jpersons;

        @XmlElement(name = "jperson")
        public List<JpersonXmlDto> getJpersons() {
            return jpersons;
        }

    }

}
