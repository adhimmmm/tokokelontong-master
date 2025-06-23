package com.example.tokokelontong.database;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;

import com.example.tokokelontong.model.DetailPembelian;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfStrukGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void generateStrukPembelian(String idPembelian, LocalDate tanggal, String namaSuplier, List<DetailPembelian> detailItems, BigDecimal total) throws IOException {
        String filename = "Struk_Pembelian_" + idPembelian + ".pdf";
        PdfWriter writer = new PdfWriter(filename);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header Toko
        document.add(new Paragraph("TOKO KELONTONG KITA")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Jl. Contoh No. 123, Malang, Jawa Timur")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Telp: 0812-XXXX-XXXX")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n")); // Spasi

        // Judul Struk
        document.add(new Paragraph("STRUK PEMBELIAN")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n")); // Spasi

        // Informasi Pembelian
        document.add(new Paragraph("ID Pembelian : " + idPembelian).setFontSize(10));
        document.add(new Paragraph("Tanggal      : " + tanggal.format(DATE_FORMATTER)).setFontSize(10));
        document.add(new Paragraph("Supplier     : " + namaSuplier).setFontSize(10));
        document.add(new Paragraph("\n")); // Spasi

        // Tabel Detail Barang
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2})); // Nama Barang, Jumlah, Harga Beli, Subtotal
        table.setWidth(UnitValue.createPercentValue(100));

        // Header Tabel
        table.addHeaderCell(new Cell().add(new Paragraph("Nama Barang").setBold()).setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBorder(Border.NO_BORDER));
        table.addHeaderCell(new Cell().add(new Paragraph("Jumlah").setBold()).setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBorder(Border.NO_BORDER));
        table.addHeaderCell(new Cell().add(new Paragraph("Harga Beli").setBold()).setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBorder(Border.NO_BORDER));
        table.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold()).setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBorder(Border.NO_BORDER));

        for (DetailPembelian item : detailItems) {
            table.addCell(new Cell().add(new Paragraph(item.getNamaBarang())).setFontSize(9).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getJumlah()))).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", item.getHargaBeli()))).setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f", item.getSubtotal()))).setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        }

        document.add(table);
        document.add(new Paragraph("\n")); // Spasi

        // Total Pembayaran
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph("TOTAL: Rp " + String.format("%,.2f", total))
                .setFontSize(12)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(8));

        document.add(new Paragraph("\n")); // Spasi

        // Footer
        document.add(new Paragraph("Terima kasih telah berbelanja!")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
    }
}