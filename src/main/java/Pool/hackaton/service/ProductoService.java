package Pool.hackaton.service;

import Pool.hackaton.model.Producto;
import Pool.hackaton.repository.ProductoRepository;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listar() {
        return productoRepository.findByEstadoTrue();
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Lista solo los productos inactivos (eliminados logicamente)
    public List<Producto> listarInactivos() {
        return productoRepository.findByEstadoFalse();
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        LocalDateTime ahora = LocalDateTime.now();
        if (producto.getIdProducto() == null) {
            // CREAR: setear created_at, limpiar los demás
            producto.setCreatedAt(ahora);
            producto.setUpdatedAt(null);
            producto.setDeletedAt(null);
            producto.setRestoredAt(null);
        } else {
            // ACTUALIZAR: conservar created_at original, actualizar updated_at
            productoRepository.findById(producto.getIdProducto()).ifPresent(
                existente -> producto.setCreatedAt(existente.getCreatedAt())
            );
            producto.setUpdatedAt(ahora);
        }
        return productoRepository.save(producto);
    }

    // Baja lógica: estado=false + deleted_at = ahora
    public boolean eliminar(Integer id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            p.setEstado(false);
            p.setDeletedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            productoRepository.save(p);
            return true;
        }
        return false;
    }

    // Restauración lógica: estado=true + restored_at = ahora
    public boolean restaurar(Integer id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            p.setEstado(true);
            p.setRestoredAt(LocalDateTime.now());
            p.setDeletedAt(null);
            p.setUpdatedAt(LocalDateTime.now());
            productoRepository.save(p);
            return true;
        }
        return false;
    }

    // ---- IMPORTAR CSV ----
    // Formato esperado (con o sin cabecera):
    //   nombre,descripcion,categoria,precio,stock
    //   iPhone 15,"128GB, color negro",Celular,3500.00,10
    //
    // Soporta campos entre comillas que contengan comas internas.
    public int importarDesdeCSV(MultipartFile archivo) throws Exception {
        int contador = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                // Parseo manual que respeta campos entre comillas
                String[] campos = parsearCSV(linea);
                if (campos.length < 5) continue;
                if (campos[0].equalsIgnoreCase("nombre")) continue; // salta cabecera

                Producto p = new Producto();
                p.setNombre(campos[0]);
                p.setDescripcion(campos[1]);
                p.setCategoria(campos[2]);
                p.setPrecio(new BigDecimal(campos[3]));
                p.setStock(Integer.parseInt(campos[4]));
                p.setEstado(true);
                p.setCreatedAt(LocalDateTime.now());
                productoRepository.save(p);
                contador++;
            }
        }
        return contador;
    }

    /**
     * Parsea una línea CSV respetando campos entre comillas.
     * Ej: iPhone 15,"128GB, color negro",Celular,3500.00,10
     * → ["iPhone 15", "128GB, color negro", "Celular", "3500.00", "10"]
     */
    private String[] parsearCSV(String linea) {
        java.util.List<String> campos = new java.util.ArrayList<>();
        boolean enComillas = false;
        StringBuilder campo = new StringBuilder();

        for (char c : linea.toCharArray()) {
            if (c == '"') {
                enComillas = !enComillas;
            } else if (c == ',' && !enComillas) {
                campos.add(campo.toString().trim());
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }
        campos.add(campo.toString().trim()); // último campo
        return campos.toArray(new String[0]);
    }

    // ---- IMPORTAR EXCEL ----
    public int importarDesdeExcel(MultipartFile archivo) throws Exception {
        int contador = 0;
        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet hoja = workbook.getSheetAt(0);
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
                p.setCreatedAt(LocalDateTime.now());
                productoRepository.save(p);
                contador++;
            }
        }
        return contador;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default      -> "";
        };
    }

    // ---- EXPORTAR EXCEL ----
    public byte[] exportarExcel() throws Exception {
        List<Producto> productos = productoRepository.findByEstadoTrue();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Productos");
            Row cabecera = hoja.createRow(0);
            String[] columnas = {"ID", "Nombre", "Descripcion", "Categoria", "Precio", "Stock", "Creado", "Actualizado"};
            for (int i = 0; i < columnas.length; i++) {
                cabecera.createCell(i).setCellValue(columnas[i]);
            }
            int numFila = 1;
            for (Producto p : productos) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(p.getIdProducto());
                fila.createCell(1).setCellValue(p.getNombre());
                fila.createCell(2).setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "");
                fila.createCell(3).setCellValue(p.getCategoria());
                fila.createCell(4).setCellValue(p.getPrecio().doubleValue());
                fila.createCell(5).setCellValue(p.getStock());
                fila.createCell(6).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
                fila.createCell(7).setCellValue(p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : "");
            }
            for (int i = 0; i < columnas.length; i++) hoja.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ---- EXPORTAR PDF ----
    public byte[] exportarPDF() throws Exception {
        List<Producto> productos = productoRepository.findByEstadoTrue();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Lista de Productos - TechStore", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        documento.add(titulo);

        PdfPTable tabla = new PdfPTable(8);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1, 3, 3, 2, 2, 1.5f, 3, 3});

        Font fuenteCabecera = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        String[] cabeceras = {"ID", "Nombre", "Descripcion", "Categoria", "Precio", "Stock", "Creado", "Actualizado"};
        for (String cab : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteCabecera));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setPadding(4);
            tabla.addCell(celda);
        }

        Font fuenteDatos = new Font(Font.FontFamily.HELVETICA, 7);
        for (Producto p : productos) {
            tabla.addCell(new Phrase(String.valueOf(p.getIdProducto()), fuenteDatos));
            tabla.addCell(new Phrase(p.getNombre(), fuenteDatos));
            tabla.addCell(new Phrase(p.getDescripcion() != null ? p.getDescripcion() : "", fuenteDatos));
            tabla.addCell(new Phrase(p.getCategoria(), fuenteDatos));
            tabla.addCell(new Phrase("S/. " + p.getPrecio(), fuenteDatos));
            tabla.addCell(new Phrase(String.valueOf(p.getStock()), fuenteDatos));
            tabla.addCell(new Phrase(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "", fuenteDatos));
            tabla.addCell(new Phrase(p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : "", fuenteDatos));
        }

        documento.add(tabla);
        documento.close();
        return out.toByteArray();
    }
}
