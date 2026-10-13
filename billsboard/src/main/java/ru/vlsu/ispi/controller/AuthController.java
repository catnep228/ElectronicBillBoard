package ru.vlsu.ispi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.vlsu.ispi.dto.UserDTO;
import ru.vlsu.ispi.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @ModelAttribute("user") UserDTO dto,
            Model model) {

        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }

        try {
            userService.register(dto);
            return "redirect:/auth/login?registered";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";

        } catch (Exception e) {
            // Ловим любую другую ошибку
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            return "register";
        }
    }
}