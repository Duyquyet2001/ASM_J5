package com.example.demo.ASM.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Thiet_Bi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thiet_bi")
    private String maThietBi;

    @Column(name = "ten_thiet_bi")
    private String tenThietBi;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "tinh_trang")
    private Boolean tinhTrang; // true = còn mới, false = đã hỏng

    @Column(name = "da_muon", nullable = false)
    private Boolean daMuon = false;


    // Nếu muốn ánh xạ Many-to-Many với PhieuMuon
    @ManyToMany(mappedBy = "thietBis")
    private List<PhieuMuon> phieuMuons = new ArrayList<>();
}
