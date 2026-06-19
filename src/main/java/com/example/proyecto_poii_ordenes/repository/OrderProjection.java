package com.example.proyecto_poii_ordenes.repository;

import java.util.Date;

//Es una interfaz para obtener los datos que queremos, Springboot crear por defecto una clase virtual que implementa y llena de datos
//automaticamente, va directamente conectado con OrderRepository, SpringBoot toma el alias que le pusimos en la consulta y hace métodos
//Asi evitamos overfetching para evitar sobre obtener datos

public interface OrderProjection {
    Integer getId();
    String getCustomerName();
    Date getOrderDate();
    Integer getStatus();
    Double getTotal();
}
