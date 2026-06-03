package com.universidad.infrastructure.senders;

import com.universidad.application.ports.NotificacionSenderPort;
import com.universidad.domain.entities.Notificacion;

// Sender concreto para SMS.
// Implementa el contrato del port para el medio de mensaje de texto.
// En un sistema real aquí iría: Twilio API, AWS SNS, Vonage, etc.
public class SMSSender implements NotificacionSenderPort {

    @Override
    public void enviar(Notificacion notificacion) {
        // En un sistema real:
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(new PhoneNumber(telefono), ...).create();
        //
        // El método enviar() de NotificacionSMS maneja la lógica de presentación
        notificacion.enviar();
    }
}