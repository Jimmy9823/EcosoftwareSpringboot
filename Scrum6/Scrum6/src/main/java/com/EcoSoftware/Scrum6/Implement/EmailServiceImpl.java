package com.EcoSoftware.Scrum6.Implement;


import com.EcoSoftware.Scrum6.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void enviarCorreo(String to, String subject, String text) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(from);
            mensaje.setTo(to);
            mensaje.setSubject(subject);
            mensaje.setText(text);
            mailSender.send(mensaje);
        } catch (MailException e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }
    }

    @Override
    public void enviarCorreosMasivos(List<String> recipients, String subject, String text) {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(from);

            // Convertir lista a array
            String[] destinatariosArray = recipients.toArray(new String[0]);

            // Destinatario "visible" (puede ser tu propio correo o el mismo 'from')
            mensaje.setTo(from);

            // TODOS los demás van en copia oculta
            mensaje.setBcc(destinatariosArray);

            mensaje.setSubject(subject);
            mensaje.setText(text);

            mailSender.send(mensaje);
        } catch (MailException e) {
            System.err.println("Error al enviar email masivo: " + e.getMessage());
        }
    }
}
