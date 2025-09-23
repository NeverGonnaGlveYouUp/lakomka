package com.lakomka.models.misc;

import jakarta.persistence.*;

@Table
@Entity
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    /**
     * Наименование маршрута
     */
    @Column(name = "name_route", length = 20, nullable = false)
    private String nameRoute;

    /**
     * Дни развоза – 7 цифр по количеству дней в неделе. Цифра это день понедельник-воскресенье. Ноль – нет развоза.
     * Например – 1234567 – развоз каждый день по маршруту, 1350000 – развоз понедельник, среда, пятница
     */
    @Column(name = "route_days", precision = 7, nullable = false)
    private Integer routeDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameRoute() {
        return nameRoute;
    }

    public void setNameRoute(String nameRoute) {
        this.nameRoute = nameRoute;
    }

    public Integer getRouteDays() {
        return routeDays;
    }

    public void setRouteDays(Integer routeDays) {
        this.routeDays = routeDays;
    }
}
