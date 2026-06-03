package com.universidad.application.dtos;

import com.universidad.domain.valueobjects.TipoSituacion;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "dtos"?
// DTO = Data Transfer Object (Objeto de Transferencia de Datos).
// Son objetos simples que transportan datos desde el exterior
// (el usuario o la interfaz) hacia los casos de uso.
// NO contienen lógica de negocio, solo llevan datos.
//
// ¿Por qué se llama "NotificacionEmailDTO"?
//   "Notificacion" → es para crear una notificación
//   "Email"        → específicamente para el medio email
//   "DTO"          → es un objeto de transferencia de datos
//
// ¿Por qué está en "application"?
// Los DTOs son la entrada a los casos de uso. Pertenecen a la
// capa de aplicación porque son el puente entre la interfaz
// (lo que el usuario ingresa) y la lógica del negocio.
//
// ¿Por qué no usamos directamente la entidad Notificacion?
// Para proteger el dominio. La entidad tiene reglas y validaciones.
// El DTO es solo un contenedor de datos "crudos" que llegan del exterior.
// ─────────────────────────────────────────────────────────────────
public class NotificacionEmailDTO {

    // Datos del destinatario para este medio
    private String nombreDestinatario;
    private String emailDestinatario; // específico de email

    // Datos propios del email que no tienen SMS ni App
    private String asunto;      // línea de asunto del correo
    private String direccionCC; // copia a otra persona (puede ser null)

    // Contenido y clasificación
    private String mensaje;
    private TipoSituacion tipoSituacion; // cuál de las 4 situaciones del enunciado

    // Constructor con todos los datos necesarios para un email
    public NotificacionEmailDTO(String nombreDestinatario, String emailDestinatario,
                                String asunto, String direccionCC,
                                String mensaje, TipoSituacion tipoSituacion) {
        this.nombreDestinatario = nombreDestinatario;
        this.emailDestinatario = emailDestinatario;
        this.asunto = asunto;
        this.direccionCC = direccionCC;
        this.mensaje = mensaje;
        this.tipoSituacion = tipoSituacion;
    }

    // Solo getters — el DTO no debe modificarse una vez creado
    public String getNombreDestinatario() { return nombreDestinatario; }
    public String getEmailDestinatario()  { return emailDestinatario; }
    public String getAsunto()             { return asunto; }
    public String getDireccionCC()        { return direccionCC; }
    public String getMensaje()            { return mensaje; }
    public TipoSituacion getTipoSituacion() { return tipoSituacion; }
}