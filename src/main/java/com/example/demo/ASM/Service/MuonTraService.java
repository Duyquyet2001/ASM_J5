package com.example.demo.ASM.Service;

import com.example.demo.ASM.Model.PMTBId;
import com.example.demo.ASM.Model.PhieuMuon;
import com.example.demo.ASM.Model.PhieuMuonThietBi;
import com.example.demo.ASM.Model.ThietBi;
import com.example.demo.ASM.Repo.PhieuMuonRepo;
import com.example.demo.ASM.Repo.PhieuMuonThietBiRepo;
import com.example.demo.ASM.Repo.ThietBiRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MuonTraService {

    private final PhieuMuonRepo phieuMuonRepo;
    private final PhieuMuonThietBiRepo ctRepo;
    private final ThietBiRepo thietBiRepo;

    // 🟢 Tạo phiếu mượn mới
    public PhieuMuon taoPhieuMuon(List<Integer> deviceIds, String maPhieu, LocalDate ngayMuon) {
        // Tạo phiếu
        PhieuMuon pm = new PhieuMuon();
        pm.setMaPhieu(maPhieu);
        pm.setNgayMuon(ngayMuon);
        pm.setTrangThai(true); // ✅ Đang mượn
        phieuMuonRepo.save(pm); // lưu để lấy ID

        // Tạo chi tiết phiếu
        for (Integer id : deviceIds) {
            ThietBi tb = thietBiRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị ID: " + id));

            // Kiểm tra nếu đang mượn rồi thì không cho mượn lại
            if (Boolean.TRUE.equals(tb.getDaMuon())) {
                throw new RuntimeException("Thiết bị " + tb.getTenThietBi() + " đang được mượn.");
            }

            PMTBId pmtbId = new PMTBId(pm.getId(), tb.getId());
            PhieuMuonThietBi ct = new PhieuMuonThietBi();
            ct.setId(pmtbId);
            ct.setPhieuMuon(pm);
            ct.setThietBi(tb);
            ct.setTrangThai("Đang mượn");
            ctRepo.save(ct);

            // cập nhật trạng thái thiết bị
            tb.setDaMuon(true);
            thietBiRepo.save(tb);
        }

        return pm;
    }

    // 🟢 Trả toàn bộ thiết bị của phiếu
    public void traHetPhieu(Integer phieuId) {
        PhieuMuon pm = phieuMuonRepo.findById(phieuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn ID: " + phieuId));

        List<PhieuMuonThietBi> list = ctRepo.findByPhieuMuonId(phieuId);

        boolean daTraHet = true;
        for (PhieuMuonThietBi ct : list) {
            if (ct.getNgayTra() == null) {
                ct.setNgayTra(LocalDate.now());
                ct.setTrangThai("Đã trả");
                ctRepo.save(ct);

                ThietBi tb = ct.getThietBi();
                tb.setDaMuon(false);
                thietBiRepo.save(tb);
            } else {
                daTraHet = false;
            }
        }

        // Cập nhật trạng thái phiếu
        pm.setTrangThai(false); // ✅ Đã trả hết
        phieuMuonRepo.save(pm);
    }


    // 🟢 Lấy tất cả lịch sử mượn trả (kể cả đang mượn)
    public List<PhieuMuonThietBi> lichSu() {
        return ctRepo.findAll();
    }


    // 🟢 Danh sách thiết bị rảnh
    public List<ThietBi> dsThietBiRanh() {
        return thietBiRepo.findByDaMuonFalse();
    }

    // 🟢 Danh sách đang mượn
    public List<PhieuMuonThietBi> dsDangMuon() {
        return ctRepo.findAllActive();
    }
}
