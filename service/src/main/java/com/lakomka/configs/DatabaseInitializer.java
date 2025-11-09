package com.lakomka.configs;

import com.lakomka.models.misc.Discount;
import com.lakomka.models.misc.Route;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.BasePrice;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.Product;
import com.lakomka.repository.misc.DiscountRepository;
import com.lakomka.repository.misc.RouteRepository;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;

@Component
@DependsOn(value = "systemUserDatabaseInitializer")
@Profile({"dev", "jpa-dev"})
@Order(2)
public class DatabaseInitializer implements CommandLineRunner {

    private final BasePersonRepository basePersonRepository;
    private final ProductRepository productRepository;
    private final JPersonRepository jPersonRepository;
    private final PasswordEncoder passwordEncoder;
    private final RouteRepository routeRepository;
    private final DiscountRepository discountRepository;

    private final String contentText = "Integer iaculis sem sit amet dui rutrum, et porttitor ligula tempus. Donec congue quam sed finibus aliquam. Morbi volutpat nunc ut libero lobortis rutrum eu sed arcu. Quisque pretium, arcu vitae placerat viverra, tellus velit facilisis eros, quis posuere.";
    private final String descText = "Donec sit amet diam risus. Sed vulputate malesuada nibh, sed blandit nisl porttitor et. Mauris ut ante purus. Pellentesque dapibus mauris non odio ultrices, ac volutpat purus ornare. Sed euismod risus sit amet metus lobortis, eget viverra nibh porta ante.";

    @Autowired
    public DatabaseInitializer(BasePersonRepository basePersonRepository,
                               ProductRepository productRepository,
                               JPersonRepository jPersonRepository,
                               PasswordEncoder passwordEncoder,
                               RouteRepository routeRepository,
                               DiscountRepository discountRepository
    ) {
        this.basePersonRepository = basePersonRepository;
        this.productRepository = productRepository;
        this.jPersonRepository = jPersonRepository;
        this.passwordEncoder = passwordEncoder;
        this.routeRepository = routeRepository;
        this.discountRepository = discountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

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
        jPerson.setBasePrice(BasePrice.KONS);
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
        person.setJPerson(jPerson);
        basePersonRepository.save(person);

        BasePerson person2 = new BasePerson();
        person2.setLogin("integerinteger");
        person2.setPassword(passwordEncoder.encode("integerinteger"));

        JPerson jPerson2 = new JPerson();
        jPerson2.setName("My Admin User 2");
        jPerson2.setNameFull("name full 2");
        jPerson2.setBasePerson(person2);
        jPerson2.setAddress("address 2");
        jPerson2.setContact("contact 2");
        jPerson2.setDay(0);
        jPerson2.setAccPrint(false);
        jPerson2.setDogovor(false);
        jPerson2.setEdo(false);
        jPerson2.setINN("INNINNINNIN2");
        jPerson2.setBasePrice(BasePrice.OPT1);
        jPerson2.setKPP("kppkppkp2");
        jPerson2.setOGRN("OGRNOGRNOGRNOG2");
        jPerson2.setEdoDate(new Date());
        jPerson2.setAddressDelivery("asdasd 2");
        jPerson2.setEmail("email2");
        jPerson2.setPhone("phone2");
        jPerson2.setMapDelivery("mapDelivery2");
        jPerson2.setPost("post2");
        jPerson2.setRest(new BigDecimal("0"));
        jPerson2.setRestTime(new BigDecimal("0"));
        jPerson2.setRoute(route);

        jPersonRepository.save(jPerson2);
        person2.setJPerson(jPerson2);
        basePersonRepository.save(person2);

        for (int i = 0; i < 50; i++) {

            Product product1 = new Product();
            product1.setName("Laptop" + i);
            product1.setArticle("LP00" + i);
            product1.setWorker("worker 1-2");
            product1.setPriceKons(new BigDecimal(random.nextInt(300, 10000)));
            product1.setPriceNal(product1.getPriceKons().add(BigDecimal.valueOf(100)));
            product1.setPriceOpt1(product1.getPriceKons().add(BigDecimal.valueOf(-100)));
            product1.setPriceOpt2(product1.getPriceKons().add(BigDecimal.valueOf(-200)));
            product1.setWeight(random.nextInt(1000));
            product1.setCountry("country 1-2");
            product1.setProductGroup("Electronics");
            product1.setDescription(descText);
            product1.setContent(contentText);
            product1.setUnit("unit");

            Product product2 = new Product();
            product2.setName("Smartphone" + i);
            product2.setArticle("SP00" + i);
            product2.setPriceKons(new BigDecimal(random.nextInt(300, 10000)));
            product2.setPriceNal(product2.getPriceKons().add(BigDecimal.valueOf(100)));
            product2.setPriceOpt1(product2.getPriceKons().add(BigDecimal.valueOf(-100)));
            product2.setPriceOpt2(product2.getPriceKons().add(BigDecimal.valueOf(-200)));
            product2.setWeight(random.nextInt(1000));
            product2.setWorker("worker 1-2");
            product2.setCountry("country 1-2");
            product2.setProductGroup("Mobile Phones");
            product2.setDescription(descText);
            product2.setContent(contentText);
            product2.setUnit("unit");

            Product product3 = new Product();
            product3.setName("Tablet" + i);
            product3.setArticle("TB00" + i);
            product3.setWeight(random.nextInt(1000));
            product3.setWorker("worker 3-4");
            product3.setCountry("country 3-4");
            product3.setPriceKons(new BigDecimal(random.nextInt(300, 10000)));
            product3.setPriceNal(product3.getPriceKons().add(BigDecimal.valueOf(100)));
            product3.setPriceOpt1(product3.getPriceKons().add(BigDecimal.valueOf(-100)));
            product3.setPriceOpt2(product3.getPriceKons().add(BigDecimal.valueOf(-200)));
            product3.setProductGroup("Tablet");
            product3.setDescription(descText);
            product3.setContent(contentText);
            product3.setUnit("unit");

            Product product4 = new Product();
            product4.setName("Headphones" + i);
            product4.setArticle("HP00" + i);
            product4.setWeight(random.nextInt(1000));
            product4.setWorker("worker 3-4");
            product4.setCountry("country 3-4");
            product4.setPriceKons(new BigDecimal(random.nextInt(300, 10000)));
            product4.setPriceNal(product4.getPriceKons().add(BigDecimal.valueOf(100)));
            product4.setPriceOpt1(product4.getPriceKons().add(BigDecimal.valueOf(-100)));
            product4.setPriceOpt2(product4.getPriceKons().add(BigDecimal.valueOf(-200)));
            product4.setProductGroup("Audio Accessories");
            product4.setDescription(descText);
            product4.setContent(contentText);
            product4.setUnit("unit");

            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);
            productRepository.save(product4);

            List<Product> products = List.of(product1, product2, product3, product4);
            List<JPerson> jPersons = List.of(jPerson2, jPerson);
            List<BasePrice> basePrices = List.of(BasePrice.OPT1, BasePrice.KONS, BasePrice.NAL, BasePrice.OPT2);
            List<Boolean> upDown = List.of(true, false);

            // актуальная скидка
            Discount discount1 = new Discount();
            discount1.setDiscount(new BigDecimal(random.nextInt(5, 70)));
            discount1.setDateStart(from(now().minusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount1.setDateEnd(from(now().plusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount1.setBitDiscount(upDown.get(random.nextInt(upDown.size())));
            discount1.setBitStop(false);
            discount1.setBasePrice(basePrices.get(random.nextInt(basePrices.size())));
            discount1.setJPerson(jPersons.get(random.nextInt(jPersons.size())));
            discount1.setProduct(products.get(random.nextInt(products.size())));

            // просроченная скидка
            Discount discount2 = new Discount();
            discount2.setDiscount(new BigDecimal(random.nextInt(5, 70)));
            discount2.setDateStart(from(now().minusDays(100L).atStartOfDay(systemDefault()).toInstant()));
            discount2.setDateEnd(from(now().minusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount2.setBitDiscount(upDown.get(random.nextInt(upDown.size())));
            discount2.setBitStop(false);
            discount2.setBasePrice(basePrices.get(random.nextInt(basePrices.size())));
            discount2.setJPerson(jPersons.get(random.nextInt(jPersons.size())));
            discount2.setProduct(products.get(random.nextInt(products.size())));

            // нулевая скидка
            Discount discount3 = new Discount();
            discount3.setDiscount(BigDecimal.ZERO);
            discount3.setDateStart(from(now().minusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount3.setDateEnd(from(now().plusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount3.setBitDiscount(upDown.get(random.nextInt(upDown.size())));
            discount3.setBitStop(false);
            discount3.setBasePrice(basePrices.get(random.nextInt(basePrices.size())));
            discount3.setJPerson(jPersons.get(random.nextInt(jPersons.size())));
            discount3.setProduct(products.get(random.nextInt(products.size())));

            // актуальная накидка
            Discount discount4 = new Discount();
            discount4.setDiscount(new BigDecimal(random.nextInt(5, 70)));
            discount4.setDateStart(from(now().minusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount4.setDateEnd(from(now().plusDays(10L).atStartOfDay(systemDefault()).toInstant()));
            discount4.setBitDiscount(upDown.get(random.nextInt(upDown.size())));
            discount4.setBitStop(false);
            discount4.setBasePrice(basePrices.get(random.nextInt(basePrices.size())));
            discount4.setJPerson(jPersons.get(random.nextInt(jPersons.size())));
            discount4.setProduct(products.get(random.nextInt(products.size())));

            discountRepository.save(discount1);
            discountRepository.save(discount2);
            discountRepository.save(discount3);
            discountRepository.save(discount4);

        }

    }
}