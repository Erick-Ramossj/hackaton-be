package Pool.hackaton.service;

import Pool.hackaton.entity.Producto;
import Pool.hackaton.repository.ProductoRepository;
// iText: imports explícitos para evitar ambigüedad con org.apache.poi.ss.usermodel.Font
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
// Apache POI: imports explícitos para evitar ambigüedad con com.itextpdf.text.Font
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * SERVICIO: Producto
 * ============================================================
 * Es el servicio más completo. Incluye:
 *   - CRUD básico
 *   - Importar productos desde CSV
 *   - Importar productos desde Excel (.xlsx)
 *   - Exportar productos a Excel (.xlsx)
 *   - Exportar productos a PDF
 *
 * ¿CÓMO AGREGAR UN NUEVO CAMPO A LOS EXPORTS?
 * -------------------------------------------------------------
 * Ejemplo: agregar "marca" a Excel y PDF
 * 1. Agrega "Marca" al array  String[] columnas  en exportarExcel()
 * 2. Agrega  fila.createCell(6).setCellValue(p.getMarca());
 * 3. En exportarPDF() agrega la columna en cabeceras[] y en los datos
 * 4. Ajusta el new PdfPTable(7) para que tenga 7 columnas en vez de 6
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Solo productos activos (para listas del frontend)
    public List<Producto> listar() {
        return productoRepository.findByEstadoTrue();
    }

    // Todos los productos incluyendo inactivos (para admin)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    // Sirve para crear y actualizar
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    // Baja lógica: no borra de la BD, solo desactiva
    public boolean eliminar(Integer id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            p.setEstado(false);
            productoRepository.save(p);
            return true;
        }
        return false;
    }

    // ===========================================================
    // IMPORTAR DESDE CSV
    // ===========================================================
    // Formato esperado (sin cabecera, separado por comas):
    //   nombre,descripcion,categoria,precio,stock
    //   iPhone 15,128GB negro,Celular,3500.00,10
    //
    // Si el CSV tiene cabecera, se detecta y se salta automáticamente.
    // Usa Java puro (BufferedReader), sin dependencia externa.
    //
    // ¿CÓMO AGREGAR MÁS COLUMNAS AL CSV?
    // 1. Agrega el campo en Producto.java y en la BD
    // 2. Lee campos[5], campos[6], etc. y haz p.setXxx()
    // ===========================================================
    public int importarDesdeCSV(MultipartFile archivo) throws Exception {
        int contador = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] campos = linea.split(",");

                // Ignorar líneas incompletas
                if (campos.length < 5) continue;
                // Ignorar cabecera si el CSV la tiene
                if (campos[0].trim().equalsIgnoreCase("nombre")) continue;

                Producto p = new Producto();
                p.setNombre(campos[0].trim());
                p.setDescripcion(campos[1].trim());
                p.setCategoria(campos[2].trim());
                p.setPrecio(new BigDecimal(campos[3].trim()));
                p.setStock(Integer.parseInt(campos[4].trim()));
                p.setEstado(true);
                // Si hay más columnas: p.setMarca(campos[5].trim()); etc.

                productoRepository.save(p);
                contador++;
            }
        }
        return contador;
    }

    // ===========================================================
    // IMPORTAR DESDE EXCEL (.xlsx)
    // ===========================================================
    // El archivo debe tener:
    //   Fila 0 (cabecera): nombre | descripcion | categoria | precio | stock
    //   Fila 1 en adelante: los datos
    //
    // ¿CÓMO AGREGAR MÁS COLUMNAS AL EXCEL?
    // 1. Agrega el campo en Producto.java y en la BD
    // 2. Lee  fila.getCell(5)  etc. y haz p.setXxx()
    // ===========================================================
    public int importarDesdeExcel(MultipartFile archivo) throws Exception {
        int contador = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet hoja = workbook.getSheetAt(0); // Primera hoja del Excel

            // i = 1: empieza desde la fila 1, saltando la cabecera (fila 0)
            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                Producto p = new Producto();
                p.setNombre(getCellValue(fila.getCell(0)));
                p.setDescripcion(getCellValue(fila.getCell(1)));
                p.setCategoria(getCellValue(fila.getCell(2)));
                p.setPrecio(new BigDecimal(getCellValue(fila.getCell(3))));
                p.setStock((int) fila.getCell(4).getNumericCellValue());
                p.setEstado(true);

                productoRepository.save(p);
                contador++;
            }
        }
        return contador;
    }

    // Lee el valor de una celda de Excel como String,
    // independientemente de si es texto o número
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default      -> "";
        };
    }

    // ===========================================================
    // EXPORTAR A EXCEL (.xlsx)
    // ===========================================================
    // Retorna byte[] → el Controller lo envía como descarga al navegador.
    //
    // ¿CÓMO AGREGAR MÁS COLUMNAS?
    // 1. Agrega el nombre en String[] columnas
    // 2. Agrega  fila.createCell(6).setCellValue(p.getMarca());  etc.
    // ===========================================================
    public byte[] exportarExcel() throws Exception {
        List<Producto> productos = productoRepository.findByEstadoTrue();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Productos");

            // --- Fila 0: cabecera ---
            Row cabecera = hoja.createRow(0);
            String[] columnas = {"ID", "Nombre", "Descripcion", "Categoria", "Precio", "Stock"};
            for (int i = 0; i < columnas.length; i++) {
                cabecera.createCell(i).setCellValue(columnas[i]);
            }

            // --- Filas de datos ---
            int numFila = 1;
            for (Producto p : productos) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(p.getIdProducto());
                fila.createCell(1).setCellValue(p.getNombre());
                fila.createCell(2).setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "");
                fila.createCell(3).setCellValue(p.getCategoria());
                fila.createCell(4).setCellValue(p.getPrecio().doubleValue());
                fila.createCell(5).setCellValue(p.getStock());
                // Si agregas más campos: fila.createCell(6).setCellValue(p.getMarca());
            }

            // Ajusta el ancho de cada columna al contenido
            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ===========================================================
    // EXPORTAR A PDF
    // ===========================================================
    // Retorna byte[] → el Controller lo envía como descarga al navegador.
    //
    // ¿CÓMO AGREGAR MÁS COLUMNAS?
    // 1. Cambia  new PdfPTable(6)  por  new PdfPTable(7)
    // 2. Agrega el nombre en String[] cabeceras
    // 3. Agrega  tabla.addCell(new Phrase(p.getMarca(), fuenteDatos));
    // ===========================================================
    public byte[] exportarPDF() throws Exception {
        List<Producto> productos = productoRepository.findByEstadoTrue();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, out);
        documento.open();

        // Título centrado
        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Lista de Productos - TechStore", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20); // Espacio debajo del título
        documento.add(titulo);

        // Tabla con 6 columnas (ajusta el número si agregas más)
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100); // Ocupa el 100% del ancho de la página
        // Ancho relativo de cada columna: ID es angosto, Descripción es ancho
        tabla.setWidths(new float[]{1, 3, 4, 2, 2, 1.5f});

        // --- Cabecera de la tabla (fondo oscuro, texto blanco) ---
        Font fuenteCabecera = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        String[] cabeceras = {"ID", "Nombre", "Descripcion", "Categoria", "Precio", "Stock"};
        for (String cab : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteCabecera));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setPadding(5);
            tabla.addCell(celda);
        }

        // --- Filas de datos ---
        Font fuenteDatos = new Font(Font.FontFamily.HELVETICA, 9);
        for (Producto p : productos) {
            tabla.addCell(new Phrase(String.valueOf(p.getIdProducto()), fuenteDatos));
            tabla.addCell(new Phrase(p.getNombre(), fuenteDatos));
            tabla.addCell(new Phrase(p.getDescripcion() != null ? p.getDescripcion() : "", fuenteDatos));
            tabla.addCell(new Phrase(p.getCategoria(), fuenteDatos));
            tabla.addCell(new Phrase("S/. " + p.getPrecio(), fuenteDatos));
            tabla.addCell(new Phrase(String.valueOf(p.getStock()), fuenteDatos));
            // Si agregas más campos: tabla.addCell(new Phrase(p.getMarca(), fuenteDatos));
        }

        documento.add(tabla);
        documento.close();

        return out.toByteArray();
    }
}
