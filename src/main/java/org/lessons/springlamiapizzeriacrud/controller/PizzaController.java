package org.lessons.springlamiapizzeriacrud.controller;

import jakarta.validation.Valid;
import org.lessons.springlamiapizzeriacrud.messages.AlertMessage;
import org.lessons.springlamiapizzeriacrud.messages.AltertMessageType;
import org.lessons.springlamiapizzeriacrud.model.Pizza;
import org.lessons.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        /*Optional<Pizza> result = pizzaRepository.findById(pizzaId);
        if (result.isPresent()) {
            model.addAttribute("pizza", result.get());
            return "/pizza/pizzaDetail";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + pizzaId + " not found");
        }*/
        Pizza pizza = getPizzaById(pizzaId);
        model.addAttribute("pizza", pizza);
        return "/pizza/pizzaDetail";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "/pizza/edit";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult, RedirectAttributes redirAttrs) {
        if (!isUniqueName(formPizza)) {
            bindingResult.addError(new FieldError("pizza", "name", formPizza.getName(), false, null, null, "pizza name must be unique"));
        }
        if (bindingResult.hasErrors()) {
            return "/pizza/edit";
        }
        redirAttrs.addFlashAttribute("message", new AlertMessage(AltertMessageType.SUCCESS, "Pizza created"));
        pizzaRepository.save(formPizza);
        return "redirect:/pizza";
    }

    @GetMapping("edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Pizza result = getPizzaById(id);
        model.addAttribute("pizza", result);
        return "/pizza/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Pizza pizzaEdit = getPizzaById(id);
        if (!pizzaEdit.getName().equals(formPizza.getName()) && !isUniqueName(formPizza)) {
            bindingResult.addError(new FieldError("pizza", "name", formPizza.getName(), false, null, null, "pizza name must be unique"));
        }
        if (bindingResult.hasErrors()) {
            return "/pizza/edit";
        }
        formPizza.setId(formPizza.getId());
        pizzaRepository.save(formPizza);
        redirectAttributes.addFlashAttribute("message", new AlertMessage(AltertMessageType.SUCCESS, "Pizza with id " + pizzaEdit.getId() + " updated"));
        return "redirect:/pizza";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Pizza pizzaToDelete = getPizzaById(id);
        pizzaRepository.delete(pizzaToDelete);
        redirectAttributes.addFlashAttribute("message", new AlertMessage(AltertMessageType.SUCCESS, "Pizza " + pizzaToDelete.getName() + " deleted"));
        return "redirect:/pizza";
    }

    private Pizza getPizzaById(Integer id) {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id" + id + " not found");
        }
        return result.get();
    }

    private boolean isUniqueName(Pizza pizzaForm) {
        Optional<Pizza> result = pizzaRepository.findByName(pizzaForm.getName());
        return result.isEmpty();
    }
}
