package com.example.demo.controller.api;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/profiles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiProfileController {

    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        List<Profile> profiles = profileService.getAllProfiles();
        return ApiResponseUtil.success("Profiles retrieved successfully", profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Integer id) {
        return profileService.getProfileById(id)
            .map(profile -> ApiResponseUtil.success("Profile retrieved successfully", profile))
            .orElse(ApiResponseUtil.error("Profile not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        Profile savedProfile = profileService.saveProfile(profile);
        return ApiResponseUtil.created(savedProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer id, @RequestBody Profile profile) {
        if (!profileService.getProfileById(id).isPresent()) {
            return ApiResponseUtil.error("Profile not found with id: " + id);
        }
        profile.setMaHoSo(id);
        Profile updatedProfile = profileService.updateProfile(profile);
        return ApiResponseUtil.success("Profile updated successfully", updatedProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable Integer id) {
        if (!profileService.getProfileById(id).isPresent()) {
            return ApiResponseUtil.error("Profile not found with id: " + id);
        }
        profileService.deleteProfile(id);
        return ApiResponseUtil.noContent();
    }
    
    // API mới: Lấy hồ sơ của người dùng hiện tại
    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                Optional<Profile> profile = profileService.getProfileByUser(user.get());
                if (profile.isPresent()) {
                    return ApiResponseUtil.success("Profile retrieved successfully", profile.get());
                } else {
                    return ApiResponseUtil.error("Profile not found for current user");
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }
    
    // API mới: Tạo hồ sơ cho người dùng hiện tại
    @PostMapping("/my-profile")
    public ResponseEntity<?> createMyProfile(@RequestBody Profile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    Profile createdProfile = profileService.createProfileForUser(user.get(), profile.getHoTen(), profile.getGioiTinh());
                    // Cập nhật các trường khác của hồ sơ
                    createdProfile.setUrlAnhDaiDien(profile.getUrlAnhDaiDien());
                    createdProfile.setNgaySinh(profile.getNgaySinh());
                    createdProfile.setSoDienThoai(profile.getSoDienThoai());
                    createdProfile.setTrinhDoHocVan(profile.getTrinhDoHocVan());
                    createdProfile.setTinhTrangHocVan(profile.getTinhTrangHocVan());
                    createdProfile.setKinhNghiem(profile.getKinhNghiem());
                    createdProfile.setTongNamKinhNghiem(profile.getTongNamKinhNghiem());
                    createdProfile.setGioiThieuBanThan(profile.getGioiThieuBanThan());
                    createdProfile.setUrlCv(profile.getUrlCv());
                    createdProfile.setCongKhai(profile.getCongKhai());
                    createdProfile.setViTriMongMuon(profile.getViTriMongMuon());
                    createdProfile.setThoiGianMongMuon(profile.getThoiGianMongMuon());
                    createdProfile.setLoaiThoiGianLamViec(profile.getLoaiThoiGianLamViec());
                    createdProfile.setHinhThucLamViec(profile.getHinhThucLamViec());
                    createdProfile.setLoaiLuongMongMuon(profile.getLoaiLuongMongMuon());
                    createdProfile.setMucLuongMongMuon(profile.getMucLuongMongMuon());
                    
                    Profile savedProfile = profileService.updateProfile(createdProfile);
                    return ApiResponseUtil.created(savedProfile);
                } catch (RuntimeException e) {
                    return ApiResponseUtil.error(e.getMessage());
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }
    
    // API mới: Cập nhật hồ sơ của người dùng hiện tại
    @PutMapping("/my-profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody Profile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                Optional<Profile> existingProfile = profileService.getProfileByUser(user.get());
                if (existingProfile.isPresent()) {
                    Profile updatedProfile = existingProfile.get();
                    // Cập nhật các trường của hồ sơ
                    updatedProfile.setHoTen(profile.getHoTen());
                    updatedProfile.setGioiTinh(profile.getGioiTinh());
                    updatedProfile.setUrlAnhDaiDien(profile.getUrlAnhDaiDien());
                    updatedProfile.setNgaySinh(profile.getNgaySinh());
                    updatedProfile.setSoDienThoai(profile.getSoDienThoai());
                    updatedProfile.setTrinhDoHocVan(profile.getTrinhDoHocVan());
                    updatedProfile.setTinhTrangHocVan(profile.getTinhTrangHocVan());
                    updatedProfile.setKinhNghiem(profile.getKinhNghiem());
                    updatedProfile.setTongNamKinhNghiem(profile.getTongNamKinhNghiem());
                    updatedProfile.setGioiThieuBanThan(profile.getGioiThieuBanThan());
                    updatedProfile.setUrlCv(profile.getUrlCv());
                    updatedProfile.setCongKhai(profile.getCongKhai());
                    updatedProfile.setViTriMongMuon(profile.getViTriMongMuon());
                    updatedProfile.setThoiGianMongMuon(profile.getThoiGianMongMuon());
                    updatedProfile.setLoaiThoiGianLamViec(profile.getLoaiThoiGianLamViec());
                    updatedProfile.setHinhThucLamViec(profile.getHinhThucLamViec());
                    updatedProfile.setLoaiLuongMongMuon(profile.getLoaiLuongMongMuon());
                    updatedProfile.setMucLuongMongMuon(profile.getMucLuongMongMuon());
                    updatedProfile.setNgayCapNhat(java.time.LocalDateTime.now());
                    
                    Profile savedProfile = profileService.updateProfile(updatedProfile);
                    return ApiResponseUtil.success("Profile updated successfully", savedProfile);
                } else {
                    return ApiResponseUtil.error("Profile not found for current user");
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }
}