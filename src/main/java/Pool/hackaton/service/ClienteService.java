package Pool.hackaton.service;

import Pool.hackaton.model.Cliente;
import Pool.hackaton.repository.ClienteRepository;
import com.itextpdf.text.*;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listar() {
        return clienteRepository.findByEstadoTrue();
    }

    public List<Cliente> listarInactivos() {
        return clienteRepository.findByEstadoFalse();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        LocalDateTime ahora = LocalDateTime.now();
        Integer idActual = cliente.getIdCliente() == null ? -1 : cliente.getIdCliente();

        if (clienteRepository.existsByDniAndIdClienteNot(cliente.getDni(), idActual)) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
        }

        if (cliente.getIdCliente() == null) {
            cliente.setCreatedAt(ahora);
            cliente.setUpdatedAt(null);
            cliente.setDeletedAt(null);
            cliente.setRestoredAt(null);
        } else {
            clienteRepository.findById(cliente.getIdCliente()).ifPresent(
                existente -> cliente.setCreatedAt(existente.getCreatedAt())
            );
            cliente.setUpdatedAt(ahora);
        }
        return clienteRepository.save(cliente);
    }

    public boolean eliminar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(false);
            c.setDeletedAt(LocalDateTime.now());
            c.setUpdatedAt(LocalDateTime.now());
            clienteRepository.save(c);
            return true;
        }
        return false;
    }

    public boolean restaurar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(true);
            c.setRestoredAt(LocalDateTime.now());
            c.setDeletedAt(null);
            c.setUpdatedAt(LocalDateTime.now());
            clienteRepository.save(c);
            return true;
        }
        return false;
    }

    // ---- IMPORTAR CSV ----
    // Formato: nombre,dni,telefono,email
    public int importarDesdeCSV(MultipartFile archivo) throws Exception {
        int contador = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] campos = parsearCSV(linea);
                if (campos.length < 4) continue;
                if (campos[0].equalsIgnoreCase("nombre")) continue;

                Cliente c = new Cliente();
                c.setNombre(campos[0]);
                c.setDni(campos[1]);
                c.setTelefono(campos.length > 2 ? campos[2] : "");
                c.setEmail(campos.length > 3 ? campos[3] : "");
                c.setEstado(true);
                c.setCreatedAt(LocalDateTime.now());

                // Saltar si el DNI ya existe
                if (!clienteRepository.existsByDni(c.getDni())) {
                    clienteRepository.save(c);
                    contador++;
                }
            }
        }
        return contador;
    }

    // ---- IMPORTAR EXCEL ----
    public int importarDesdeExcel(MultipartFile archivo) throws Exception {
        int contador = 0;
        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet hoja = workbook.getSheetAt(0);
            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                String dni = getCellValue(fila.getCell(1));
                if (clienteRepository.existsByDni(dni)) continue; // saltar duplicados

                Cliente c = new Cliente();
                c.setNombre(getCellValue(fila.getCell(0)));
                c.setDni(dni);
                c.setTelefono(getCellValue(fila.getCell(2)));
                c.setEmail(getCellValue(fila.getCell(3)));
                c.setEstado(true);
                c.setCreatedAt(LocalDateTime.now());
                clienteRepository.save(c);
                contador++;
            }
        }
        return contador;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default      -> "";
        };
    }

    private String[] parsearCSV(String linea) {
        List<String> campos = new ArrayList<>();
        boolean enComillas = false;
        StringBuilder campo = new StringBuilder();
        for (char c : linea.toCharArray()) {
            if (c == '"')            { enComillas = !enComillas; }
            else if (c == ',' && !enComillas) { campos.add(campo.toString().trim()); campo.setLength(0); }
            else                     { campo.append(c); }
        }
        campos.add(campo.toString().trim());
        return campos.toArray(new String[0]);
    }

    // ---- EXPORTAR EXCEL ----
    public byte[] exportarExcel() throws Exception {
        List<Cliente> clientes = clienteRepository.findByEstadoTrue();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Clientes");
            Row cab = hoja.createRow(0);
            String[] cols = {"ID", "Nombre", "DNI", "Telefono", "Email", "Creado", "Actualizado"};
            for (int i = 0; i < cols.length; i++) cab.createCell(i).setCellValue(cols[i]);

            int fila = 1;
            for (Cliente c : clientes) {
                Row r = hoja.createRow(fila++);
                r.createCell(0).setCellValue(c.getIdCliente());
                r.createCell(1).setCellValue(c.getNombre());
                r.createCell(2).setCellValue(c.getDni());
                r.createCell(3).setCellValue(c.getTelefono() != null ? c.getTelefono() : "");
                r.createCell(4).setCellValue(c.getEmail()    != null ? c.getEmail()    : "");
                r.createCell(5).setCellValue(c.getCreatedAt() != null ? c.getCreatedAt().toString() : "");
                r.createCell(6).setCellValue(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : "");
            }
            for (int i = 0; i < cols.length; i++) hoja.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ---- EXPORTAR PDF ----
    public byte[] exportarPDF() throws Exception {
        List<Cliente> clientes = clienteRepository.findByEstadoTrue();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, out);
        doc.open();

        com.itextpdf.text.Font fTitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
        Paragraph titulo = new Paragraph("Lista de Clientes - TechStore", fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        doc.add(titulo);

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1, 4, 2, 2, 3});

        com.itextpdf.text.Font fCab = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        for (String cab : new String[]{"ID", "Nombre", "DNI", "Telefono", "Email"}) {
            PdfPCell celda = new PdfPCell(new Phrase(cab, fCab));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setPadding(5);
            tabla.addCell(celda);
        }

        com.itextpdf.text.Font fData = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
        for (Cliente c : clientes) {
            tabla.addCell(new Phrase(String.valueOf(c.getIdCliente()), fData));
            tabla.addCell(new Phrase(c.getNombre(), fData));
            tabla.addCell(new Phrase(c.getDni(), fData));
            tabla.addCell(new Phrase(c.getTelefono() != null ? c.getTelefono() : "", fData));
            tabla.addCell(new Phrase(c.getEmail()    != null ? c.getEmail()    : "", fData));
        }

        doc.add(tabla);
        doc.close();
        return out.toByteArray();
    }
}
