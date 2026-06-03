package com.universidad.domain.entities;

import com.universidad.domain.valueobjects.Destinatario;
import com.universidad.domain.valueobjects.EstadoNotificacion;
import com.universidad.domain.valueobjects.MedioEnvio;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "NotificacionEmail"?
//   "Notificacion" → hereda de la clase base, es una notificación
//   "Email"        → su medio de envío es el correo electrónico
// Sigue el patrón: tipo de notificación + medio.
//
// ¿Por qué "extends Notificacion"?
// El enunciado dice "distintos tipos de notificaciones".
// Esta es la implementación de "tipo Email". Hereda todos los
// atributos comunes (codigo, destinatario, mensaje, fechaEnvio, estado)
// y agrega su INFORMACIÓN ADICIONAL propia.
// El enunciado dice "manejar información adicional" → esto.
//
// ¿Por qué está en "domain/entities"?
// Porque NotificacionEmail ES una entidad del negocio.
// Tiene identidad propia (hereda el codigo único) y representa
// un concepto real del dominio universitario.
// ─────────────────────────────────────────────────────────────────
public class NotificacionEmail extends Notificacion {

    // INFORMACIÓN ADICIONAL exclusiva del email.
    // El enunciado dice "información adicional según el medio" → estos.
    private String asunto;      // línea de asunto del correo
    private String direccionCC; // copia a otro destinatario (opcional)
    private MedioEnvio medio;   // identifica que esta notificación es EMAIL

    // Constructor: llama al constructor padre con "super()" para
    // inicializar los atributos comunes, luego agrega los propios.
    public NotificacionEmail(String codigo, Destinatario destinatario,
                             String mensaje, String asunto, String direccionCC) {
        // super() llama al constructor de Notificacion (la clase padre)
        // para inicializar: codigo, destinatario, mensaje, fechaEnvio, estado
        super(codigo, destinatario, mensaje);

        // Luego inicializa los atributos propios de Email
        this.asunto = asunto;
        this.direccionCC = direccionCC;
        this.medio = MedioEnvio.EMAIL;
    }

    // ─── POLIMORFISMO ─────────────────────────────────────────────
    // @Override indica que este método reemplaza al abstracto de Notificacion.
    // Esta es "la manera particular" del Email de enviar.
    // El enunciado dice "ejecutar el envío de manera particular" → esto.
    @Override
    public void enviar() {
        // En un sistema real aquí iría: JavaMail, SendGrid API, etc.
        // Por ahora simulamos el envío con mensajes en consola.
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         📧 ENVIANDO POR EMAIL            ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ Código    : " + getCodigo());
        System.out.println("║ Para      : " + getDestinatario().getEmail());
        System.out.println("║ Asunto    : " + asunto);
        if (direccionCC != null) {
            System.out.println("║ CC        : " + direccionCC);
        }
        System.out.println("║ Mensaje   : " + getMensaje());
        System.out.println("║ Fecha     : " + getFechaEnvio());
        System.out.println("╚══════════════════════════════════════════╝");

        // Actualiza el estado a ENVIADO al terminar exitosamente
        setEstado(EstadoNotificacion.ENVIADO);
        System.out.println("✅ Estado actualizado: " + getEstado());
        System.out.println();
    }

    // Getters de los atributos adicionales
    public String getAsunto()      { return asunto; }
    public String getDireccionCC() { return direccionCC; }
    public MedioEnvio getMedio()   { return medio; }
}