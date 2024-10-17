package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.entity.EstadoOrden;
import com.example.ventaComputadora.domain.entity.Orden;
import com.example.ventaComputadora.domain.entity.Pago;
import com.example.ventaComputadora.infra.repository.OrdenRepository;
import com.example.ventaComputadora.infra.repository.PagoRepository;
import com.example.ventaComputadora.services.PagoService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para manejar las operaciones relacionadas con los pagos.
 */
@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoRepository pagoRepository;
    private final OrdenRepository ordenRepository;
    private final PagoService pagoService;
    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    /**
     * Realiza un pago para una orden.
     *
     * @param pago Detalles del pago a realizar.
     * @return El pago realizado.
     */
    @PostMapping("/realizar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Pago> realizarPago(@RequestBody Pago pago) {
        Orden orden = ordenRepository.findById(pago.getOrden().getId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (orden.getEstado() == EstadoOrden.PAGADO) {
            throw new RuntimeException("La orden ya ha sido pagada.");
        }

        double montoTotal = orden.getProductos().stream()
                .mapToDouble(producto -> producto.getPrecio())
                .sum();

        pago.setMonto(montoTotal);
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());

        Pago nuevoPago = pagoRepository.save(pago);

        orden.setEstado(EstadoOrden.PAGADO);
        ordenRepository.save(orden);

        logger.info("Pago realizado con ID: {}", nuevoPago.getId());
        return ResponseEntity.ok(nuevoPago);
    }

    /**
     * Lista los pagos de una orden.
     *
     * @param ordenId ID de la orden.
     * @return Lista de pagos de la orden.
     */
    @GetMapping("/orden/{ordenId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Pago>> listarPagosPorOrden(@PathVariable Long ordenId) {
        List<Pago> pagos = pagoRepository.findByOrdenId(ordenId);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Genera un comprobante de pago en formato PDF para una orden y lo descarga.
     *
     * @param ordenId ID de la orden.
     * @return El comprobante de pago en formato PDF.
     */
    @GetMapping("/comprobante/orden/{ordenId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> descargarComprobantePorOrden(@PathVariable Long ordenId) {
        ByteArrayOutputStream out;
        try {
            out = pagoService.generarComprobantePorOrden(ordenId);
        } catch (DocumentException e) {
            logger.error("Error al generar el comprobante para la orden con ID: " + ordenId, e);
            return ResponseEntity.status(500).build();
        } catch (RuntimeException e) {
            logger.error("Pago no encontrado para la orden con ID: " + ordenId, e);
            return ResponseEntity.status(404).body(null); // Devuelve 404 si no se encuentra el pago para la orden
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=comprobante_pago.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
