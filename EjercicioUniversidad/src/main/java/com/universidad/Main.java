package com.universidad;

// ─── IMPORTS DE CASOS DE USO ──────────────────────────────────────
// Importamos los 3 casos de uso, uno por cada medio de envío.
// Cada uno orquesta el flujo completo para su medio específico.
import com.universidad.application.usecases.EnviarNotificacionEmailUseCase;
import com.universidad.application.usecases.EnviarNotificacionSMSUseCase;
import com.universidad.application.usecases.EnviarNotificacionAppUseCase;

// ─── IMPORTS DE DTOs ──────────────────────────────────────────────
// Los DTOs transportan los datos del usuario hacia los casos de uso.
// Cada medio tiene su propio DTO con su información adicional.
import com.universidad.application.dtos.NotificacionEmailDTO;
import com.universidad.application.dtos.NotificacionSMSDTO;
import com.universidad.application.dtos.NotificacionAppDTO;

// ─── IMPORTS DE SENDERS (INFRAESTRUCTURA) ─────────────────────────
// Las implementaciones concretas de cada medio de envío.
// Implementan NotificacionSenderPort → permiten el polimorfismo.
import com.universidad.infrastructure.senders.EmailSender;
import com.universidad.infrastructure.senders.SMSSender;
import com.universidad.infrastructure.senders.AppPushSender;

// ─── IMPORTS DEL DOMINIO ──────────────────────────────────────────
// El Domain Service con las reglas de negocio del sistema.
import com.universidad.domain.services.NotificacionDomainService;

// Los enums que representan las situaciones del enunciado.
import com.universidad.domain.valueobjects.TipoSituacion;

// ─────────────────────────────────────────────────────────────────
// ¿Por qué se llama "Main"?
// Es el punto de entrada estándar de Java. Todo programa Java
// necesita una clase con un método "public static void main".
// Es la primera clase que ejecuta la JVM (Java Virtual Machine).
//
// ¿Por qué está directamente en "com.universidad" y no en un paquete?
// Porque es el punto de arranque del sistema completo. No pertenece
// a ninguna capa específica (domain, application, infrastructure).
// Desde aquí se ensamblan todas las piezas y se inicia la ejecución.
//
// En DDD, este ensamblado se llama "Composition Root":
// el lugar donde se conectan todas las dependencias.
// ─────────────────────────────────────────────────────────────────
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     SISTEMA DE NOTIFICACIONES — UNIVERSIDAD             ║");
        System.out.println("║     Aplicando: DDD + Polimorfismo                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // ─── ENSAMBLAJE DE DEPENDENCIAS (Composition Root) ────────
        // Creamos UNA sola instancia del Domain Service.
        // Contiene las reglas de negocio que se comparten
        // entre todos los casos de uso.
        NotificacionDomainService domainService = new NotificacionDomainService();

        // Creamos los senders concretos de cada medio.
        // Cada uno implementa NotificacionSenderPort → polimorfismo.
        // Los casos de uso solo ven el "port", no el sender concreto.
        EmailSender    emailSender    = new EmailSender();
        SMSSender      smsSender      = new SMSSender();
        AppPushSender  appPushSender  = new AppPushSender();

        // Creamos los casos de uso inyectando sus dependencias.
        // Nota: el caso de uso recibe el PORT (interface), no el sender concreto.
        // Esto significa que podríamos cambiar EmailSender por otro sender
        // sin modificar el caso de uso. Eso es lo que garantiza el crecimiento.
        EnviarNotificacionEmailUseCase emailUseCase =
                new EnviarNotificacionEmailUseCase(emailSender, domainService);

        EnviarNotificacionSMSUseCase smsUseCase =
                new EnviarNotificacionSMSUseCase(smsSender, domainService);

        EnviarNotificacionAppUseCase appUseCase =
                new EnviarNotificacionAppUseCase(appPushSender, domainService);

        // ══════════════════════════════════════════════════════════
        // SITUACIÓN 1 — PUBLICACIÓN DE CALIFICACIONES
        // El enunciado dice: "Publicación de calificaciones"
        // Demostramos los 3 medios para la misma situación.
        // ══════════════════════════════════════════════════════════
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  SITUACIÓN 1: PUBLICACIÓN DE CALIFICACIONES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // Por EMAIL → información adicional: asunto, CC
        emailUseCase.ejecutar(new NotificacionEmailDTO(
                "Juan Pérez",
                "juan.perez@universidad.edu.co",
                "Calificaciones Disponibles — Semestre 2024-2",
                "coordinacion@universidad.edu.co",
                "Estimado Juan Pérez, sus calificaciones del semestre 2024-2 " +
                        "ya están disponibles en el portal académico.",
                TipoSituacion.PUBLICACION_CALIFICACIONES
        ));

        // Por SMS → información adicional: operadora
        smsUseCase.ejecutar(new NotificacionSMSDTO(
                "Juan Pérez",
                "3001234567",
                "Claro",
                "Tus calificaciones del semestre 2024-2 ya están disponibles. " +
                        "Ingresa al portal académico.",
                TipoSituacion.PUBLICACION_CALIFICACIONES
        ));

        // Por APP → información adicional: tipoAlerta
        appUseCase.ejecutar(new NotificacionAppDTO(
                "Juan Pérez",
                "fX3kP9mQ2rL8vN1wZqA5",
                "INFO",
                "📊 Tus calificaciones ya están publicadas. ¡Revísalas!",
                TipoSituacion.PUBLICACION_CALIFICACIONES
        ));

        // ══════════════════════════════════════════════════════════
        // SITUACIÓN 2 — RECORDATORIO DE PAGO DE MATRÍCULA
        // El enunciado dice: "Recordatorio de pago de matrícula"
        // ══════════════════════════════════════════════════════════
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  SITUACIÓN 2: RECORDATORIO DE PAGO DE MATRÍCULA");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // Por EMAIL
        emailUseCase.ejecutar(new NotificacionEmailDTO(
                "María López",
                "maria.lopez@universidad.edu.co",
                "⚠️ Recordatorio: Pago de Matrícula vence el 31 de Enero",
                null, // sin CC en este caso
                "Estimada María López, le recordamos que el plazo para el pago " +
                        "de matrícula del semestre 2025-1 vence el 31 de enero de 2025.",
                TipoSituacion.RECORDATORIO_PAGO_MATRICULA
        ));

        // Por SMS
        smsUseCase.ejecutar(new NotificacionSMSDTO(
                "María López",
                "3109876543",
                "Movistar",
                "URGENTE: El pago de tu matrícula vence el 31/01. " +
                        "Evita recargos. Universidad.",
                TipoSituacion.RECORDATORIO_PAGO_MATRICULA
        ));

        // Por APP
        appUseCase.ejecutar(new NotificacionAppDTO(
                "María López",
                "aB2cD3eF4gH5iJ6kL7mN",
                "URGENTE",
                "💰 Tu matrícula vence el 31 de enero. ¡Paga a tiempo!",
                TipoSituacion.RECORDATORIO_PAGO_MATRICULA
        ));

        // ══════════════════════════════════════════════════════════
        // SITUACIÓN 3 — AVISO DE CANCELACIÓN DE CLASE
        // El enunciado dice: "Aviso de cancelación de clase"
        // ══════════════════════════════════════════════════════════
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  SITUACIÓN 3: AVISO DE CANCELACIÓN DE CLASE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // Por EMAIL
        emailUseCase.ejecutar(new NotificacionEmailDTO(
                "Carlos Ruiz",
                "carlos.ruiz@universidad.edu.co",
                "Cancelación de Clase — Ingeniería de Software — Martes 21 Enero",
                null,
                "Estimado Carlos Ruiz, le informamos que la clase de Ingeniería " +
                        "de Software del martes 21 de enero ha sido cancelada. " +
                        "Se reprogramará próximamente.",
                TipoSituacion.AVISO_CANCELACION_CLASE
        ));

        // Por SMS
        smsUseCase.ejecutar(new NotificacionSMSDTO(
                "Carlos Ruiz",
                "3157894561",
                "Tigo",
                "La clase de Ing. Software del martes 21/01 fue CANCELADA. " +
                        "Se reprogramará. Universidad.",
                TipoSituacion.AVISO_CANCELACION_CLASE
        ));

        // Por APP
        appUseCase.ejecutar(new NotificacionAppDTO(
                "Carlos Ruiz",
                "nO8pQ9rS0tU1vW2xY3zA",
                "ALERTA",
                "🚫 Clase cancelada: Ing. Software — Martes 21/01. Se reprogramará.",
                TipoSituacion.AVISO_CANCELACION_CLASE
        ));

        // ══════════════════════════════════════════════════════════
        // SITUACIÓN 4 — CONFIRMACIÓN DE INSCRIPCIÓN A EVENTOS
        // El enunciado dice: "Confirmación de inscripción a eventos académicos"
        // ══════════════════════════════════════════════════════════
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  SITUACIÓN 4: CONFIRMACIÓN DE INSCRIPCIÓN A EVENTO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // Por EMAIL
        emailUseCase.ejecutar(new NotificacionEmailDTO(
                "Ana Gómez",
                "ana.gomez@universidad.edu.co",
                "✅ Inscripción Confirmada — Congreso de Innovación Tecnológica 2025",
                "eventos@universidad.edu.co",
                "Estimada Ana Gómez, su inscripción al Congreso de Innovación " +
                        "Tecnológica 2025 ha sido confirmada. El evento se realizará " +
                        "el 15 de febrero en el Auditorio Principal.",
                TipoSituacion.CONFIRMACION_INSCRIPCION_EVENTO
        ));

        // Por SMS
        smsUseCase.ejecutar(new NotificacionSMSDTO(
                "Ana Gómez",
                "3204567890",
                "Claro",
                "¡Inscripción confirmada! Congreso Innovación Tecnológica 2025 — " +
                        "15 Feb, Auditorio Principal. Universidad.",
                TipoSituacion.CONFIRMACION_INSCRIPCION_EVENTO
        ));

        // Por APP
        appUseCase.ejecutar(new NotificacionAppDTO(
                "Ana Gómez",
                "bC4dE5fG6hI7jK8lM9nO",
                "INFO",
                "🎉 ¡Inscripción confirmada! Congreso Innov. Tec. 2025 — 15 Feb.",
                TipoSituacion.CONFIRMACION_INSCRIPCION_EVENTO
        ));

        // ══════════════════════════════════════════════════════════
        // DEMOSTRACIÓN DE REGLA DE NEGOCIO — MENSAJE VACÍO
        // Prueba que el Domain Service rechaza mensajes inválidos.
        // ══════════════════════════════════════════════════════════
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  PRUEBA DE REGLA DE NEGOCIO: MENSAJE VACÍO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // Intentamos enviar un SMS con mensaje vacío.
        // El Domain Service debe rechazarlo antes de crear la notificación.
        System.out.println("Intentando enviar SMS con mensaje vacío...");
        smsUseCase.ejecutar(new NotificacionSMSDTO(
                "Pedro Silva",
                "3001112233",
                "Movistar",
                "   ", // mensaje con solo espacios → inválido
                TipoSituacion.AVISO_CANCELACION_CLASE
        ));

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   Sistema ejecutado correctamente.                      ║");
        System.out.println("║   4 situaciones × 3 medios = 12 notificaciones          ║");
        System.out.println("║   Polimorfismo aplicado: mismo enviar(), 3 formas        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}