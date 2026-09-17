package music.license.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import music.license.dto.colaborador.ColaboradorBeatRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.AcuerdoCreditosEvento;
import music.license.model.Beat;
import music.license.model.ColaboradorBeat;
import music.license.model.EstadoAcuerdo;
import music.license.model.EstadoColaborador;
import music.license.model.TipoEventoAcuerdo;
import music.license.model.TipoNotificacion;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosEventoRepository;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;
import music.license.repository.ColaboradorBeatRepository;
import music.license.repository.UsuarioRepository;
import music.license.security.AutorizacionUtil;

@Service
public class ColaboradorBeatService {

    private final ColaboradorBeatRepository colaboradorBeatRepository;
    private final BeatRepository beatRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcuerdoCreditosRepository acuerdoCreditosRepository;
    private final AcuerdoCreditosEventoRepository acuerdoCreditosEventoRepository;
    private final NotificacionService notificacionService;

    public ColaboradorBeatService(
            ColaboradorBeatRepository colaboradorBeatRepository,
            BeatRepository beatRepository,
            UsuarioRepository usuarioRepository,
            AcuerdoCreditosRepository acuerdoCreditosRepository,
            AcuerdoCreditosEventoRepository acuerdoCreditosEventoRepository,
            NotificacionService notificacionService) {

        this.colaboradorBeatRepository = colaboradorBeatRepository;
        this.beatRepository = beatRepository;
        this.usuarioRepository = usuarioRepository;
        this.acuerdoCreditosRepository = acuerdoCreditosRepository;
        this.acuerdoCreditosEventoRepository = acuerdoCreditosEventoRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Solo ve una fila quien es el productor dueño del beat, o alguno de sus colaboradores
     * (declarados en el mismo beat) - el split de creditos no es informacion publica.
     */
    public List<ColaboradorBeat> obtenerTodosVisibles(Usuario solicitante) {
        return colaboradorBeatRepository.findAll().stream()
                .filter(c -> esVisible(c.getBeat(), solicitante))
                .toList();
    }

    public ColaboradorBeat obtenerPorIdVisible(Long id, Usuario solicitante) {
        ColaboradorBeat colaboradorBeat = obtenerPorId(id);

        if (!esVisible(colaboradorBeat.getBeat(), solicitante)) {
            throw new ResourceNotFoundException("Colaborador no encontrado");
        }

        return colaboradorBeat;
    }

    private boolean esVisible(Beat beat, Usuario solicitante) {
        return AutorizacionUtil.esDuenioOColaborador(
                beat.getProductor(), solicitante, colaboradorBeatRepository.findByBeatId(beat.getId()));
    }

    /**
     * Las invitaciones (en cualquier estado) dirigidas al propio usuario autenticado.
     */
    public List<ColaboradorBeat> obtenerMisInvitaciones(Usuario solicitante, EstadoColaborador estado) {
        List<ColaboradorBeat> todas = colaboradorBeatRepository.findByUsuarioId(solicitante.getId());

        if (estado == null) {
            return todas;
        }

        return todas.stream().filter(c -> c.getEstado() == estado).toList();
    }

    public ColaboradorBeat obtenerPorId(Long id) {
        return colaboradorBeatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador no encontrado"));
    }

    public ColaboradorBeat crear(ColaboradorBeatRequest request, Usuario solicitante) {
        Beat beat = beatRepository.findById(request.getBeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Beat no encontrado"));

        AutorizacionUtil.exigirPropietario(
                beat.getProductor(), solicitante, "Solo el productor dueño del beat puede invitar colaboradores");

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario colaborador no encontrado"));

        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(usuario);
        colaboradorBeat.setRol(request.getRol());
        colaboradorBeat.setPorcentajePropuesto(request.getPorcentajePropuesto());
        // toda invitacion nueva empieza pendiente: la aceptacion es un paso explicito del colaborador
        colaboradorBeat.setEstado(EstadoColaborador.PENDIENTE);

        ColaboradorBeat guardado = colaboradorBeatRepository.save(colaboradorBeat);

        registrarEvento(beat, solicitante, TipoEventoAcuerdo.PROPUESTA,
                "Propuso a " + usuario.getNombre() + " como " + request.getRol()
                        + " con " + request.getPorcentajePropuesto() + "%");

        notificacionService.crear(usuario, TipoNotificacion.INVITACION_COLABORACION,
                solicitante.getNombre() + " te invitó a colaborar en \"" + beat.getTitulo()
                        + "\" como " + request.getRol() + " (" + request.getPorcentajePropuesto() + "%)");

        reabrirAcuerdoYReiniciarAceptaciones(beat, solicitante);

        return guardado;
    }

    public ColaboradorBeat actualizar(Long id, ColaboradorBeatRequest datosActualizados, Usuario solicitante) {
        ColaboradorBeat colaboradorBeat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                colaboradorBeat.getBeat().getProductor(),
                solicitante,
                "Solo el productor dueño del beat puede editar un colaborador");

        colaboradorBeat.setRol(datosActualizados.getRol());
        colaboradorBeat.setPorcentajePropuesto(datosActualizados.getPorcentajePropuesto());
        // cambiar el rol o el porcentaje invalida la aceptacion previa de este colaborador
        colaboradorBeat.setEstado(EstadoColaborador.PENDIENTE);
        // beat y usuario no se reasignan via PUT

        ColaboradorBeat guardado = colaboradorBeatRepository.save(colaboradorBeat);

        registrarEvento(colaboradorBeat.getBeat(), solicitante, TipoEventoAcuerdo.MODIFICACION,
                "Modificó la propuesta de " + colaboradorBeat.getUsuario().getNombre()
                        + " a " + datosActualizados.getRol() + " con " + datosActualizados.getPorcentajePropuesto() + "%");

        reabrirAcuerdoYReiniciarAceptaciones(colaboradorBeat.getBeat(), solicitante);

        return guardado;
    }

    public ColaboradorBeat aceptar(Long id, Usuario solicitante) {
        ColaboradorBeat colaboradorBeat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                colaboradorBeat.getUsuario(), solicitante, "Solo el colaborador invitado puede aceptar esta invitación");

        AcuerdoCreditos acuerdo = acuerdoAbiertoDelBeat(colaboradorBeat.getBeat());

        colaboradorBeat.setEstado(EstadoColaborador.ACEPTADO);
        ColaboradorBeat guardado = colaboradorBeatRepository.save(colaboradorBeat);

        registrarEvento(colaboradorBeat.getBeat(), solicitante, TipoEventoAcuerdo.ACEPTACION,
                "Aceptó su propuesta como " + colaboradorBeat.getRol()
                        + " con " + colaboradorBeat.getPorcentajePropuesto() + "%");

        notificacionService.crear(colaboradorBeat.getBeat().getProductor(), TipoNotificacion.ACEPTACION_COLABORACION,
                solicitante.getNombre() + " aceptó colaborar en \"" + colaboradorBeat.getBeat().getTitulo() + "\"");

        cerrarAcuerdoSiCorresponde(acuerdo, solicitante);

        return guardado;
    }

    public ColaboradorBeat rechazar(Long id, Usuario solicitante) {
        ColaboradorBeat colaboradorBeat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                colaboradorBeat.getUsuario(), solicitante, "Solo el colaborador invitado puede rechazar esta invitación");

        acuerdoAbiertoDelBeat(colaboradorBeat.getBeat());

        colaboradorBeat.setEstado(EstadoColaborador.RECHAZADO);
        ColaboradorBeat guardado = colaboradorBeatRepository.save(colaboradorBeat);

        registrarEvento(colaboradorBeat.getBeat(), solicitante, TipoEventoAcuerdo.RECHAZO,
                "Rechazó su propuesta como " + colaboradorBeat.getRol()
                        + " con " + colaboradorBeat.getPorcentajePropuesto() + "%");

        notificacionService.crear(colaboradorBeat.getBeat().getProductor(), TipoNotificacion.RECHAZO_COLABORACION,
                solicitante.getNombre() + " rechazó colaborar en \"" + colaboradorBeat.getBeat().getTitulo() + "\"");

        return guardado;
    }

    public void eliminar(Long id, Usuario solicitante) {
        ColaboradorBeat colaboradorBeat = obtenerPorId(id);
        AutorizacionUtil.exigirPropietario(
                colaboradorBeat.getBeat().getProductor(),
                solicitante,
                "Solo el productor dueño del beat puede quitar colaboradores");

        Beat beat = colaboradorBeat.getBeat();
        String nombreColaborador = colaboradorBeat.getUsuario().getNombre();

        colaboradorBeatRepository.deleteById(id);

        registrarEvento(beat, solicitante, TipoEventoAcuerdo.ELIMINACION,
                "Quitó a " + nombreColaborador + " del acuerdo");

        reabrirAcuerdoYReiniciarAceptaciones(beat, solicitante);
    }

    /**
     * Busca el acuerdo de creditos del beat y valida que siga abierto.
     * Sin un acuerdo abierto no tiene sentido aceptar/rechazar una propuesta.
     */
    private AcuerdoCreditos acuerdoAbiertoDelBeat(Beat beat) {
        AcuerdoCreditos acuerdo = acuerdoCreditosRepository.findByBeatId(beat.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "El productor todavía no ha abierto el acuerdo de créditos de este beat"));

        if (acuerdo.getEstado() != EstadoAcuerdo.ABIERTO) {
            throw new IllegalStateException("El acuerdo de créditos de este beat ya está cerrado");
        }

        return acuerdo;
    }

    /**
     * Cierra el acuerdo solo si TODOS los colaboradores aceptaron y el split suma exactamente 100%.
     * Si alguien rechazo, o la suma no cuadra, el acuerdo se queda abierto aunque el resto haya aceptado.
     */
    private void cerrarAcuerdoSiCorresponde(AcuerdoCreditos acuerdo, Usuario quienDisparoElCierre) {
        List<ColaboradorBeat> colaboradores = colaboradorBeatRepository.findByBeatId(acuerdo.getBeat().getId());

        boolean todosAceptaron = !colaboradores.isEmpty()
                && colaboradores.stream().allMatch(c -> c.getEstado() == EstadoColaborador.ACEPTADO);

        BigDecimal suma = colaboradores.stream()
                .map(ColaboradorBeat::getPorcentajePropuesto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean sumaCorrecta = suma.compareTo(new BigDecimal("100")) == 0;

        if (todosAceptaron && sumaCorrecta) {
            acuerdo.setEstado(EstadoAcuerdo.CERRADO);
            acuerdo.setFechaCierre(LocalDateTime.now());
            acuerdoCreditosRepository.save(acuerdo);

            registrarEvento(acuerdo.getBeat(), quienDisparoElCierre, TipoEventoAcuerdo.CIERRE,
                    "El acuerdo se cerró: todos los colaboradores aceptaron y el split suma 100%");
        }
    }

    /**
     * "Cualquier cambio reabre el acuerdo y reinicia las aceptaciones" (invitar, editar o quitar
     * un colaborador): si hay un acuerdo de creditos para el beat, vuelve a ABIERTO, y todos los
     * colaboradores que ya habian aceptado o rechazado quedan de nuevo en PENDIENTE.
     */
    private void reabrirAcuerdoYReiniciarAceptaciones(Beat beat, Usuario quienDisparoElCambio) {
        acuerdoCreditosRepository.findByBeatId(beat.getId()).ifPresent(acuerdo -> {
            if (acuerdo.getEstado() == EstadoAcuerdo.CERRADO) {
                acuerdo.setEstado(EstadoAcuerdo.ABIERTO);
                acuerdo.setFechaCierre(null);
                acuerdoCreditosRepository.save(acuerdo);

                registrarEvento(beat, quienDisparoElCambio, TipoEventoAcuerdo.REAPERTURA,
                        "El acuerdo se reabrió por un cambio en los colaboradores; las aceptaciones se reiniciaron");
            }
        });

        List<ColaboradorBeat> colaboradores = colaboradorBeatRepository.findByBeatId(beat.getId());

        for (ColaboradorBeat colaborador : colaboradores) {
            if (colaborador.getEstado() != EstadoColaborador.PENDIENTE) {
                colaborador.setEstado(EstadoColaborador.PENDIENTE);
                colaboradorBeatRepository.save(colaborador);
            }
        }
    }

    private void registrarEvento(Beat beat, Usuario usuario, TipoEventoAcuerdo tipo, String detalle) {
        AcuerdoCreditosEvento evento = new AcuerdoCreditosEvento();
        evento.setBeat(beat);
        evento.setUsuario(usuario);
        evento.setTipo(tipo);
        evento.setDetalle(detalle);
        evento.setFecha(LocalDateTime.now());

        acuerdoCreditosEventoRepository.save(evento);
    }
}
