package ru.vlsu.ispi.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.Message;
import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.MessageDAO;

@Repository
public class MessageDAOImpl implements MessageDAO {

    private DBImpl impl;

    public void setImpl(DBImpl impl) {
        this.impl = impl;
    }

    @Override
    public void create(Message message) {

        String sql = """
                INSERT INTO messages
                (ad_id, sender_id, recipient_id, text, sentAt, readFlag)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setLong(1, message.getAd().getId());
            ps.setLong(2, message.getSender().getId());
            ps.setLong(3, message.getRecipient().getId());
            ps.setString(4, message.getText());

            if (message.getSentAt() != null) {
                ps.setTimestamp(
                        5,
                        Timestamp.valueOf(message.getSentAt())
                );
            } else {
                ps.setTimestamp(
                        5,
                        Timestamp.valueOf(
                                java.time.LocalDateTime.now()
                        )
                );
            }

            ps.setBoolean(6, message.isReadFlag());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при создании сообщения",
                    e
            );
        }
    }

    @Override
    public void update(Message message) {

        String sql = """
                UPDATE messages
                SET text = ?, readFlag = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setString(1, message.getText());
            ps.setBoolean(2, message.isReadFlag());
            ps.setLong(3, message.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при обновлении сообщения",
                    e
            );
        }
    }

    @Override
    public void delete(long id) {

        String sql = """
                DELETE FROM messages
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при удалении сообщения",
                    e
            );
        }
    }

    @Override
    public Message getById(long id) {

        String sql = """
                SELECT
                    id,
                    ad_id,
                    sender_id,
                    recipient_id,
                    text,
                    sentAt,
                    readFlag
                FROM messages
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapMessage(rs);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении сообщения",
                    e
            );
        }

        return null;
    }

    @Override
    public List<Message> getAll() {

        String sql = """
                SELECT
                    id,
                    ad_id,
                    sender_id,
                    recipient_id,
                    text,
                    sentAt,
                    readFlag
                FROM messages
                ORDER BY sentAt DESC
                """;

        List<Message> messages = new ArrayList<>();

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                messages.add(mapMessage(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении сообщений",
                    e
            );
        }

        return messages;
    }

    @Override
    public List<Message> getByAdId(long adId) {

        String sql = """
                SELECT
                    id,
                    ad_id,
                    sender_id,
                    recipient_id,
                    text,
                    sentAt,
                    readFlag
                FROM messages
                WHERE ad_id = ?
                ORDER BY sentAt ASC
                """;

        List<Message> messages = new ArrayList<>();

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setLong(1, adId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении сообщений объявления",
                    e
            );
        }

        return messages;
    }

    @Override
    public List<Message> getByRecipientId(long recipientId) {

        String sql = """
                SELECT
                    id,
                    ad_id,
                    sender_id,
                    recipient_id,
                    text,
                    sentAt,
                    readFlag
                FROM messages
                WHERE recipient_id = ?
                ORDER BY sentAt DESC
                """;

        List<Message> messages = new ArrayList<>();

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setLong(1, recipientId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении сообщений получателя",
                    e
            );
        }

        return messages;
    }

    @Override
    public List<Message> getByUserId(long userId) {

        String sql = """
                SELECT
                    id,
                    ad_id,
                    sender_id,
                    recipient_id,
                    text,
                    sentAt,
                    readFlag
                FROM messages
                WHERE sender_id = ?
                   OR recipient_id = ?
                ORDER BY sentAt DESC
                """;

        List<Message> messages = new ArrayList<>();

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при получении сообщений пользователя",
                    e
            );
        }

        return messages;
    }

    @Override
    public void markAsRead(long id) {

        String sql = """
                UPDATE messages
                SET readFlag = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                     impl.getConnection().prepareStatement(sql)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Ошибка при отметке сообщения как прочитанного",
                    e
            );
        }
    }

    private Message mapMessage(ResultSet rs)
            throws SQLException {

        Message message = new Message();

        message.setId(rs.getLong("id"));

        Ad ad = new Ad();
        ad.setId(rs.getLong("ad_id"));
        message.setAd(ad);

        User sender = new User();
        sender.setId(rs.getLong("sender_id"));
        message.setSender(sender);

        User recipient = new User();
        recipient.setId(rs.getLong("recipient_id"));
        message.setRecipient(recipient);

        message.setText(rs.getString("text"));

        Timestamp sentAt =
                rs.getTimestamp("sentAt");

        if (sentAt != null) {
            message.setSentAt(
                    sentAt.toLocalDateTime()
            );
        }

        message.setReadFlag(
                rs.getBoolean("readFlag")
        );

        return message;
    }
}