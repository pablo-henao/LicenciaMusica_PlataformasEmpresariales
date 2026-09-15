package music.license.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import music.license.model.Beat;
import music.license.model.Compra;
import music.license.model.EstadoCompra;
import music.license.model.TipoLicencia;
import music.license.model.TipoLicenciaEnum;
import music.license.model.Usuario;

class ContratoPdfGeneratorTest {

    private final ContratoPdfGenerator generador = new ContratoPdfGenerator();

    @Test
    void generar_conCompraCompleta_devuelveUnPdfValido() {
        Usuario productor = new Usuario();
        productor.setNombre("DJ Productor");

        Usuario comprador = new Usuario();
        comprador.setNombre("Comprador Test");
        comprador.setEmail("comprador@test.com");

        Beat beat = new Beat();
        beat.setTitulo("Sueños de Medallo");
        beat.setGenero("Reggaeton");
        beat.setBpm(95);
        beat.setProductor(productor);

        TipoLicencia tipoLicencia = new TipoLicencia();
        tipoLicencia.setBeat(beat);
        tipoLicencia.setTipo(TipoLicenciaEnum.NO_EXCLUSIVA);
        tipoLicencia.setPrecio(new BigDecimal("50000"));
        tipoLicencia.setCondiciones("Uso no comercial, hasta 10000 streams");

        Compra compra = new Compra();
        compra.setId(1L);
        compra.setComprador(comprador);
        compra.setTipoLicencia(tipoLicencia);
        compra.setFecha(LocalDateTime.now());
        compra.setEstado(EstadoCompra.COMPLETADA);

        byte[] pdf = generador.generar(compra);

        assertThat(pdf).isNotEmpty();
        // todo PDF valido empieza con la firma "%PDF-"
        assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");
    }

    @Test
    void generar_sinCondiciones_noLanzaExcepcion() {
        Usuario productor = new Usuario();
        productor.setNombre("DJ Productor");

        Usuario comprador = new Usuario();
        comprador.setNombre("Comprador Test");
        comprador.setEmail("comprador@test.com");

        Beat beat = new Beat();
        beat.setTitulo("Beat Sin Condiciones");
        beat.setGenero("Trap");
        beat.setBpm(140);
        beat.setProductor(productor);

        TipoLicencia tipoLicencia = new TipoLicencia();
        tipoLicencia.setBeat(beat);
        tipoLicencia.setTipo(TipoLicenciaEnum.EXCLUSIVA);
        tipoLicencia.setPrecio(new BigDecimal("300000"));
        tipoLicencia.setCondiciones(null);

        Compra compra = new Compra();
        compra.setId(2L);
        compra.setComprador(comprador);
        compra.setTipoLicencia(tipoLicencia);
        compra.setFecha(LocalDateTime.now());
        compra.setEstado(EstadoCompra.COMPLETADA);

        byte[] pdf = generador.generar(compra);

        assertThat(pdf).isNotEmpty();
    }
}
