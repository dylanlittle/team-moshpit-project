document.addEventListener("DOMContentLoaded", () => {
    const selector = document.getElementById("dates-selector");
    const upcoming = document.getElementById("upcoming-dates");
    const previous = document.getElementById("previous-dates");

    function toggleDates() {
        if (selector.value === "upcoming") {
            upcoming.hidden = false;
            previous.hidden = true;
        } else {
            upcoming.hidden = true;
            previous.hidden = false;
        }
    }

    // Initial state
    toggleDates();

    // Listen for changes
    selector.addEventListener("change", toggleDates);
});