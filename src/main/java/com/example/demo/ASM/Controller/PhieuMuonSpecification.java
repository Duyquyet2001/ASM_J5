package com.example.demo.ASM.Controller;
import com.example.demo.ASM.Model.PhieuMuon;
import com.example.demo.ASM.Model.ThietBi;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PhieuMuonSpecification {

    // 1️⃣ Lọc theo từ khóa (mã phiếu hoặc tên thiết bị)
    public static Specification<PhieuMuon> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();

            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            Join<PhieuMuon, ThietBi> joinThietBi = root.join("thietBis", JoinType.LEFT);
            query.distinct(true);

            return cb.or(
                    cb.like(cb.lower(root.get("maPhieu")), likePattern),
                    cb.like(cb.lower(joinThietBi.get("tenThietBi")), likePattern),
                    cb.like(cb.lower(joinThietBi.get("maThietBi")), likePattern)
            );
        };
    }

    // 2️⃣ Lọc theo khoảng ngày mượn
    public static Specification<PhieuMuon> ngayMuonBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null)
                return cb.between(root.get("ngayMuon"), from, to);
            if (from != null)
                return cb.greaterThanOrEqualTo(root.get("ngayMuon"), from);
            return cb.lessThanOrEqualTo(root.get("ngayMuon"), to);
        };
    }

    // 3️⃣ Lọc theo thiết bị cụ thể
    public static Specification<PhieuMuon> byThietBiId(Integer thietBiId) {
        return (root, query, cb) -> {
            if (thietBiId == null) return cb.conjunction();

            Join<PhieuMuon, ThietBi> joinThietBi = root.join("thietBis", JoinType.INNER);
            query.distinct(true);

            return cb.equal(joinThietBi.get("id"), thietBiId);
        };
    }
}
