var today = new Date();
var dd = String(today.getDate()).padStart(2, '0');
var mm = String(today.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
var yyyy = today.getFullYear();
var formattedDate = yyyy + '-' + mm + '-' + dd;
var nextDay = new Date(today);
nextDay.setDate(nextDay.getDate() + 1);
var nextDayFormatted = nextDay.toISOString().split("T")[0];

// Lấy giá trị từ các trường nhập liệu
var checkInDateInput = document.getElementById("checkInDate");
var checkOutDateInput = document.getElementById("checkOutDate");
var numberOfPeopleInput = document.getElementById("numberOfPeople");

// Lấy giá trị từ trường "checkIn"
var checkInValue = checkInDateInput.value;

// Lấy giá trị từ trường "checkOut"
var checkOutValue = checkOutDateInput.value;

// Lấy giá trị số người đã chọn
var numberOfPeopleValue = numberOfPeopleInput.value;

// Kiểm tra và xử lý ngày
checkInDateInput.addEventListener("input", function () {
    var checkInValue = checkInDateInput.value;
    if (!isValidDate(checkInValue)) {

        var errorToastData = {
            title: "Lỗi!",
            message: 'Ngày nhận phòng không hợp lệ. Vui lòng nhập lại',
            type: "error",
            duration: 5000,
        };
        toastNotificationCountdown(
            errorToastData.title,
            errorToastData.message,
            errorToastData.type,
            errorToastData.duration
        );
        checkInDateInput.value = formattedDate; // Đặt lại ngày nhận phòng thành ngày hôm nay
    }
});

checkOutDateInput.addEventListener("input", function () {
    var checkOutValue = checkOutDateInput.value;
    if (!isValidDate(checkOutValue)) {
        var errorToastData = {
            title: "Lỗi!",
            message: 'Ngày trả phòng không hợp lệ',
            type: "error",
            duration: 5000,
        };
        toastNotificationCountdown(
            errorToastData.title,
            errorToastData.message,
            errorToastData.type,
            errorToastData.duration
        );
        checkOutDateInput.value = nextDayFormatted; // Đặt lại ngày trả phòng thành ngày mai
    }
});


// Hàm kiểm tra định dạng ngày hợp lệ
function isValidDate(dateString) {
    var pattern = /^\d{4}-\d{2}-\d{2}$/;
    return pattern.test(dateString) && !isNaN(Date.parse(dateString));
}

// Xử lý khi người dùng nhấn nút "Tìm Phòng"
var submitButton = document.querySelector("button[type='submit']");
submitButton.addEventListener("click", function () {
    if (checkInDateInput.value >= checkOutDateInput.value) {
        var errorToastData = {
            title: "Lỗi!",
            message: 'Ngày trả phòng phải sau ngày nhận phòng. Vui lòng nhập lại',
            type: "error",
            duration: 5000,
        };
        toastNotificationCountdown(
            errorToastData.title,
            errorToastData.message,
            errorToastData.type,
            errorToastData.duration
        );
        event.preventDefault();
    }
});

// Ngăn người dùng chọn ngày trước đó
var today = new Date().toISOString().split("T")[0];
document.getElementById("checkInDate").min = today;
document.getElementById("checkOutDate").min = today;

// Lấy các tham số truy vấn từ URL
var urlParams = new URLSearchParams(window.location.search);
var checkInParam = urlParams.get("checkIn");
var checkOutParam = urlParams.get("checkOut");
var numberOfPeopleParam = urlParams.get("numberOfPeople");

// Đặt lại giá trị trường "checkIn" nếu có tham số truy vấn
if (checkInParam) {
    document.getElementById("checkInDate").value = checkInParam;
}

// Đặt lại giá trị trường "checkOut" nếu có tham số truy vấn
if (checkOutParam) {
    document.getElementById("checkOutDate").value = checkOutParam;
}

// Đặt lại giá trị trường "numberOfPeople" nếu có tham số truy vấn
if (numberOfPeopleParam) {
    document.getElementById("numberOfPeople").value = numberOfPeopleParam;
}


// Lấy giá trị từ các trường input
var checkInDate = new Date(document.getElementById("checkInDate").value);
var checkOutDate = new Date(document.getElementById("checkOutDate").value);

// Tính hiệu giữa hai ngày trong mili giây
var timeDifference = checkOutDate - checkInDate;

// Chuyển đổi mili giây thành số ngày
var numberOfDays = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));

// Hiển thị kết quả trong thẻ p
var numberOfNightsElement = document.getElementById("numberOfNights");
numberOfNightsElement.textContent = "cho " + numberOfDays + " đêm";




