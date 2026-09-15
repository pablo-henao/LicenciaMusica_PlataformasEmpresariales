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

@Getter
@Setter
@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "tipo_licencia_id", nullable = false)
    private TipoLicencia tipoLicencia;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private EstadoCompra estado;

    // sin @Lob a proposito: en Postgres, byte[] simple mapea a "bytea" (columna normal),
    // mientras que @Lob puede mapear a un large object (OID) segun el driver/dialecto
    private byte[] contratoPdf;
}