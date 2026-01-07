    document.addEventListener("DOMContentLoaded", () => {
        const submitBtn = document.getElementById("submitLineupBtn");
        const addBtn = document.getElementById("addArtistBtn");
        const container = document.getElementById("artistContainer");

        const getFirstSelect = () => container.querySelector("select");

        function updateButtons() {
            const hasSelection = Boolean(getFirstSelect().value);

            // Update submit button text
            submitBtn.textContent = hasSelection
                ? "Submit Lineup"
                : "Skip";

            // Show/hide "Add another artist"
            addBtn.style.display = hasSelection ? "inline-block" : "none";
        }

        // Initial state
        updateButtons();

        // Listen for changes on any select
        container.addEventListener("change", updateButtons);

        // Add artist functionality
        const template = container.querySelector(".artist-select").outerHTML;

        addBtn.addEventListener("click", () => {
            container.insertAdjacentHTML("beforeend", template);
        });
    });