package com.lakomka.services.xml.exports;

import com.lakomka.dto.OrderItemXmlDto;
import com.lakomka.dto.OrderXmlDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.order.OrderItem;
import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.order.OrderItemRepository;
import com.lakomka.repository.order.OrderRepository;
import com.lakomka.services.S3Service;
import com.lakomka.util.DateFormatUtil;
import com.lakomka.utils.FileUtil;
import com.lakomka.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.lakomka.configs.SystemUserDatabaseInitializer.SYSTEM_USER;
import static com.lakomka.util.DateFormatUtil.WITH_SECONDS_FORMATTER;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderExport {

    private final S3Service s3Service;
    private final SessionUtil sessionUtil;
    private final FileUtil fileUtil;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public String safeExportXml(Order order, List<OrderItem> newItems) {
        try {
            // Create XML content
            String xmlContent = createXmlContent(order, newItems);

            // Upload to S3
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = DateFormatUtil.formatDate(now, WITH_SECONDS_FORMATTER);
            String fileName = "orders/order_" + order.getId() + "_" + formattedDateTime + ".xml";
            MultipartFile file = fileUtil.createMultipartFile(fileName, xmlContent.getBytes());
            s3Service.uploadFile(file, false);

            log.info("Successfully Export XML: order #{} to S3 as {} for {}",
                    order.getId(),
                    fileName,
                    SYSTEM_USER.equals(order.getBasePerson().getLogin()) ? order.getGuest() : order.getBasePerson().getLogin()
            );

            return fileName;
        } catch (Throwable t) {
            log.error("Error Export XML: order #{} for {}. {}",
                    order.getId(),
                    SYSTEM_USER.equals(order.getBasePerson().getLogin()) ? order.getGuest() : order.getBasePerson().getLogin(),
                    t.getMessage(),
                    t);
            return null;
        }
    }

    public boolean safeExportXml(BasePerson user, HttpServletRequest request, long orderNum) {
        Optional<Order> order;
        if (user == null) {
            String currentSessionId = sessionUtil.getCurrentSessionId(request);
            order = orderRepository.findByIdAndGuest(orderNum, currentSessionId);
        } else {
            order = orderRepository.findByIdAndBasePerson(orderNum, user);
        }

        if (order.isEmpty()) {
            return false;
        }

        List<OrderItem> items = order.map(orderItemRepository::findAllByOrder)
                .stream()
                .flatMap(Collection::stream)
                .toList();

        String exportedXmlFile = safeExportXml(order.get(), items);
        if (isNull(exportedXmlFile)) {
            return false;
        }

        order.get().setExportedFileName(exportedXmlFile);
        orderRepository.save(order.get());

        return true;
    }

    private String createXmlContent(Order order, List<OrderItem> newItems) throws JAXBException {

        // Create root element with order and items
        OrderXmlWrapper wrapper = new OrderXmlWrapper();
        wrapper.setOrder(order.toOrderXmlDTO());
        wrapper.setOrderItems(newItems.stream().map(OrderItem::toOrderItemsXmlDto).toList());

        // Marshal to XML
        JAXBContext context = JAXBContext.newInstance(OrderXmlWrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(wrapper, writer);

        return writer.toString();

    }

    // Wrapper class to hold order and items in XML structure
    @Setter
    @XmlRootElement(name = "order_export")
    private static class OrderXmlWrapper {

        private OrderXmlDto order;
        private List<OrderItemXmlDto> orderItems;

        @XmlElement(name = "order")
        public OrderXmlDto getOrder() {
            return order;
        }

        @XmlElement(name = "order_items")
        public List<OrderItemXmlDto> getOrderItems() {
            return orderItems;
        }

    }

}
