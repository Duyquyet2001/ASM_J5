package com.example.demo.ASM.Controller;

import com.example.demo.ASM.Model.ThietBi;
import com.example.demo.ASM.Repo.ThietBiRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    // 🟢 Danh sách thiết bị + tìm kiếm / lọc
    @GetMapping
    public String getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean tinhTrang,
            @RequestParam(required = false) Boolean daMuon,
            @RequestParam(defaultValue = "maThietBi") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,        // ✅ trang hiện tại
            @RequestParam(defaultValue = "10") int size,       // ✅ số dòng mỗi trang
            Model model
    ) {
        // 🟢 Lọc theo keyword, tình trạng, mượn
        Specification<ThietBi> spec = Specification
                .where(ThietBiSpecification.keyword(keyword))
                .and(ThietBiSpecification.byTinhTrang(tinhTrang))
                .and(ThietBiSpecification.byDaMuon(daMuon));

        // 🟢 Sắp xếp
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        // 🟢 Phân trang
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ThietBi> pageThietBi = repo.findAll(spec, pageable);

        // 🟣 Lấy dữ liệu trang hiện tại
        List<ThietBi> dsThietBi = pageThietBi.getContent();

        // 🟢 Đưa dữ liệu ra view
        model.addAttribute("dsThietBi", dsThietBi);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageThietBi.getTotalPages());
        model.addAttribute("totalItems", pageThietBi.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("keyword", keyword);
        model.addAttribute("tinhTrang", tinhTrang);
        model.addAttribute("daMuon", daMuon);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("pageTitle", "Quản lý Thiết Bị");
        model.addAttribute("activePage", "thietbi");
        repo.flush();

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
    public String delete(@PathVariable int id, HttpSession session) {
        try {
            var tb = repo.findById(id).orElse(null);
            if (tb == null) {
                session.setAttribute("message", "❌ Không tìm thấy thiết bị cần xóa!");
                return "redirect:/thiet-bi";
            }

            if (Boolean.TRUE.equals(tb.getDaMuon())) {
                session.setAttribute("message",
                        "⚠️ Thiết bị '" + tb.getTenThietBi() + "' đang được mượn, không thể xóa!");
                return "redirect:/thiet-bi";
            }

            // ✅ Xóa thiết bị — DB tự xử lý liên kết (ON DELETE CASCADE)
            repo.deleteById(id);

            session.setAttribute("message", "✅ Đã xóa thiết bị '" + tb.getTenThietBi() + "' thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "❌ Lỗi khi xóa thiết bị: " + e.getMessage());
        }

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
