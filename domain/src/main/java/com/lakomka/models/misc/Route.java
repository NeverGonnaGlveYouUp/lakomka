package com.lakomka.models.misc;

import com.lakomka.models.person.JPerson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Table
@Entity
@Getter
@Setter
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
    private String routeDays;

    @OneToMany(mappedBy="route")
    private Set<JPerson> jPeople;

    String[] daysOfWeek = {
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресение"
    };

    public String getRouteString(){
        if (routeDays != null){
            String[] days = routeDays.split("");
            StringBuilder result = new StringBuilder();
            for (String day : days) {
                int dayIndex = Integer.parseInt(day) - 1;
                if (dayIndex >= 0 && dayIndex <= 6){
                    result.append(daysOfWeek[dayIndex]).append(dayIndex != 6 ? " - " : ".");
                }
            }
            return nameRoute + ": " + result;
        } else return null;
    }
}
