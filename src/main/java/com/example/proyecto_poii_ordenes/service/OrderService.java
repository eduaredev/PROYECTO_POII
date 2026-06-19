package com.example.proyecto_poii_ordenes.service;

import com.example.proyecto_poii_ordenes.repository.OrderProjection;
import com.example.proyecto_poii_ordenes.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// Etiqueta para mantener preparada la memoria
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Object> processPendingOrders() {
        Map<String, Object> resultData = new HashMap<>();

        // Obtenemos los datos de la DB en Azure
        List<OrderProjection> allOrders = orderRepository.findAllOrdersToProcess();

        // Los filtramos y ordenamos mediante una collection y stream, que solamente extraigo la informacion y uso la API de Streams de Java
        // para procesarla en la Memoria RAM del servidor, el filtro descarta cuualquier orden que no sea pendiente o en proceso
        // y la ordena de acuerdo a la fecha en el caso de la fecha más reciente a la más antigua
        List<OrderProjection> pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == 1 || o.getStatus() == 2) // 1=Pendiente, 2=En proceso
                .sorted(Comparator.comparing(OrderProjection::getOrderDate).reversed())
                .collect(Collectors.toList());

        // Guardamos todo en un archivo mandando a llamar el metodo privado que se creó hasta abajo
        saveToFile(pendingOrders);

        // THREADS

        // En este caso cree dos arreglos, en donde guardare tanto la cantidad total de ordenes que cumplan con los filtros
        // y la cantidad total de la suma de los totales de cada orden

        final int[] totalCount = {0};
        final double[] totalAmount = {0.0};

        // Se pusieron como arreglos debido a problemas de memoria con los Threads, y es debido a Java y su gestión de memoria
        // Cuando creo un nuevo hilo, este hilo tiene su propio espacio y tiempo, cuando se termine su ejecución su memoria stack
        // se destruye antes de que el nuevo hilo haya terminado de hacer su cálculo,
        // si pusiese una variable normal esta se guarda en el stack y no afecta a la memoria del Thread por lo que nunca veria ningun resultado
        // en cambio un arreglo es una instancia osea un objeto que se guarda en el heap que es una memoria global a largo plazo
        // aunque los hilos tengan memoria stack si sobreescriben sobre memoria heap hara que funcione y apliquen lo requerido

        Thread threadCounter = new Thread(() -> {
            totalCount[0] = pendingOrders.size();
            System.out.println("Hilo 1: " + totalCount[0] + " ordenes");
        });

        Thread threadCalculator = new Thread(() -> {
            totalAmount[0] = pendingOrders.stream().mapToDouble(OrderProjection::getTotal).sum();
            System.out.println("Hilo 2: " + totalAmount[0]);
        });

        threadCounter.start();
        threadCalculator.start();


        //Sincroniza los procesos para que siga el programa despues de terminar los hilos
        try {
            threadCounter.join();
            threadCalculator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        //Finalmente almaceno la lista, el resultado del thread de conteo y suma en un tipo diccionario osea hash map y se la devuelvo
        // al controlador que es la clase OrderController para despues mandarla al front

        resultData.put("orders", pendingOrders);
        resultData.put("totalCount", totalCount[0]);
        resultData.put("totalAmount", totalAmount[0]);

        return resultData;
    }

    private void saveToFile(List<OrderProjection> orders) {
        String filePath = "ordenes_pendientes.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ID,CustomerName,OrderDate,Status,Total\n");
            for (OrderProjection o : orders) {
                writer.write(o.getId() + "," + o.getCustomerName() + "," +
                        o.getOrderDate() + "," + o.getStatus() + "," + o.getTotal() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }
}