package com.example.demo.ASM.Controller;

import com.example.demo.ASM.Service.MuonTraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/muon-tra")
public class MuonTraController {

    private final MuonTraService svc;

    // ✅ Trang lịch sử mượn – trả
    @GetMapping
    public String lichSu(Model model) {
        model.addAttribute("history", svc.lichSu());
        return "muon-tra";
    }

    // ✅ Trả toàn bộ thiết bị trong phiếu
    @PostMapping("/tra-het/{phieuId}")
    public String traHet(@PathVariable Integer phieuId, RedirectAttributes ra) {
        try {
            svc.traHetPhieu(phieuId);
            ra.addFlashAttribute("msg", "Đã trả toàn bộ thiết bị của phiếu " + phieuId);
        } catch (Exception e) {
            ra.addFlashAttribute("err", e.getMessage());
        }
        return "redirect:/phieu-muon";
    }
}
