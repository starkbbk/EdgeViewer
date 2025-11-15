"use strict";
const img = document.getElementById("edge-image");
const info = document.getElementById("info");
// For the assignment, it's enough to show a static processed PNG
// exported from your Android app.
img.onload = () => {
    info.innerText = `Resolution: ${img.naturalWidth}x${img.naturalHeight} | Source: processed frame`;
};
