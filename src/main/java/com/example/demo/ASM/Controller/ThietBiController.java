package com.example.demo.ASM.Controller;

import com.example.demo.ASM.Model.ThietBi;
import com.example.demo.ASM.Repo.ThietBiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/thiet-bi")
public class ThietBiController {

    @Autowired
    private ThietBiRepo repo;

    // 🟢 Danh sách thiết bị
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("dsThietBi", repo.findAll());
        model.addAttribute("tb", new ThietBi());
        model.addAttribute("pageTitle", "Quản lý Thiết Bị");
        model.addAttribute("activePage", "thietbi");
        return "thiet-bi-list";
    }

    // 🟢 Thêm thiết bị mới
    @PostMapping("/add")
    public String add(@ModelAttribute ThietBi tb) {
        tb.setDaMuon(false);   // mặc định chưa mượn
        tb.setTinhTrang(true); // ✅ còn mới
        repo.save(tb);
        return "redirect:/thiet-bi";
    }

    // 🟢 Xóa thiết bị
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        repo.deleteById(id);
        return "redirect:/thiet-bi";
    }

    // 🟢 Hiện form sửa thiết bị
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        ThietBi tb = repo.findById(id).orElse(null);
        model.addAttribute("tb", tb);
        model.addAttribute("pageTitle", "Sửa Thiết Bị");
        model.addAttribute("activePage", "thietbi");
        return "thiet-bi-edit";
    }

    // 🟢 Xử lý cập nhật
    @PostMapping("/update")
    public String update(@ModelAttribute ThietBi tb) {
        ThietBi old = repo.findById(tb.getId()).orElse(null);
        if (old != null) {
            // Giữ nguyên trạng thái mượn
            tb.setDaMuon(old.getDaMuon());
            repo.save(tb);
        }
        return "redirect:/thiet-bi";
    }
}
