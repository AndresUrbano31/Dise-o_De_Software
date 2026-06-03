package com.universidad.domain.entities;

import com.universidad.domain.valueobjects.Destinatario;
import com.universidad.domain.valueobjects.EstadoNotificacion;
import com.universidad.domain.valueobjects.MedioEnvio;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "NotificacionApp"?
//   "Notificacion" → hereda de la clase base
//   "App"          → su medio es la aplicación móvil
//
// Representa el tipo de notificación push en la app universitaria.
// Su INFORMACIÓN ADICIONAL es el tipo de alerta que clasifica
// visualmente la notificación dentro de la app.
// Su manera PARTICULAR de enviar es usando Firebase Cloud Messaging.
// ─────────────────────────────────────────────────────────────────
public class NotificacionApp extends Notificacion {

    // INFORMACIÓN ADICIONAL exclusiva de App Móvil.
    // tipoAlerta clasifica la notificación visualmente en la app:
    //   "INFO"    → notificación informativa (calificaciones)
    //   "ALERTA"  → requiere atención (cancelación de clase)
    //   "URGENTE" → acción inmediata requerida (vencimiento de pago)
    private String tipoAlerta;
    private MedioEnvio medio;

    public NotificacionApp(String codigo, Destinatario destinatario,
                           String mensaje, String tipoAlerta) {
        // Inicializa los atributos comunes a través del padre
        super(codigo, destinatario, mensaje);

        // Atributos propios de App
        this.tipoAlerta = tipoAlerta;
        this.medio = MedioEnvio.APP_MOVIL;
    }

    // ─── POLIMORFISMO ─────────────────────────────────────────────
    // La manera particular de App de enviar.
    // En un sistema real aquí iría: Firebase Admin SDK, etc.
    @Override
    public void enviar() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║      🔔 ENVIANDO POR APP MÓVIL           ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ Código      : " + getCodigo());
        System.out.println("║ Dispositivo : " + getDestinatario().getIdDispositivo());
        System.out.println("║ Tipo Alerta : " + tipoAlerta);
        System.out.println("║ Mensaje     : " + getMensaje());
        System.out.println("║ Fecha       : " + getFechaEnvio());
        System.out.println("╚══════════════════════════════════════════╝");

        // Actualiza el estado a ENVIADO
        setEstado(EstadoNotificacion.ENVIADO);
        System.out.println("✅ Estado actualizado: " + getEstado());
        System.out.println();
    }

    public String getTipoAlerta() { return tipoAlerta; }
    public MedioEnvio getMedio()  { return medio; }
}