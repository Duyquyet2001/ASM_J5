package com.example.demo.ASM.Model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PMTBId implements Serializable {
    private Integer phieuMuonId;
    private Integer thietBiId;
}
