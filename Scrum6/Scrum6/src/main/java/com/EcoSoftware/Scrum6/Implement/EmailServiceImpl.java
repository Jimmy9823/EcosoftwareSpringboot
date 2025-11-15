package com.EcoSoftware.Scrum6.Implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.EcoSoftware.Scrum6.Service.EmailService;

import jakarta.mail.internet.MimeMessage;
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarCorreo(String para, String asunto, String contenidoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(para);
            helper.setSubject(asunto);

            // IMPORTANTE: true -> indica que es HTML
            helper.setText(contenidoHtml, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    @Override
    public void enviarCorreosMasivos(List<String> recipients, String subject, String contenidoHtml) {
        for (String r : recipients) {
            try {
                enviarCorreo(r, subject, contenidoHtml);
            } catch (Exception e) {
                System.err.println("Error al enviar a: " + r);
            }
        }
    }
}
