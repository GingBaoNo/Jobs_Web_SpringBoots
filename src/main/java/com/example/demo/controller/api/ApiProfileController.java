package com.example.demo.controller.api;

import com.example.demo.entity.Profile;
import com.example.demo.service.ProfileService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiProfileController {

    @Autowired
    private ProfileService profileService;

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
}