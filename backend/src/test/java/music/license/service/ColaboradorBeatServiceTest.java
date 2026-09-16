package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import music.license.dto.colaborador.ColaboradorBeatRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.AcuerdoCreditos;
import music.license.model.AcuerdoCreditosEvento;
import music.license.model.Beat;
import music.license.model.ColaboradorBeat;
import music.license.model.EstadoAcuerdo;
import music.license.model.EstadoColaborador;
import music.license.model.RolColaborador;
import music.license.model.TipoEventoAcuerdo;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosEventoRepository;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;
import music.license.repository.ColaboradorBeatRepository;
import music.license.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ColaboradorBeatServiceTest {

    @Mock
    private ColaboradorBeatRepository colaboradorBeatRepository;

    @Mock
    private BeatRepository beatRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AcuerdoCreditosRepository acuerdoCreditosRepository;

    @Mock
    private AcuerdoCreditosEventoRepository acuerdoCreditosEventoRepository;

    @InjectMocks
    private ColaboradorBeatService colaboradorBeatService;

    @Captor
    private ArgumentCaptor<AcuerdoCreditosEvento> eventoCaptor;

    private Beat beat;
    private Usuario productor;
    private Usuario otroUsuario;
    private Usuario invitado;

    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setId(1L);

        otroUsuario = new Usuario();
        otroUsuario.setId(99L);

        beat = new Beat();
        beat.setId(1L);
        beat.setProductor(productor);

        invitado = new Usuario();
        invitado.setId(2L);
        invitado.setNombre("Vocalista Invitado");
    }

    @Test
    void crear_conDuenioDelBeat_quedaPendienteYPersiste() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(2L);
        request.setRol(RolColaborador.VOCALISTA);
        request.setPorcentajePropuesto(new BigDecimal("20"));

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(invitado));
        when(colaboradorBeatRepository.save(any(ColaboradorBeat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(colaboradorBeatRepository.findByBeatId(1L)).thenReturn(List.of());
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.empty());

        ColaboradorBeat resultado = colaboradorBeatService.crear(request, productor);

        assertThat(resultado.getBeat()).isEqualTo(beat);
        assertThat(resultado.getUsuario()).isEqualTo(invitado);
        assertThat(resultado.getEstado()).isEqualTo(EstadoColaborador.PENDIENTE);

        verify(acuerdoCreditosEventoRepository).save(eventoCaptor.capture());
        AcuerdoCreditosEvento evento = eventoCaptor.getValue();
        assertThat(evento.getTipo()).isEqualTo(TipoEventoAcuerdo.PROPUESTA);
        assertThat(evento.getUsuario()).isEqualTo(productor);
        assertThat(evento.getBeat()).isEqualTo(beat);
    }

    @Test
    void crear_conAcuerdoYaCerrado_loReabreYReiniciaLosDemas() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(2L);
        request.setRol(RolColaborador.VOCALISTA);
        request.setPorcentajePropuesto(new BigDecimal("20"));

        ColaboradorBeat existente = new ColaboradorBeat();
        existente.setId(7L);
        existente.setEstado(EstadoColaborador.ACEPTADO);

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setEstado(EstadoAcuerdo.CERRADO);
        acuerdo.setBeat(beat);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(invitado));
        when(colaboradorBeatRepository.save(any(ColaboradorBeat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(colaboradorBeatRepository.findByBeatId(1L)).thenReturn(List.of(existente));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));

        colaboradorBeatService.crear(request, productor);

        assertThat(acuerdo.getEstado()).isEqualTo(EstadoAcuerdo.ABIERTO);
        assertThat(acuerdo.getFechaCierre()).isNull();
        assertThat(existente.getEstado()).isEqualTo(EstadoColaborador.PENDIENTE);
        verify(acuerdoCreditosRepository).save(acuerdo);
        verify(colaboradorBeatRepository).save(existente);
    }

    @Test
    void crear_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(2L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        assertThatThrownBy(() -> colaboradorBeatService.crear(request, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(colaboradorBeatRepository, never()).save(any());
    }

    @Test
    void crear_conBeatInexistente_lanzaResourceNotFoundException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(99L);
        request.setUsuarioId(2L);

        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.crear(request, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crear_conUsuarioInvitadoInexistente_lanzaResourceNotFoundException() {
        ColaboradorBeatRequest request = new ColaboradorBeatRequest();
        request.setBeatId(1L);
        request.setUsuarioId(99L);

        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.crear(request, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void aceptar_conAcuerdoAbiertoYSumaIncompleta_quedaAceptadoPeroNoCierraElAcuerdo() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(invitado);
        colaboradorBeat.setEstado(EstadoColaborador.PENDIENTE);
        colaboradorBeat.setPorcentajePropuesto(new BigDecimal("50"));

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setBeat(beat);
        acuerdo.setEstado(EstadoAcuerdo.ABIERTO);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));
        when(colaboradorBeatRepository.save(colaboradorBeat)).thenReturn(colaboradorBeat);
        when(colaboradorBeatRepository.findByBeatId(1L)).thenReturn(List.of(colaboradorBeat));

        ColaboradorBeat resultado = colaboradorBeatService.aceptar(5L, invitado);

        assertThat(resultado.getEstado()).isEqualTo(EstadoColaborador.ACEPTADO);
        assertThat(acuerdo.getEstado()).isEqualTo(EstadoAcuerdo.ABIERTO);
        verify(acuerdoCreditosRepository, never()).save(acuerdo);
    }

    @Test
    void aceptar_ultimoColaboradorConSuma100_cierraElAcuerdo() {
        ColaboradorBeat colaborador1 = new ColaboradorBeat();
        colaborador1.setId(5L);
        colaborador1.setBeat(beat);
        colaborador1.setUsuario(invitado);
        colaborador1.setEstado(EstadoColaborador.PENDIENTE);
        colaborador1.setPorcentajePropuesto(new BigDecimal("40"));

        ColaboradorBeat colaborador2 = new ColaboradorBeat();
        colaborador2.setId(6L);
        colaborador2.setEstado(EstadoColaborador.ACEPTADO);
        colaborador2.setPorcentajePropuesto(new BigDecimal("60"));

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setBeat(beat);
        acuerdo.setEstado(EstadoAcuerdo.ABIERTO);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaborador1));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));
        when(colaboradorBeatRepository.save(colaborador1)).thenReturn(colaborador1);
        when(colaboradorBeatRepository.findByBeatId(1L)).thenReturn(List.of(colaborador1, colaborador2));

        colaboradorBeatService.aceptar(5L, invitado);

        assertThat(acuerdo.getEstado()).isEqualTo(EstadoAcuerdo.CERRADO);
        assertThat(acuerdo.getFechaCierre()).isNotNull();
        verify(acuerdoCreditosRepository).save(acuerdo);

        verify(acuerdoCreditosEventoRepository, times(2)).save(eventoCaptor.capture());
        List<TipoEventoAcuerdo> tipos = eventoCaptor.getAllValues().stream()
                .map(AcuerdoCreditosEvento::getTipo)
                .toList();
        assertThat(tipos).containsExactly(TipoEventoAcuerdo.ACEPTACION, TipoEventoAcuerdo.CIERRE);
    }

    @Test
    void aceptar_conSumaDistintaA100AunqueTodosAceptaron_noCierraElAcuerdo() {
        ColaboradorBeat colaborador1 = new ColaboradorBeat();
        colaborador1.setId(5L);
        colaborador1.setBeat(beat);
        colaborador1.setUsuario(invitado);
        colaborador1.setEstado(EstadoColaborador.PENDIENTE);
        colaborador1.setPorcentajePropuesto(new BigDecimal("40"));

        ColaboradorBeat colaborador2 = new ColaboradorBeat();
        colaborador2.setId(6L);
        colaborador2.setEstado(EstadoColaborador.ACEPTADO);
        colaborador2.setPorcentajePropuesto(new BigDecimal("50"));

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setBeat(beat);
        acuerdo.setEstado(EstadoAcuerdo.ABIERTO);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaborador1));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));
        when(colaboradorBeatRepository.save(colaborador1)).thenReturn(colaborador1);
        when(colaboradorBeatRepository.findByBeatId(1L)).thenReturn(List.of(colaborador1, colaborador2));

        colaboradorBeatService.aceptar(5L, invitado);

        assertThat(acuerdo.getEstado()).isEqualTo(EstadoAcuerdo.ABIERTO);
        verify(acuerdoCreditosRepository, never()).save(acuerdo);
    }

    @Test
    void aceptar_conUsuarioQueNoEsElInvitado_lanzaAccessDeniedException() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(invitado);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));

        assertThatThrownBy(() -> colaboradorBeatService.aceptar(5L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(colaboradorBeatRepository, never()).save(any());
    }

    @Test
    void aceptar_sinAcuerdoAbierto_lanzaIllegalStateException() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(invitado);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorBeatService.aceptar(5L, invitado))
                .isInstanceOf(IllegalStateException.class);

        verify(colaboradorBeatRepository, never()).save(any());
    }

    @Test
    void aceptar_conAcuerdoYaCerrado_lanzaIllegalStateException() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(invitado);

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setEstado(EstadoAcuerdo.CERRADO);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));

        assertThatThrownBy(() -> colaboradorBeatService.aceptar(5L, invitado))
                .isInstanceOf(IllegalStateException.class);

        verify(colaboradorBeatRepository, never()).save(any());
    }

    @Test
    void rechazar_conUsuarioQueEsElInvitado_quedaRechazado() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(invitado);
        colaboradorBeat.setEstado(EstadoColaborador.PENDIENTE);

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setEstado(EstadoAcuerdo.ABIERTO);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));
        when(colaboradorBeatRepository.save(colaboradorBeat)).thenReturn(colaboradorBeat);

        ColaboradorBeat resultado = colaboradorBeatService.rechazar(5L, invitado);

        assertThat(resultado.getEstado()).isEqualTo(EstadoColaborador.RECHAZADO);
    }

    @Test
    void eliminar_conDuenioDelBeat_borraYReabreSiHabiaAcuerdoCerrado() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);
        colaboradorBeat.setUsuario(invitado);

        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        acuerdo.setEstado(EstadoAcuerdo.CERRADO);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));
        when(acuerdoCreditosRepository.findByBeatId(1L)).thenReturn(Optional.of(acuerdo));
        when(colaboradorBeatRepository.findByBeatId(1L)).thenReturn(List.of());

        colaboradorBeatService.eliminar(5L, productor);

        verify(colaboradorBeatRepository).deleteById(5L);
        assertThat(acuerdo.getEstado()).isEqualTo(EstadoAcuerdo.ABIERTO);
        verify(acuerdoCreditosRepository).save(acuerdo);

        verify(acuerdoCreditosEventoRepository, times(2)).save(eventoCaptor.capture());
        List<TipoEventoAcuerdo> tipos = eventoCaptor.getAllValues().stream()
                .map(AcuerdoCreditosEvento::getTipo)
                .toList();
        assertThat(tipos).containsExactly(TipoEventoAcuerdo.ELIMINACION, TipoEventoAcuerdo.REAPERTURA);
    }

    @Test
    void eliminar_conUsuarioQueNoEsDuenioDelBeat_lanzaAccessDeniedException() {
        ColaboradorBeat colaboradorBeat = new ColaboradorBeat();
        colaboradorBeat.setId(5L);
        colaboradorBeat.setBeat(beat);

        when(colaboradorBeatRepository.findById(5L)).thenReturn(Optional.of(colaboradorBeat));

        assertThatThrownBy(() -> colaboradorBeatService.eliminar(5L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(colaboradorBeatRepository, never()).deleteById(5L);
    }
}
