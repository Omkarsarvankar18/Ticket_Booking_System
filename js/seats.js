
const API_BASE_URL = "http://localhost:8080/api/v1";

const selectedShow = JSON.parse(localStorage.getItem("selectedShow"));
const selectedMovie = JSON.parse(localStorage.getItem("selectedMovie"));
const selectedTheatre = JSON.parse(localStorage.getItem("selectedTheatre"));

let allSeats = [];
let selectedSeats = [];

const seatContainer = document.getElementById("seatContainer");
const continueBtn = document.getElementById("continueBtn");

// Check selected show
if (!selectedShow || !selectedShow.id) {
    alert("Please select a show first.");
    window.location.href = "movies.html";
}

// Format show date and time
function formatShowDate(dateTime) {
    if (!dateTime) return "Date not available";

    const date = new Date(dateTime);
    return date.toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric"
    });
}

function formatShowTime(dateTime) {
    if (!dateTime) return "Time not available";

    const date = new Date(dateTime);
    return date.toLocaleTimeString("en-IN", {
        hour: "2-digit",
        minute: "2-digit",
        hour12: true
    });
}

// Display movie and show information
function displayShowDetails() {

    document.getElementById("movieTitle").textContent =
        selectedMovie?.title || selectedShow.movieTitle || "Movie";

    document.getElementById("showDetails").textContent =
        `${selectedTheatre?.name || "Theatre"} • ` +
        `${formatShowDate(selectedShow.startTime)} • ` +
        `${formatShowTime(selectedShow.startTime)}`;

    document.getElementById("summaryMovie").textContent =
        selectedMovie?.title || selectedShow.movieTitle || "Movie";

    document.getElementById("summaryTheatre").textContent =
        selectedTheatre?.name || "Theatre";
}

// Fetch seats from backend
async function loadSeats() {

    try {

        const token = localStorage.getItem("token");
        const tokenType = localStorage.getItem("tokenType") || "Bearer";

        const headers = {};

        if (token) {
            headers["Authorization"] = `${tokenType} ${token}`;
        }

        const response = await fetch(
            `${API_BASE_URL}/shows/${selectedShow.id}/seats`,
            { headers }
        );

        if (!response.ok) {
            throw new Error(`Unable to load seats. Status: ${response.status}`);
        }

        allSeats = await response.json();

        // Sort seats by seat number
        allSeats.sort((a, b) =>
            a.seat.seatNumber.localeCompare(
                b.seat.seatNumber,
                undefined,
                { numeric: true }
            )
        );

        if (allSeats.length === 0) {
            seatContainer.innerHTML =
                `<p class="seat-error">No seats are available for this show.</p>`;
            return;
        }

        displaySeats();
        updateSummary();

    } catch (error) {

        console.error("Seat loading error:", error);

        seatContainer.innerHTML =
            `<p class="seat-error">
                Unable to load seats. Please check your backend connection.
            </p>`;
    }
}

// Group seats by row (A, B, C...)
function groupSeatsByRow() {

    const rows = {};

    allSeats.forEach(showSeat => {

        const seatNumber = showSeat.seat?.seatNumber;

        if (!seatNumber) return;

        const rowName = seatNumber.match(/^[A-Za-z]+/)?.[0] || "Other";

        if (!rows[rowName]) {
            rows[rowName] = [];
        }

        rows[rowName].push(showSeat);
    });

    return rows;
}

// Display seats
function displaySeats() {

    seatContainer.innerHTML = "";

    const rows = groupSeatsByRow();

    Object.keys(rows).sort().forEach(rowName => {

        const rowDiv = document.createElement("div");
        rowDiv.className = "seat-row";

        const rowLabel = document.createElement("span");
        rowLabel.className = "row-label";
        rowLabel.textContent = rowName;

        rowDiv.appendChild(rowLabel);

        rows[rowName].forEach(showSeat => {

            const seatButton = document.createElement("button");

            const seatNumber = showSeat.seat.seatNumber;
            const seatStatus = showSeat.status?.toUpperCase();
            const seatType = showSeat.seat.seatType?.toUpperCase();

            const isBooked = seatStatus === "BOOKED";

            seatButton.type = "button";
            seatButton.className = "seat";

            if (isBooked) {
                seatButton.classList.add("booked");
                seatButton.disabled = true;
            } else {
                seatButton.classList.add("available");
            }

            if (seatType === "PREMIUM") {
                seatButton.classList.add("premium");
            }

            seatButton.textContent = seatNumber;
            seatButton.title =
                `${seatNumber} | ${seatType || "REGULAR"} | ₹${showSeat.price}`;

            seatButton.addEventListener("click", () => {
                toggleSeat(showSeat, seatButton);
            });

            rowDiv.appendChild(seatButton);
        });

        seatContainer.appendChild(rowDiv);
    });
}

// Select or deselect a seat
function toggleSeat(showSeat, seatButton) {

    const existingIndex = selectedSeats.findIndex(
        seat => seat.id === showSeat.id
    );

    if (existingIndex !== -1) {

        selectedSeats.splice(existingIndex, 1);
        seatButton.classList.remove("selected");
        seatButton.classList.add("available");

    } else {

        selectedSeats.push(showSeat);
        seatButton.classList.remove("available");
        seatButton.classList.add("selected");
    }

    updateSummary();
}

// Update booking summary
function updateSummary() {

    const totalSeats = selectedSeats.length;

    const totalPrice = selectedSeats.reduce(
        (total, seat) => total + Number(seat.price),
        0
    );

    const seatNames = selectedSeats.map(
        seat => seat.seat.seatNumber
    );

    document.getElementById("seatCount").textContent =
        `${totalSeats} ${totalSeats === 1 ? "seat" : "seats"} selected`;

    document.getElementById("selectedSeatNames").textContent =
        seatNames.length > 0 ? seatNames.join(", ") : "None";

    document.getElementById("summarySeatCount").textContent = totalSeats;

    document.getElementById("ticketAmount").textContent =
        `₹${totalPrice.toFixed(2)}`;

    document.getElementById("totalAmount").textContent =
        `₹${totalPrice.toFixed(2)}`;

    continueBtn.disabled = totalSeats === 0;
}

// Continue to booking
continueBtn.addEventListener("click", () => {

    if (selectedSeats.length === 0) {
        alert("Please select at least one seat.");
        return;
    }

    const bookingSelection = {
        showId: selectedShow.id,
        movieId: selectedShow.movieId,
        theatreId: selectedTheatre?.id,
        selectedSeats: selectedSeats.map(seat => ({
            showSeatId: seat.id,
            seatNumber: seat.seat.seatNumber,
            seatType: seat.seat.seatType,
            price: seat.price
        })),
        totalAmount: selectedSeats.reduce(
            (total, seat) => total + Number(seat.price),
            0
        )
    };

    localStorage.setItem(
        "bookingSelection",
        JSON.stringify(bookingSelection)
    );

    window.location.href = "booking.html";

    // Next step: Booking page
});

// Initialize page
if (selectedShow?.id) {
    displayShowDetails();
    loadSeats();
}
