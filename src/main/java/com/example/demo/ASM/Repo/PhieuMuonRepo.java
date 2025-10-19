package com.example.demo.ASM.Repo;

import com.example.demo.ASM.Model.PhieuMuon;
import com.example.demo.ASM.Model.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuMuonRepo extends JpaRepository<PhieuMuon, Integer>, JpaSpecificationExecutor<PhieuMuon> {

    // Tìm phiếu mượn theo mã phiếu
    PhieuMuon findByMaPhieu(String maPhieu);

    // Tìm tất cả phiếu mượn có chứa thiết bị nào đó
    // (dùng JPQL hoặc query method tuỳ bạn)
    List<PhieuMuon> findByThietBis_MaThietBi(String maThietBi);

    public interface ThietBiRepo extends JpaRepository<ThietBi, Integer> {
        List<ThietBi> findByDaMuonFalse();
    }

}
