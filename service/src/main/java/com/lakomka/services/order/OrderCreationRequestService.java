package com.lakomka.services.order;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.JPerson;
import com.lakomka.repository.person.JPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class OrderCreationRequestService {

    private final JPersonRepository jPersonRepository;

    public OrderCreationRequest fill(BasePerson user, OrderCreationRequest request, boolean allowOrdersDetailsEdit) {

        // If both parameters are null (Guest without order details), throw RuntimeException
        if (request == null && user == null) {
            throw new RuntimeException("Both OrderCreationRequest and User cannot be null");
        }

        // If request is null, create a new one
        if (request == null) {
            request = new OrderCreationRequest();
        }

        // If user is not null and find JPerson, fill null fields of request
        if (nonNull(user)) {

            Optional<JPerson> optionalJPerson = jPersonRepository.findById(user.getId());

            if (optionalJPerson.isPresent()) {

                JPerson jPerson = optionalJPerson.get();

                // bitAccPrint and bitSertifPrint leave them as is

                if (request.getDateDelivery() == null && !allowOrdersDetailsEdit) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    request.setDateDelivery(calendar.getTime());
                }

                if (request.getAddressDelivery() == null && !allowOrdersDetailsEdit) {
                    request.setAddressDelivery(jPerson.getAddressDelivery());
                }

                if (request.getEmail() == null && !allowOrdersDetailsEdit) {
                    request.setEmail(jPerson.getEmail());
                }

                if (request.getTelephone() == null && !allowOrdersDetailsEdit) {
                    request.setTelephone(jPerson.getPhone());
                }

                if (request.getContact() == null && !allowOrdersDetailsEdit) {
                    request.setContact(jPerson.getContact());
                }

                // Prim field can be changed by user
                if (request.getPrim() == null && allowOrdersDetailsEdit) {
                    request.setPrim(jPerson.getPrim());
                }

            }
        }

        // Validate that at least telephone field is filled
        // If all fields are empty - throw exception
        if (request.getTelephone() == null || request.getTelephone().trim().isEmpty()) {
            // Check if all relevant fields are empty
            boolean allFieldsEmpty =
                    (request.getAddressDelivery() == null || request.getAddressDelivery().trim().isEmpty())
                            && (request.getEmail() == null || request.getEmail().trim().isEmpty())
                            && (request.getContact() == null || request.getContact().trim().isEmpty())
                            && (request.getPrim() == null || request.getPrim().trim().isEmpty());

            if (allFieldsEmpty) {
                throw new RuntimeException("At least telephone field must be filled");
            }

        }

        return request;

    }

}