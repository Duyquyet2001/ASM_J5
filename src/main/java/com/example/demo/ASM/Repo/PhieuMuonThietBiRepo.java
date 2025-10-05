package com.example.demo.ASM.Repo;

import com.example.demo.ASM.Model.PMTBId;
import com.example.demo.ASM.Model.PhieuMuonThietBi;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PhieuMuonThietBiRepo extends JpaRepository<PhieuMuonThietBi, PMTBId> {

    @Query("SELECT p FROM PhieuMuonThietBi p WHERE p.phieuMuon.id = :pmId")
    List<PhieuMuonThietBi> findByPhieuMuonId(@Param("pmId") Integer pmId);

    @Query("SELECT p FROM PhieuMuonThietBi p WHERE p.ngayTra IS NULL ORDER BY p.phieuMuon.ngayMuon DESC")
    List<PhieuMuonThietBi> findAllActive();

    @Query("SELECT p FROM PhieuMuonThietBi p WHERE p.ngayTra IS NOT NULL ORDER BY p.ngayTra DESC")
    List<PhieuMuonThietBi> findHistory();
}
