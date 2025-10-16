package com.example.demo.controller;

import com.example.demo.entity.Company;
import com.example.demo.entity.User;
import com.example.demo.service.CompanyService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EmployerController {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private UserService userService;
    
    // Trang dashboard của nhà tuyển dụng
    @GetMapping("/employer/dashboard")
    public String employerDashboard(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("title", "Bảng điều khiển Nhà Tuyển Dụng");
            
            // Lấy thông tin công ty nếu có
            Company company = companyService.getCompanyByUser(user).orElse(null);
            model.addAttribute("company", company);
        }
        return "employer/dashboard";
    }
    
    // Trang quản lý công ty
    @GetMapping("/employer/company")
    public String manageCompany(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            // Nếu chưa có công ty, tạo form đăng ký mới
            model.addAttribute("company", new Company());
            model.addAttribute("isRegistration", true);
        } else {
            model.addAttribute("company", company);
            model.addAttribute("isRegistration", false);
        }
        
        model.addAttribute("title", "Quản lý công ty");
        return "employer/company";
    }
    
    // Xử lý đăng ký công ty
    @PostMapping("/employer/company/register")
    public String registerCompany(Authentication authentication, 
                                 @ModelAttribute Company company, 
                                 Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Kiểm tra xem công ty đã tồn tại chưa
            if (companyService.existsByTenCongTy(company.getTenCongTy())) {
                model.addAttribute("errorMessage", "Tên công ty đã tồn tại");
                model.addAttribute("company", company);
                model.addAttribute("isRegistration", true);
                return "employer/company";
            }
            
            // Tạo công ty mới
            Company newCompany = companyService.registerCompany(
                user,
                company.getTenCongTy(),
                company.getTenNguoiDaiDien(),
                company.getMaSoThue(),
                company.getDiaChi(),
                company.getLienHeCty()
            );
            
            model.addAttribute("successMessage", "Đăng ký công ty thành công!");
            model.addAttribute("company", newCompany);
            model.addAttribute("isRegistration", false);
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi đăng ký công ty: " + e.getMessage());
            model.addAttribute("company", company);
            model.addAttribute("isRegistration", true);
            return "employer/company";
        }
        
        return "employer/company";
    }
    
    // Trang danh sách công ty
    @GetMapping("/companies")
    public String listCompanies(Model model) {
        model.addAttribute("title", "Danh sách công ty");
        model.addAttribute("companies", companyService.getAllCompanies());
        return "public/companies";
    }
}