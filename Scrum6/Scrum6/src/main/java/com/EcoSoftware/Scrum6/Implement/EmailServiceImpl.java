package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void enviarCorreo(String to, String subject, String htmlContent) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que es HTML

            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar correo HTML: " + e.getMessage());
        }
    }

    @Override
    public void enviarCorreosMasivos(List<String> recipients, String subject, String htmlContent) {
        if (recipients == null || recipients.isEmpty()) return;

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(from);

            String[] destinatariosArray = recipients.toArray(new String[0]);
            helper.setTo(from); // visible
            helper.setBcc(destinatariosArray); // ocultos

            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML

            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar correo masivo HTML: " + e.getMessage());
        }
    }
}
