package org.lessons.springlamiapizzeriacrud.controller;

import jakarta.validation.Valid;
import org.lessons.springlamiapizzeriacrud.model.Pizza;
import org.lessons.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    @Autowired
    private PizzaRepository pizzaRepository;

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String searchString, Model model) {
        List<Pizza> pizzas;
        if (searchString == null || searchString.isBlank()) {
            pizzas = pizzaRepository.findAll();
        } else {
            pizzas = pizzaRepository.findByNameContainingIgnoreCase(searchString);
        }
        model.addAttribute("pizzaList", pizzas);
        model.addAttribute("searchString", searchString == null ? "" : searchString);
        return "/pizza/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Integer pizzaId, Model model) {
        Optional<Pizza> result = pizzaRepository.findById(pizzaId);
        if (result.isPresent()) {
            model.addAttribute("pizza", result.get());
            return "/pizza/pizzaDetail";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + pizzaId + " not found");
        }
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "/pizza/create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pizza/create";
        }
        pizzaRepository.save(formPizza);
        return "redirect:/pizza";
    }
}
