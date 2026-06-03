package com.universidad.domain.services;

import com.universidad.domain.entities.Notificacion;
import com.universidad.domain.valueobjects.EstadoNotificacion;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "services"?
// En DDD un Domain Service contiene reglas de negocio que no
// pertenecen a una sola entidad. Cuando una regla involucra
// varias clases o es demasiado compleja para vivir en una entidad,
// va aquí.
//
// ¿Por qué se llama "NotificacionDomainService"?
//   "Notificacion" → las reglas son sobre notificaciones
//   "Domain"       → las reglas son del negocio, no de tecnología
//   "Service"      → es un servicio que orquesta reglas
//
// ¿Por qué está en "domain"?
// Porque sus reglas nacen del enunciado del negocio universitario,
// no de ninguna tecnología. No importa si usamos MySQL o MongoDB,
// estas reglas siempre aplican.
// ─────────────────────────────────────────────────────────────────
public class NotificacionDomainService {

    // ─── REGLA 1 ──────────────────────────────────────────────────
    // ¿De dónde viene esta regla?
    // El enunciado pide registrar el "estado" de cada notificación.
    // Si el sistema registra estados, es para tomar decisiones con ellos.
    // La decisión más lógica: solo reintentar lo que falló.
    //
    // ¿Por qué no está en la clase Notificacion directamente?
    // Porque mañana la universidad podría agregar condiciones:
    // "solo reenviar si falló Y si han pasado menos de 24 horas"
    // Esa complejidad no debe vivir dentro de la entidad.
    // El Service está preparado para crecer con esas reglas.
    public boolean puedeReenviarse(Notificacion notificacion) {
        // Una notificación solo puede reenviarse si su estado es FALLIDO.
        // PENDIENTE → aún no se ha intentado, no aplica reenvío.
        // ENVIADO   → ya llegó al destinatario, no necesita reenvío.
        // FALLIDO   → algo salió mal, tiene sentido intentarlo de nuevo.
        return notificacion.getEstado() == EstadoNotificacion.FALLIDO;
    }

    // ─── REGLA 2 ──────────────────────────────────────────────────
    // ¿De dónde viene esta regla?
    // El enunciado define 4 situaciones con mensajes reales e importantes.
    // Un mensaje vacío en cualquiera de esas situaciones sería un error
    // grave: el estudiante recibiría una notificación sin información.
    // Además, enviar mensajes vacíos desperdicia recursos (SMS tiene costo).
    //
    // ¿Por qué no está en Notificacion directamente?
    // Porque la validación podría crecer:
    // "válido si no está vacío Y tiene mínimo 10 caracteres Y no supera 500"
    // Esa complejidad pertenece al Service, no a la entidad.
    public boolean esMensajeValido(String mensaje) {
        // mensaje != null      → verifica que el mensaje exista
        // .trim()              → elimina espacios al inicio y al final
        // .isEmpty()           → pregunta si quedó vacío después del trim
        // !                    → lo negamos porque queremos que NO esté vacío
        // Casos que rechaza:
        //   null        → no existe
        //   ""          → vacío
        //   "    "      → solo espacios, después del trim queda vacío
        return mensaje != null && !mensaje.trim().isEmpty();
    }
}