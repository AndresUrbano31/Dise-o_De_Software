package com.universidad.domain.entities;

import com.universidad.domain.valueobjects.Destinatario;
import com.universidad.domain.valueobjects.EstadoNotificacion;
import java.time.LocalDateTime;


public abstract class Notificacion {


    private String codigo;
    private Destinatario destinatario;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private EstadoNotificacion estado;

    public Notificacion(String codigo, Destinatario destinatario, String mensaje) {
        this.codigo = codigo;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.fechaEnvio = LocalDateTime.now();
        this.estado = EstadoNotificacion.PENDIENTE;
    }


    public abstract void enviar();

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public Destinatario getDestinatario() { return destinatario; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public EstadoNotificacion getEstado() { return estado; }
    public void setEstado(EstadoNotificacion estado) { this.estado = estado; }
}