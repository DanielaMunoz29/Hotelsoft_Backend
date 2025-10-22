package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.ContactenosDTO;
import com.proyectohotelsoft.backend.services.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactoServiceImpl implements ContactoService {


    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:no-reply@hotelsoft.com}")
    private String fromEmail;


    @Override
    public void enviarCorreo(ContactenosDTO emailDTO) {
        List<String> correos = List.of("nicolas.echeverryf@uqvirtual.edu.co",
                                       "andresf.manriquef@uqvirtual.edu.co",
                                       "daniela.munoza1@uqvirtual.edu.co");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(correos.toArray(new String[0]));
            message.setSubject(emailDTO.asunto());

            String cuerpo = String.format(
                """
                Hay un nuevo mensaje de parte de un cliente:

                Nombre: %s
                Correo: %s
                Mensaje: %s
                """, emailDTO.nombre(), emailDTO.correo(), emailDTO.mensaje()
            );
            message.setText(cuerpo);

            javaMailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("No se pudo enviar el correo. Verifique la configuración SMTP o el destinatario.", e);
        }
    }
}