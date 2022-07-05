package com.fadyfaheem;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mail {
    public static void sendMail(String recipient, String body){
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        String email = MySQL.getSenderEmail();
        String password = MySQL.getSenderPassword();

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        Message message = prepareMessage(session, email, recipient, body);

        ExecutorService emailExecutor = Executors.newCachedThreadPool();
        // I literally had to create a whole ass thread just for executing the email.
        // it's honestly slightly absurd. It takes almost 4 secs to send an email.
        emailExecutor.execute(() -> {
            try {
                if (MySQL.areAllReqMetToSendEmail()) {
                    Transport.send(message);
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static Message prepareMessage(Session session, String machineEmail, String receiverEmail, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(machineEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            message.setSubject("Regarding Vending Machine Located At " + MySQL.getLocationForEmail());
            message.setText(body);
            return message;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
