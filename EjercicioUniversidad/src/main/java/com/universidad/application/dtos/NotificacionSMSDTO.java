package com.universidad.application.dtos;

import com.universidad.domain.valueobjects.TipoSituacion;

// DTO específico para notificaciones por SMS.
// Tiene los datos del destinatario propios de este medio:
// el número de teléfono y la operadora.
// No tiene asunto ni CC porque eso es exclusivo del email.
public class NotificacionSMSDTO {

    private String nombreDestinatario;
    private String telefonoDestinatario; // específico de SMS
    private String operadora;            // específico de SMS (Claro, Movistar, etc.)
    private String mensaje;
    private TipoSituacion tipoSituacion;

    public NotificacionSMSDTO(String nombreDestinatario, String telefonoDestinatario,
                              String operadora, String mensaje,
                              TipoSituacion tipoSituacion) {
        this.nombreDestinatario = nombreDestinatario;
        this.telefonoDestinatario = telefonoDestinatario;
        this.operadora = operadora;
        this.mensaje = mensaje;
        this.tipoSituacion = tipoSituacion;
    }

    public String getNombreDestinatario()    { return nombreDestinatario; }
    public String getTelefonoDestinatario()  { return telefonoDestinatario; }
    public String getOperadora()             { return operadora; }
    public String getMensaje()               { return mensaje; }
    public TipoSituacion getTipoSituacion()  { return tipoSituacion; }
}