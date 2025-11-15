const img = document.getElementById("edge-image") as HTMLImageElement;
const info = document.getElementById("info") as HTMLDivElement;

// For the assignment, it's enough to show a static processed PNG
// exported from your Android app.
img.onload = () => {
  info.innerText = `Resolution: ${img.naturalWidth}x${img.naturalHeight} | Source: processed frame`;
};