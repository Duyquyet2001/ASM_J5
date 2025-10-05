package com.example.demo.ASM.Service;

import com.example.demo.ASM.Model.PhieuMuonThietBi;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    public byte[] exportLichSuToExcel(List<PhieuMuonThietBi> lichSuList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lịch sử mượn – trả");

        // ===== 1️⃣ Tiêu đề đầu bảng =====
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO LỊCH SỬ MƯỢN – TRẢ THIẾT BỊ");

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

        // ===== 2️⃣ Header =====
        Row header = sheet.createRow(2);
        String[] headers = {"STT", "Mã Phiếu", "Tên Thiết Bị", "Ngày Mượn", "Ngày Trả", "Trạng Thái"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ===== 3️⃣ Dữ liệu =====
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int rowNum = 3;
        int stt = 1;

        for (PhieuMuonThietBi item : lichSuList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(stt++);
            row.createCell(1).setCellValue(item.getPhieuMuon().getMaPhieu());
            row.createCell(2).setCellValue(item.getThietBi().getTenThietBi());
            row.createCell(3).setCellValue(
                    item.getPhieuMuon().getNgayMuon() != null ? item.getPhieuMuon().getNgayMuon().format(fmt) : ""
            );
            row.createCell(4).setCellValue(
                    item.getNgayTra() != null ? item.getNgayTra().format(fmt) : "Chưa trả"
            );
            row.createCell(5).setCellValue(item.getTrangThai());
        }

        // ===== 4️⃣ Tự động căn cột =====
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ===== 5️⃣ Xuất file =====
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
