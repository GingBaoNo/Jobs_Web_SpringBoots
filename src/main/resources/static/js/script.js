// Custom JavaScript for the job portal
document.addEventListener('DOMContentLoaded', function() {
    // Handle job application
    const applyButtons = document.querySelectorAll('.apply-btn');
    applyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const jobId = this.getAttribute('data-job-id');
            if (!jobId) {
                alert('Không thể xác định công việc cần ứng tuyển');
                return;
            }
            
            // Confirmation for job application
            if (confirm('Bạn có chắc chắn muốn ứng tuyển vào công việc này?')) {
                // Submit application via AJAX
                fetch('/apply-job', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
                    },
                    body: JSON.stringify({ jobId: jobId })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Ứng tuyển thành công!');
                        this.disabled = true;
                        this.textContent = 'Đã ứng tuyển';
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra khi ứng tuyển');
                });
            }
        });
    });
    
    // Handle job saving
    const saveButtons = document.querySelectorAll('.save-btn');
    saveButtons.forEach(button => {
        button.addEventListener('click', function() {
            const jobId = this.getAttribute('data-job-id');
            if (!jobId) {
                alert('Không thể xác định công việc cần lưu');
                return;
            }
            
            // Toggle save status
            const isSaved = this.classList.contains('saved');
            const action = isSaved ? 'remove' : 'save';
            
            fetch('/save-job', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
                },
                body: JSON.stringify({ jobId: jobId, action: action })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    if (isSaved) {
                        this.classList.remove('saved', 'text-danger');
                        this.classList.add('text-secondary');
                        this.innerHTML = '<i class="far fa-bookmark"></i> Lưu';
                    } else {
                        this.classList.remove('text-secondary');
                        this.classList.add('saved', 'text-danger');
                        this.innerHTML = '<i class="fas fa-bookmark"></i> Đã lưu';
                    }
                } else {
                    alert('Lỗi: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi lưu công việc');
            });
        });
    });
    
    // Handle responsive navigation
    const navbarToggler = document.querySelector('.navbar-toggler');
    if (navbarToggler) {
        navbarToggler.addEventListener('click', function() {
            const navbarCollapse = document.querySelector('.navbar-collapse');
            navbarCollapse.classList.toggle('show');
        });
    }
    
    // Add smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });
    
    // Handle form validation
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
    
    // Job search functionality
    const searchForm = document.getElementById('job-search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(event) {
            event.preventDefault();
            
            const formData = new FormData(searchForm);
            let queryString = '?';
            
            for (const [key, value] of formData.entries()) {
                if (value) {
                    queryString += `${encodeURIComponent(key)}=${encodeURIComponent(value)}&`;
                }
            }
            
            queryString = queryString.slice(0, -1); // Remove the last '&'
            
            // Redirect to search results page
            window.location.href = '/jobs' + queryString;
        });
    }
    
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
    
    // Handle image preview for file uploads
    const imageUpload = document.getElementById('imageUpload');
    if (imageUpload) {
        imageUpload.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    document.getElementById('imagePreview').src = event.target.result;
                }
                reader.readAsDataURL(file);
            }
        });
    }
});

// Utility functions
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
    notification.style.zIndex = '9999';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(notification);
        bsAlert.close();
    }, 5000);
}

function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}