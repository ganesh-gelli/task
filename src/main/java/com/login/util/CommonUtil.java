package com.login.util;

import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

@Service
public class CommonUtil {

      public static void sendEmail(String to, String userMessage, String subject, String from) throws UnknownHostException, MessagingException {
          String host = "smtp.mailtrap.io";
          Properties props = new Properties();
          props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.starttls.enable", "true");
          props.put("mail.smtp.host", host);
          props.put("mail.smtp.port", "2525");
          props.put("mail.smtp.ssl.protocols", "TLSv1.2");
          Session session = Session.getInstance(props,
                  new javax.mail.Authenticator() {
                      protected PasswordAuthentication getPasswordAuthentication() {
                          return new PasswordAuthentication("c0a2b12cdf855e", "644f192d4d6e80");
                      }
                  });
          try {
              Message message = new MimeMessage(session);
              message.setFrom(new InternetAddress(from));
              message.setRecipients(Message.RecipientType.TO,
                      InternetAddress.parse(to));
              message.setSubject(subject);
              message.setText(userMessage);
              Transport.send(message);
          } catch (MessagingException e) {
              throw new RuntimeException(e);
          }
    }

    public static String genearteOTP(int len) {
        String numbers = "0123456789";
        Random randomMethod = new Random();
        char[] otp = new char[len];
        for (int i = 0; i < len; i++) {
            otp[i] = numbers.charAt(randomMethod.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    public static String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

}
