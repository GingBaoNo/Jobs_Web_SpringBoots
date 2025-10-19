// Hàm để tải thông tin hồ sơ hiện tại
async function loadMyProfile() {
    try {
        const response = await fetch('/api/v1/profiles/my-profile');
        const result = await response.json();
        
        if (result.success) {
            const profile = result.data;
            document.getElementById('fullName').value = profile.hoTen || '';
            document.getElementById('gender').value = profile.gioiTinh || '';
            document.getElementById('dateOfBirth').value = profile.ngaySinh || '';
            document.getElementById('phone').value = profile.soDienThoai || '';
            document.getElementById('educationLevel').value = profile.trinhDoHocVan || '';
            document.getElementById('eduStatus').value = profile.tinhTrangHocVan || '';
            document.getElementById('experience').value = profile.kinhNghiem || '';
            document.getElementById('totalExperience').value = profile.tongNamKinhNghiem || '';
            document.getElementById('bio').value = profile.gioiThieuBanThan || '';
            document.getElementById('cvUrl').value = profile.urlCv || '';
            document.getElementById('publicProfile').checked = profile.congKhai || false;
            document.getElementById('desiredPosition').value = profile.viTriMongMuon || '';
            document.getElementById('desiredTime').value = profile.thoiGianMongMuon || '';
            document.getElementById('workTimeType').value = profile.loaiThoiGianLamViec || '';
            document.getElementById('workForm').value = profile.hinhThucLamViec || '';
            document.getElementById('salaryType').value = profile.loaiLuongMongMuon || '';
            document.getElementById('desiredSalary').value = profile.mucLuongMongMuon || '';
        } else {
            console.log('Không tìm thấy hồ sơ:', result.message);
            // Nếu chưa có hồ sơ thì không có gì để làm
        }
    } catch (error) {
        console.error('Lỗi khi tải hồ sơ:', error);
    }
}

// Hàm để cập nhật hồ sơ
async function updateProfile(event) {
    event.preventDefault();
    
    const profileData = {
        hoTen: document.getElementById('fullName').value,
        gioiTinh: document.getElementById('gender').value,
        ngaySinh: document.getElementById('dateOfBirth').value,
        soDienThoai: document.getElementById('phone').value,
        trinhDoHocVan: document.getElementById('educationLevel').value,
        tinhTrangHocVan: document.getElementById('eduStatus').value,
        kinhNghiem: document.getElementById('experience').value,
        tongNamKinhNghiem: document.getElementById('totalExperience').value ? parseFloat(document.getElementById('totalExperience').value) : 0,
        gioiThieuBanThan: document.getElementById('bio').value,
        urlCv: document.getElementById('cvUrl').value,
        congKhai: document.getElementById('publicProfile').checked,
        viTriMongMuon: document.getElementById('desiredPosition').value,
        thoiGianMongMuon: document.getElementById('desiredTime').value,
        loaiThoiGianLamViec: document.getElementById('workTimeType').value,
        hinhThucLamViec: document.getElementById('workForm').value,
        loaiLuongMongMuon: document.getElementById('salaryType').value,
        mucLuongMongMuon: document.getElementById('desiredSalary').value ? parseInt(document.getElementById('desiredSalary').value) : null
    };
    
    try {
        const response = await fetch('/api/v1/profiles/my-profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('Cập nhật hồ sơ thành công!');
            loadMyProfile(); // Load lại thông tin sau khi cập nhật
        } else {
            alert('Lỗi khi cập nhật hồ sơ: ' + result.message);
        }
    } catch (error) {
        console.error('Lỗi khi gửi yêu cầu cập nhật:', error);
        alert('Lỗi kết nối mạng');
    }
}

// Hàm để tạo hồ sơ mới nếu chưa tồn tại
async function createProfile(event) {
    event.preventDefault();
    
    const profileData = {
        hoTen: document.getElementById('fullName').value,
        gioiTinh: document.getElementById('gender').value,
        ngaySinh: document.getElementById('dateOfBirth').value,
        soDienThoai: document.getElementById('phone').value,
        trinhDoHocVan: document.getElementById('educationLevel').value,
        tinhTrangHocVan: document.getElementById('eduStatus').value,
        kinhNghiem: document.getElementById('experience').value,
        tongNamKinhNghiem: document.getElementById('totalExperience').value ? parseFloat(document.getElementById('totalExperience').value) : 0,
        gioiThieuBanThan: document.getElementById('bio').value,
        urlCv: document.getElementById('cvUrl').value,
        congKhai: document.getElementById('publicProfile').checked,
        viTriMongMuon: document.getElementById('desiredPosition').value,
        thoiGianMongMuon: document.getElementById('desiredTime').value,
        loaiThoiGianLamViec: document.getElementById('workTimeType').value,
        hinhThucLamViec: document.getElementById('workForm').value,
        loaiLuongMongMuon: document.getElementById('salaryType').value,
        mucLuongMongMuon: document.getElementById('desiredSalary').value ? parseInt(document.getElementById('desiredSalary').value) : null
    };
    
    try {
        const response = await fetch('/api/v1/profiles/my-profile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('Tạo hồ sơ thành công!');
            loadMyProfile(); // Load lại thông tin sau khi tạo
        } else {
            alert('Lỗi khi tạo hồ sơ: ' + result.message);
        }
    } catch (error) {
        console.error('Lỗi khi gửi yêu cầu tạo hồ sơ:', error);
        alert('Lỗi kết nối mạng');
    }
}

// Xác định xem đang tạo hay cập nhật hồ sơ
async function saveProfile(event) {
    event.preventDefault();
    
    try {
        // Gửi yêu cầu GET để kiểm tra xem hồ sơ đã tồn tại chưa
        const response = await fetch('/api/v1/profiles/my-profile');
        const result = await response.json();
        
        if (result.success) {
            // Hồ sơ đã tồn tại, cập nhật
            updateProfile(event);
        } else {
            // Hồ sơ chưa tồn tại, tạo mới
            createProfile(event);
        }
    } catch (error) {
        console.error('Lỗi khi kiểm tra hồ sơ:', error);
        alert('Lỗi kết nối mạng');
    }
}

// Gán sự kiện cho form và nút reset
document.addEventListener('DOMContentLoaded', function() {
    // Gán sự kiện submit cho form
    document.getElementById('profile-form').addEventListener('submit', saveProfile);
    
    // Gán sự kiện cho nút reset
    document.getElementById('resetBtn').addEventListener('click', function() {
        loadMyProfile(); // Load lại thông tin hiện tại (hủy các thay đổi)
    });
    
    // Tải thông tin hồ sơ khi trang được tải
    loadMyProfile();
});