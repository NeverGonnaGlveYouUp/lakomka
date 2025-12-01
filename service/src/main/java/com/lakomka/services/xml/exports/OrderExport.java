package com.lakomka.services.xml.exports;

import com.lakomka.dto.OrderItemXmlDto;
import com.lakomka.dto.OrderXmlDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.order.OrderItem;
import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.order.OrderItemRepository;
import com.lakomka.repository.order.OrderRepository;
import com.lakomka.services.S3Service;
import com.lakomka.utils.FileUtil;
import com.lakomka.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class OrderExport extends AbstractXmlExport<OrderItem, Order, OrderItemXmlDto, OrderXmlDto> {

    private final SessionUtil sessionUtil;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderExport(S3Service s3Service,
                       FileUtil fileUtil,
                       SessionUtil sessionUtil,
                       OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository) {
        super(s3Service, fileUtil);
        this.sessionUtil = sessionUtil;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public String safeExportXml(Order order, List<OrderItem> newItems) {
        return super.safeExportXml(
                order,
                newItems,
                "orders",
                "or",
                Order::toOrderXmlDTO,
                OrderItem::toOrderItemsXmlDto,
                this::partOfFileName
        );
    }

    /**
     * Из листа элементов заказа выделяет номер заказа для использования в имени файла.
     * Предполагается что у всех элементов один и тот же заказ.
     *
     * @param orderItems - элементы заказа
     * @return - строка для имени xml файла
     */
    private String partOfFileName(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getOrder)
                .map(Order::getId)
                .distinct()
                .map(id -> Long.toString(id))
                .findAny()
                .orElse("unknown");
    }

    /**
     * Экспорт заказа для вызова из контроллера
     *
     * @param user     BasePerson
     * @param request  HttpServletRequest
     * @param orderNum id заказа
     * @return true если экспорт удался
     */
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

        String exportedXmlFile = super.safeExportXml(
                order.get(),
                items,
                "orders",
                "order",
                Order::toOrderXmlDTO,
                OrderItem::toOrderItemsXmlDto,
                this::partOfFileName
        );


        if (isNull(exportedXmlFile)) {
            return false;
        }

        order.get().setExportedFileName(exportedXmlFile);
        orderRepository.save(order.get());

        return true;
    }

}
