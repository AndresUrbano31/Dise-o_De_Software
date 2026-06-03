package com.universidad.domain.valueobjects;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué es un "enum"?
// Porque los medios de envío son un conjunto FIJO definido por
// el enunciado. Solo existen exactamente estos 3 medios:
// correo electrónico, mensaje de texto y notificación en app.
// Un enum impide que alguien invente medios no existentes.
//
// ¿Por qué se llama "MedioEnvio"?
//   "Medio" → el canal de comunicación
//   "Envio" → que se usa para enviar notificaciones
// Refleja exactamente el lenguaje del enunciado:
// "la universidad puede comunicarse por diferentes medios"
//
// ¿Por qué está en "valueobjects"?
// El medio de envío es un valor descriptivo, no tiene identidad
// propia. Es parte de la información del negocio universitario.
// ─────────────────────────────────────────────────────────────────
public enum MedioEnvio {

    // Correo electrónico → implementado por NotificacionEmail y EmailSender
    EMAIL,

    // Mensaje de texto → implementado por NotificacionSMS y SMSSender
    SMS,

    // Notificación en aplicación móvil → implementado por NotificacionApp y AppPushSender
    APP_MOVIL
}