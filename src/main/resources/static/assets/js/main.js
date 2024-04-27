// Header Scroll
let nav = document.querySelector(".navbar");
window.onscroll = function() {
	if (document.documentElement.scrollTop > 50) {
		nav.classList.add("header-scrolled");
	} else {
		nav.classList.remove("header-scrolled");
	}
	
	let scrollToTopButton = document.getElementById("scrollToTopBtn");
    if (document.documentElement.scrollTop > 300) { // Adjust this value as needed
        scrollToTopButton.style.display = "block";
    } else {
        scrollToTopButton.style.display = "none";
    }
}

// Scroll to Top Function
function scrollToTop() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

// Add Event Listener for Scroll To Top Button
document.getElementById("scrollToTopBtn").addEventListener("click", function() {
    scrollToTop();
});

// Swiper Slider
var swiper = new Swiper(".mySwiper", {
	direction: "vertical",
	loop: true,
	pagination: {
		el: ".swiper-pagination",
		clickable: true,
	},
	autoplay: {
		delay: 3500,
	},
});

// nav hide 
let navBar = document.querySelectorAll(".nav-link");
let navCollapse = document.querySelector(".navbar-collapse.collapse");
navBar.forEach(function(a) {
	a.addEventListener("click", function() {
		navCollapse.classList.remove("show");
	})
})

// Our Partner
var swiper = new Swiper(".our-partner", {
	slidesPerView: 5,
	spaceBetween: 30,
	loop: true,
	autoplay: {
		delay: 2000,
	},
	breakpoints: {
		'991': {
			slidesPerView: 5,
			spaceBetween: 10,
		},
		'767': {
			slidesPerView: 3,
			spaceBetween: 10,
		},
		'320': {
			slidesPerView: 2,
			spaceBetween: 8,
		},


	},
});

//Hidden password
function password_show_hide(number) {
	if (number == 1) {
		var x = document.getElementById("password1");
		var show_eye = document.getElementById("show_eye1");
		var hide_eye = document.getElementById("hide_eye1");
		hide_eye.classList.remove("d-none");
		if (x.type === "password") {
			x.type = "text";
			show_eye.style.display = "none";
			hide_eye.style.display = "block";
		} else {
			x.type = "password";
			show_eye.style.display = "block";
			hide_eye.style.display = "none";
		}
	} else if (number == 2) {
		var x = document.getElementById("password2");
		var show_eye = document.getElementById("show_eye2");
		var hide_eye = document.getElementById("hide_eye2");
		hide_eye.classList.remove("d-none");
		if (x.type === "password") {
			x.type = "text";
			show_eye.style.display = "none";
			hide_eye.style.display = "block";
		} else {
			x.type = "password";
			show_eye.style.display = "block";
			hide_eye.style.display = "none";
		}
	} else {
		var x = document.getElementById("password3");
		var show_eye = document.getElementById("show_eye3");
		var hide_eye = document.getElementById("hide_eye3");
		hide_eye.classList.remove("d-none");
		if (x.type === "password") {
			x.type = "text";
			show_eye.style.display = "none";
			hide_eye.style.display = "block";
		} else {
			x.type = "password";
			show_eye.style.display = "block";
			hide_eye.style.display = "none";
		}
	}

}

function menuToggle() {
	const toggleMenu = document.querySelector(".menu");
	toggleMenu.classList.toggle('active')
}


