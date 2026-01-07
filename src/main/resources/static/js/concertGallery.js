function openModal(element) {
    const modal = document.getElementById("galleryModal");
    const modalImg = document.getElementById("modalImg");
    const modalVideo = document.getElementById("modalVideo");

    // 1. Get the data from the clicked element
    const type = element.getAttribute("data-type"); // Ensure your <img> and <video> in the grid have th:data-type
    const source = element.getAttribute("src");

    // 2. Hide both initially and stop any playing video
    modalImg.style.display = "none";
    modalVideo.style.display = "none";
    modalVideo.pause();

    // 3. Show the correct one
    if (type === 'image') {
        modalImg.src = source;
        modalImg.style.display = "block";
    } else {
        modalVideo.src = source;
        modalVideo.style.display = "block";
        modalVideo.load(); // This "refreshes" the video player with the new source
        modalVideo.play();
    }

    // 4. Set Text
    document.getElementById("modalAuthor").innerText = "Posted by: " + element.getAttribute("data-author");
    document.getElementById("modalCaption").innerText = element.getAttribute("data-caption");

    modal.style.display = "flex";
}

function closeModal() {
    const modal = document.getElementById("galleryModal");
    const modalVideo = document.getElementById("modalVideo");

    modal.style.display = "none";
    modalVideo.pause(); // Stop the sound when closing!
    modalVideo.src = "";
}