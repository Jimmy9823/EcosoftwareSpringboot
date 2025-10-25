package com.EcoSoftware.Scrum6.Service;

import java.util.List;

public interface EmailService {
    /** Envía un correo individual. */
    void enviarCorreo(String para, String asunto, String contenido);

    /** Envía el mismo correo a una lista de destinatarios. */
    void enviarCorreosMasivos(List<String> recipients, String subject, String text);
}


