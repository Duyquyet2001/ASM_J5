package com.example.demo.ASM.Service;


import com.example.demo.ASM.Model.PhieuMuon;
import com.example.demo.ASM.Model.ThietBi;
import com.example.demo.ASM.Repo.PhieuMuonRepo;
import com.example.demo.ASM.Repo.ThietBiRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PhieuMuonService {

    private final PhieuMuonRepo phieuMuonRepo;
    private final ThietBiRepo thietBiRepo;

    public List<PhieuMuon> getAll() {
        return phieuMuonRepo.findAll();
    }

    public PhieuMuon getById(Integer id) {
        return phieuMuonRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phiếu mượn ID " + id));
    }

    public void delete(Integer id) {
        PhieuMuon pm = getById(id);

        // ✅ Kiểm tra: nếu phiếu còn thiết bị đang mượn thì không được xóa
        boolean conMuon = pm.getThietBis().stream()
                .anyMatch(tb -> Boolean.TRUE.equals(tb.getDaMuon()));

        if (conMuon) {
            throw new IllegalStateException("Không thể xóa phiếu đang có thiết bị mượn!");
        }

        phieuMuonRepo.deleteById(id);
    }

    public List<ThietBi> dsThietBiRanh() {
        return thietBiRepo.findByDaMuonFalse();
    }
}
