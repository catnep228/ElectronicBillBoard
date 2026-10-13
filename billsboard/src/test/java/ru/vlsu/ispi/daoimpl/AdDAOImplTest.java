package ru.vlsu.ispi.daoimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.AdPhoto;
import ru.vlsu.ispi.beans.AdStatus;
import ru.vlsu.ispi.beans.Category;
import ru.vlsu.ispi.beans.User;


class AdDAOImplTest {

    // TODO: подставьте id реальных записей из ваших таблиц users и categories
    private static final long SELLER_ID = 1L;
    private static final long CATEGORY_ID = 1L;

    private final AdDAOImpl dao = new AdDAOImpl();

    /** id созданных в тесте объявлений: удаляем их после каждого теста */
    private final List<Long> createdIds = new ArrayList<>();

    @BeforeAll
    static void checkDatabaseAvailable() {
        boolean available;
        try (Connection con = DBImpl.getConnection()) {
            available = con != null && con.isValid(2);
        } catch (Exception e) {
            available = false;
        }
        assumeTrue(available, "БД недоступна: интеграционный тест пропущен");
    }

    @AfterEach
    void cleanUp() {
        for (Long id : createdIds) {
            dao.delete(id);
        }
        createdIds.clear();
    }

    @Test
    @DisplayName("Создание двух объявлений и вывод всех объявлений")
    void createTwoAds_thenGetAll() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Ad first = buildAd("Тест: велосипед", "Горный велосипед", "15000.00", now);
        first.setPhotos(List.of(buildPhoto("bike.jpg", 1)));


        Ad second = buildAd("Тест: ноутбук", "Ноутбук в хорошем состоянии", "45000.50",
                now.plusSeconds(1));


        dao.create(first);
        createdIds.add(first.getId());

        dao.create(second);
        createdIds.add(second.getId());

        assertThat(first.getId()).isPositive();
        assertThat(second.getId()).isPositive();
        assertThat(second.getId()).isNotEqualTo(first.getId());


        List<Ad> all = dao.getAll();

        System.out.println("===== Все объявления (" + all.size() + ") =====");
        for (Ad ad : all) {
            System.out.printf("id=%d | %s | %s | %s | фото: %d%n",
                    ad.getId(),
                    ad.getTitle(),
                    ad.getPrice(),
                    ad.getStatus(),
                    ad.getPhotos() == null ? 0 : ad.getPhotos().size());
        }
        System.out.println("=========================================");

        // --- проверяем ---
        List<Long> ids = all.stream().map(Ad::getId).toList();
        assertThat(ids).contains(first.getId(), second.getId());

        Ad loadedFirst = findById(all, first.getId());
        assertThat(loadedFirst.getTitle()).isEqualTo("Тест: велосипед");
        assertThat(loadedFirst.getPrice()).isEqualByComparingTo("15000.00");
        assertThat(loadedFirst.getPhotos()).hasSize(1);
        assertThat(loadedFirst.getPhotos().get(0).getFileName()).isEqualTo("bike.jpg");

        Ad loadedSecond = findById(all, second.getId());
        assertThat(loadedSecond.getTitle()).isEqualTo("Тест: ноутбук");
        assertThat(loadedSecond.getPrice()).isEqualByComparingTo("45000.50");
        assertThat(loadedSecond.getPhotos()).isEmpty();

        // Сортировка по дате создания по убыванию: более новое идёт раньше
        assertThat(ids.indexOf(second.getId()))
                .isLessThan(ids.indexOf(first.getId()));
    }

    // ---------- вспомогательные методы ----------

    private Ad buildAd(String title, String description, String price, LocalDateTime createdAt) {
        Ad ad = new Ad();
        ad.setTitle(title);
        ad.setDescription(description);
        ad.setPrice(new BigDecimal(price));
        // TODO: если нужен конкретный статус, замените, например, на AdStatus.ACTIVE
        ad.setStatus(AdStatus.values()[0]);
        ad.setCreatedAt(createdAt);

        User seller = new User();
        seller.setId(SELLER_ID);
        ad.setSeller(seller);

        Category category = new Category();
        category.setId(CATEGORY_ID);
        ad.setCategory(category);

        return ad;
    }

    private AdPhoto buildPhoto(String fileName, int position) {
        AdPhoto photo = new AdPhoto();
        photo.setFileName(fileName);
        photo.setPosition(position);
        return photo;
    }

    private Ad findById(List<Ad> ads, long id) {
        return ads.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Объявление id=" + id + " не найдено"));
    }
}