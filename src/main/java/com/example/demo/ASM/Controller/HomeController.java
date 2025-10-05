package com.example.demo.ASM.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/phieu-muon"; // hoặc /muon-tra tùy bạn muốn trang mặc định
    }
}
