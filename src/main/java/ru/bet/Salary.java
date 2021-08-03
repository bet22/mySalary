package ru.bet;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String salary;
    private Integer date;

    public Salary (String salary, Integer date) {
        this.salary = salary;
        this.date = date;
    }

    public Salary () {
    }
}
