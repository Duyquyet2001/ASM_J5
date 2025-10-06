package com.example.demo.ASM.Service;

import com.example.demo.ASM.Model.PhieuMuonThietBi;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    public byte[] exportLichSuToExcel(List<PhieuMuonThietBi> lichSuList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Lịch sử mượn trả");

            // ✅ Header
            Row header = sheet.createRow(0);
            String[] headers = {"STT", "Mã Phiếu", "Tên Thiết Bị", "Ngày Mượn", "Ngày Trả", "Trạng Thái"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ✅ Dữ liệu
            int rowNum = 1;
            for (PhieuMuonThietBi item : lichSuList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(item.getPhieuMuon().getMaPhieu());
                row.createCell(2).setCellValue(item.getThietBi().getTenThietBi());
                row.createCell(3).setCellValue(String.valueOf(item.getPhieuMuon().getNgayMuon()));
                row.createCell(4).setCellValue(
                        item.getNgayTra() != null ? String.valueOf(item.getNgayTra()) : "Chưa trả"
                );
                row.createCell(5).setCellValue(item.getTrangThai());
            }

            // ❌ Đừng dùng autoSizeColumn() trên server headless
            // ✅ Thay bằng set độ rộng cột cố định (20 ký tự)
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
