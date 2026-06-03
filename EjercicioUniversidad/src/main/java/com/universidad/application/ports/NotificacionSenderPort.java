package com.universidad.application.ports;

import com.universidad.domain.entities.Notificacion;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "ports"?
// En DDD un Port (puerto) es una puerta de salida entre la capa de aplicación
// y el mundo exterior (infraestructura). Define QUÉ necesita
// hacer la aplicación sin saber CÓMO lo hace la tecnología concreta.
//
// ¿Por qué se llama "NotificacionSenderPort"?
//   "Notificacion" → el objeto que se va a enviar
//   "Sender"       → quien tiene la responsabilidad de enviar
//   "Port"         → es una puerta/contrato entre capas (DDD)
//
// ¿Por qué es una "interface" y no una clase?
// Porque solo define el CONTRATO (qué debe hacerse), no la
// implementación (cómo hacerse). Cada medio de envío implementa
// este contrato a su manera. Esto es lo que permite el polimorfismo.
//
// ¿Por qué está en "application" y no en "domain"?
// Porque el port coordina la comunicación entre la lógica de
// aplicación (casos de uso) y la infraestructura (senders).
// No es una regla de negocio pura, es un contrato técnico.
//
// ¿CÓMO SOPORTA EL CRECIMIENTO?
// El enunciado dice "preparado para crecer con nuevos medios".
// Si mañana llega WhatsApp, solo se crea WhatsAppSender que
// implemente esta interface. Los casos de uso NO se tocan.
// ─────────────────────────────────────────────────────────────────
public interface NotificacionSenderPort {

    // Contrato que TODOS los medios de envío deben cumplir.
    // Recibe la notificación completa para que el sender
    // pueda acceder a todos sus datos (destinatario, mensaje, etc.)
    // y ejecutar el envío según su tecnología particular.
    //
    // POLIMORFISMO EN ACCIÓN:
    // EmailSender.enviar()    → conecta a SMTP
    // SMSSender.enviar()      → llama API de mensajería
    // AppPushSender.enviar()  → usa Firebase
    // Mismo nombre, comportamiento diferente en cada uno.
    void enviar(Notificacion notificacion);
}