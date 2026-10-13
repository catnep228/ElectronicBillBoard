package ru.vlsu.ispi.dao;

import java.util.List;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.AdPhoto;

public interface AdDAO {

    List<Ad> getBySellerId(long sellerId);

    void create(Ad ad);

    void update(Ad ad);

    void delete(long id);

    Ad getById(long id);

    List<Ad> getAll();

    void addPhoto(AdPhoto photo);

    void deletePhoto(long photoId);
}