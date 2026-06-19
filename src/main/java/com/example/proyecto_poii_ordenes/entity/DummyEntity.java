package com.example.proyecto_poii_ordenes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
// Funciona como un ancla
// Le pasamos la clase vacia de JpaRepository, sirve para ejecutar las consultas sin tener que mapear toda la base de datos
@Entity
public class DummyEntity {
    @Id
    private Integer id;
}