package com.example.proyecto_poii_ordenes.controller;

import com.example.proyecto_poii_ordenes.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

// Etiqueta de Springboot para indicar la responsabilidad de recibir las peticiones HTTP, en este caso seria para devolver la pagina web
// cuando alguien accese al url en algun navegador
@Controller
public class OrderController {

    // Sirve para implementar las dependencias en lugar de yo instanciarlas
    // Es lo mismo que hacer OrderService orderService = new Orderservice(); . Es un servicio automático de Springboot
    @Autowired
    private OrderService orderService;

    @GetMapping("/") // Es el mapeo de la ruta, se ejecuta el codigo cuando alguien accesa
    public String viewOrders(Model model) { //Definimos el metodo para ejecutar al visitar la pagina, model servira para guardar los datos
        // que se mandaran al frontend (html)
        // Mandamos a llamar el metodo de processPending en la capa de servicios
        Map<String, Object> data = orderService.processPendingOrders();

        // Les asignamos llaves a los datos para poder mandarlos al frontend, llaves me refiero a id's
        model.addAttribute("orders", data.get("orders"));
        model.addAttribute("totalCount", data.get("totalCount"));
        model.addAttribute("totalAmount", data.get("totalAmount"));

        return "orders";
    }
}
