// Function to display toast notification with countdown and progress bar
function toastNotificationCountdown(title, message, type, duration) {
    var toastContainer = document.getElementById('toast');

    var toastElement = document.createElement('div');
    toastElement.className = 'toast align-items-center'; // Add your desired classes

    // Set toast type
    if (type === 'success') {
        toastElement.classList.add('bg-white', 'text-white');
    } else if (type === 'error') {
        toastElement.classList.add('bg-white', 'text-white');
    } else {
        toastElement.classList.add('bg-white', 'text-white');
    }

    var toastHeader = document.createElement('div');
    toastHeader.className = 'toast-header';
    var iconClass = '';
    switch (type) {
        case 'success':
            iconClass = 'fa-check-circle fa-success';
            break;
        case 'error':
            iconClass = 'fa-times-circle fa-danger';
            break;
        case 'info':
            iconClass = 'fa-info-circle fa-info';
            break;
        default:
            iconClass = 'fa-info-circle fa-info';
            break;
    }

    var toastIcon = document.createElement('i');
    toastIcon.className = 'fas ' + iconClass;

    var toastTitle = document.createElement('strong');
    toastTitle.className = 'me-auto';
    toastTitle.innerText = title;

    var toastCloseButton = document.createElement('button');
    toastCloseButton.type = 'button';
    toastCloseButton.className = 'btn-close';
    toastCloseButton.setAttribute('data-bs-dismiss', 'toast');
    toastCloseButton.setAttribute('aria-label', 'Close');

	toastHeader.appendChild(toastIcon);
    toastHeader.appendChild(toastTitle);
    toastHeader.appendChild(toastCloseButton);

    var toastBody = document.createElement('div');
    toastBody.className = 'toast-body';
    toastBody.innerText = message;


    // Progress bar container
    var progressContainer = document.createElement('div');
    progressContainer.className = 'progress mt-2';

    // Progress bar
    var countdownBar = document.createElement('div');
    countdownBar.className = 'progress-bar';
    countdownBar.setAttribute('role', 'progressbar');
    countdownBar.setAttribute('aria-valuemin', '0');
    countdownBar.setAttribute('aria-valuemax', '100');
    
    switch (type) {
        case 'success':
            countdownBar.classList.add('bg-success');
            break;
        case 'error':
            countdownBar.classList.add('bg-danger');
            break;
        case 'info':
            countdownBar.classList.add('bg-info');
            break;
        default:
            countdownBar.classList.add('bg-info');
            break;
    }

    progressContainer.appendChild(countdownBar);

    toastElement.appendChild(toastHeader);
    toastElement.appendChild(toastBody);
    toastElement.appendChild(progressContainer);

    toastContainer.appendChild(toastElement);

    // Calculate progress bar step
    var interval = 100; // Set your desired interval value
    var step = (duration / 100) * interval;

    // Create a new instance of Bootstrap Toast
    var bsToast = new bootstrap.Toast(toastElement);

    // Show the toast
    bsToast.show();

    var startTime = Date.now();
    var progressBarWidth = 100;

    function updateCountdownBar() {
        var elapsed = Date.now() - startTime;
        var remainingTime = duration - elapsed;
        var percentage = (remainingTime / duration) * 100;
        countdownBar.style.width = percentage + '%';

        if (percentage > 0) {
            setTimeout(updateCountdownBar, interval);
        } else {
            // Hide the toast when the progress bar reaches 0%
            bsToast.hide();
        }
    }

    // Start updating the countdown bar
    setTimeout(updateCountdownBar, interval);
}


// Function to display stored toast data on page load
function displayStoredToast() {
    var storedToastData = localStorage.getItem('toastData');
    if (storedToastData) {
        var toastData = JSON.parse(storedToastData);
        toastNotificationCountdown(
            toastData.title,
            toastData.message,
            toastData.type,
            toastData.duration,
            toastData.animationDuration
        );

        // Clear the stored data after displaying the toast
        localStorage.removeItem('toastData');
    }
}

// Call the function to display the stored toast when the page loads
window.onload = function () {
    displayStoredToast();
};