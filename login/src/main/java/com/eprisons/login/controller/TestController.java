package com.eprisons.login.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Welcome Admin";
    }

    @GetMapping("/officer/dashboard")
    public String officerDashboard() {
        return "Welcome Prison Officer";
    }

    @GetMapping("/viewer/dashboard")
    public String viewerDashboard() {
        return "Welcome Viewer";
    }
}

