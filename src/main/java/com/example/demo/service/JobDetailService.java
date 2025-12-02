package com.example.demo.service;

import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.WorkField;
import com.example.demo.repository.JobDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobDetailService {

    @Autowired
    private JobDetailRepository jobDetailRepository;

    public List<JobDetail> getAllJobs() {
        return jobDetailRepository.findAll();
    }

    public List<JobDetail> getJobsByCompany(Company company) {
        return jobDetailRepository.findByCompany(company);
    }

    public List<JobDetail> getActiveJobs() {
        return jobDetailRepository.findByTrangThaiDuyetAndTrangThaiTinTuyen("Đã duyệt", "Mở");
    }

    public List<JobDetail> getActiveJobsWithValidDate() {
        return jobDetailRepository.findByNgayKetThucTuyenDungAfterAndTrangThaiDuyetAndTrangThaiTinTuyen(
            LocalDate.now(), "Đã duyệt", "Mở");
    }

    public List<JobDetail> getJobsByTrangThaiDuyet(String trangThaiDuyet) {
        return jobDetailRepository.findByTrangThaiDuyet(trangThaiDuyet);
    }

    public List<JobDetail> getJobsByWorkField(WorkField workField) {
        return jobDetailRepository.findByWorkField(workField);
    }

    public List<JobDetail> getJobsByKeyword(String keyword) {
        return jobDetailRepository.findByTieuDeContainingOrChiTietContaining(keyword);
    }

    public JobDetail getJobById(Integer id) {
        return jobDetailRepository.findById(id).orElse(null);
    }

    public JobDetail saveJob(JobDetail jobDetail) {
        return jobDetailRepository.save(jobDetail);
    }

    public JobDetail updateJob(JobDetail jobDetail) {
        return jobDetailRepository.save(jobDetail);
    }

    public void deleteJob(Integer id) {
        jobDetailRepository.deleteById(id);
    }

    public List<JobDetail> getFeaturedJobs() {
        // Lấy các công việc nổi bật: những công việc được duyệt, còn hiệu lực và có nhiều lượt xem
        return jobDetailRepository.findTop10ByTrangThaiDuyetAndTrangThaiTinTuyenOrderByLuotXemDesc("Đã duyệt", "Mở");
    }

    public void incrementViewCount(JobDetail jobDetail) {
        jobDetail.setLuotXem(jobDetail.getLuotXem() + 1);
        jobDetailRepository.save(jobDetail);
    }

    // Phương thức tìm kiếm nâng cao
    public List<JobDetail> searchJobs(String keyword, Integer workFieldId, Integer workTypeId, Integer minSalary, Integer maxSalary) {
        // Khi người dùng tìm kiếm hoặc lọc, áp dụng các điều kiện trạng thái hợp lý:
        // - Công việc phải đang mở (trangThaiTinTuyen = 'Mở')
        // - Công việc chưa hết hạn (ngayKetThucTuyenDung >= CURRENT_DATE)
        // - Không yêu cầu công việc phải đã được duyệt để cho phép người dùng thấy cả công việc đang chờ
        return jobDetailRepository.findByKeywordAndFiltersWithoutStatus(
            keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null,
            workFieldId,
            workTypeId,
            minSalary,
            maxSalary
        );
    }

    public List<JobDetail> searchJobsByCompany(String companyName) {
        return jobDetailRepository.findByCompanyContaining(companyName);
    }

    // Phương thức tìm kiếm với phân trang
    public org.springframework.data.domain.Page<JobDetail> searchJobsWithPaging(String keyword, Integer workFieldId, Integer workTypeId, Integer minSalary, Integer maxSalary, org.springframework.data.domain.Pageable pageable) {
        // Khi người dùng tìm kiếm (có keyword) hoặc lọc (chỉ có filter),
        // chúng ta nên cho phép hiển thị cả công việc chưa duyệt để tiện cho việc kiểm tra
        // nhưng vẫn đảm bảo các công việc đang đóng hoặc hết hạn không được hiển thị
        return jobDetailRepository.findByKeywordAndFiltersWithoutStatusWithPaging(
            keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null,
            workFieldId,
            workTypeId,
            minSalary,
            maxSalary,
            pageable
        );
    }

    // Phương thức tìm kiếm đơn lẻ theo ngành nghề hoặc hình thức làm việc
    public List<JobDetail> searchJobsBySingleCriteria(Integer workFieldId, Integer workTypeId) {
        if ((workFieldId != null && workFieldId > 0) || (workTypeId != null && workTypeId > 0)) {
            return jobDetailRepository.findByWorkFieldOrWorkType(workFieldId, workTypeId);
        }
        // Nếu không có tiêu chí nào được cung cấp, trả về tất cả công việc
        return jobDetailRepository.findAll();
    }

    // Phương thức tìm kiếm đơn lẻ có phân trang
    public org.springframework.data.domain.Page<JobDetail> searchJobsBySingleCriteriaWithPaging(Integer workFieldId, Integer workTypeId, org.springframework.data.domain.Pageable pageable) {
        if ((workFieldId != null && workFieldId > 0) || (workTypeId != null && workTypeId > 0)) {
            return jobDetailRepository.findByWorkFieldOrWorkTypeWithPaging(workFieldId, workTypeId, pageable);
        }
        // Nếu không có tiêu chí nào được cung cấp, trả về tất cả công việc với phân trang
        return jobDetailRepository.findAll(pageable);
    }

    // Phương thức tìm kiếm kết hợp theo ngành nghề và hình thức làm việc (không áp dụng điều kiện trạng thái)
    public List<JobDetail> searchJobsByCombinedCriteria(Integer workFieldId, Integer workTypeId) {
        if ((workFieldId != null && workFieldId > 0) && (workTypeId != null && workTypeId > 0)) {
            // Nếu cả hai tiêu chí đều được cung cấp, sử dụng AND
            return jobDetailRepository.findByWorkFieldAndWorkType(workFieldId, workTypeId);
        } else if ((workFieldId != null && workFieldId > 0) || (workTypeId != null && workTypeId > 0)) {
            // Nếu chỉ một tiêu chí được cung cấp, sử dụng OR như trước
            return jobDetailRepository.findByWorkFieldOrWorkType(workFieldId, workTypeId);
        }
        // Nếu không có tiêu chí nào được cung cấp, trả về tất cả công việc
        return jobDetailRepository.findAll();
    }

    // Phương thức tìm kiếm kết hợp có phân trang
    public org.springframework.data.domain.Page<JobDetail> searchJobsByCombinedCriteriaWithPaging(Integer workFieldId, Integer workTypeId, org.springframework.data.domain.Pageable pageable) {
        if ((workFieldId != null && workFieldId > 0) && (workTypeId != null && workTypeId > 0)) {
            // Nếu cả hai tiêu chí đều được cung cấp, sử dụng AND
            return jobDetailRepository.findByWorkFieldAndWorkTypeWithPaging(workFieldId, workTypeId, pageable);
        } else if ((workFieldId != null && workFieldId > 0) || (workTypeId != null && workTypeId > 0)) {
            // Nếu chỉ một tiêu chí được cung cấp, sử dụng OR như trước
            return jobDetailRepository.findByWorkFieldOrWorkTypeWithPaging(workFieldId, workTypeId, pageable);
        }
        // Nếu không có tiêu chí nào được cung cấp, trả về tất cả công việc với phân trang
        return jobDetailRepository.findAll(pageable);
    }
}