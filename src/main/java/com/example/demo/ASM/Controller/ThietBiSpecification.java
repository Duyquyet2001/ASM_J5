package com.example.demo.ASM.Controller;


import com.example.demo.ASM.Model.ThietBi;
import org.springframework.data.jpa.domain.Specification;

public class ThietBiSpecification {

    // 1️⃣ Lọc theo từ khóa (mã hoặc tên thiết bị)
    public static Specification<ThietBi> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();
            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("maThietBi")), like),
                    cb.like(cb.lower(root.get("tenThietBi")), like)
            );
        };
    }

    // 2️⃣ Lọc theo tình trạng (true = mới, false = hỏng)
    public static Specification<ThietBi> byTinhTrang(Boolean tinhTrang) {
        return (root, query, cb) -> {
            if (tinhTrang == null) return cb.conjunction();
            return cb.equal(root.get("tinhTrang"), tinhTrang);
        };
    }

    // 3️⃣ Lọc theo trạng thái mượn (true = đang mượn, false = còn trống)
    public static Specification<ThietBi> byDaMuon(Boolean daMuon) {
        return (root, query, cb) -> {
            if (daMuon == null) return cb.conjunction();
            return cb.equal(root.get("daMuon"), daMuon);
        };
    }
}
