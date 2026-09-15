package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import music.license.dto.beat.BeatRequest;
import music.license.exception.ResourceNotFoundException;
import music.license.model.Beat;
import music.license.model.EstadoBeat;
import music.license.model.Rol;
import music.license.model.Usuario;
import music.license.repository.BeatRepository;

@ExtendWith(MockitoExtension.class)
class BeatServiceTest {

    @Mock
    private BeatRepository beatRepository;

    @InjectMocks
    private BeatService beatService;

    private Beat beat;
    private Usuario productor;
    private Usuario otroUsuario;

    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setId(10L);
        productor.setRol(Rol.PRODUCTOR);

        otroUsuario = new Usuario();
        otroUsuario.setId(20L);
        otroUsuario.setRol(Rol.PRODUCTOR);

        beat = new Beat();
        beat.setId(1L);
        beat.setTitulo("Sueños de Medallo");
        beat.setGenero("Reggaeton");
        beat.setBpm(95);
        beat.setEstado(EstadoBeat.BORRADOR);
        beat.setProductor(productor);
    }

    @Test
    void obtenerTodos_devuelveListaDeBeats() {
        when(beatRepository.findAll()).thenReturn(List.of(beat));

        List<Beat> resultado = beatService.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Sueños de Medallo");
    }

    @Test
    void obtenerPorId_conIdExistente_devuelveBeat() {
        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        Beat resultado = beatService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_conIdInexistente_lanzaResourceNotFoundException() {
        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> beatService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Beat no encontrado");
    }

    @Test
    void guardar_conRolProductor_persisteElBeat() {
        when(beatRepository.save(beat)).thenReturn(beat);

        Beat resultado = beatService.guardar(beat, productor);

        assertThat(resultado).isEqualTo(beat);
        verify(beatRepository).save(beat);
    }

    @Test
    void guardar_conRolComprador_lanzaAccessDeniedException() {
        Usuario comprador = new Usuario();
        comprador.setId(30L);
        comprador.setRol(Rol.COMPRADOR);

        assertThatThrownBy(() -> beatService.guardar(beat, comprador))
                .isInstanceOf(AccessDeniedException.class);

        verify(beatRepository, never()).save(beat);
    }

    @Test
    void actualizar_conDuenio_actualizaCamposYPersiste() {
        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));
        when(beatRepository.save(beat)).thenReturn(beat);

        BeatRequest datosNuevos = new BeatRequest();
        datosNuevos.setTitulo("Nuevo Titulo");
        datosNuevos.setGenero("Trap");
        datosNuevos.setBpm(140);
        datosNuevos.setUrlPreview("https://ejemplo.com/preview.mp3");

        Beat resultado = beatService.actualizar(1L, datosNuevos, productor);

        assertThat(resultado.getTitulo()).isEqualTo("Nuevo Titulo");
        assertThat(resultado.getGenero()).isEqualTo("Trap");
        assertThat(resultado.getBpm()).isEqualTo(140);
        assertThat(resultado.getEstado()).isEqualTo(EstadoBeat.BORRADOR);
        verify(beatRepository).save(beat);
    }

    @Test
    void actualizar_conUsuarioQueNoEsDuenio_lanzaAccessDeniedException() {
        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        assertThatThrownBy(() -> beatService.actualizar(1L, new BeatRequest(), otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(beatRepository, never()).save(beat);
    }

    @Test
    void actualizar_conIdInexistente_lanzaResourceNotFoundException() {
        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> beatService.actualizar(99L, new BeatRequest(), productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminar_conDuenio_borraElBeat() {
        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        beatService.eliminar(1L, productor);

        verify(beatRepository).deleteById(1L);
    }

    @Test
    void eliminar_conUsuarioQueNoEsDuenio_lanzaAccessDeniedException() {
        when(beatRepository.findById(1L)).thenReturn(Optional.of(beat));

        assertThatThrownBy(() -> beatService.eliminar(1L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);

        verify(beatRepository, never()).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_lanzaResourceNotFoundException() {
        when(beatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> beatService.eliminar(99L, productor))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
