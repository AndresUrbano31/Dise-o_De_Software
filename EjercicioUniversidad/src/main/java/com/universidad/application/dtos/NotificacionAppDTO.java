package com.universidad.application.dtos;

import com.universidad.domain.valueobjects.TipoSituacion;
// DTO = Data Transfer Object (Objeto de Transferencia de Datos)
//Es un objeto simple que solo transporta datos de un lugar a otro. No tiene lógica, no tiene reglas, no hace nada. Solo lleva información.
// DTO específico para notificaciones por App Móvil.
// Tiene los datos del destinatario propios de este medio:
// el idDispositivo (asignado por Firebase al instalar la app)
// y el tipoAlerta (clasifica visualmente la notificación en la app).
public class NotificacionAppDTO {

    private String nombreDestinatario;
    private String idDispositivo; // específico de App — ID Firebase del celular
    private String tipoAlerta;   // específico de App — "INFO", "ALERTA", "URGENTE"
    private String mensaje;
    private TipoSituacion tipoSituacion;

    public NotificacionAppDTO(String nombreDestinatario, String idDispositivo,
                              String tipoAlerta, String mensaje,
                              TipoSituacion tipoSituacion) {
        this.nombreDestinatario = nombreDestinatario;
        this.idDispositivo = idDispositivo;
        this.tipoAlerta = tipoAlerta;
        this.mensaje = mensaje;
        this.tipoSituacion = tipoSituacion;
    }

    public String getNombreDestinatario()   { return nombreDestinatario; }
    public String getIdDispositivo()        { return idDispositivo; }
    public String getTipoAlerta()           { return tipoAlerta; }
    public String getMensaje()              { return mensaje; }
    public TipoSituacion getTipoSituacion() { return tipoSituacion; }
}