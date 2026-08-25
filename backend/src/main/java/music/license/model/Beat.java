package music.license.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "beats")
public class Beat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String genero;

    private Integer bpm;

    @ManyToOne
    @JoinColumn(name = "productor_id", nullable = false)
    private Usuario productor;

    private String urlPreview;

    @Enumerated(EnumType.STRING)
    private EstadoBeat estado;
}