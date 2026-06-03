package com.universidad.domain.entities;

import com.universidad.domain.valueobjects.Destinatario;
import com.universidad.domain.valueobjects.EstadoNotificacion;
import com.universidad.domain.valueobjects.MedioEnvio;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "NotificacionSMS"?
//   "Notificacion" → hereda de la clase base
//   "SMS"          → su medio es el mensaje de texto
//
// Representa el tipo de notificación por mensaje de texto.
// Su INFORMACIÓN ADICIONAL es la operadora del destinatario.
// Su manera PARTICULAR de enviar es usando una API de mensajería.
// ─────────────────────────────────────────────────────────────────
public class NotificacionSMS extends Notificacion {

    // INFORMACIÓN ADICIONAL exclusiva del SMS.
    // La operadora es relevante porque algunos sistemas de mensajería
    // manejan rutas diferentes según si es Claro, Movistar, etc.
    private String operadora;
    private MedioEnvio medio;

    public NotificacionSMS(String codigo, Destinatario destinatario,
                           String mensaje, String operadora) {
        // Inicializa los atributos comunes a través del padre
        super(codigo, destinatario, mensaje);

        // Atributos propios de SMS
        this.operadora = operadora;
        this.medio = MedioEnvio.SMS;
    }

    // ─── POLIMORFISMO ─────────────────────────────────────────────
    // La manera particular del SMS de enviar.
    // En un sistema real aquí iría: Twilio API, AWS SNS, etc.
    @Override
    public void enviar() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         📱 ENVIANDO POR SMS              ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ Código    : " + getCodigo());
        System.out.println("║ Teléfono  : " + getDestinatario().getTelefono());
        System.out.println("║ Operadora : " + operadora);
        System.out.println("║ Mensaje   : " + getMensaje());
        System.out.println("║ Fecha     : " + getFechaEnvio());
        System.out.println("╚══════════════════════════════════════════╝");

        // Actualiza el estado a ENVIADO
        setEstado(EstadoNotificacion.ENVIADO);
        System.out.println("✅ Estado actualizado: " + getEstado());
        System.out.println();
    }

    public String getOperadora()  { return operadora; }
    public MedioEnvio getMedio()  { return medio; }
}