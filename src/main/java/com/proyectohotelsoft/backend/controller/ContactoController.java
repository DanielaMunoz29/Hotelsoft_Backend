package com.proyectohotelsoft.backend.controller;


import com.proyectohotelsoft.backend.dto.ContactenosDTO;
import com.proyectohotelsoft.backend.dto.EmailDTO;
import com.proyectohotelsoft.backend.services.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/contactenos")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://hotelsoftback-1495464507.northamerica-northeast1.run.app")
public class ContactoController {

    @Autowired
    private final ContactoService contactoService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody ContactenosDTO request) {
        try {
            contactoService.enviarCorreo(request);
            return ResponseEntity.ok("Correo enviado con éxito");
        } catch (MailException e) {
            // Excepciones de envío o conexión
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el correo: " + e.getMessage());
        } catch (Exception e) {
            // Cualquier otro error inesperado
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al enviar el correo.");
        }
    }

}