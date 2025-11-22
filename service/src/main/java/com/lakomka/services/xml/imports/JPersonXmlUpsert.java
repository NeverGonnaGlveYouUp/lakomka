package com.lakomka.services.xml.imports;

import com.lakomka.dto.JpersonXmlDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.BasePrice;
import com.lakomka.models.person.JPerson;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.util.DateFormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class JPersonXmlUpsert {

    private final JPersonRepository jPersonRepository;
    private final PasswordEncoder passwordEncoder;

    public Stat upsert(List<JpersonXmlDto> jpersonXmlDtoList) {
        int created = 0;
        int updated = 0;
        int unchanged = 0;

        for (JpersonXmlDto dto : jpersonXmlDtoList) {
            try {
                if (dto.getShopId() == null) {
                    // Create new JPerson
                    JPerson newJPerson = createNewJPerson(dto);
                    jPersonRepository.save(newJPerson);
                    created++;
                } else {
                    // Update existing JPerson
                    Optional<JPerson> existingJPersonOpt = jPersonRepository.findById(dto.getShopId());
                    if (existingJPersonOpt.isPresent()) {
                        JPerson existingJPerson = existingJPersonOpt.get();
                        if (updateJPersonIfNeeded(existingJPerson, dto)) {
                            updated++;
                            jPersonRepository.save(existingJPerson);
                        } else {
                            unchanged++;
                        }
                    } else {
                        // If shopId exists but entity doesn't exist, create new one (id will changed!)
                        JPerson newJPerson = createNewJPerson(dto);
                        jPersonRepository.save(newJPerson);
                        created++;
                    }
                }
            } catch (Exception e) {
                log.error("Error processing JPersonXmlDTO with shopId: {}, officeId: {}. Counting as unchanged.",
                        dto.getShopId(), dto.getOfficeId(), e);
                unchanged++;
            }
        }

        return new Stat(jpersonXmlDtoList.size(), updated, created, unchanged);
    }

    private JPerson createNewJPerson(JpersonXmlDto dto) {
        JPerson jPerson = new JPerson();

        // Set basic fields

        //jPerson.setOfficeId(dto.getOfficeId()) // todo
        jPerson.setOGRN(dto.getOGRN());
        jPerson.setINN(dto.getINN());
        jPerson.setKPP(dto.getKPP());
        jPerson.setName(dto.getName());
        jPerson.setNameFull(dto.getNameFull());
        jPerson.setAddress(dto.getAddress());
        jPerson.setPhone(dto.getPhone());
        jPerson.setEmail(dto.getEmail());
        jPerson.setContact(dto.getContact());
        jPerson.setPost(dto.getPost());
        jPerson.setAddressDelivery(dto.getAddressDelivery());
        jPerson.setBasePrice(BasePrice.valueOf(dto.getBasePrice()));
        jPerson.setMapDelivery(dto.getMapDelivery());
        jPerson.setPrim(dto.getPrim());
        // Set optional fields
        jPerson.setDay(dto.getShippingDelayDays());
        jPerson.setRest(dto.getRest() != null ? dto.getRest() : BigDecimal.ZERO);
        jPerson.setRestTime(dto.getRestTime());
        jPerson.setPayVid(dto.isPayVid());
        jPerson.setAccPrint(dto.isAccPrint());
        jPerson.setSertifPrint(dto.isSertifPrint());
        jPerson.setVzrDoc(dto.isVzrDoc());
        jPerson.setDogovor(dto.isDogovor());
        jPerson.setDogovorAlt(dto.getDogovorAlt());
        jPerson.setEdo(dto.isEdo());
        jPerson.setEdoDate(dto.getEdoDate() != null ? DateFormatUtil.parseDate(dto.getEdoDate()) : null);
        jPerson.setGlobalDiscount(dto.getGlobalDiscount() != null ? dto.getGlobalDiscount() : 0);
        jPerson.setDpAgreement(dto.isDpAgreement());
        jPerson.setDiscounts(Set.of()); // todo

        // Create BasePerson for new JPerson
        // Login = INN, Password = OGRN todo
        BasePerson basePerson = new BasePerson();
        basePerson.setLogin(dto.getINN());
        basePerson.setPassword(passwordEncoder.encode(dto.getOGRN()));
        basePerson.setJPerson(jPerson);
        jPerson.setBasePerson(basePerson);

        return jPerson;
    }

    // Update only fields that have changed
    private boolean updateJPersonIfNeeded(JPerson existing, JpersonXmlDto dto) {

        List<String> changedFields = new ArrayList<>();

        // INN/OGRN is unchangeable ?
//        if (dto.getOGRN() != null && !dto.getOGRN().equals(existing.getOGRN())) {
//            changedFields.add(String.format("OGRN: '%s' -> '%s'", existing.getOGRN(), dto.getOGRN()));
//            existing.setOGRN(dto.getOGRN());
//        }
//        if (dto.getINN() != null && !dto.getINN().equals(existing.getINN())) {
//            changedFields.add(String.format("INN: '%s' -> '%s'", existing.getINN(), dto.getINN()));
//            existing.setINN(dto.getINN());
//        }

        if (dto.getKPP() != null && !dto.getKPP().equals(existing.getKPP())) {
            changedFields.add(String.format("KPP: '%s' -> '%s'", existing.getKPP(), dto.getKPP()));
            existing.setKPP(dto.getKPP());
        }
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            changedFields.add(String.format("Name: '%s' -> '%s'", existing.getName(), dto.getName()));
            existing.setName(dto.getName());
        }
        if (dto.getNameFull() != null && !dto.getNameFull().equals(existing.getNameFull())) {
            changedFields.add(String.format("NameFull: '%s' -> '%s'", existing.getNameFull(), dto.getNameFull()));
            existing.setNameFull(dto.getNameFull());
        }
        if (dto.getAddress() != null && !dto.getAddress().equals(existing.getAddress())) {
            changedFields.add(String.format("Address: '%s' -> '%s'", existing.getAddress(), dto.getAddress()));
            existing.setAddress(dto.getAddress());
        }
        if (dto.getPhone() != null && !dto.getPhone().equals(existing.getPhone())) {
            changedFields.add(String.format("Phone: '%s' -> '%s'", existing.getPhone(), dto.getPhone()));
            existing.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            changedFields.add(String.format("Email: '%s' -> '%s'", existing.getEmail(), dto.getEmail()));
            existing.setEmail(dto.getEmail());
        }
        if (dto.getContact() != null && !dto.getContact().equals(existing.getContact())) {
            changedFields.add(String.format("Contact: '%s' -> '%s'", existing.getContact(), dto.getContact()));
            existing.setContact(dto.getContact());
        }
        if (dto.getPost() != null && !dto.getPost().equals(existing.getPost())) {
            changedFields.add(String.format("Post: '%s' -> '%s'", existing.getPost(), dto.getPost()));
            existing.setPost(dto.getPost());
        }
        if (dto.getAddressDelivery() != null && !dto.getAddressDelivery().equals(existing.getAddressDelivery())) {
            changedFields.add(String.format("AddressDelivery: '%s' -> '%s'", existing.getAddressDelivery(), dto.getAddressDelivery()));
            existing.setAddressDelivery(dto.getAddressDelivery());
        }
        if (dto.getMapDelivery() != null && !dto.getMapDelivery().equals(existing.getMapDelivery())) {
            changedFields.add(String.format("MapDelivery: '%s' -> '%s'", existing.getMapDelivery(), dto.getMapDelivery()));
            existing.setMapDelivery(dto.getMapDelivery());
        }
        if (dto.getBasePrice() != null && !dto.getBasePrice().equals(existing.getBasePrice().name())) {
            changedFields.add(String.format("BasePrice: '%s' -> '%s'", existing.getBasePrice().name(), dto.getBasePrice()));
            existing.setBasePrice(BasePrice.valueOf(dto.getBasePrice()));
        }
        if (dto.getShippingDelayDays() != null && !dto.getShippingDelayDays().equals(existing.getDay())) {
            changedFields.add(String.format("ShippingDelayDays: '%s' -> '%s'", existing.getDay(), dto.getShippingDelayDays()));
            existing.setDay(dto.getShippingDelayDays());
        }
        if (dto.getRest() != null && dto.getRest().compareTo(existing.getRest()) != 0) {
            changedFields.add(String.format("Rest: '%s' -> '%s'", existing.getRest(), dto.getRest()));
            existing.setRest(dto.getRest());
        }
        if (dto.getRestTime() != null && dto.getRestTime().compareTo(existing.getRestTime()) != 0) {
            changedFields.add(String.format("RestTime: '%s' -> '%s'", existing.getRestTime(), dto.getRestTime()));
            existing.setRestTime(dto.getRestTime());
        }
        if (dto.isPayVid() != existing.isPayVid()) {
            changedFields.add(String.format("PayVid: '%s' -> '%s'", existing.isPayVid(), dto.isPayVid()));
            existing.setPayVid(dto.isPayVid());
        }
        if (dto.isAccPrint() != existing.isAccPrint()) {
            changedFields.add(String.format("AccPrint: '%s' -> '%s'", existing.isAccPrint(), dto.isAccPrint()));
            existing.setAccPrint(dto.isAccPrint());
        }
        if (dto.isSertifPrint() != existing.isSertifPrint()) {
            changedFields.add(String.format("SertifPrint: '%s' -> '%s'", existing.isSertifPrint(), dto.isSertifPrint()));
            existing.setSertifPrint(dto.isSertifPrint());
        }
        if (dto.isVzrDoc() != existing.isVzrDoc()) {
            changedFields.add(String.format("VzrDoc: '%s' -> '%s'", existing.isVzrDoc(), dto.isVzrDoc()));
            existing.setVzrDoc(dto.isVzrDoc());
        }
        if (dto.isDogovor() != existing.isDogovor()) {
            changedFields.add(String.format("Dogovor: '%s' -> '%s'", existing.isDogovor(), dto.isDogovor()));
            existing.setDogovor(dto.isDogovor());
        }
        if (dto.getDogovorAlt() != null && !dto.getDogovorAlt().equals(existing.getDogovorAlt())) {
            changedFields.add(String.format("DogovorAlt: '%s' -> '%s'", existing.getDogovorAlt(), dto.getDogovorAlt()));
            existing.setDogovorAlt(dto.getDogovorAlt());
        }
        if (dto.isEdo() != existing.isEdo()) {
            changedFields.add(String.format("Edo: '%s' -> '%s'", existing.isEdo(), dto.isEdo()));
            existing.setEdo(dto.isEdo());
        }
        String existEdoDate = existing.getEdoDate() == null
                ? null
                : DateFormatUtil.formatDate(existing.getEdoDate(), DateFormatUtil.SHORT_DATE_FORMATTER);
        if (dto.getEdoDate() != null && !dto.getEdoDate().equals(existEdoDate)) {
            Date newEdoDate = DateFormatUtil.parseDate(dto.getEdoDate());
            String formatDate = DateFormatUtil.formatDate(newEdoDate, DateFormatUtil.SHORT_DATE_FORMATTER);
            changedFields.add(String.format("EdoDate: '%s' -> '%s'", existEdoDate, formatDate));
            existing.setEdoDate(newEdoDate);
        }
        if (dto.getPrim() != null && !dto.getPrim().equals(existing.getPrim())) {
            changedFields.add(String.format("Prim: '%s' -> '%s'", existing.getPrim(), dto.getPrim()));
            existing.setPrim(dto.getPrim());
        }
        if (dto.getGlobalDiscount() != null && !dto.getGlobalDiscount().equals(existing.getGlobalDiscount())) {
            changedFields.add(String.format("GlobalDiscount: '%s' -> '%s'", existing.getGlobalDiscount(), dto.getGlobalDiscount()));
            existing.setGlobalDiscount(dto.getGlobalDiscount());
        }
        if (dto.isDpAgreement() != existing.isDpAgreement()) {
            changedFields.add(String.format("DpAgreement: '%s' -> '%s'", existing.isDpAgreement(), dto.isDpAgreement()));
            existing.setDpAgreement(dto.isDpAgreement());
        }

        if (!changedFields.isEmpty()) {
            log.info("JPerson with INN/OGRN '{}/{}' has changed fields: {}",
                    existing.getINN(), existing.getOGRN(), String.join(", ", changedFields));
            return true;
        }

        return false;
    }
}