package com.lakomka.services.xml.exports;

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
import java.util.Optional;
import java.util.function.Function;

import static com.lakomka.util.DateFormatUtil.WITH_SECONDS_FORMATTER;

/**
 * Component with core logic for export to xml
 * @param <I> - item type (entity)
 * @param <H> - head type (entity)
 * @param <ID> - item (dto)
 * @param <HD> - head (dto)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractXmlExport<I, H, ID, HD> {

    private final S3Service s3Service;
    private final FileUtil fileUtil;

    /**
     * Core logic for export to xml
     * @param head - single objech for head of xml
     * @param list - list oblects for items part of xml
     * @param folder - folder on S3 for save file
     * @param prefix - prefix of exported file
     * @param headMapper - функция преобразования entity в tdo для заголовка
     * @param dtoMapper - функция преобразования entity в tdo для элементов
     * @param fileNamePartGenerator - how to generate xml filename
     * @return
     */
    protected String safeExportXml(H head,
                                   List<I> list,
                                   String folder,
                                   String prefix,
                                   Function<H, HD> headMapper,
                                   Function<I, ID> dtoMapper,
                                   Function<List<I>, String> fileNamePartGenerator
    ) {
        try {
            // Create XML content
            String xmlContent = createXmlContent(head, list, headMapper, dtoMapper);

            // Upload to S3
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = DateFormatUtil.formatDate(now, WITH_SECONDS_FORMATTER);
            String fileName = folder + "/" + prefix + "_" + fileNamePartGenerator.apply(list) + "_" + formattedDateTime + ".xml";
            MultipartFile file = fileUtil.createMultipartFile(fileName, xmlContent.getBytes());
            s3Service.uploadFile(file, false);

            log.info("Successfully Export XML: {} to S3 as {}", folder, fileName);

            return fileName;
        } catch (Throwable t) {
            log.error("Error Export XML: {}. {}", folder, t.getMessage(), t);
            return null;
        }
    }

    /**
     *
     * @param head - объект заголовок экспорта
     * @param list - лист элементов экспорта
     * @param headMapper - функция преобразования entity в tdo для заголовка
     * @param itemMapper - функция преобразования entity в tdo для элементов
     * @return - xml
     * @throws JAXBException -
     */
    private String createXmlContent(
            H head,
            List<I> list,
            Function<H, HD> headMapper,
            Function<I, ID> itemMapper
    ) throws JAXBException {

        XmlWrapper<HD, ID> wrapper = new XmlWrapper<>();

        wrapper.setHead(Optional.ofNullable(head)
                .map(headMapper)
                .orElse(null));
        wrapper.setItems(list.stream()
                .map(itemMapper)
                .toList());

        // Marshal to XML
        JAXBContext context = JAXBContext.newInstance(XmlWrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(wrapper, writer);

        return writer.toString();
    }

    /**
     * Wrapper class to hold items in XML structure
     * @param <CH> - type for single head object (aka order)
     * @param <CI> - type for list of items (aka orderItem)
     */
    @Setter
    @XmlRootElement(name = "export")
    protected static class XmlWrapper<CH, CI> {

        private CH head;
        private List<CI> items;

        @XmlElement(name = "head")
        public CH getHead() {
            return head;
        }

        @XmlElement(name = "items")
        public List<CI> getItems() {
            return items;
        }

    }
}
