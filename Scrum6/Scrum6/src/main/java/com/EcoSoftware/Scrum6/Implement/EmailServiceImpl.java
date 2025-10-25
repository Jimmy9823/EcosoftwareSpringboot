package com.EcoSoftware.Scrum6.Implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.EcoSoftware.Scrum6.Service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarCorreo(String para, String asunto, String contenido) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(contenido);
        mailSender.send(mensaje);
    }

    @Override
    public void enviarCorreosMasivos(List<String> recipients, String subject, String text) {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        for (String recipient : recipients) {
            try {
                // Reutiliza el método individual para asegurar que si uno falla, no detenga a los demás.
                enviarCorreo(recipient, subject, text); 
            } catch (MailException e) {
                // Manejar o registrar el error para el destinatario específico
                // En un entorno productivo, podrías usar un logger aquí.
                System.err.println("Error al enviar correo masivo a: " + recipient + ". Motivo: " + e.getMessage());
            }
        }
    }
}
