document.addEventListener("DOMContentLoaded", function () {

    // Retrieve values from hidden input fields
    var checkInInput = document.getElementById("checkIn");
    var checkOutInput = document.getElementById("checkOut");
    var cancelInput = document.getElementById("cancel");

    // Kiểm tra xem dữ liệu có tồn tại hay không
    if (checkInInput && checkOutInput && cancelInput) {
        var checkInValue = parseInt(checkInInput.value);
        var checkOutValue = parseInt(checkOutInput.value);
        var cancelValue = parseInt(cancelInput.value);

        // Create an array with the retrieved values
        var yValues = [cancelValue, checkInValue, checkOutValue];

        var xValues = ["Hủy bỏ", "Đã nhận phòng", "Đã trả phòng"];
        var barColors = ["#b91d47", "#00aba9", "#2b5797"];

        new Chart("doughnutChart", {
            type: "doughnut",
            data: {
                labels: xValues,
                datasets: [{
                    backgroundColor: barColors,
                    data: yValues
                }]
            },
            options: {
                title: {
                    display: true,
                }
            }
        });
    } else {
        console.error("Dữ liệu trống. Không thể tạo biểu đồ.");
    }
});


document.addEventListener("DOMContentLoaded", function () {
    var vacancyPercentage = parseInt(document.getElementById("vacancyPercentage").value);
    var occupancyPercentage = parseInt(document.getElementById("occupancyPercentage").value);
    var xValues = ["Tỉ lệ lấp đầy", "Tỷ lệ trống"];
    var yValues = [occupancyPercentage, vacancyPercentage];
    var barColors = [
        "#1e7145",
        "#e8c3b9"
    ];

    new Chart("doughnutChart2", {
        type: "doughnut",
        data: {
            labels: xValues,
            datasets: [{
                backgroundColor: barColors,
                data: yValues
            }]
        },
        options: {
            title: {
                display: true,
            }
        }
    });
});
