package ru.msu.cmc.webprac.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Автосалон");
        return "dashboard";
    }

    @GetMapping("/cars")
    public String cars(Model model) {
        model.addAttribute("pageTitle", "Автомобили");
        return "cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Автомобиль");
        model.addAttribute("entityId", id);
        return "cars";
    }

    @GetMapping("/brands")
    public String brands(Model model) {
        model.addAttribute("pageTitle", "Марки автомобилей");
        return "brands";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("pageTitle", "Клиенты");
        return "clients";
    }

    @GetMapping("/clients/{id}")
    public String clientDetails(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Клиент");
        model.addAttribute("entityId", id);
        return "clients";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("pageTitle", "Заказы");
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Заказ");
        model.addAttribute("entityId", id);
        return "orders";
    }

    @GetMapping("/test-drives")
    public String testDrives(Model model) {
        model.addAttribute("pageTitle", "Тест-драйвы");
        return "test-drives";
    }
}
