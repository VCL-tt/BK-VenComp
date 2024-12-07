package com.example.ventaComputadora.services.implement;

import com.example.ventaComputadora.domain.entity.Orden;
import com.example.ventaComputadora.domain.entity.Pago;
import com.example.ventaComputadora.infra.repository.OrdenRepository;
import com.example.ventaComputadora.infra.repository.PagoRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar comprobantes en formato PDF.
 */
@Service
@RequiredArgsConstructor
public class ComprobanteService {
    private final PagoRepository pagoRepository;
    private final OrdenRepository ordenRepository;

    /**
     * Genera un comprobante de pago en formato PDF.
     *
     * @param pagoId ID del pago.
     * @return El comprobante de pago en un InputStream.
     * @throws DocumentException Si ocurre un error al generar el documento PDF.
     */
    @Transactional(readOnly = true)
    public ByteArrayInputStream generarComprobante(Long pagoId) throws DocumentException {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        Orden orden = pago.getOrden();

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Comprobante de Pago"));
        document.add(new Paragraph("Orden ID: " + orden.getId()));
        document.add(new Paragraph("Fecha de Pago: " + pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph("Método de Pago: " + pago.getMetodoPago()));
        document.add(new Paragraph("Monto Total: $" + pago.getMonto()));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 3});

        PdfPCell hcell;
        hcell = new PdfPCell(new Paragraph("Producto"));
        table.addCell(hcell);

        hcell = new PdfPCell(new Paragraph("Precio"));
        table.addCell(hcell);

        orden.getProductos().forEach(producto -> {
            PdfPCell cell;

            cell = new PdfPCell(new Paragraph(producto.getNombre()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("$" + producto.getPrecio()));
            table.addCell(cell);
        });

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
