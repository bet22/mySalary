package ru.bet;


import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SalaryRepo extends CrudRepository<Salary, Integer> {
    List<Salary> findBySalary(String salary);

}
