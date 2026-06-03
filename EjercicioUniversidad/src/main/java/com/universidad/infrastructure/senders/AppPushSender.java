package com.universidad.infrastructure.senders;

import com.universidad.application.ports.NotificacionSenderPort;
import com.universidad.domain.entities.Notificacion;

// Sender concreto para App Móvil.
// Implementa el contrato del port para notificaciones push.
// En un sistema real aquí iría: Firebase Admin SDK, OneSignal, etc.
public class AppPushSender implements NotificacionSenderPort {

    @Override
    public void enviar(Notificacion notificacion) {
        // En un sistema real:
        // FirebaseApp.initializeApp(options);
        // FirebaseMessaging.getInstance().send(message);
        //
        // El método enviar() de NotificacionApp maneja la lógica de presentación
        notificacion.enviar();
    }
}