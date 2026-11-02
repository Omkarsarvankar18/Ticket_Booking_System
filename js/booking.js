const API_BASE_URL = "http://localhost:8080/api/v1";

const bookingDetails = document.getElementById("bookingDetails");
const confirmBookingBtn = document.getElementById("confirmBookingBtn");
const message = document.getElementById("message");

// Get saved customer and booking details
const userId = localStorage.getItem("userId");
const token = localStorage.getItem("token");
const tokenType = localStorage.getItem("tokenType") || "Bearer";

const bookingSelection = JSON.parse(
    localStorage.getItem("bookingSelection") || "null"
);

const selectedMovie = JSON.parse(
    localStorage.getItem("selectedMovie") || "null"
);

const selectedTheatre = JSON.parse(
    localStorage.getItem("selectedTheatre") || "null"
);

const selectedShow = JSON.parse(
    localStorage.getItem("selectedShow") || "null"
);


// Display booking summary
function displayBookingDetails() {

    if (!userId || !token) {
        alert("Please login first!");
        window.location.href = "login.html";
        return;
    }

    if (!bookingSelection ||
        !bookingSelection.selectedSeats ||
        bookingSelection.selectedSeats.length === 0) {

        bookingDetails.innerHTML = "<p>No seats selected.</p>";
        confirmBookingBtn.disabled = true;
        return;
    }

    const seats = bookingSelection.selectedSeats;

    const totalAmount = seats.reduce(
        (total, seat) => total + Number(seat.price),
        0
    );

    bookingDetails.innerHTML = `
        <div class="booking-detail">
            <span>Customer</span>
            <span>${escapeHTML(localStorage.getItem("userName") || "")}</span>
        </div>

        <div class="booking-detail">
            <span>Movie</span>
            <span>${escapeHTML(selectedMovie?.title || "Movie")}</span>
        </div>

        <div class="booking-detail">
            <span>Theatre</span>
            <span>${escapeHTML(selectedTheatre?.name || "Theatre")}</span>
        </div>

        <div class="booking-detail">
            <span>Show Date</span>
            <span>${escapeHTML(selectedShow?.startTime || "N/A")}</span>
        </div>

        <h3>Selected Seats</h3>

        <div class="seat-list">
            ${seats.map(seat => `
                <div class="seat-item">
                    <span>${escapeHTML(seat.seatNumber)}</span>
                    <span>₹${Number(seat.price).toFixed(2)}</span>
                </div>
            `).join("")}
        </div>

        <div class="booking-detail">
            <span>Total Seats</span>
            <span>${seats.length}</span>
        </div>

        <div class="booking-detail">
            <span>Total Amount</span>
            <span class="total">₹${totalAmount.toFixed(2)}</span>
        </div>
    `;
}


// Escape HTML characters
function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}


// Confirm booking
async function confirmBooking() {

    if (!bookingSelection || !userId || !token) {
        message.textContent = "Booking information is missing. Please login and select seats again.";
        message.className = "message error";
        return;
    }

    const requestData = {
        showId: bookingSelection.showId,
        showSeatIds: bookingSelection.selectedSeats.map(
            seat => seat.showSeatId
        )
    };

    confirmBookingBtn.disabled = true;
    confirmBookingBtn.textContent = "Processing...";

    try {

        const response = await fetch(
            `${API_BASE_URL}/bookings/users/${encodeURIComponent(userId)}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `${tokenType} ${token}`,
                    "Accept": "application/json"
                },
                body: JSON.stringify(requestData)
            }
        );

        const responseText = await response.text();

        let data = {};

        try {
            data = responseText ? JSON.parse(responseText) : {};
        } catch {
            throw new Error("Invalid response received from server.");
        }

        if (!response.ok) {
            throw new Error(
                data.message || "Booking could not be completed!"
            );
        }

        // Save confirmed booking response
        localStorage.setItem(
            "confirmedBooking",
            JSON.stringify(data)
        );

        // Clear previous seat selection
        localStorage.removeItem("bookingSelection");

        // Display success message
        message.textContent = "Your booking has been confirmed successfully!";
        message.className = "message success";

        // Display confirmed booking details
        bookingDetails.innerHTML = `
            <h2 style="text-align:center;color:green;">
                Booking Confirmed!
            </h2>

            <div class="booking-detail">
                <span>Booking Reference</span>
                <span>${escapeHTML(data.bookingReference || "N/A")}</span>
            </div>

            <div class="booking-detail">
                <span>Booking ID</span>
                <span>${escapeHTML(data.id ?? "N/A")}</span>
            </div>

            <div class="booking-detail">
                <span>Booking Status</span>
                <span>${escapeHTML(data.status || "CONFIRMED")}</span>
            </div>

            <div class="booking-detail">
                <span>Total Amount</span>
                <span class="total">
                    ₹${Number(data.totalAmount ?? 0).toFixed(2)}
                </span>
            </div>

            <p style="text-align:center;color:green;margin-top:20px;">
                Your seats have been booked successfully!
            </p>

            <p style="text-align:center;margin-top:10px;">
                Redirecting to movies page in 2 seconds...
            </p>
        `;

        // Update button
        confirmBookingBtn.textContent = "Booking Completed";
        confirmBookingBtn.disabled = true;

        // Automatically redirect after 2 seconds
        setTimeout(() => {
            window.location.href = "movies.html";
        }, 2000);

    } catch (error) {

        console.error("Booking Error:", error);

        message.textContent = error.message;
        message.className = "message error";

        confirmBookingBtn.disabled = false;
        confirmBookingBtn.textContent = "Try Again";
    }
}


// Button event
confirmBookingBtn.addEventListener("click", confirmBooking);

// Load booking summary
displayBookingDetails();