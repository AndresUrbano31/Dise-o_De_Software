package com.universidad.application.usecases;

import com.universidad.application.dtos.NotificacionSMSDTO;
import com.universidad.application.ports.NotificacionSenderPort;
import com.universidad.domain.entities.NotificacionSMS;
import com.universidad.domain.services.NotificacionDomainService;
import com.universidad.domain.valueobjects.Destinatario;

// Caso de uso para enviar notificaciones por SMS.
// Misma estructura que EmailUseCase, cambia:
//   - el DTO de entrada (NotificacionSMSDTO)
//   - la entidad que construye (NotificacionSMS)
//   - los datos del Destinatario (teléfono en lugar de email)
public class EnviarNotificacionSMSUseCase {

    private final NotificacionSenderPort sender;
    private final NotificacionDomainService domainService;

    public EnviarNotificacionSMSUseCase(NotificacionSenderPort sender,
                                        NotificacionDomainService domainService) {
        this.sender = sender;
        this.domainService = domainService;
    }

    public void ejecutar(NotificacionSMSDTO dto) {

        // PASO 1 — Validar reglas de negocio
        if (!domainService.esMensajeValido(dto.getMensaje())) {
            System.out.println("❌ Error: el mensaje no puede estar vacío.");
            return;
        }

        // PASO 2 — Construir Destinatario con teléfono (dato clave de SMS)
        Destinatario destinatario = new Destinatario(
                dto.getNombreDestinatario(),
                null,  // email no aplica para SMS
                dto.getTelefonoDestinatario(),
                null   // idDispositivo no aplica para SMS
        );

        // PASO 3 — Crear la entidad con su información adicional de SMS
        NotificacionSMS notificacion = new NotificacionSMS(
                generarCodigo("SMS"),
                destinatario,
                dto.getMensaje(),
                dto.getOperadora()
        );

        // PASO 4 — Enviar usando el port
        sender.enviar(notificacion);
    }

    private String generarCodigo(String medio) {
        return "NOT-" + medio + "-" + System.currentTimeMillis();
    }
}