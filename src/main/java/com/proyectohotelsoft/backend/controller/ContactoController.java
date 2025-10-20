package com.proyectohotelsoft.backend.controller;


import com.proyectohotelsoft.backend.dto.EmailDTO;
import com.proyectohotelsoft.backend.services.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/contactenos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactoController {

    @Autowired
    private final ContactoService contactoService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO request) {

        contactoService.enviarCorreo(request);
        return ResponseEntity.ok("Correo enviado con éxito");
    }

}