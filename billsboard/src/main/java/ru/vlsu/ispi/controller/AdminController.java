package ru.vlsu.ispi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.AdStatus;
import ru.vlsu.ispi.beans.Category;
import ru.vlsu.ispi.dao.AdDAO;
import ru.vlsu.ispi.dao.CategoryDAO;
import ru.vlsu.ispi.dto.AdDTO;
import ru.vlsu.ispi.dto.CategoryDTO;

@Controller
public class AdminController {

    private final AdDAO adDAO;
    private final CategoryDAO categoryDAO;

    public AdminController(
            AdDAO adDAO,
            CategoryDAO categoryDAO) {

        this.adDAO = adDAO;
        this.categoryDAO = categoryDAO;
    }


    /*
     * ============================
     * ГЛАВНАЯ СТРАНИЦА АДМИНИСТРАТОРА
     * ============================
     */

    @GetMapping("/admin")
    public String adminPage(
            Authentication authentication,
            Model model) {

        /*
         * Дополнительная проверка.
         *
         * Основную защиту /admin/** желательно
         * также сделать в SecurityConfig.
         */

        if (!isAdmin(authentication)) {
            return "redirect:/";
        }


        /*
         * Все объявления
         */

        List<Ad> ads = adDAO.getAll();

        List<AdDTO> advertisements = new ArrayList<>();

        for (Ad ad : ads) {

            AdDTO dto = new AdDTO();

            dto.setId(ad.getId());
            dto.setTitle(ad.getTitle());
            dto.setDescription(ad.getDescription());
            dto.setPrice(ad.getPrice());
            dto.setStatus(ad.getStatus());
            dto.setCreatedAt(ad.getCreatedAt());


            /*
             * Продавец
             */

            if (ad.getSeller() != null) {

                dto.setSellerId(ad.getSeller().getId());

                String sellerName = ad.getSeller().getName();

                if (ad.getSeller().getSecondName() != null
                        && !ad.getSeller().getSecondName().isBlank()) {

                    sellerName += " "
                            + ad.getSeller().getSecondName();
                }

                dto.setSellerName(sellerName);
            }


            /*
             * Категория
             */

            if (ad.getCategory() != null) {

                dto.setCategoryId(
                        ad.getCategory().getId()
                );

                dto.setCategoryName(
                        ad.getCategory().getName()
                );
            }


            /*
             * Фотографии
             */

            if (ad.getPhotos() != null) {

                ad.getPhotos().forEach(photo -> {

                    ru.vlsu.ispi.dto.AdPhotoDTO photoDTO =
                            new ru.vlsu.ispi.dto.AdPhotoDTO();

                    photoDTO.setId(photo.getId());
                    photoDTO.setAdId(ad.getId());
                    photoDTO.setFileName(photo.getFileName());
                    photoDTO.setPosition(photo.getPosition());

                    dto.getPhotos().add(photoDTO);
                });
            }

            advertisements.add(dto);
        }


        /*
         * Все категории
         */

        List<Category> categoriesEntity =
                categoryDAO.getAll();

        List<CategoryDTO> categories =
                new ArrayList<>();

        for (Category category : categoriesEntity) {

            CategoryDTO dto = new CategoryDTO();

            dto.setId(category.getId());
            dto.setName(category.getName());

            if (category.getParent() != null) {

                dto.setParentId(
                        category.getParent().getId()
                );

                dto.setParentName(
                        category.getParent().getName()
                );
            }

            categories.add(dto);
        }


        model.addAttribute(
                "advertisements",
                advertisements
        );

        model.addAttribute(
                "categories",
                categories
        );

        model.addAttribute(
                "statuses",
                AdStatus.values()
        );


        return "admin";
    }


    /*
     * ============================
     * СОЗДАНИЕ КАТЕГОРИИ
     * ============================
     */

    @PostMapping("/admin/categories/create")
    public String createCategory(
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam(value = "parentId", required = false)
                    Long parentId,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(authentication)) {
            return "redirect:/";
        }


        /*
         * Проверка названия
         */

        if (name == null || name.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Название категории не может быть пустым."
            );

            return "redirect:/admin";
        }


        name = name.trim();


        /*
         * Создаём категорию
         */

        Category category = new Category();

        category.setName(name);


        /*
         * Родительская категория
         */

        if (parentId != null) {

            Category parent = new Category();

            parent.setId(parentId);

            category.setParent(parent);
        }


        categoryDAO.create(category);


        redirectAttributes.addFlashAttribute(
                "success",
                "Категория успешно создана."
        );


        return "redirect:/admin";
    }


    /*
     * ============================
     * УДАЛЕНИЕ ОБЪЯВЛЕНИЯ
     * ============================
     */

    @PostMapping("/admin/ads/{id}/delete")
    public String deleteAd(
            Authentication authentication,
            @PathVariable("id") long id,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(authentication)) {
            return "redirect:/";
        }


        Ad ad = adDAO.getById(id);

        if (ad == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Объявление не найдено."
            );

            return "redirect:/admin";
        }


        /*
         * Администратор может удалить
         * любое объявление.
         */

        adDAO.delete(id);


        redirectAttributes.addFlashAttribute(
                "success",
                "Объявление успешно удалено."
        );


        return "redirect:/admin";
    }


    /*
     * ============================
     * ФОРМА РЕДАКТИРОВАНИЯ
     * ============================
     */

    @GetMapping("/admin/ads/{id}/edit")
    public String editAdPage(
            Authentication authentication,
            @PathVariable("id") long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(authentication)) {
            return "redirect:/";
        }


        Ad ad = adDAO.getById(id);

        if (ad == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Объявление не найдено."
            );

            return "redirect:/admin";
        }


        model.addAttribute(
                "ad",
                ad
        );

        model.addAttribute(
                "categories",
                categoryDAO.getAll()
        );

        model.addAttribute(
                "statuses",
                AdStatus.values()
        );


        return "adminEditAd";
    }


    /*
     * ============================
     * СОХРАНЕНИЕ РЕДАКТИРОВАНИЯ
     * ============================
     */

    @PostMapping("/admin/ads/{id}/edit")
    public String editAd(
            Authentication authentication,
            @PathVariable("id") long id,

            @RequestParam("title")
                    String title,

            @RequestParam("description")
                    String description,

            @RequestParam("price")
                    String priceValue,

            @RequestParam("categoryId")
                    long categoryId,

            @RequestParam("status")
                    String statusValue,

            RedirectAttributes redirectAttributes) {

        if (!isAdmin(authentication)) {
            return "redirect:/";
        }


        Ad ad = adDAO.getById(id);

        if (ad == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Объявление не найдено."
            );

            return "redirect:/admin";
        }


        /*
         * Проверяем название
         */

        if (title == null || title.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Название объявления не может быть пустым."
            );

            return "redirect:/admin/ads/" + id + "/edit";
        }


        /*
         * Проверяем цену
         */

        BigDecimal price;

        try {

            price = new BigDecimal(
                    priceValue.replace(",", ".")
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Некорректная цена."
            );

            return "redirect:/admin/ads/" + id + "/edit";
        }


        /*
         * Проверяем категорию
         */

        Category category = new Category();

        category.setId(categoryId);


        /*
         * Проверяем статус
         */

        AdStatus status;

        try {

            status = AdStatus.valueOf(statusValue);

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Некорректный статус объявления."
            );

            return "redirect:/admin/ads/" + id + "/edit";
        }


        /*
         * Обновляем объявление.
         *
         * ВАЖНО:
         * seller НЕ меняем.
         *
         * Поэтому объявление продолжает
         * принадлежать своему продавцу.
         *
         * Но администратор может изменить
         * его независимо от владельца.
         */

        ad.setTitle(title.trim());
        ad.setDescription(description);
        ad.setPrice(price);
        ad.setCategory(category);
        ad.setStatus(status);


        adDAO.update(ad);


        redirectAttributes.addFlashAttribute(
                "success",
                "Объявление успешно изменено."
        );


        return "redirect:/admin";
    }


    /*
     * ============================
     * ПРОВЕРКА АДМИНИСТРАТОРА
     * ============================
     */

    private boolean isAdmin(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }


        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN")
                );
    }
}