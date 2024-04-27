// createService

function preview1() {
    frame1.src = URL.createObjectURL(event.target.files[0]);
}

function clearImage() {
    document.getElementById('formFile1').value = null;
    frame1.src = "";
}


document.addEventListener('DOMContentLoaded', function () {
    const confirmButton = document.getElementById('addService');


    confirmButton.addEventListener('click', function () {
        const service_name1 = document.getElementById('service_name1').value;
        const service_price1 = document.getElementById('service_price1').value;
        const status1 = document.getElementById('status1').value;
        const serviceDes1 = document.getElementById('serviceDes1').value;

        var vietnameseNameRegex = /^[\p{L}\s]+$/u;


        document.getElementById('serviceNameError1').innerText = '';
        document.getElementById('servicePriceError1').innerText = '';
        document.getElementById('serviceDesError1').innerText = '';
        document.getElementById('preview1').innerText = '';
        var fileInput = document.getElementById('formFile1');
        var preview = document.getElementById('preview1');

        if (fileInput.files.length > 0) {
            var file = fileInput.files[0];
            var allowedTypes = ['image/jpeg', 'image/png'];

            if (!allowedTypes.includes(file.type)) {
                preview.innerHTML = 'Chỉ chấp nhận file ảnh (JPEG, PNG)';
                return;
            }
        } else {
            const selectedFile = fileInput.files[0];
            if (!selectedFile) {
                preview.innerText = 'Vui lòng chọn ảnh.';
                return;
            }
        }


        if (!service_name1) {
            document.getElementById('serviceNameError1').innerText = 'Vui lòng nhập tên.';
            return;
        }
        if (!vietnameseNameRegex.test(service_name1)) {
            document.getElementById('serviceNameError1').innerText = 'Tên không hợp lệ';
            return;
        }
        if (service_name1.length < 6 || service_name1.length > 33) {
            document.getElementById('serviceNameError1').innerText = 'Độ dài của tên dịch vụ phải từ 6 đến 33 kí tự.';
            return;
        }


        if (service_price1 == '') {
            document.getElementById('servicePriceError1').innerText = 'Vui lòng nhập giá tiền.';
            return;
        }

        if (!/^\d+$/.test(service_price1)) {
            document.getElementById('servicePriceError1').innerText = 'Giá tiền không hợp lệ';
            return;
        }
        // Kiểm tra giá trị tiền không quá 10 chữ số và hiển thị thông báo lỗi
        if (service_price1.length > 10 || service_price1.length < 6) {
            document.getElementById('servicePriceError1').innerText = 'Số tiền nhập từ 6 tới 10 số.';
            return;
        }


        if (serviceDes1.trim() !== '') {
            if (serviceDes1.length < 10 || serviceDes1.length > 2000) {
                document.getElementById('serviceDesError1').innerText = 'Độ dài phải từ 10 đến 2000 kí tự.';
                return;
            }
        }


        var formData = new FormData();
        formData.append('serviceName', service_name1);
        formData.append('servicePrice', service_price1);
        formData.append('serviceDes', serviceDes1);
        formData.append('status', status1);
        formData.append('serviceImage', fileInput.files[0]);

        const url = '/saleManager/createService';

        fetch(url, {
            method: 'POST',
            // headers: {
            //     'Content-Type': 'application/json',
            // },
            body: formData,
        })
            .then(response => response.json())
            .then(data => {
                if (data && data.message === "Thêm dịch vụ thành công") {

                    var toastData = {
                        title: "Thành công!",
                        message: "Thêm nhật dịch vụ thành công.",
                        type: "success",
                        duration: 5000,
                    };
                    localStorage.setItem('toastData', JSON.stringify(toastData));
                    setTimeout(function () {
                        window.location.href = `/saleManager/viewService`;
                    }, 100);

                } else {
                    var errorToastData = {
                        title: "Lỗi!",
                        message: (data && data.error ? data.error : (data && data.exceptionMessage ? data.exceptionMessage : 'Lỗi khi thêm.')),
                        type: "error",
                        duration: 5000,
                    };
                    toastNotificationCountdown(
                        errorToastData.title,
                        errorToastData.message,
                        errorToastData.type,
                        errorToastData.duration
                    );
                }
            })
            .catch(error => {

                var errorToastData = {
                    title: "Lỗi!",
                    message: error,
                    type: "error",
                    duration: 5000,
                };
                toastNotificationCountdown(
                    errorToastData.title,
                    errorToastData.message,
                    errorToastData.type,
                    errorToastData.duration
                );
            });
    });
});