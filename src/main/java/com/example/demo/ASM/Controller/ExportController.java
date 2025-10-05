package com.example.demo.ASM.Controller;

import com.example.demo.ASM.Service.ExcelExportService;
import com.example.demo.ASM.Service.MuonTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ExportController {

    private final ExcelExportService excelExportService;
    private final MuonTraService muonTraService;
    @GetMapping("/export/muon-tra")
    public ResponseEntity<byte[]> exportMuonTra() {
        try {
            byte[] file = excelExportService.exportLichSuToExcel(muonTraService.lichSu());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lich_su_muon_tra.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(file);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
