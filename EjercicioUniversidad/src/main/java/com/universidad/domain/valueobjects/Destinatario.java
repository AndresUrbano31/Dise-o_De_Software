package com.universidad.domain.valueobjects;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "valueobjects"?
// En DDD un Value Object es un objeto que NO tiene identidad propia.
// No importa "cuál Destinatario" sino "qué datos de contacto tiene".
// Si dos destinatarios tienen el mismo email y teléfono, son iguales.
// A diferencia de una entidad, no tiene un ID que lo identifique.
//
// ¿Por qué se llama "Destinatario"?
// Porque el enunciado dice "según el destinatario".
// Es la persona que recibe la notificación.
// Agrupa TODOS sus datos de contacto en un solo objeto en lugar
// de tener los datos sueltos dentro de Notificacion.
//
// ¿Por qué está en "domain"?
// Porque el concepto de "destinatario" pertenece al negocio
// de la universidad, no a ninguna tecnología externa.
// ─────────────────────────────────────────────────────────────────
public class Destinatario {

    // El nombre de la persona para personalizar el mensaje.
    // Ej: "Estimado Juan Pérez, sus notas están disponibles"
    private String nombre;

    // Dirección de correo. La necesita NotificacionEmail para enviar.
    // Puede ser null si el estudiante no tiene email registrado.
    private String email;

    // Número celular. Lo necesita NotificacionSMS para enviar.
    // Puede ser null si el estudiante no tiene teléfono registrado.
    private String telefono;

    // ID único que Firebase asigna al celular cuando instala la app.
    // Lo necesita NotificacionApp para saber a qué dispositivo enviar.
    // El enunciado menciona "notificación en aplicación móvil" → esto.
    // Puede ser null si el estudiante no tiene la app instalada.
    private String idDispositivo;

    // ─── CONSTRUCTOR ──────────────────────────────────────────────
    // Recibe todos los datos de contacto de una vez.
    // Un Destinatario nace completo y no cambia.
    // Si los datos cambian → se crea un Destinatario nuevo.
    public Destinatario(String nombre, String email,
                        String telefono, String idDispositivo) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.idDispositivo = idDispositivo;
    }

    // ─── SOLO GETTERS ─────────────────────────────────────────────
    // ¿Por qué no hay setters?
    // Destinatario es un Value Object → es INMUTABLE.
    // Los datos de contacto que se registraron al crear la
    // notificación no deben cambiar. Si cambiaran, se perdería
    // el registro histórico de a quién se le envió qué y cuándo.
    // Eso afectaría la trazabilidad y auditoría del sistema.

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }
}