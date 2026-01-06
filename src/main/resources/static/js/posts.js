
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("new-post-form");
    const mediaInput = document.getElementById("media_file");
    const clearBtn = document.getElementById("clearMediaBtn");

    const uploadWrapper = document.getElementById("uploadWrapper");
    const bar = document.getElementById("uploadProgressBar");
    const status = document.getElementById("uploadStatus");

    const mediaUrlHidden = document.getElementById("mediaUrl");
    const mediaTypeHidden = document.getElementById("mediaType");
    const submitBtn = document.getElementById("post-submit");

    clearBtn.classList.add("d-none");

    mediaInput.addEventListener("change", function () {
        if (mediaInput.files && mediaInput.files.length > 0) {
            clearBtn.classList.remove("d-none");
        } else {
            clearBtn.classList.add("d-none");
        }
    });

    clearBtn.addEventListener("click", function () {
        mediaInput.value = "";
        clearBtn.classList.add("d-none");
    });

    function showProgress() {
        uploadWrapper.classList.remove("d-none");
        bar.style.width = "0%";
        bar.textContent = "0%";
        status.textContent = "Uploading…";
    }

    function hideProgress() {
        uploadWrapper.classList.add("d-none");
        bar.style.width = "0%";
        bar.textContent = "";
        status.textContent = "";
    }

    hideProgress();

    if (clearBtn && mediaInput) {
        clearBtn.addEventListener("click", function () {
            mediaInput.value = "";
            clearBtn.classList.add("d-none");
            hideProgress();
        });
    }

    if (mediaInput) {
        mediaInput.addEventListener("change", function () {
            if (mediaInput.files && mediaInput.files.length > 0) {
                clearBtn.classList.remove("d-none");
            } else {
                clearBtn.classList.add("d-none");
                hideProgress();
            }
        });
    }

    form.addEventListener("submit", async function (e) {
        const file = mediaInput.files && mediaInput.files[0];

        if (!file) {
            hideProgress();
            return;
        }

        e.preventDefault();
        submitBtn.disabled = true;
        showProgress();

        const fileType = (file.type || "").toLowerCase();
        let inferredMediaType = "image";
        if (fileType.startsWith("audio/")) inferredMediaType = "audio";
        else if (fileType.startsWith("video/")) inferredMediaType = "video";

        try {
            const sigRes = await fetch("/cloudinary/signature");
            const sig = await sigRes.json();

            const fd = new FormData();
            fd.append("file", file);
            fd.append("api_key", sig.apiKey);
            fd.append("timestamp", sig.timestamp);
            fd.append("signature", sig.signature);
            fd.append("folder", sig.folder);

            const uploadUrl =
                `https://api.cloudinary.com/v1_1/${sig.cloudName}/auto/upload`;

            const uploaded = await new Promise((resolve, reject) => {
                const xhr = new XMLHttpRequest();
                xhr.open("POST", uploadUrl);

                xhr.upload.onprogress = function (evt) {
                    if (evt.lengthComputable) {
                        const pct = Math.round((evt.loaded / evt.total) * 100);
                        bar.style.width = pct + "%";
                        bar.textContent = pct + "%";
                    }
                };

                xhr.onload = function () {
                    if (xhr.status >= 200 && xhr.status < 300) {
                        resolve(JSON.parse(xhr.responseText));
                    } else {
                        reject(new Error("Upload failed"));
                    }
                };

                xhr.onerror = () => reject(new Error("Network error"));
                xhr.send(fd);
            });

            mediaUrlHidden.value = uploaded.secure_url;
            mediaTypeHidden.value = inferredMediaType;

            mediaInput.disabled = true;

            hideProgress();

            form.submit();

        } catch (err) {
            console.error(err);
            status.textContent = "Upload failed. Please try again.";
            submitBtn.disabled = false;
        }
    });
});
