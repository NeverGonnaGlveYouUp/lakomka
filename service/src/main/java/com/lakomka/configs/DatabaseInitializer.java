package com.lakomka.configs;

import com.lakomka.models.misc.Route;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.Product;
import com.lakomka.repository.misc.RouteRepository;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

@Component
@Profile({"dev", "jpa-dev"})
public class DatabaseInitializer implements CommandLineRunner {

    private final BasePersonRepository basePersonRepository;
    private final ProductRepository productRepository;
    private final JPersonRepository jPersonRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RouteRepository routeRepository;

    private final String contentText = "Integer iaculis sem sit amet dui rutrum, et porttitor ligula tempus. Donec congue quam sed finibus aliquam. Morbi volutpat nunc ut libero lobortis rutrum eu sed arcu. Quisque pretium, arcu vitae placerat viverra, tellus velit facilisis eros, quis posuere.";
    private final String descText = "Donec sit amet diam risus. Sed vulputate malesuada nibh, sed blandit nisl porttitor et. Mauris ut ante purus. Pellentesque dapibus mauris non odio ultrices, ac volutpat purus ornare. Sed euismod risus sit amet metus lobortis, eget viverra nibh porta ante.";

    @Autowired
    public DatabaseInitializer(BasePersonRepository basePersonRepository,
                               ProductRepository productRepository,
                               JPersonRepository jPersonRepository,
                               PasswordEncoder passwordEncoder) {
        this.basePersonRepository = basePersonRepository;
        this.productRepository = productRepository;
        this.jPersonRepository = jPersonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        for (int i = 0; i < 50; i++) {

            Product product1 = new Product();
            product1.setName("Laptop" + i);
            product1.setArticle("LP00" + i);
            product1.setWorker("worker 1-2");
            product1.setPriceKons(new BigDecimal(random.nextInt(10000)));
            product1.setWeight(random.nextInt(1000));
            product1.setCountry("country 1-2");
            product1.setGroup("Electronics");
            product1.setDescription(descText);
            product1.setContent(contentText);

            Product product2 = new Product();
            product2.setName("Smartphone" + i);
            product2.setArticle("SP00" + i);
            product2.setPriceKons(new BigDecimal(random.nextInt(10000)));
            product2.setWeight(random.nextInt(1000));
            product2.setWorker("worker 1-2");
            product2.setCountry("country 1-2");
            product2.setGroup("Mobile Phones");
            product2.setDescription(descText);
            product2.setContent(contentText);

            Product product3 = new Product();
            product3.setName("Tablet" + i);
            product3.setArticle("TB00" + i);
            product3.setWeight(random.nextInt(1000));
            product3.setWorker("worker 3-4");
            product3.setCountry("country 3-4");
            product3.setPriceKons(new BigDecimal(random.nextInt(10000)));
            product3.setGroup("Tablet");
            product3.setDescription(descText);
            product3.setContent(contentText);

            Product product4 = new Product();
            product4.setName("Headphones" + i);
            product4.setArticle("HP00" + i);
            product4.setWeight(random.nextInt(1000));
            product4.setWorker("worker 3-4");
            product4.setCountry("country 3-4");
            product4.setPriceKons(new BigDecimal(random.nextInt(10000)));
            product4.setGroup("Audio Accessories");
            product4.setDescription(descText);
            product4.setContent(contentText);

            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);
            productRepository.save(product4);
        }

        Route route = new Route();
        route.setNameRoute("route");
        route.setRouteDays("1234567");
        routeRepository.save(route);

        BasePerson person = new BasePerson();
        person.setLogin("stringstring");
        person.setPassword(passwordEncoder.encode("stringstring"));

        JPerson jPerson = new JPerson();
        jPerson.setName("My Admin User");
        jPerson.setNameFull("name full");
        jPerson.setBasePerson(person);
        jPerson.setAddress("address");
        jPerson.setContact("contact");
        jPerson.setDay(0);
        jPerson.setAccPrint(false);
        jPerson.setDogovor(false);
        jPerson.setEdo(false);
        jPerson.setINN("INNINNINNINN");
        jPerson.setBasePrice("base");
        jPerson.setKPP("kppkppkpp");
        jPerson.setOGRN("OGRNOGRNOGRNOGR");
        jPerson.setEdoDate(new Date());
        jPerson.setAddressDelivery("asdasd");
        jPerson.setEmail("email");
        jPerson.setPhone("phone");
        jPerson.setMapDelivery("mapDelivery");
        jPerson.setPost("post");
        jPerson.setRest(new BigDecimal("0"));
        jPerson.setRestTime(new BigDecimal("0"));
        jPerson.setRoute(route);

        jPersonRepository.save(jPerson);
        person.setjPerson(jPerson);
        basePersonRepository.save(person);


    }
}