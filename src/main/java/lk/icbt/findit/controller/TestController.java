package lk.icbt.findit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/admin/test")
    public String adminTest() {
        return "ADMIN ACCESS OK!";
    }

    @GetMapping("/merchant/test")
    public String merchantTest() {
        return "MERCHANT ACCESS OK!";
    }

    @GetMapping("/customer/test")
    public String customerTest() {
        return "CUSTOMER ACCESS OK!";
    }

}
