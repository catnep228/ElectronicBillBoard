package ru.vlsu.ispi.dao;

import java.util.List;
import ru.vlsu.ispi.beans.Message;

public interface MessageDAO {

    void create(Message message);

    void update(Message message);

    void delete(long id);

    Message getById(long id);

    List<Message> getAll();

    List<Message> getByAdId(long adId);

    List<Message> getByRecipientId(long recipientId);

    List<Message> getByUserId(long userId);

    void markAsRead(long id);
}