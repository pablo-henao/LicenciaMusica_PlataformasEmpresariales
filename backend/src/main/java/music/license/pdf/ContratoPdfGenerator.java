package music.license.pdf;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;

import music.license.model.Compra;
import music.license.model.TipoLicencia;
import music.license.model.Usuario;

/**
 * Genera el contrato en PDF de una compra completada, con los terminos
 * de la licencia tal como quedaron registrados al momento del checkout.
 */
@Component
public class ContratoPdfGenerator {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font FONT_SECCION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
    private static final Font FONT_TEXTO = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font FONT_CLAUSULA = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);

    public byte[] generar(Compra compra) {
        TipoLicencia tipoLicencia = compra.getTipoLicencia();
        Usuario comprador = compra.getComprador();
        Usuario productor = tipoLicencia.getBeat().getProductor();

        Document documento = new Document();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            documento.add(centrado("Contrato de Licencia Musical", FONT_TITULO));
            documento.add(centrado("Licencia+ — Compra #" + compra.getId(), FONT_SUBTITULO, 20));

            documento.add(seccion("Partes"));
            documento.add(parrafo("Productor (licenciante): " + productor.getNombre()));
            documento.add(parrafo(
                    "Comprador (licenciatario): " + comprador.getNombre() + " (" + comprador.getEmail() + ")"));

            documento.add(seccion("Obra licenciada"));
            documento.add(parrafo("Título: " + tipoLicencia.getBeat().getTitulo()));
            documento.add(parrafo(
                    "Género: " + tipoLicencia.getBeat().getGenero() + " — BPM: " + tipoLicencia.getBeat().getBpm()));

            documento.add(seccion("Términos de la licencia"));
            documento.add(parrafo("Tipo de licencia: " + formatearTipo(tipoLicencia)));
            documento.add(parrafo("Precio pagado: $" + tipoLicencia.getPrecio()));
            documento.add(parrafo("Condiciones: " + formatearCondiciones(tipoLicencia)));

            documento.add(seccion("Fecha de la transacción"));
            documento.add(parrafo(
                    compra.getFecha() != null ? compra.getFecha().format(FORMATO_FECHA) : "No registrada"));

            Paragraph clausula = new Paragraph(
                    "Al completar esta compra, el comprador declara aceptar los terminos y condiciones "
                            + "de la licencia descrita en este documento, generado automaticamente por la "
                            + "plataforma Licencia+ al momento del checkout.",
                    FONT_CLAUSULA);
            clausula.setSpacingBefore(24);
            documento.add(clausula);

            documento.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el contrato en PDF", e);
        }

        return salida.toByteArray();
    }

    private String formatearTipo(TipoLicencia tipoLicencia) {
        return tipoLicencia.getTipo() != null ? tipoLicencia.getTipo().name() : "No especificado";
    }

    private String formatearCondiciones(TipoLicencia tipoLicencia) {
        return tipoLicencia.getCondiciones() != null && !tipoLicencia.getCondiciones().isBlank()
                ? tipoLicencia.getCondiciones()
                : "No se especificaron condiciones adicionales.";
    }

    private Paragraph centrado(String texto, Font font) {
        return centrado(texto, font, 4);
    }

    private Paragraph centrado(String texto, Font font, float espacioDespues) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(espacioDespues);
        return p;
    }

    private Paragraph seccion(String texto) {
        Paragraph p = new Paragraph(texto, FONT_SECCION);
        p.setSpacingBefore(14);
        p.setSpacingAfter(4);
        return p;
    }

    private Paragraph parrafo(String texto) {
        return new Paragraph(texto, FONT_TEXTO);
    }
}
