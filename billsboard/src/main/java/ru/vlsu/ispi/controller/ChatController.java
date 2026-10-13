package ru.vlsu.ispi.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.vlsu.ispi.beans.Ad;
import ru.vlsu.ispi.beans.Message;
import ru.vlsu.ispi.beans.User;
import ru.vlsu.ispi.dao.AdDAO;
import ru.vlsu.ispi.dao.MessageDAO;
import ru.vlsu.ispi.dao.UserDAO;

@Controller
public class ChatController {

    private final MessageDAO messageDAO;
    private final UserDAO userDAO;
    private final AdDAO adDAO;

    public ChatController(
            MessageDAO messageDAO,
            UserDAO userDAO,
            AdDAO adDAO) {

        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
        this.adDAO = adDAO;
    }

    /*
     * Список существующих чатов
     */
    @GetMapping("/chat")
    public String chats(
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

        List<Message> messages =
                messageDAO.getByUserId(currentUser.getId());

        List<ChatItem> chats = new ArrayList<>();

        for (Message message : messages) {

            long adId = message.getAd().getId();

            ChatItem existingChat = null;

            for (ChatItem chat : chats) {
                if (chat.getAdId() == adId) {
                    existingChat = chat;
                    break;
                }
            }

            /*
             * Сообщения идут от новых к старым.
             * Поэтому первое сообщение для объявления
             * является последним сообщением чата.
             */
            if (existingChat != null) {
                continue;
            }

            User otherUser;

            if (message.getSender().getId()
                    .equals(currentUser.getId())) {

                otherUser = userDAO.getById(
                        message.getRecipient().getId()
                );

            } else {

                otherUser = userDAO.getById(
                        message.getSender().getId()
                );
            }

            String userName = "Пользователь";

            if (otherUser != null) {

                userName = otherUser.getName();

                if (otherUser.getSecondName() != null
                        && !otherUser.getSecondName().isBlank()) {

                    userName += " "
                            + otherUser.getSecondName();
                }
            }

            ChatItem chat = new ChatItem();

            chat.setAdId(adId);
            chat.setUserId(
                    otherUser != null
                            ? otherUser.getId()
                            : 0
            );
            chat.setUserName(userName);
            chat.setLastMessage(message.getText());
            chat.setLastMessageTime(message.getSentAt());

            chats.add(chat);
        }

        model.addAttribute("chats", chats);

        return "chat";
    }


    /*
     * Открытие конкретного чата
     */
    @GetMapping("/chat/{adId}")
    public String openChat(
            @PathVariable("adId") long adId,
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

        Ad ad = adDAO.getById(adId);

        if (ad == null) {
            return "redirect:/chat";
        }

        List<Message> messages =
                messageDAO.getByAdId(adId);

        /*
         * Проверяем, что текущий пользователь
         * действительно участвует в этом чате.
         */
        boolean hasAccess = false;

        for (Message message : messages) {

            if (message.getSender().getId()
                    .equals(currentUser.getId())
                    || message.getRecipient().getId()
                    .equals(currentUser.getId())) {

                hasAccess = true;
                break;
            }
        }

        /*
         * Если сообщений нет или пользователь
         * не участвует в переписке — доступ запрещён.
         */
        if (!hasAccess) {
            return "redirect:/chat";
        }

        /*
         * Определяем собеседника.
         */
        User otherUser = null;

        for (Message message : messages) {

            if (message.getSender().getId()
                    .equals(currentUser.getId())) {

                otherUser = userDAO.getById(
                        message.getRecipient().getId()
                );

            } else {

                otherUser = userDAO.getById(
                        message.getSender().getId()
                );
            }

            if (otherUser != null) {
                break;
            }
        }

        /*
         * Отмечаем входящие сообщения как прочитанные.
         */
        for (Message message : messages) {

            if (message.getRecipient().getId()
                    .equals(currentUser.getId())
                    && !message.getReadFlag()) {

                messageDAO.markAsRead(
                        message.getId()
                );
            }
        }

        String otherUserName = "Пользователь";

        if (otherUser != null) {

            otherUserName = otherUser.getName();

            if (otherUser.getSecondName() != null
                    && !otherUser.getSecondName().isBlank()) {

                otherUserName += " "
                        + otherUser.getSecondName();
            }
        }

        model.addAttribute("ad", ad);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("otherUserName", otherUserName);

        return "chatView";
    }


    /*
     * Отправка сообщения внутри существующего чата
     */
    @PostMapping("/chat/{adId}/message")
    public String sendMessage(
            @PathVariable("adId") long adId,
            @RequestParam("text") String text,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/auth/login";
        }

        User currentUser =
                userDAO.getByEmail(authentication.getName());

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Ad ad = adDAO.getById(adId);

        if (ad == null) {
            return "redirect:/chat";
        }

        List<Message> messages =
                messageDAO.getByAdId(adId);

        User recipient = null;

        /*
         * Находим собеседника среди существующих сообщений.
         */
        for (Message message : messages) {

            if (message.getSender().getId()
                    .equals(currentUser.getId())) {

                recipient = userDAO.getById(
                        message.getRecipient().getId()
                );

                break;
            }

            if (message.getRecipient().getId()
                    .equals(currentUser.getId())) {

                recipient = userDAO.getById(
                        message.getSender().getId()
                );

                break;
            }
        }

        if (recipient == null) {
            return "redirect:/chat/" + adId;
        }

        if (text == null || text.trim().isEmpty()) {
            return "redirect:/chat/" + adId;
        }

        String messageText = text.trim();

        if (messageText.length() > 2000) {
            messageText = messageText.substring(0, 2000);
        }

        Message message = new Message();

        message.setAd(ad);
        message.setSender(currentUser);
        message.setRecipient(recipient);
        message.setText(messageText);
        message.setSentAt(LocalDateTime.now());
        message.setReadFlag(false);

        messageDAO.create(message);

        return "redirect:/chat/" + adId;
    }


    /*
     * DTO для списка чатов
     */
    public static class ChatItem {

        private long adId;
        private long userId;
        private String userName;
        private String lastMessage;
        private LocalDateTime lastMessageTime;

        public long getAdId() {
            return adId;
        }

        public void setAdId(long adId) {
            this.adId = adId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
        }

        public LocalDateTime getLastMessageTime() {
            return lastMessageTime;
        }

        public void setLastMessageTime(
                LocalDateTime lastMessageTime) {

            this.lastMessageTime = lastMessageTime;
        }
    }
}