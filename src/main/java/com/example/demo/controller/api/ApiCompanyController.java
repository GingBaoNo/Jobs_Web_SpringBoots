package com.example.demo.controller.api;

import com.example.demo.entity.Company;
import com.example.demo.service.CompanyService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiCompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ResponseEntity<?> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ApiResponseUtil.success("Companies retrieved successfully", companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Integer id) {
        return companyService.getCompanyById(id)
            .map(company -> ApiResponseUtil.success("Company retrieved successfully", company))
            .orElse(ApiResponseUtil.error("Company not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody Company company) {
        Company savedCompany = companyService.saveCompany(company);
        return ApiResponseUtil.created(savedCompany);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Integer id, @RequestBody Company company) {
        if (!companyService.getCompanyById(id).isPresent()) {
            return ApiResponseUtil.error("Company not found with id: " + id);
        }
        company.setMaCongTy(id);
        Company updatedCompany = companyService.updateCompany(company);
        return ApiResponseUtil.success("Company updated successfully", updatedCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Integer id) {
        if (!companyService.getCompanyById(id).isPresent()) {
            return ApiResponseUtil.error("Company not found with id: " + id);
        }
        companyService.deleteCompany(id);
        return ApiResponseUtil.noContent();
    }
}