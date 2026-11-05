package com.clinica.servicios.service;

import com.clinica.servicios.domain.Receta;
import com.clinica.servicios.domain.RecetaItem;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class RecetaPdfService {

    private static final Color TEAL = new Color(13, 110, 110);
    private static final Color TEAL_CLARO = new Color(224, 242, 241);
    private static final Color GRIS = new Color(100, 116, 139);
    private static final Color BORDE = new Color(203, 213, 225);

    private final RecetaService recetaService;

    public RecetaPdfService(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @Transactional(readOnly = true)
    public byte[] generar(Long recetaId) {
        Receta receta = recetaService.obtener(recetaId);
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.A4, 42, 42, 42, 42);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD, TEAL);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, GRIS);
            Font seccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, TEAL);
            Font texto = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.BLACK);
            Font textoNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, Color.BLACK);
            Font textoPequeno = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, GRIS);
            Font encabezadoTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Font.BOLD, Color.WHITE);

            Paragraph nombreClinica = new Paragraph("CLINICA SAN RAFAEL", titulo);
            nombreClinica.setAlignment(Element.ALIGN_CENTER);
            documento.add(nombreClinica);

            Paragraph direccion = new Paragraph(
                    "Av. Salud 1250, Lima - Peru  |  Central telefonica: (01) 555-0100  |  RUC 20123456789",
                    subtitulo);
            direccion.setAlignment(Element.ALIGN_CENTER);
            documento.add(direccion);

            Paragraph tituloReceta = new Paragraph("RECETA MEDICA", 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Font.BOLD, Color.BLACK));
            tituloReceta.setAlignment(Element.ALIGN_CENTER);
            tituloReceta.setSpacingBefore(12);
            tituloReceta.setSpacingAfter(4);
            documento.add(tituloReceta);

            Paragraph codigoFecha = new Paragraph(
                    "Codigo: " + receta.getCodigo() + "        Fecha de emision: " + receta.getFechaEmisionTexto(),
                    textoPequeno);
            codigoFecha.setAlignment(Element.ALIGN_RIGHT);
            documento.add(codigoFecha);
            documento.add(new Paragraph(" "));

            PdfPTable datos = new PdfPTable(2);
            datos.setWidthPercentage(100);
            datos.setWidths(new float[]{1f, 1f});
            datos.setSpacingBefore(6);
            datos.setSpacingAfter(10);

            datos.addCell(celdaDatos("PACIENTE", 
                    receta.getPaciente().getNombreCompleto() + "\n"
                            + "Documento: " + nvl(receta.getPaciente().getDocumento()) + "\n"
                            + "Edad: " + receta.getPaciente().getEdad() + " anos   Sexo: "
                            + receta.getPaciente().getSexoTexto() + "\n"
                            + "Alergias: " + nvl(receta.getPaciente().getAlergias(), "Ninguna registrada"),
                    texto, textoNegrita));
            datos.addCell(celdaDatos("MEDICO RESPONSABLE",
                    receta.getMedico().getNombreCompleto() + "\n"
                            + "Especialidad: " + nvl(receta.getMedico().getEspecialidad()) + "\n"
                            + "Colegiatura: " + nvl(receta.getMedico().getColegiatura()) + "\n"
                            + "Telefono: " + nvl(receta.getMedico().getTelefono(), "No registrado"),
                    texto, textoNegrita));
            documento.add(datos);

            Paragraph diagnostico = new Paragraph();
            diagnostico.add(new Phrase("Diagnostico: ", textoNegrita));
            diagnostico.add(new Phrase(nvl(receta.getHistorial().getDiagnostico()), texto));
            diagnostico.setSpacingAfter(8);
            documento.add(diagnostico);

            Paragraph tituloMedicamentos = new Paragraph("MEDICAMENTOS INDICADOS", seccion);
            tituloMedicamentos.setSpacingBefore(4);
            tituloMedicamentos.setSpacingAfter(6);
            documento.add(tituloMedicamentos);

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2.5f, 2.1f, 1.5f, 1.8f, 1.2f, 1.1f, 2.6f});
            tabla.setHeaderRows(1);

            agregarEncabezado(tabla, "Medicamento", encabezadoTabla);
            agregarEncabezado(tabla, "Presentacion", encabezadoTabla);
            agregarEncabezado(tabla, "Dosis", encabezadoTabla);
            agregarEncabezado(tabla, "Frecuencia", encabezadoTabla);
            agregarEncabezado(tabla, "Duracion", encabezadoTabla);
            agregarEncabezado(tabla, "Cantidad", encabezadoTabla);
            agregarEncabezado(tabla, "Indicaciones", encabezadoTabla);

            for (RecetaItem item : receta.getItems()) {
                agregarCelda(tabla, nvl(item.getMedicamento()), texto);
                agregarCelda(tabla, nvl(item.getPresentacion()), texto);
                agregarCelda(tabla, nvl(item.getDosis()), texto);
                agregarCelda(tabla, nvl(item.getFrecuencia()), texto);
                agregarCelda(tabla, nvl(item.getDuracion()), texto);
                agregarCelda(tabla, nvl(item.getCantidad()), texto);
                agregarCelda(tabla, nvl(item.getIndicaciones()), texto);
            }
            documento.add(tabla);

            Paragraph indicaciones = new Paragraph();
            indicaciones.setSpacingBefore(12);
            indicaciones.add(new Phrase("Indicaciones generales: ", textoNegrita));
            indicaciones.add(new Phrase(nvl(receta.getIndicacionesGenerales(), "Sin indicaciones adicionales."), texto));
            documento.add(indicaciones);

            Paragraph firma = new Paragraph();
            firma.setSpacingBefore(55);
            firma.setAlignment(Element.ALIGN_CENTER);
            firma.add(new Phrase("_______________________________________________\n", texto));
            firma.add(new Phrase(receta.getMedico().getNombreCompleto() + "\n", textoNegrita));
            firma.add(new Phrase("CMP: " + nvl(receta.getMedico().getColegiatura()) + "\n", textoPequeno));
            firma.add(new Phrase("Firma y sello del medico", textoPequeno));
            documento.add(firma);

            Paragraph pie = new Paragraph(
                    "Documento generado electronicamente por el Sistema de Gestion Clinica. "
                            + "Valido solo con firma y sello del medico. Conserve esta receta.",
                    textoPequeno);
            pie.setSpacingBefore(30);
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

            documento.close();
            return salida.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new IllegalStateException("No se pudo generar el PDF de la receta.", e);
        }
    }

    private PdfPCell celdaDatos(String etiqueta, String contenido, Font texto, Font textoNegrita) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(TEAL_CLARO);
        celda.setBorderColor(BORDE);
        celda.setPadding(8);
        Paragraph titulo = new Paragraph(etiqueta, textoNegrita);
        titulo.setSpacingAfter(3);
        celda.addElement(titulo);
        celda.addElement(new Paragraph(contenido, texto));
        return celda;
    }

    private void agregarEncabezado(PdfPTable tabla, String valor, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(valor, font));
        celda.setBackgroundColor(TEAL);
        celda.setBorderColor(BORDE);
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }

    private void agregarCelda(PdfPTable tabla, String valor, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(valor, font));
        celda.setBorderColor(BORDE);
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    private String nvl(String valor) {
        return nvl(valor, "-");
    }

    private String nvl(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor;
    }
}
