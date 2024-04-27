$(document).ready( function () {
    $('#myTable').DataTable();
} );

//popover
document.addEventListener("DOMContentLoaded", function () {
    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
    const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));
});


document.addEventListener('DOMContentLoaded', function() {
    const userButton = document.getElementById('userButton');
    const userMenu = document.getElementById('userMenu');

    userButton.addEventListener('click', function(event) {
        event.stopPropagation(); // Ngăn chặn sự lan truyền của sự kiện click

        const isMenuDisplayed = userMenu.classList.contains('show');

        if (isMenuDisplayed) {
            userMenu.classList.remove('show');
        } else {
            // Ẩn tất cả các dropdown menu khác nếu có
            const allMenus = document.querySelectorAll('.user-menu');
            allMenus.forEach(menu => {
                if (menu !== userMenu) {
                    menu.classList.remove('show');
                }
            });

            userMenu.classList.add('show');
        }
    });

    document.addEventListener('click', function(event) {
        if (!userButton.contains(event.target)) {
            userMenu.classList.remove('show');
        }
    });
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
