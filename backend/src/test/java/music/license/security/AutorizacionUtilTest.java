package music.license.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import music.license.model.ColaboradorBeat;
import music.license.model.Usuario;

class AutorizacionUtilTest {

    @Test
    void esDuenioOColaborador_conElProductor_devuelveTrue() {
        Usuario productor = new Usuario();
        productor.setId(1L);

        boolean resultado = AutorizacionUtil.esDuenioOColaborador(productor, productor, List.of());

        assertThat(resultado).isTrue();
    }

    @Test
    void esDuenioOColaborador_conUnColaboradorDeclarado_devuelveTrue() {
        Usuario productor = new Usuario();
        productor.setId(1L);

        Usuario colaborador = new Usuario();
        colaborador.setId(2L);

        ColaboradorBeat fila = new ColaboradorBeat();
        fila.setUsuario(colaborador);

        boolean resultado = AutorizacionUtil.esDuenioOColaborador(productor, colaborador, List.of(fila));

        assertThat(resultado).isTrue();
    }

    @Test
    void esDuenioOColaborador_conUsuarioAjeno_devuelveFalse() {
        Usuario productor = new Usuario();
        productor.setId(1L);

        Usuario colaborador = new Usuario();
        colaborador.setId(2L);

        Usuario ajeno = new Usuario();
        ajeno.setId(99L);

        ColaboradorBeat fila = new ColaboradorBeat();
        fila.setUsuario(colaborador);

        boolean resultado = AutorizacionUtil.esDuenioOColaborador(productor, ajeno, List.of(fila));

        assertThat(resultado).isFalse();
    }
}
