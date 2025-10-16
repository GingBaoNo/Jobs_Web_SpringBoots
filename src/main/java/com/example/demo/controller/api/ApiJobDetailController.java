package com.example.demo.controller.api;

import com.example.demo.entity.JobDetail;
import com.example.demo.service.JobDetailService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-details")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiJobDetailController {

    @Autowired
    private JobDetailService jobDetailService;

    @GetMapping
    public ResponseEntity<?> getAllJobDetails() {
        List<JobDetail> jobDetails = jobDetailService.getAllJobs();
        return ApiResponseUtil.success("Job details retrieved successfully", jobDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobDetailById(@PathVariable Integer id) {
        JobDetail jobDetail = jobDetailService.getJobById(id);
        if (jobDetail != null) {
            return ApiResponseUtil.success("Job detail retrieved successfully", jobDetail);
        } else {
            return ApiResponseUtil.error("Job detail not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createJobDetail(@RequestBody JobDetail jobDetail) {
        JobDetail savedJobDetail = jobDetailService.saveJob(jobDetail);
        return ApiResponseUtil.created(savedJobDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobDetail(@PathVariable Integer id, @RequestBody JobDetail jobDetail) {
        if (jobDetailService.getJobById(id) == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + id);
        }
        jobDetail.setMaCongViec(id);
        JobDetail updatedJobDetail = jobDetailService.updateJob(jobDetail);
        return ApiResponseUtil.success("Job detail updated successfully", updatedJobDetail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobDetail(@PathVariable Integer id) {
        if (jobDetailService.getJobById(id) == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + id);
        }
        jobDetailService.deleteJob(id);
        return ApiResponseUtil.noContent();
    }
}