package com.universidad.application.usecases;

import com.universidad.application.dtos.NotificacionEmailDTO;
import com.universidad.application.ports.NotificacionSenderPort;
import com.universidad.domain.entities.NotificacionEmail;
import com.universidad.domain.services.NotificacionDomainService;
import com.universidad.domain.valueobjects.Destinatario;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "usecases"?
// Un caso de uso representa UNA acción concreta que el sistema
// puede realizar. Es el director de orquesta: coordina el dominio
// y la infraestructura pero no contiene lógica de negocio propia.
//
// ¿Por qué se llama "EnviarNotificacionEmailUseCase"?
//   "Enviar"        → la acción que realiza
//   "Notificacion"  → el objeto sobre el que actúa
//   "Email"         → el medio específico
//   "UseCase"       → indica que es un caso de uso (patrón DDD)
// Sigue el formato: Verbo + Qué + Medio + UseCase
//
// ¿Por qué está en "application"?
// Los casos de uso pertenecen a la capa de aplicación porque
// orquestan la lógica sin ser ni dominio puro ni infraestructura.
// ─────────────────────────────────────────────────────────────────
public class EnviarNotificacionEmailUseCase {

    // Depende del PORT (interface), no del sender concreto.
    // Esto permite cambiar EmailSender por otro sin tocar este usecase.
    private final NotificacionSenderPort sender;

    // El domain service valida las reglas de negocio antes de enviar.
    private final NotificacionDomainService domainService;

    // Constructor — recibe las dependencias desde afuera (inyección de dependencias)
    public EnviarNotificacionEmailUseCase(NotificacionSenderPort sender,
                                          NotificacionDomainService domainService) {
        this.sender = sender;
        this.domainService = domainService;
    }

    // "ejecutar" es el método estándar de los casos de uso en DDD.
    // Recibe el DTO con los datos crudos del exterior.
    public void ejecutar(NotificacionEmailDTO dto) {

        // PASO 1 — Validar las reglas de negocio antes de proceder
        if (!domainService.esMensajeValido(dto.getMensaje())) {
            System.out.println("❌ Error: el mensaje no puede estar vacío.");
            return; // detiene la ejecución si el mensaje no es válido
        }

        // PASO 2 — Construir el Destinatario con los datos del DTO
        // El email tiene email pero no tiene teléfono ni idDispositivo
        Destinatario destinatario = new Destinatario(
                dto.getNombreDestinatario(),
                dto.getEmailDestinatario(),
                null,  // teléfono no aplica para email
                null   // idDispositivo no aplica para email
        );

        // PASO 3 — Crear la entidad NotificacionEmail con su info adicional
        NotificacionEmail notificacion = new NotificacionEmail(
                generarCodigo("EMAIL"),
                destinatario,
                dto.getMensaje(),
                dto.getAsunto(),
                dto.getDireccionCC()
        );

        // PASO 4 — Enviar usando el port (polimorfismo en acción)
        // El sender no sabe qué tipo de notificación es,
        // solo llama a enviar() y la notificación sabe cómo hacerlo.
        sender.enviar(notificacion);
    }

    // Genera un código único simple para la notificación
    private String generarCodigo(String medio) {
        return "NOT-" + medio + "-" + System.currentTimeMillis();
    }
}