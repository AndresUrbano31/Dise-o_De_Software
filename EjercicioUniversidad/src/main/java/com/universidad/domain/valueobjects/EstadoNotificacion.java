package com.universidad.domain.valueobjects;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué es un "enum" y no una clase normal?
// Porque el estado solo puede ser uno de un conjunto FIJO de valores.
// Un enum garantiza que nadie pueda inventar estados inválidos
// como "enviando", "Enviado", "ENVIADO" o "enviadoo".
// Solo existen exactamente los valores que definimos aquí.
//
// ¿Por qué se llama "EstadoNotificacion"?
// El nombre describe exactamente qué representa:
//   "Estado" → en qué punto del proceso está
//   "Notificacion" → de qué objeto es el estado
// El enunciado dice "registrar estado" → esto lo implementa.
//
// ¿Por qué está en "valueobjects"?
// Porque el estado es un valor que describe a la notificación,
// no tiene identidad propia. Es parte de los datos del negocio.
// ─────────────────────────────────────────────────────────────────
public enum EstadoNotificacion {

    // La notificación fue creada pero aún no se ha intentado enviar.
    // Toda notificación nace con este estado (lo asigna el constructor).
    PENDIENTE,

    // El envío se realizó correctamente por el medio correspondiente.
    // El sender lo asigna cuando el envío termina sin errores.
    ENVIADO,

    // Ocurrió un error durante el envío y no llegó al destinatario.
    // El sender lo asigna cuando ocurre una excepción o fallo.
    // Este estado habilita la regla de reenvío en NotificacionDomainService.
    FALLIDO
}