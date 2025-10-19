package com.example.demo.ASM.Controller;

import com.example.demo.ASM.Model.PhieuMuon;
import com.example.demo.ASM.Model.ThietBi;
import com.example.demo.ASM.Repo.PhieuMuonRepo;
import com.example.demo.ASM.Repo.ThietBiRepo;
import com.example.demo.ASM.Service.MuonTraService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
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
    public String getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer thietBiId,
            @RequestParam(defaultValue = "maPhieu") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,              // ✅ thêm phân trang
            @RequestParam(defaultValue = "10") int size,             // ✅ số dòng mỗi trang
            Model model,
            HttpSession session
    ) {
        Specification<PhieuMuon> spec = Specification
                .where(PhieuMuonSpecification.keyword(keyword))
                .and(PhieuMuonSpecification.ngayMuonBetween(fromDate, toDate))
                .and(PhieuMuonSpecification.byThietBiId(thietBiId));

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PhieuMuon> pagePhieuMuon = phieuMuonRepo.findAll(spec, pageable);

        model.addAttribute("dsPhieuMuon", pagePhieuMuon.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pagePhieuMuon.getTotalPages());
        model.addAttribute("totalItems", pagePhieuMuon.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("dsThietBi", thietBiRepo.findByTinhTrangTrueAndDaMuonFalse());
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("thietBiId", thietBiId);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("pageTitle", "Quản lý Phiếu Mượn");
        model.addAttribute("activePage", "phieu");

        Object message = session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "phieu-muon-list";
    }




    // 🟢 Thêm phiếu mượn mới — cho phép chọn ngày mượn
    @PostMapping("/add")
    public String add(@RequestParam String maPhieu,
                      @RequestParam LocalDate ngayMuon,
                      @RequestParam(value = "thietBiIds", required = false) List<Integer> thietBiIds,
                      HttpSession session) {
        try {
            if (thietBiIds == null || thietBiIds.isEmpty()) {
                throw new RuntimeException("Vui lòng chọn ít nhất 1 thiết bị để mượn!");
            }

            muonTraService.taoPhieuMuon(thietBiIds, maPhieu, ngayMuon);

            // ✅ Lưu thông báo và mã phiếu vào Session
            session.setAttribute("lastMaPhieu", maPhieu);
            session.setAttribute("message", "Đã thêm phiếu mượn " + maPhieu + " thành công!");

            return "redirect:/phieu-muon";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "❌ Lỗi khi thêm phiếu: " + e.getMessage());
            return "redirect:/phieu-muon";
        }
    }

    // 🟢 Trả phiếu — tự động set ngày trả = LocalDate.now()
    @GetMapping("/return/{id}")
    public String traPhieu(@PathVariable Integer id, HttpSession session) {
        try {
            PhieuMuon pm = phieuMuonRepo.findById(id).orElse(null);
            if (pm != null) {
                muonTraService.traHetPhieu(id);
                session.setAttribute("message", "✅ Đã trả thành công phiếu mượn " + pm.getMaPhieu());
            } else {
                session.setAttribute("message", "⚠️ Không tìm thấy phiếu mượn có ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "❌ Lỗi khi trả phiếu: " + e.getMessage());
        }
        return "redirect:/phieu-muon";
    }

    @GetMapping("/delete/{id}")
    public String deletePhieuMuon(@PathVariable Integer id, HttpSession session) {
        try {
            PhieuMuon pm = phieuMuonRepo.findById(id).orElse(null);
            if (pm == null) {
                session.setAttribute("message", "❌ Không tìm thấy phiếu mượn cần xóa!");
                return "redirect:/phieu-muon";
            }

            // 🟡 Bước 1: Trả trạng thái thiết bị về "chưa mượn"
            for (ThietBi tb : pm.getThietBis()) {
                tb.setDaMuon(false);
                thietBiRepo.save(tb);
            }

            // 🟠 Bước 2: Dọn liên kết trong danh sách ManyToMany
            pm.getThietBis().clear();
            phieuMuonRepo.save(pm);

            // 🔴 Bước 3: Xóa phiếu mượn
            phieuMuonRepo.deleteById(id);

            // 🟢 Bước 4: Thông báo
            session.setAttribute("message",
                    "✅ Đã xóa phiếu mượn '" + pm.getMaPhieu() + "' và cập nhật trạng thái thiết bị!");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "❌ Lỗi khi xóa phiếu mượn: " + e.getMessage());
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
