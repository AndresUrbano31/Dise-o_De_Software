package com.universidad.infrastructure.senders;

import com.universidad.application.ports.NotificacionSenderPort;
import com.universidad.domain.entities.Notificacion;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "senders"?
// Son las clases que realmente SABEN CÓMO enviar por cada medio.
// "Sender" en inglés = "enviador". Cada uno es el especialista
// de su medio de comunicación.
//
// ¿Por qué se llama "EmailSender"?
//   "Email"  → el medio que sabe manejar
//   "Sender" → su rol es enviar por ese medio
//
// ¿Por qué está en "infrastructure"?
// Porque aquí vive la tecnología concreta: SMTP, APIs, Firebase.
// La infraestructura es todo lo que depende de tecnología externa.
//
// ¿Por qué "implements NotificacionSenderPort"?
// Porque el port define el contrato que TODOS los senders deben
// cumplir. Esto es lo que hace posible el polimorfismo:
// el caso de uso solo conoce el port, no el sender concreto.
//
// ¿CÓMO SOPORTA EL CRECIMIENTO?
// Si mañana llega WhatsApp:
//   1. Crear WhatsAppSender implements NotificacionSenderPort
//   2. Implementar enviar() con la API de WhatsApp
//   3. Este archivo NO se toca. Ningún otro archivo se toca.
// ─────────────────────────────────────────────────────────────────
public class EmailSender implements NotificacionSenderPort {

    // @Override → indica que este método implementa el del port.
    // Esta es la implementación CONCRETA y PARTICULAR del email.
    // El enunciado dice "ejecutar el envío de manera particular" → esto.
    @Override
    public void enviar(Notificacion notificacion) {
        // En un sistema real aquí iría la conexión SMTP:
        // Session session = Session.getInstance(props, authenticator);
        // Message message = new MimeMessage(session);
        // Transport.send(message);
        //
        // El método enviar() de la entidad ya maneja la impresión
        // y actualización del estado, el sender solo lo invoca.
        notificacion.enviar();
    }
}