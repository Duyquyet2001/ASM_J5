package com.example.demo.ASM.Repo;

import com.example.demo.ASM.Model.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThietBiRepo extends JpaRepository<ThietBi, Integer>, JpaSpecificationExecutor<ThietBi> {

    // Tìm thiết bị theo mã
    ThietBi findByMaThietBi(String maThietBi);

    // Lấy danh sách thiết bị còn trong kho (chưa mượn)
    List<ThietBi> findByDaMuonFalse();

    // Lấy danh sách thiết bị còn mới (tinhTrang = true)
    List<ThietBi> findByTinhTrangTrue();

    // Kết hợp: còn mới và chưa mượn
    List<ThietBi> findByTinhTrangTrueAndDaMuonFalse();

}
