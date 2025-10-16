package com.example.demo.service;

import com.example.demo.entity.Company;
import com.example.demo.entity.User;
import com.example.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
    
    public List<Company> getCompaniesByDaXacThuc(Boolean daXacThuc) {
        return companyRepository.findByDaXacThuc(daXacThuc);
    }
    
    public List<Company> getUnverifiedCompanies() {
        // Companies with daXacThuc = false (chưa xác thực)
        return companyRepository.findByDaXacThuc(false);
    }
    
    public List<Company> getVerifiedCompanies() {
        return companyRepository.findByDaXacThuc(true);
    }
    
    public List<Company> getRejectedCompanies() {
        // For now, we'll consider all unverified companies as potentially "rejected"
        return companyRepository.findByDaXacThuc(false);
    }
    
    public Optional<Company> getCompanyById(Integer id) {
        return companyRepository.findById(id);
    }
    
    public Optional<Company> getCompanyByUser(User user) {
        return companyRepository.findByUser(user);
    }
    
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }
    
    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }
    
    public void deleteCompany(Integer id) {
        companyRepository.deleteById(id);
    }
    
    public boolean existsByTenCongTy(String tenCongTy) {
        return companyRepository.existsByTenCongTy(tenCongTy);
    }
    
    public Company registerCompany(User user, String tenCongTy, String tenNguoiDaiDien, String maSoThue, String diaChi, String lienHeCty) {
        if (companyRepository.existsByTenCongTy(tenCongTy)) {
            throw new RuntimeException("Tên công ty đã tồn tại");
        }
        
        Company company = new Company(user, tenCongTy);
        company.setTenNguoiDaiDien(tenNguoiDaiDien);
        company.setMaSoThue(maSoThue);
        company.setDiaChi(diaChi);
        company.setLienHeCty(lienHeCty);
        company.setDaXacThuc(false); // Chưa xác thực khi mới đăng ký
        
        return saveCompany(company);
    }
    
    public Company approveCompany(Integer companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            company.setDaXacThuc(true);
            return companyRepository.save(company);
        }
        throw new RuntimeException("Company not found with id: " + companyId);
    }
    
    public Company rejectCompany(Integer companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            // For rejection, we keep daXacThuc as false
            company.setDaXacThuc(false);
            return companyRepository.save(company);
        }
        throw new RuntimeException("Company not found with id: " + companyId);
    }
}