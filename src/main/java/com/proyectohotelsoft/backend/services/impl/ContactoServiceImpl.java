package com.proyectohotelsoft.backend.services.impl;

import com.proyectohotelsoft.backend.dto.EmailDTO;
import com.proyectohotelsoft.backend.services.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactoServiceImpl implements ContactoService {


    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:no-reply@hotelsoft.com}")
    private String fromEmail;

    @Override
    public void enviarCorreo(EmailDTO emailDTO) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailDTO.getDestinatario());
        message.setSubject(emailDTO.getAsunto());
        message.setText(emailDTO.getCuerpo());

        javaMailSender.send(message);
    }
}