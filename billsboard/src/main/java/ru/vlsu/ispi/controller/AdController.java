package ru.vlsu.ispi.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.AdPhoto;
import ru.vlsu.ispi.beans.AdStatus;
import ru.vlsu.ispi.beans.Category;
import ru.vlsu.ispi.beans.Message;
import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.AdDAO;
import ru.vlsu.ispi.dao.CategoryDAO;
import ru.vlsu.ispi.dao.MessageDAO;
import ru.vlsu.ispi.dao.UserDAO;
import ru.vlsu.ispi.dto.AdDTO;
import ru.vlsu.ispi.dto.AdPhotoDTO;

@Controller
public class AdController {

    private final AdDAO adDAO;
    private final CategoryDAO categoryDAO;
    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    private static final Path UPLOAD_DIR =
            Paths.get(System.getProperty("user.dir"), "uploads");

    public AdController(
            AdDAO adDAO,
            CategoryDAO categoryDAO,
            MessageDAO messageDAO,
            UserDAO userDAO) {

        this.adDAO = adDAO;
        this.categoryDAO = categoryDAO;
        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
    }

    /*
     * ============================================================
     * СОЗДАНИЕ ОБЪЯВЛЕНИЯ
     * ============================================================
     */

    @GetMapping("/FormForAd")
    public String showCreateForm(
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/auth/login";
        }

        model.addAttribute(
                "categories",
                categoryDAO.getAll()
        );

        return "FormForAd";
    }

    @PostMapping("/FormForAd")
    public String createAd(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") String priceValue,
            @RequestParam("categoryId") long categoryId,
            @RequestParam(
                    value = "photos",
                    required = false
            ) MultipartFile[] photos,
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/auth/login";
        }

        User seller =
                userDAO.getByEmail(authentication.getName());

        if (seller == null) {
            return "redirect:/auth/login";
        }

        title = title == null ? "" : title.trim();
        description =
                description == null
                        ? ""
                        : description.trim();

        if (title.isEmpty()) {
            return showCreateFormWithError(
                    model,
                    "Введите название объявления."
            );
        }

        if (title.length() > 150) {
            return showCreateFormWithError(
                    model,
                    "Название не должно превышать 150 символов."
            );
        }

        if (description.length() > 4000) {
            return showCreateFormWithError(
                    model,
                    "Описание не должно превышать 4000 символов."
            );
        }

        BigDecimal price;

        try {
            price = new BigDecimal(priceValue);
        } catch (Exception e) {
            return showCreateFormWithError(
                    model,
                    "Введите корректную цену."
            );
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            return showCreateFormWithError(
                    model,
                    "Цена не может быть отрицательной."
            );
        }

        Category category =
                categoryDAO.getById(categoryId);

        if (category == null) {
            return showCreateFormWithError(
                    model,
                    "Выбранная категория не существует."
            );
        }

        Ad ad = new Ad();

        ad.setTitle(title);
        ad.setDescription(
                description.isEmpty()
                        ? null
                        : description
        );
        ad.setPrice(price);
        ad.setStatus(AdStatus.ACTIVE);
        ad.setCreatedAt(LocalDateTime.now());
        ad.setSeller(seller);
        ad.setCategory(category);

        List<AdPhoto> adPhotos =
                new ArrayList<>();

        if (photos != null) {

            int position = 0;

            for (MultipartFile file : photos) {

                if (file == null || file.isEmpty()) {
                    continue;
                }

                String originalFilename =
                        file.getOriginalFilename();

                if (originalFilename == null
                        || originalFilename.isBlank()) {
                    continue;
                }

                String contentType =
                        file.getContentType();

                if (contentType == null
                        || !contentType.startsWith("image/")) {
                    continue;
                }

                String extension =
                        getFileExtension(
                                originalFilename
                        );

                String fileName =
                        UUID.randomUUID()
                                + extension;

                try {

                    Files.createDirectories(
                            UPLOAD_DIR
                    );

                    Path target =
                            UPLOAD_DIR
                                    .resolve(fileName)
                                    .normalize();

                    Files.copy(
                            file.getInputStream(),
                            target,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                } catch (IOException e) {

                    deleteUploadedFiles(adPhotos);

                    return showCreateFormWithError(
                            model,
                            "Не удалось сохранить фотографию."
                    );
                }

                AdPhoto photo = new AdPhoto();

                photo.setFileName(fileName);
                photo.setPosition(position);
                photo.setAd(ad);

                adPhotos.add(photo);

                position++;
            }
        }

        ad.setPhotos(adPhotos);

        try {

            adDAO.create(ad);

        } catch (RuntimeException e) {

            deleteUploadedFiles(adPhotos);

            return showCreateFormWithError(
                    model,
                    "Не удалось создать объявление."
            );
        }

        return "redirect:/ads/" + ad.getId();
    }

    private String showCreateFormWithError(
            Model model,
            String error) {

        model.addAttribute("error", error);

        model.addAttribute(
                "categories",
                categoryDAO.getAll()
        );

        return "FormForAd";
    }

    /*
     * ============================================================
     * РЕДАКТИРОВАНИЕ ОБЪЯВЛЕНИЯ
     * ============================================================
     */

    @GetMapping("/ads/{id}/edit")
    public String showEditForm(
            @PathVariable("id") long id,
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/auth/login";
        }

        User currentUser =
                userDAO.getByEmail(authentication.getName());

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Ad ad = adDAO.getById(id);

        if (ad == null) {
            return "redirect:/";
        }

        /*
         * Редактировать объявление может
         * только его продавец.
         */
        if (ad.getSeller() == null
                || !ad.getSeller()
                        .getId()
                        .equals(currentUser.getId())) {

            return "redirect:/ads/" + id;
        }

        model.addAttribute("ad", ad);

        model.addAttribute(
                "categories",
                categoryDAO.getAll()
        );

        model.addAttribute(
                "statuses",
                AdStatus.values()
        );

        return "editAd";
    }

    @PostMapping("/ads/{id}/edit")
    public String updateAd(
            @PathVariable("id") long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") String priceValue,
            @RequestParam("categoryId") long categoryId,
            @RequestParam("status") String statusValue,
            @RequestParam(
                    value = "photos",
                    required = false
            ) MultipartFile[] photos,
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/auth/login";
        }

        User currentUser =
                userDAO.getByEmail(authentication.getName());

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Ad ad = adDAO.getById(id);

        if (ad == null) {
            return "redirect:/";
        }

        /*
         * Проверяем владельца.
         */
        if (ad.getSeller() == null
                || !ad.getSeller()
                        .getId()
                        .equals(currentUser.getId())) {

            return "redirect:/ads/" + id;
        }

        title = title == null ? "" : title.trim();

        description =
                description == null
                        ? ""
                        : description.trim();

        if (title.isEmpty()) {
            return showEditFormWithError(
                    model,
                    ad,
                    "Введите название объявления."
            );
        }

        if (title.length() > 150) {
            return showEditFormWithError(
                    model,
                    ad,
                    "Название не должно превышать 150 символов."
            );
        }

        if (description.length() > 4000) {
            return showEditFormWithError(
                    model,
                    ad,
                    "Описание не должно превышать 4000 символов."
            );
        }

        BigDecimal price;

        try {

            price = new BigDecimal(priceValue);

        } catch (Exception e) {

            return showEditFormWithError(
                    model,
                    ad,
                    "Введите корректную цену."
            );
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {

            return showEditFormWithError(
                    model,
                    ad,
                    "Цена не может быть отрицательной."
            );
        }

        Category category =
                categoryDAO.getById(categoryId);

        if (category == null) {

            return showEditFormWithError(
                    model,
                    ad,
                    "Выбранная категория не существует."
            );
        }

        AdStatus status;

        try {

            status =
                    AdStatus.valueOf(statusValue);

        } catch (Exception e) {

            return showEditFormWithError(
                    model,
                    ad,
                    "Некорректный статус объявления."
            );
        }

        /*
         * Обновляем поля.
         */
        ad.setTitle(title);

        ad.setDescription(
                description.isEmpty()
                        ? null
                        : description
        );

        ad.setPrice(price);
        ad.setCategory(category);
        ad.setStatus(status);

        try {

            adDAO.update(ad);

        } catch (RuntimeException e) {

            return showEditFormWithError(
                    model,
                    ad,
                    "Не удалось сохранить изменения."
            );
        }

        /*
         * Добавляем новые фотографии.
         */
        if (photos != null) {

            int position = 0;

            if (ad.getPhotos() != null) {

                for (AdPhoto photo : ad.getPhotos()) {

                    if (photo.getPosition() >= position) {
                        position =
                                photo.getPosition() + 1;
                    }
                }
            }

            for (MultipartFile file : photos) {

                if (file == null || file.isEmpty()) {
                    continue;
                }

                String originalFilename =
                        file.getOriginalFilename();

                if (originalFilename == null
                        || originalFilename.isBlank()) {
                    continue;
                }

                String contentType =
                        file.getContentType();

                if (contentType == null
                        || !contentType.startsWith("image/")) {
                    continue;
                }

                String extension =
                        getFileExtension(
                                originalFilename
                        );

                String fileName =
                        UUID.randomUUID()
                                + extension;

                try {

                    Files.createDirectories(
                            UPLOAD_DIR
                    );

                    Path target =
                            UPLOAD_DIR
                                    .resolve(fileName)
                                    .normalize();

                    Files.copy(
                            file.getInputStream(),
                            target,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    AdPhoto photo =
                            new AdPhoto();

                    photo.setAd(ad);
                    photo.setFileName(fileName);
                    photo.setPosition(position);

                    adDAO.addPhoto(photo);

                    position++;

                } catch (IOException e) {

                    /*
                     * Основные изменения уже сохранены.
                     * Поэтому просто прекращаем добавление
                     * фотографий.
                     */
                    break;

                } catch (RuntimeException e) {

                    break;
                }
            }
        }

        return "redirect:/ads/" + id;
    }

    private String showEditFormWithError(
            Model model,
            Ad ad,
            String error) {

        /*
         * Повторно загружаем объявление,
         * чтобы JSP получила актуальные данные.
         */
        Ad actualAd = adDAO.getById(ad.getId());

        if (actualAd != null) {
            ad = actualAd;
        }

        model.addAttribute("ad", ad);

        model.addAttribute(
                "categories",
                categoryDAO.getAll()
        );

        model.addAttribute(
                "statuses",
                AdStatus.values()
        );

        model.addAttribute("error", error);

        return "editAd";
    }

    /*
     * ============================================================
     * ПРОСМОТР ОБЪЯВЛЕНИЯ
     * ============================================================
     */

    @GetMapping("/ads/{id}")
    public String viewAd(
            @PathVariable("id") long id,
            Model model) {

        Ad ad = adDAO.getById(id);

        if (ad == null) {
            return "redirect:/";
        }

        AdDTO dto = new AdDTO();

        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setStatus(ad.getStatus());
        dto.setCreatedAt(ad.getCreatedAt());

        if (ad.getSeller() != null) {

            dto.setSellerId(
                    ad.getSeller().getId()
            );

            String sellerName =
                    ad.getSeller().getName();

            if (ad.getSeller().getSecondName() != null) {

                sellerName +=
                        " " + ad.getSeller().getSecondName();
            }

            dto.setSellerName(sellerName);
        }

        if (ad.getCategory() != null) {

            dto.setCategoryId(
                    ad.getCategory().getId()
            );

            dto.setCategoryName(
                    ad.getCategory().getName()
            );
        }

        if (ad.getPhotos() != null) {

            for (AdPhoto photo : ad.getPhotos()) {

                AdPhotoDTO photoDTO =
                        new AdPhotoDTO();

                photoDTO.setId(photo.getId());
                photoDTO.setAdId(ad.getId());
                photoDTO.setFileName(photo.getFileName());
                photoDTO.setPosition(photo.getPosition());

                dto.getPhotos().add(photoDTO);
            }
        }

        model.addAttribute(
                "advertisement",
                dto
        );

        return "ad";
    }

    /*
     * ============================================================
     * ПЕРВОЕ СООБЩЕНИЕ ПРОДАВЦУ
     * ============================================================
     */

    @PostMapping("/ads/{id}/message")
    public String sendMessage(
            @PathVariable("id") long id,
            @RequestParam("text") String text,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/auth/login";
        }

        Ad ad = adDAO.getById(id);

        if (ad == null) {
            return "redirect:/";
        }

        if (ad.getSeller() == null) {
            return "redirect:/ads/" + id;
        }

        User sender =
                userDAO.getByEmail(authentication.getName());

        if (sender == null) {
            return "redirect:/auth/login";
        }

        if (sender.getId()
                .equals(ad.getSeller().getId())) {

            return "redirect:/ads/" + id;
        }

        if (text == null) {
            return "redirect:/ads/" + id;
        }

        String messageText =
                text.trim();

        if (messageText.isEmpty()) {
            return "redirect:/ads/" + id;
        }

        if (messageText.length() > 2000) {
            messageText =
                    messageText.substring(0, 2000);
        }

        Message message =
                new Message();

        message.setAd(ad);
        message.setSender(sender);
        message.setRecipient(ad.getSeller());
        message.setText(messageText);
        message.setSentAt(LocalDateTime.now());
        message.setReadFlag(false);

        messageDAO.create(message);

        return "redirect:/chat/" + id;
    }

    /*
     * ============================================================
     * ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
     * ============================================================
     */

    private String getFileExtension(
            String fileName) {

        int dotIndex =
                fileName.lastIndexOf('.');

        if (dotIndex >= 0
                && dotIndex < fileName.length() - 1) {

            String extension =
                    fileName.substring(dotIndex)
                            .toLowerCase();

            if (extension.equals(".jpg")
                    || extension.equals(".jpeg")
                    || extension.equals(".png")
                    || extension.equals(".gif")
                    || extension.equals(".webp")
                    || extension.equals(".bmp")) {

                return extension;
            }
        }

        return ".jpg";
    }

    private void deleteUploadedFiles(
            List<AdPhoto> photos) {

        if (photos == null) {
            return;
        }

        for (AdPhoto photo : photos) {

            if (photo == null
                    || photo.getFileName() == null) {
                continue;
            }

            try {

                Path file =
                        UPLOAD_DIR
                                .resolve(
                                        photo.getFileName()
                                )
                                .normalize();

                Files.deleteIfExists(file);

            } catch (IOException ignored) {
            }
        }
    }
}