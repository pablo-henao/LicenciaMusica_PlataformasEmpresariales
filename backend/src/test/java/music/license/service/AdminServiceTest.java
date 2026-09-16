package music.license.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import music.license.model.AcuerdoCreditos;
import music.license.model.Beat;
import music.license.model.Compra;
import music.license.model.Rol;
import music.license.model.Usuario;
import music.license.repository.AcuerdoCreditosRepository;
import music.license.repository.BeatRepository;
import music.license.repository.CompraRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private BeatRepository beatRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private AcuerdoCreditosRepository acuerdoCreditosRepository;

    @InjectMocks
    private AdminService adminService;

    private Usuario admin;
    private Usuario noAdmin;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        admin = new Usuario();
        admin.setId(1L);
        admin.setRol(Rol.ADMIN);

        noAdmin = new Usuario();
        noAdmin.setId(2L);
        noAdmin.setRol(Rol.PRODUCTOR);

        pageable = PageRequest.of(0, 20);
    }

    @Test
    void obtenerTodosLosBeats_conRolAdmin_devuelveTodos() {
        Beat beat = new Beat();
        Page<Beat> pagina = new PageImpl<>(List.of(beat));

        when(beatRepository.findAll(pageable)).thenReturn(pagina);

        Page<Beat> resultado = adminService.obtenerTodosLosBeats(admin, pageable);

        assertThat(resultado.getContent()).containsExactly(beat);
    }

    @Test
    void obtenerTodosLosBeats_conRolNoAdmin_lanzaAccessDeniedException() {
        assertThatThrownBy(() -> adminService.obtenerTodosLosBeats(noAdmin, pageable))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void obtenerTodasLasCompras_conRolAdmin_devuelveTodas() {
        Compra compra = new Compra();
        Page<Compra> pagina = new PageImpl<>(List.of(compra));

        when(compraRepository.findAll(pageable)).thenReturn(pagina);

        Page<Compra> resultado = adminService.obtenerTodasLasCompras(admin, pageable);

        assertThat(resultado.getContent()).containsExactly(compra);
    }

    @Test
    void obtenerTodasLasCompras_conRolNoAdmin_lanzaAccessDeniedException() {
        assertThatThrownBy(() -> adminService.obtenerTodasLasCompras(noAdmin, pageable))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void obtenerTodosLosAcuerdos_conRolAdmin_devuelveTodos() {
        AcuerdoCreditos acuerdo = new AcuerdoCreditos();
        Page<AcuerdoCreditos> pagina = new PageImpl<>(List.of(acuerdo));

        when(acuerdoCreditosRepository.findAll(pageable)).thenReturn(pagina);

        Page<AcuerdoCreditos> resultado = adminService.obtenerTodosLosAcuerdos(admin, pageable);

        assertThat(resultado.getContent()).containsExactly(acuerdo);
    }

    @Test
    void obtenerTodosLosAcuerdos_conRolNoAdmin_lanzaAccessDeniedException() {
        assertThatThrownBy(() -> adminService.obtenerTodosLosAcuerdos(noAdmin, pageable))
                .isInstanceOf(AccessDeniedException.class);
    }
}
