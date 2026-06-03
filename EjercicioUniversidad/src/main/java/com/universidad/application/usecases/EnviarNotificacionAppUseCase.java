package com.universidad.application.usecases;

import com.universidad.application.dtos.NotificacionAppDTO;
import com.universidad.application.ports.NotificacionSenderPort;
import com.universidad.domain.entities.NotificacionApp;
import com.universidad.domain.services.NotificacionDomainService;
import com.universidad.domain.valueobjects.Destinatario;

// Caso de uso para enviar notificaciones por App Móvil.
// Misma estructura que los anteriores, cambia:
//   - el DTO de entrada (NotificacionAppDTO)
//   - la entidad que construye (NotificacionApp)
//   - los datos del Destinatario (idDispositivo en lugar de email/teléfono)
public class EnviarNotificacionAppUseCase {

    private final NotificacionSenderPort sender;
    private final NotificacionDomainService domainService;

    public EnviarNotificacionAppUseCase(NotificacionSenderPort sender,
                                        NotificacionDomainService domainService) {
        this.sender = sender;
        this.domainService = domainService;
    }

    public void ejecutar(NotificacionAppDTO dto) {

        // PASO 1 — Validar reglas de negocio
        if (!domainService.esMensajeValido(dto.getMensaje())) {
            System.out.println("❌ Error: el mensaje no puede estar vacío.");
            return;
        }

        // PASO 2 — Construir Destinatario con idDispositivo (dato clave de App)
        Destinatario destinatario = new Destinatario(
                dto.getNombreDestinatario(),
                null,  // email no aplica para App
                null,  // teléfono no aplica para App
                dto.getIdDispositivo()
        );

        // PASO 3 — Crear la entidad con su información adicional de App
        NotificacionApp notificacion = new NotificacionApp(
                generarCodigo("APP"),
                destinatario,
                dto.getMensaje(),
                dto.getTipoAlerta()
        );

        // PASO 4 — Enviar usando el port
        sender.enviar(notificacion);
    }

    private String generarCodigo(String medio) {
        return "NOT-" + medio + "-" + System.currentTimeMillis();
    }
}