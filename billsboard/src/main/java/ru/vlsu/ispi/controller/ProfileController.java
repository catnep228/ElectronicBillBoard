
package ru.vlsu.ispi.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.AdDAO;
import ru.vlsu.ispi.dao.UserDAO;

@Controller
public class ProfileController {

    private final UserDAO userDAO;
    private final AdDAO adDAO;

    public ProfileController(UserDAO userDAO, AdDAO adDAO) {
        this.userDAO = userDAO;
        this.adDAO = adDAO;
    }

    @GetMapping("/profile")
    public String profile(
            Principal principal,
            Model model) {

        String email = principal.getName();

        User user = userDAO.getByEmail(email);

        if (user == null) {
            return "redirect:/login";
        }

        List<Ad> ads = adDAO.getBySellerId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("ads", ads);

        return "profile";
    }
}
