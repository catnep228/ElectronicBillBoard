package ru.vlsu.ispi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.Category;
import ru.vlsu.ispi.dao.AdDAO;
import ru.vlsu.ispi.dao.CategoryDAO;
import ru.vlsu.ispi.dto.AdDTO;
import ru.vlsu.ispi.dto.CategoryDTO;

@Controller
public class HomeController {

    private final AdDAO adDAO;
    private final CategoryDAO categoryDAO;

    public HomeController(
            AdDAO adDAO,
            CategoryDAO categoryDAO) {

        this.adDAO = adDAO;
        this.categoryDAO = categoryDAO;
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @PostMapping("/")
    public String indexPost() {
        return "redirect:/";
    }

    @GetMapping("/")
    public String index(
            @RequestParam(value = "sort", required = false, defaultValue = "") String sort,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            Model model) {

        List<Ad> ads = adDAO.getAll();

        System.out.println("========== ОБЪЯВЛЕНИЯ ==========");

        for (Ad ad : ads) {

            System.out.println(
                    "AD id=" + ad.getId()
                            + ", title=" + ad.getTitle()
                            + ", photos=" + ad.getPhotos().size()
            );

            for (var photo : ad.getPhotos()) {

                System.out.println(
                        "    PHOTO id=" + photo.getId()
                                + ", fileName=" + photo.getFileName()
                                + ", position=" + photo.getPosition()
                );
            }
        }

        System.out.println("================================");


        /*
         * Entity -> DTO
         */

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

                    sellerName += " " + ad.getSeller().getSecondName();
                }

                dto.setSellerName(sellerName);
            }


            /*
             * Категория
             */

            if (ad.getCategory() != null) {

                dto.setCategoryId(ad.getCategory().getId());
                dto.setCategoryName(ad.getCategory().getName());
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
         * ФИЛЬТР ПО КАТЕГОРИИ
         */

        if (categoryId != null) {

            advertisements.removeIf(ad ->
                    ad.getCategoryId() == null
                            || !ad.getCategoryId().equals(categoryId)
            );
        }


        /*
         * ФИЛЬТР ПО СТАТУСУ
         */

        if (status != null && !status.isBlank()) {

            advertisements.removeIf(ad ->
                    ad.getStatus() == null
                            || !ad.getStatus().name().equals(status)
            );
        }


        /*
         * СОРТИРОВКА
         */

        switch (sort) {

            /*
             * Новые сначала
             */

            case "newest":

                advertisements.sort((a, b) -> {

                    if (a.getCreatedAt() == null) {
                        return 1;
                    }

                    if (b.getCreatedAt() == null) {
                        return -1;
                    }

                    return b.getCreatedAt()
                            .compareTo(a.getCreatedAt());
                });

                break;


            /*
             * Старые сначала
             */

            case "oldest":

                advertisements.sort((a, b) -> {

                    if (a.getCreatedAt() == null) {
                        return 1;
                    }

                    if (b.getCreatedAt() == null) {
                        return -1;
                    }

                    return a.getCreatedAt()
                            .compareTo(b.getCreatedAt());
                });

                break;


            /*
             * Сначала дешёвые
             */

            case "priceAsc":

                advertisements.sort((a, b) -> {

                    if (a.getPrice() == null) {
                        return 1;
                    }

                    if (b.getPrice() == null) {
                        return -1;
                    }

                    return a.getPrice()
                            .compareTo(b.getPrice());
                });

                break;


            /*
             * Сначала дорогие
             */

            case "priceDesc":

                advertisements.sort((a, b) -> {

                    if (a.getPrice() == null) {
                        return 1;
                    }

                    if (b.getPrice() == null) {
                        return -1;
                    }

                    return b.getPrice()
                            .compareTo(a.getPrice());
                });

                break;


            /*
             * Название А-Я
             */

            case "titleAsc":

                advertisements.sort((a, b) -> {

                    String titleA = a.getTitle() == null
                            ? ""
                            : a.getTitle();

                    String titleB = b.getTitle() == null
                            ? ""
                            : b.getTitle();

                    return titleA.compareToIgnoreCase(titleB);
                });

                break;


            /*
             * Название Я-А
             */

            case "titleDesc":

                advertisements.sort((a, b) -> {

                    String titleA = a.getTitle() == null
                            ? ""
                            : a.getTitle();

                    String titleB = b.getTitle() == null
                            ? ""
                            : b.getTitle();

                    return titleB.compareToIgnoreCase(titleA);
                });

                break;


            /*
             * Без сортировки
             */

            default:
                break;
        }


        /*
         * Получаем категории
         */

        List<Category> categoriesEntity = categoryDAO.getAll();

        /*
         * Entity -> DTO
         */

        List<CategoryDTO> categories = new ArrayList<>();

        for (Category category : categoriesEntity) {

            CategoryDTO dto = new CategoryDTO();

            dto.setId(category.getId());
            dto.setName(category.getName());

            if (category.getParent() != null) {

                dto.setParentId(category.getParent().getId());
                dto.setParentName(category.getParent().getName());
            }

            categories.add(dto);
        }


        /*
         * Передаём данные в JSP
         */

        model.addAttribute(
                "advertisements",
                advertisements
        );

        model.addAttribute(
                "categories",
                categories
        );


        /*
         * Сохраняем выбранные значения.
         * JSP может использовать их через param,
         * но эти атрибуты тоже доступны отдельно.
         */

        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);


        return "index";
    }
}