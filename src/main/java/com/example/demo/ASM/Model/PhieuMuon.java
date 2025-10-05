package com.example.demo.ASM.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Phieu_Muon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuMuon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phieu")
    private String maPhieu;

    @Column(name = "ngay_muon", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayMuon;


    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = false; // mặc định chưa trả


    // Many-to-Many với ThietBi
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "PhieuMuon_ThietBi",
            joinColumns = @JoinColumn(name = "phieu_muon_id"),
            inverseJoinColumns = @JoinColumn(name = "thiet_bi_id")
    )
    private List<ThietBi> thietBis = new ArrayList<>();
}
