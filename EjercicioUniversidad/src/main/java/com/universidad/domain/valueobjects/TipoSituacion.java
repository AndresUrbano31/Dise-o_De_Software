package com.universidad.domain.valueobjects;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué es un "enum"?
// Las situaciones que maneja la universidad son FIJAS y conocidas.
// El enunciado las lista explícitamente: son exactamente 4.
// Un enum garantiza que solo existan esas 4 situaciones válidas.
//
// ¿Por qué se llama "TipoSituacion"?
//   "Tipo" → categoría o clasificación
//   "Situacion" → el evento universitario que genera la notificación
// El enunciado dice "distintos tipos de notificaciones según
// la situación" → esto lo implementa.
//
// ¿Por qué está en "valueobjects"?
// La situación es un valor descriptivo que clasifica la notificación.
// No tiene identidad propia, solo indica qué evento la generó.
//
// DIFERENCIA CLAVE con MedioEnvio:
//   TipoSituacion → responde ¿QUÉ se comunica? (el contenido)
//   MedioEnvio    → responde ¿CÓMO se comunica? (el canal)
// ─────────────────────────────────────────────────────────────────
public enum TipoSituacion {

    // "Publicación de calificaciones"
    // Se envía cuando el docente publica las notas del período.
    PUBLICACION_CALIFICACIONES,

    // "Recordatorio de pago de matrícula"
    // Se envía cuando se acerca la fecha límite de pago.
    RECORDATORIO_PAGO_MATRICULA,

    // "Aviso de cancelación de clase"
    // Se envía cuando un docente cancela una clase programada.
    AVISO_CANCELACION_CLASE,

    // "Confirmación de inscripción a eventos académicos"
    // Se envía cuando el estudiante se inscribe exitosamente a un evento.
    CONFIRMACION_INSCRIPCION_EVENTO
}