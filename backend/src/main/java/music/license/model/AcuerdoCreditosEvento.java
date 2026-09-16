package music.license.model;

import java.time.LocalDateTime;

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

/**
 * Registro de auditoria del acuerdo de creditos de un beat: quien propuso, acepto,
 * rechazo o modifico algo, y cuando. Se referencia al Beat (no al AcuerdoCreditos)
 * porque un colaborador puede proponerse antes de que el productor abra formalmente
 * el acuerdo, y el evento igual debe quedar registrado.
 */
@Getter
@Setter
@Entity
@Table(name = "acuerdo_creditos_eventos")
public class AcuerdoCreditosEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "beat_id", nullable = false)
    private Beat beat;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoEventoAcuerdo tipo;

    private String detalle;

    private LocalDateTime fecha;
}
