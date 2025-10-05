package com.example.demo.ASM.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "PhieuMuon_ThietBi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuMuonThietBi {

    @EmbeddedId
    private PMTBId id;

    @ManyToOne
    @MapsId("phieuMuonId") // ✅ map tới field trong PMTBId
    @JoinColumn(name = "phieu_muon_id")
    private PhieuMuon phieuMuon;

    @ManyToOne
    @MapsId("thietBiId") // ✅ map tới field trong PMTBId
    @JoinColumn(name = "thiet_bi_id")
    private ThietBi thietBi;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_tra")
    private LocalDate ngayTra;

    @Column(name = "trang_thai", length = 20)
    private String trangThai;
}
