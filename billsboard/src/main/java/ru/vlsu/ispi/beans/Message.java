package ru.vlsu.ispi.beans;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(nullable = false, length = 2000)
    private String text;

    @Column(name = "sentAt")
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(name = "readFlag", nullable = false)
    private boolean readFlag = false;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public Ad getAd() {
        return ad;
    }


    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }


    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getRecipient() {
        return recipient;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }


    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    public boolean getReadFlag() {
        return readFlag;
    }

    public boolean isReadFlag() {
        return readFlag;
    }
}