package com.example.ventaComputadora.services.implement;

import com.example.ventaComputadora.domain.entity.*;
import com.example.ventaComputadora.domain.entity.enums.EstadoOrden;
import com.example.ventaComputadora.infra.repository.OrdenRepository;
import com.example.ventaComputadora.infra.repository.PagoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para manejar los pagos.
 */
@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository pagoRepository;
    private final OrdenRepository ordenRepository;
    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    /**
     * Realiza un pago.
     *
     * @param pago Información del pago.
     * @return El pago realizado.
     */
    @Transactional
    public Pago realizarPago(Pago pago) {
        Orden orden = ordenRepository.findById(pago.getOrden().getId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (orden.getEstado() == EstadoOrden.PAGADO) {
            throw new RuntimeException("La orden ya ha sido pagada.");
        }

        double montoTotal = orden.getProductos().stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        pago.setMonto(montoTotal);
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());

        Pago nuevoPago = pagoRepository.save(pago);

        orden.setEstado(EstadoOrden.PAGADO);
        ordenRepository.save(orden);

        logger.info("Pago realizado con ID: {}", nuevoPago.getId());
        return nuevoPago;
    }

    /**
     * Lista los pagos de una orden.
     *
     * @param ordenId ID de la orden.
     * @return Lista de pagos de la orden.
     */
    @Transactional(readOnly = true)
    public List<Pago> listarPagosPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId);
    }

    /**
     * Genera un comprobante de pago en formato PDF para una orden.
     *
     * @param ordenId ID de la orden.
     * @return El comprobante de pago en un ByteArrayOutputStream.
     * @throws DocumentException Si ocurre un error al generar el documento PDF.
     */
    public ByteArrayOutputStream generarComprobantePorOrden(Long ordenId) throws DocumentException {
        List<Pago> pagos = pagoRepository.findByOrdenId(ordenId);
        if (pagos.isEmpty()) {
            throw new RuntimeException("Pago no encontrado para la orden");
        }

        Pago pago = pagos.get(0);
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Estilos
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);

        // Título
        Paragraph title = new Paragraph("Comprobante de Pago", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // Espacio en blanco

        // Información del pago
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        document.add(new Paragraph("Fecha del Pago: " + pago.getFechaPago().format(formatter), normalFont));
        document.add(new Paragraph("Monto: $" + pago.getMonto(), normalFont));
        document.add(new Paragraph("Estado: " + pago.getEstado(), normalFont));
        document.add(new Paragraph("Nombre del Usuario: " + orden.getUsuario().getUsername(), normalFont));
        document.add(new Paragraph(" ")); // Espacio en blanco

        // Información de productos
        document.add(new Paragraph("Productos:", boldFont));
        for (Producto producto : orden.getProductos()) {
            // Crear tabla para cada producto
            PdfPTable productTable = new PdfPTable(2);
            productTable.setWidthPercentage(100);
            productTable.setSpacingBefore(10f);
            productTable.setSpacingAfter(10f);
            productTable.setWidths(new int[]{1, 3});

            // Añadir celdas
            productTable.addCell(createCell("Producto", boldFont, Element.ALIGN_LEFT));
            productTable.addCell(createCell(producto.getNombre(), normalFont, Element.ALIGN_LEFT));

            productTable.addCell(createCell("Precio", boldFont, Element.ALIGN_LEFT));
            productTable.addCell(createCell("$" + producto.getPrecio(), normalFont, Element.ALIGN_LEFT));

            productTable.addCell(createCell("Descripción", boldFont, Element.ALIGN_LEFT));
            productTable.addCell(createCell(producto.getDescripcion(), normalFont, Element.ALIGN_LEFT));

            document.add(productTable);


            document.add(new Paragraph(" ")); // Espacio en blanco entre productos
        }

        document.close();

        return out;
    }

    private PdfPCell createCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
