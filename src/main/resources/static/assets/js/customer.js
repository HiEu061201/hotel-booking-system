$(
    document).ready(function () {
    $('#myTable').DataTable();
});


//popover
document.addEventListener("DOMContentLoaded", function () {
    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
    const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));
});


//popup
document.addEventListener("DOMContentLoaded", function () {
    const openPopupBtn = document.getElementById("openPopupBtn");
    const closePopupBtn = document.getElementById("closePopupBtn");
    const popup = document.getElementById("createPopup");
    const overlay = document.getElementById("overlay");

    openPopupBtn.addEventListener("click", (e) => {
        e.preventDefault(); // Prevent the form submission
        popup.style.display = "block";
        overlay.classList.add("active");
    });

    closePopupBtn.addEventListener("click", (e) => {
        e.preventDefault(); // Prevent the form submission
        popup.style.display = "none";
        overlay.classList.remove("active");
    })
});


//calculate payment
document.addEventListener("DOMContentLoaded", function () {
    // Get the necessary elements
    const depositPaymentRadio = document.getElementById('depositPayment');
    const fullPaymentRadio = document.getElementById('fullPayment');
    const paymentAmountElement = document.getElementById('paymentAmount');
    const totalPriceElement = document.getElementById('totalPrice');
    // const orderTotal = document.getElementById("orderTotal");

// Define the event listeners for the radio buttons
    depositPaymentRadio.addEventListener('change', updatePaymentAmount);
    fullPaymentRadio.addEventListener('change', updatePaymentAmount);

// Function to update the payment amount based on the selected option
    function updatePaymentAmount() {
        const totalPrice = parseFloat(totalPriceElement.innerText.replace(/\D/g, ''));
        let paymentAmount;

        if (depositPaymentRadio.checked) {
            paymentAmount = Math.floor(totalPrice * 0.5);
        } else if (fullPaymentRadio.checked) {
            paymentAmount = totalPrice;
        }

        // Update the payment amount element with the formatted value
        if (paymentAmount) {
            paymentAmountElement.innerText = paymentAmount.toLocaleString('en') + ' VND';

        }
    }

// Call the function initially to set the default payment amount
    updatePaymentAmount();


});

document.addEventListener("DOMContentLoaded", function () {
    const submitButton = document.getElementById("confirmBooking");
    const paymentAmountElement = document.getElementById("paymentAmount");

    submitButton.addEventListener("click", function (event) {
        const inputName = document.getElementById("inputName").value.trim();
        const inputEmail = document.getElementById("inputEmail").value.trim();
        const inputPhone = document.getElementById("inputPhone").value.trim();
        const inputAddress = document.getElementById("inputAddress").value.trim();
        const inputNote = document.getElementById("exampleFormControlTextarea1").value.trim();
        const orderInfo = document.getElementById("orderInfo").value.trim();

        const paymentAmountText = paymentAmountElement.innerText;
        const paymentAmount = parseFloat(paymentAmountText.replace(/[^\d.]/g, ''));
        const gridCheck = document.getElementById("gridCheck");

        var nameValidationMsg = document.getElementById("title-validation-name");
        var nameRegex = /^[\p{L}\s]+$/u;
 
        if (inputName.length == 0) {
            nameValidationMsg.textContent = "Tên không được để trống";
            return; // Prevent the form from being submitted
        } else if (inputName.length < 6 || inputName.length > 32) {
            nameValidationMsg.textContent = "Tên phải có từ 6 đến 32 ký tự";
            return; // Prevent the form from being submitted
        } else if (!nameRegex.test(inputName)) {
            nameValidationMsg.textContent = "Tên không hợp lệ";
            return;
        } else {
            nameValidationMsg.textContent = "";
        }


        var phoneValidationMsg = document.getElementById("title-validation-phone");
        var phoneRegex = /^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$/;
        if (inputPhone.length == 0) {
            phoneValidationMsg.textContent = "Điện thoại không được để trống";
            return; // Prevent the form from being submitted
        } else if (!phoneRegex.test(inputPhone)) {
            phoneValidationMsg.textContent = "Điện thoại không đúng";
            return;
        } else {
            phoneValidationMsg.textContent = "";
        }


        var emailValidationMsg = document.getElementById("title-validation-email");
        var emailRegex =
            /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

        if (inputEmail.length == 0) {

            emailValidationMsg.textContent = "Email không được để trống";
            return; // Prevent the form from being submitted
        } else if (!emailRegex.test(inputEmail)) {
            emailValidationMsg.textContent = "Email không đúng định dạng";
            return;
        } else {
            emailValidationMsg.textContent = "";
        }

        var addressValidationMsg = document.getElementById("title-validation-address");
        var addressMaxLength = 50;
        var addressMinLength = 10;

        if (inputAddress.length == 0) {
            addressValidationMsg.textContent = "địa chỉ không được để trống";
            return; // Prevent the form from being submitted
        } else if (inputAddress.length < addressMinLength || inputAddress.length > addressMaxLength) {
            addressValidationMsg.textContent = "địa chỉ phải có độ dài từ " + addressMinLength + " đến " + addressMaxLength + " ký tự.";
            return;
        } else {
            addressValidationMsg.textContent = "";
        }


        var textareaValidationMsg = document.getElementById("title-validation-textarea");
        var textareaMaxLength = 2000;
        var textareaMinLength = 10;

        if (inputNote.length > 0) {
            if (inputNote.length < textareaMinLength || inputNote.length > textareaMaxLength) {
                textareaValidationMsg.textContent = " Độ dài từ " + textareaMinLength + " đến " + textareaMaxLength + " ký tự.";
                return;
            } else {
                textareaValidationMsg.textContent = "";
            }
        } else {
            // If the input is empty, no need to check length
            textareaValidationMsg.textContent = "";
        }

        if (!gridCheck.checked) {
            event.preventDefault();

            var errorToastData = {
                title: "Lỗi!",
                message: 'Vui lòng đọc và đánh dấu vào ô bên cạnh.',
                type: "error",
                duration: 5000,

            };
            toastNotificationCountdown(
                errorToastData.title,
                errorToastData.message,
                errorToastData.type,
                errorToastData.duration
            );
        } else {

            const bookingData = {
                name: inputName,
                phone: inputPhone,
                email: inputEmail,
                address: inputAddress,
                paymentAmount: paymentAmount,
                orderInfo: orderInfo,
                note: inputNote
            };

            // Gửi dữ liệu JSON đến máy chủ
            fetch("/room/addBooking", {
                method: "POST",
                // mode: 'no-cors',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(bookingData)
            })
                .then(response => {
                    if (response.ok) {
                        var toastData = {
                            title: "Thành công!",
                            message: "Đặt phòng thành công, đi đến trang thanh toán",
                            type: "success",
                            duration: 5000,
                        };
                        toastNotificationCountdown(
                            toastData.title,
                            toastData.message,
                            toastData.type,
                            toastData.duration,
                        );
                        setTimeout(function () {
                            window.location.href = `/submitOrder`;
                        }, 2000);


                    } else {

                        var errorToastData = {
                            title: "Lỗi!",
                            message: 'Gửi thất bại',
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
                        message: 'Gửi thất bại' + error,
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

        }


    });


});
document.addEventListener("DOMContentLoaded", function () {
    const submitButton1 = document.getElementById("confirmBooking1");
    const paymentAmountElement = document.getElementById("paymentAmount");
    const orderInfo = document.getElementById("orderInfo").value;
    // Lắng nghe sự kiện click trên nút "Xác nhận đặt phòng"
    submitButton1.addEventListener("click", function (event) {
        if (checkUser != null || checkUser == null && isUserAuthenticated == true) {

            const paymentAmountText = paymentAmountElement.innerText;
            const paymentAmount = parseFloat(paymentAmountText.replace(/[^\d.]/g, ''));

            const bookingData = {
                paymentAmount: paymentAmount,
                orderInfo: orderInfo
            };

            // Gửi dữ liệu JSON đến máy chủ
            fetch("/room/addBooking", {
                method: "POST",
                // mode: 'no-cors',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(bookingData)
            })
                .then(response => {
                    if (response.ok) {
                        var toastData = {
                            title: "Thành công!",
                            message: "Đặt phòng thành công, đi đến trang thanh toán",
                            type: "success",
                            duration: 5000,
                        };
                        toastNotificationCountdown(
                            toastData.title,
                            toastData.message,
                            toastData.type,
                            toastData.duration
                        );

                        setTimeout(function () {
                            window.location.href = `/room/invoice`;
                        }, 2000);


                    } else {
                        var errorToastData = {
                            title: "Lỗi!",
                            message: 'Gửi thất bại',
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
                        message: 'Gửi thất bại' + error,
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

        } else {

            var errorToastData = {
                title: "Lỗi!",
                message: 'Hãy đặt phòng với tư cách là khách hàng',
                type: "error",
                duration: 5000,
            };
            toastNotificationCountdown(
                errorToastData.title,
                errorToastData.message,
                errorToastData.type,
                errorToastData.duration
            );
            return;
        }

    })
    ;


});


document.addEventListener("DOMContentLoaded", function () {
    const openservicePopupBtn = document.getElementById("viewServiceDetails");
    const closeservicePopupBtn = document.getElementById("closeServicePopupBtn");
    const popup = document.getElementById("servicePopup");
    const detailoverlay = document.getElementById("detailoverlay");

    openservicePopupBtn.addEventListener("click", (e) => {
        e.preventDefault(); // Prevent the form submission
        popup.style.display = "block";
        detailoverlay.classList.add("active");
    });

    closeservicePopupBtn.addEventListener("click", (e) => {
        e.preventDefault(); // Prevent the form submission
        popup.style.display = "none";
        detailoverlay.classList.remove("active");
    })
});