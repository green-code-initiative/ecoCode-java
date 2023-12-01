package fr.greencodeinitiative.java.checks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public class AvoidSpringRepositoryCallInLoopCheck {
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> smellGetAllEmployeesByIds(List<Integer> ids) {
        List<Employee> employees = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Employee> employee = employeeRepository.findById(id); // Noncompliant {{Avoid Spring repository call in loop or stream}}
            if (employee.isPresent()) {
                employees.add(employee.get());
            }
        }
        return employees;
    }

    public class Employee {
        private Integer id;
        private String name;

        public Employee(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
    }

    public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    }
    
}