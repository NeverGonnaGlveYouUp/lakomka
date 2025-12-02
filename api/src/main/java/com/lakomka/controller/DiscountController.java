package com.lakomka.controller;

import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.misc.DiscountRepository;
import com.lakomka.services.xml.exports.DiscountExport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@Slf4j
public class DiscountController {

    private final DiscountRepository discountRepository;
    private final DiscountExport discountExport;

    /**
     * Export all Discounts to Xml file on S3 storage
     *
     * @param user - user
     * @return - exported file name if success, null if fail
     */
    @GetMapping("/export-to-s3")
    @Transactional
    public ResponseEntity<?> discountExportXml(
            @AuthenticationPrincipal BasePerson user
    ) {
        if (isNull(user)) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(discountExport.safeExportXml(discountRepository.findAll()));

    }

}
