package com.proyectohotelsoft.backend.controller;

import com.mercadopago.resources.preference.Preference;
import com.proyectohotelsoft.backend.dto.ChangePasswordDTO;
import com.proyectohotelsoft.backend.dto.LimpiezaDto;
import com.proyectohotelsoft.backend.dto.RegisterUserDTO;
import com.proyectohotelsoft.backend.dto.UserDTO;
import com.proyectohotelsoft.backend.services.LimpiezaService;
import com.proyectohotelsoft.backend.services.ReservaService;
import com.proyectohotelsoft.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las operaciones relacionadas con usuarios
 * Proporciona endpoints para administrar usuarios del sistema
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://hotelsoftback-1495464507.northamerica-northeast1.run.app")
public class UserController {

    private final UserService userService;

    private final LimpiezaService limpiezaService;
    private final ReservaService reservaService;

    /**
     * Constructor que recibe las dependencias necesarias
     *
     * @param userService    Servicio para operaciones de usuarios
     * @param reservaService
     */
    public UserController(UserService userService, LimpiezaService limpiezaService, ReservaService reservaService) {
        this.userService = userService;
        this.limpiezaService = limpiezaService;
        this.reservaService = reservaService;
    }

    /**
     * Obtiene todos los usuarios del sistema
     * 
     * @return Lista completa de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca un usuario por su número de cédula
     * 
     * @param cedula Número de cédula del usuario
     * @return Información del usuario encontrado
     */
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<?> getUserByCedula(@PathVariable("cedula") String cedula) {
        try {
            UserDTO user = userService.getUserByCedula(cedula);
            return ResponseEntity.ok(user);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca un usuario por su dirección de email
     * 
     * @param email Dirección de email del usuario
     * @return Información del usuario encontrado
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        try {
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crea un nuevo usuario en el sistema
     * 
     * @param registerUserDTO Datos del usuario a crear
     * @return Mensaje de confirmación
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUserDTO registerUserDTO) {
        try {
            userService.registerUser(registerUserDTO);
            return ResponseEntity.ok("Usuario creado exitosamente");
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualiza la información de un usuario usando su cédula
     * 
     * @param cedula  Cédula del usuario a actualizar
     * @param userDTO Nuevos datos del usuario
     * @return Usuario actualizado
     */
    @PutMapping("/cedula/{cedula}")
    public ResponseEntity<?> updateUserByCedula(@PathVariable("cedula") String cedula, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUserByCedula(cedula, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualiza la información de un usuario usando su email
     * 
     * @param email   Email del usuario a actualizar
     * @param userDTO Nuevos datos del usuario
     * @return Usuario actualizado
     */
    @PutMapping("/email/{email}")
    public ResponseEntity<?> updateUserByEmail(@PathVariable("email") String email, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUserByEmail(email, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Cambia el rol de un usuario usando su cédula
     * 
     * @param cedula Cédula del usuario
     * @param role   Nuevo rol a asignar
     * @return Usuario con el rol actualizado
     */
    @PatchMapping("/cedula/{cedula}/role")
    public ResponseEntity<?> updateUserRoleByCedula(@PathVariable("cedula") String cedula, @RequestBody String role) {
        try {
            UserDTO updatedUser = userService.updateUserRoleByCedula(cedula, role);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Activa un usuario usando su cédula
     * 
     * @param cedula Cédula del usuario a activar
     * @return Usuario activado
     */
    @PatchMapping("/cedula/{cedula}/enable")
    public ResponseEntity<?> enableUserByCedula(@PathVariable("cedula") String cedula) {
        try {
            UserDTO enabledUser = userService.enableUserByCedula(cedula);
            return ResponseEntity.ok(enabledUser);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Desactiva un usuario usando su cédula
     * 
     * @param cedula Cédula del usuario a desactivar
     * @return Usuario desactivado
     */
    @PatchMapping("/cedula/{cedula}/disable")
    public ResponseEntity<?> disableUserByCedula(@PathVariable("cedula") String cedula) {
        try {
            UserDTO disabledUser = userService.disableUserByCedula(cedula);
            return ResponseEntity.ok(disabledUser);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Elimina un usuario del sistema usando su cédula
     * 
     * @param cedula Cédula del usuario a eliminar
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/cedula/{cedula}")
    public ResponseEntity<?> deleteUserByCedula(@PathVariable("cedula") String cedula) {
        try {
            userService.deleteUserByCedula(cedula);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Verifica si existe un usuario con el email especificado
     * 
     * @param email Email a verificar
     * @return true si existe, false si no existe
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable("email") String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return ResponseEntity.ok(exists);
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Verifica si existe un usuario con la cédula especificada
     * 
     * @param cedula Cédula a verificar
     * @return true si existe, false si no existe
     */
    @GetMapping("/exists/cedula/{cedula}")
    public ResponseEntity<Boolean> checkCedulaExists(@PathVariable("cedula") String cedula) {
        try {
            boolean exists = userService.existsByCedula(cedula);
            return ResponseEntity.ok(exists);
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Cambia la contraseña de un usuario usando su cédula
     * 
     * @param cedula            Cédula del usuario
     * @param changePasswordDTO Objeto con el token y nueva contraseña
     * @return Mensaje de confirmación
     */
    @PatchMapping("/cedula/{cedula}/change-password")
    public ResponseEntity<?> changePasswordByCedula(
            @PathVariable("cedula") String cedula,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            // Validar longitud mínima de contraseña
            if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres");
            }

            // Aquí puedes validar el token si es necesario
            userService.changePasswordByCedula(cedula, changePasswordDTO.getNewPassword());
            return ResponseEntity.ok("Contraseña cambiada exitosamente");
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Cambia la contraseña de un usuario usando su email
     * 
     * @param email             Email del usuario
     * @param changePasswordDTO Objeto con el token y nueva contraseña
     * @return Mensaje de confirmación
     */
    @PatchMapping("/email/{email}/change-password")
    public ResponseEntity<?> changePasswordByEmail(
            @PathVariable("email") String email,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            // Validar longitud mínima de contraseña
            if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres");
            }

            // Aquí puedes validar el token si es necesario
            userService.changePasswordByEmail(email, changePasswordDTO.getNewPassword());
            return ResponseEntity.ok("Contraseña cambiada exitosamente");
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @PostMapping("/registrarLimpieza")
    public ResponseEntity<LimpiezaDto> registrarLimpieza(@RequestBody LimpiezaDto dto) {
        return ResponseEntity.ok(limpiezaService.registrarLimpieza(dto));
    }

    @GetMapping("/ListarLimpiezas")
    public ResponseEntity<List<LimpiezaDto>> listarLimpiezas() {
        return ResponseEntity.ok(limpiezaService.listarLimpiezas());
    }
    @GetMapping("/listarLimpiezaByusuario/{userId}")
    public ResponseEntity<List<LimpiezaDto>> listarLimpiezasPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(limpiezaService.listarLimpiezasPorUsuario(userId));
    }
    // 🔹 Actualizar una limpieza
    @PutMapping("/editarLimpieza/{id}")
    public ResponseEntity<LimpiezaDto> actualizarLimpieza(
            @PathVariable Long id,
            @RequestBody LimpiezaDto limpiezaDto) {
        LimpiezaDto actualizado = limpiezaService.actualizarLimpieza(id, limpiezaDto);
        return ResponseEntity.ok(actualizado);
    }


    @PostMapping("/realizar-pago")
    public ResponseEntity<Preference> realizarPago(@RequestParam("idReserva") String idReserva) throws Exception {
        return ResponseEntity.ok(reservaService.realizarPagoReserva(idReserva));
    }

    }