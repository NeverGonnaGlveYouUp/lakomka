package com.lakomka.controller;

import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.misc.DiscountRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.services.xml.exports.DiscountExport;
import com.lakomka.services.xml.exports.JPersonExport;
import com.lakomka.services.xml.exports.OrderExport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ExportController {

    private final JPersonRepository jPersonRepository;
    private final DiscountRepository discountRepository;
    private final JPersonExport jPersonExport;
    private final DiscountExport discountExport;
    private final OrderExport orderExport;

    /**
     * Export all JPersons to Xml file on S3 storage
     *
     * @param user - user
     * @return - exported file name if success, null if fail
     */
    @GetMapping("/jpersons/export-all-to-s3")
    @Transactional
    public ResponseEntity<?> jpersonExportXml(
            @AuthenticationPrincipal BasePerson user
    ) {
        if (isNull(user)) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(jPersonExport.safeExportXml(jPersonRepository.findAll()));

    }

    /**
     * Export all Discounts to Xml file on S3 storage
     *
     * @param user - user
     * @return - exported file name if success, null if fail
     */
    @GetMapping("/discount/export-all-to-s3")
    @Transactional
    public ResponseEntity<?> discountExportXml(
            @AuthenticationPrincipal BasePerson user
    ) {
        if (isNull(user)) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(discountExport.safeExportXml(discountRepository.findAll()));

    }

    /**
     * Export order to Xml file on S3 storage
     *
     * @param user    - user
     * @param request - HttpServletRequest
     * @return - true if success
     */
    @GetMapping("/orders/export-one-to-s3")
    public ResponseEntity<?> orderExportXml(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request,
            @RequestParam long orderId
    ) {
        if (isNull(user)) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(orderExport.safeExportXml(user, request, orderId));

    }
}
