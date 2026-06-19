package com.example.proyecto_poii_ordenes.repository;

import com.example.proyecto_poii_ordenes.entity.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<DummyEntity, Integer> {

    @Query(value = "SELECT soh.SalesOrderID as id, " +
            "p.FirstName + ' ' + p.LastName as customerName, " +
            "soh.OrderDate as orderDate, " +
            "soh.Status as status, " +
            "soh.TotalDue as total " +
            "FROM Sales.SalesOrderHeader soh " +
            "JOIN Sales.Customer c ON soh.CustomerID = c.CustomerID " +
            "JOIN Person.Person p ON c.PersonID = p.BusinessEntityID",
            nativeQuery = true)
    List<OrderProjection> findAllOrdersToProcess();
}
