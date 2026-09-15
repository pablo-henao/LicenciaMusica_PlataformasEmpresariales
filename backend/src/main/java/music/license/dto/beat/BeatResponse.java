package music.license.dto.beat;

import music.license.model.Beat;

public class BeatResponse {

    private Long id;
    private String titulo;
    private String genero;
    private Integer bpm;
    private String urlPreview;
    private String estado;
    private Long productorId;
    private String productorNombre;

    public static BeatResponse desde(Beat beat) {
        BeatResponse dto = new BeatResponse();
        dto.id = beat.getId();
        dto.titulo = beat.getTitulo();
        dto.genero = beat.getGenero();
        dto.bpm = beat.getBpm();
        dto.urlPreview = beat.getUrlPreview();
        dto.estado = beat.getEstado() != null ? beat.getEstado().name() : null;

        if (beat.getProductor() != null) {
            dto.productorId = beat.getProductor().getId();
            dto.productorNombre = beat.getProductor().getNombre();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
        return genero;
    }

    public Integer getBpm() {
        return bpm;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public String getEstado() {
        return estado;
    }

    public Long getProductorId() {
        return productorId;
    }

    public String getProductorNombre() {
        return productorNombre;
    }
}
