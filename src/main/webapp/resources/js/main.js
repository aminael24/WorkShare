document.addEventListener("DOMContentLoaded", function () {
    const mobileNavToggleBtn = document.querySelector(".mobile-nav-toggle");
    const body = document.body;
    const navLinks = document.querySelectorAll("#navmenu a");

    function mobileNavToggle() {
        body.classList.toggle("mobile-nav-active");
        mobileNavToggleBtn.classList.toggle("bi-list");
        mobileNavToggleBtn.classList.toggle("bi-x");
    }

    if (mobileNavToggleBtn) {
        mobileNavToggleBtn.addEventListener("click", mobileNavToggle);
    }

    navLinks.forEach(function (link) {
        link.addEventListener("click", function () {
            if (body.classList.contains("mobile-nav-active")) {
                mobileNavToggle();
            }
        });
    });
});