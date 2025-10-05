package com.example.demo.ASM.Controller;

import com.example.demo.ASM.Model.PhieuMuon;
import com.example.demo.ASM.Model.ThietBi;
import com.example.demo.ASM.Repo.PhieuMuonRepo;
import com.example.demo.ASM.Repo.ThietBiRepo;
import com.example.demo.ASM.Service.MuonTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/phieu-muon")
@RequiredArgsConstructor
public class PhieuMuonController {

    private final PhieuMuonRepo phieuMuonRepo;
    private final ThietBiRepo thietBiRepo;
    private final MuonTraService muonTraService;

    // 🟢 Hiển thị danh sách phiếu mượn
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("dsPhieuMuon", phieuMuonRepo.findAll());
        // ✅ Chỉ hiển thị thiết bị còn hoạt động và chưa mượn
        model.addAttribute("dsThietBi", thietBiRepo.findByTinhTrangTrueAndDaMuonFalse());
        model.addAttribute("pageTitle", "Quản lý Phiếu Mượn");
        model.addAttribute("activePage", "phieu");
        return "phieu-muon-list";
    }

    // 🟢 Thêm phiếu mượn mới — cho phép chọn ngày mượn
    @PostMapping("/add")
    public String add(@RequestParam String maPhieu,
                      @RequestParam LocalDate ngayMuon,
                      @RequestParam(value = "thietBiIds", required = false) List<Integer> thietBiIds) {
        try {
            if (thietBiIds == null || thietBiIds.isEmpty()) {
                throw new RuntimeException("Vui lòng chọn ít nhất 1 thiết bị để mượn!");
            }

            muonTraService.taoPhieuMuon(thietBiIds, maPhieu, ngayMuon);
            return "redirect:/phieu-muon";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/phieu-muon?error=" + e.getMessage();
        }
    }

    // 🟢 Trả phiếu — tự động set ngày trả = LocalDate.now()
    @GetMapping("/return/{id}")
    public String traPhieu(@PathVariable Integer id) {
        try {
            muonTraService.traHetPhieu(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/phieu-muon";
    }

    // 🟢 Xóa phiếu — trả hết thiết bị trước khi xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        try {
            PhieuMuon pm = phieuMuonRepo.findById(id).orElse(null);
            if (pm != null) {
                muonTraService.traHetPhieu(id);
                phieuMuonRepo.delete(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/phieu-muon";
    }

    // 🟢 Hiển thị form sửa phiếu
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        PhieuMuon pm = phieuMuonRepo.findById(id).orElse(null);
        if (pm == null) {
            return "redirect:/phieu-muon";
        }
        model.addAttribute("pm", pm);
        // ✅ Chỉ hiển thị thiết bị còn hoạt động (tinhTrang = true)
        model.addAttribute("dsThietBi", thietBiRepo.findByTinhTrangTrue());
        model.addAttribute("pageTitle", "Sửa Phiếu Mượn");
        model.addAttribute("activePage", "phieu");
        return "phieu-muon-edit";
    }

    // 🟢 Cập nhật phiếu mượn
    @PostMapping("/update")
    public String update(@ModelAttribute PhieuMuon pm,
                         @RequestParam(value = "thietBiIds", required = false) List<Integer> thietBiIds) {

        PhieuMuon oldPm = phieuMuonRepo.findById(pm.getId()).orElse(null);
        if (oldPm == null) {
            throw new RuntimeException("Không tìm thấy phiếu mượn ID: " + pm.getId());
        }

        List<ThietBi> oldList = oldPm.getThietBis();
        List<ThietBi> newList = (thietBiIds != null)
                ? thietBiRepo.findAllById(thietBiIds)
                : List.of();

        // 🟡 1️⃣ Trả lại thiết bị bị bỏ chọn
        for (ThietBi tb : oldList) {
            if (!newList.contains(tb)) {
                tb.setDaMuon(false);
                thietBiRepo.save(tb);
            }
        }

        // 🟢 2️⃣ Đánh dấu các thiết bị mới mượn thêm
        for (ThietBi tb : newList) {
            if (!oldList.contains(tb)) {
                tb.setDaMuon(true);
                thietBiRepo.save(tb);
            }
        }

        // 🟣 3️⃣ Cập nhật lại phiếu
        oldPm.setMaPhieu(pm.getMaPhieu());
        oldPm.setNgayMuon(pm.getNgayMuon());
        oldPm.setThietBis(newList);
        oldPm.setTrangThai(pm.getTrangThai()); // chỉ đọc trong form

        phieuMuonRepo.save(oldPm);

        return "redirect:/phieu-muon";
    }
}
