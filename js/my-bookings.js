
const API_BASE_URL = "http://localhost:8080/api/v1";

const bookingsContainer =
    document.getElementById("bookingsContainer");

async function loadMyBookings() {

    const userId = localStorage.getItem("userId");
    const token = localStorage.getItem("token");
    const tokenType = localStorage.getItem("tokenType") || "Bearer";

    // Check login
    if (!userId || !token) {
        alert("Please login to view your bookings.");
        window.location.href = "login.html";
        return;
    }

    try {

        // Call booking history API
        const response = await fetch(
            `${API_BASE_URL}/bookings/users/${encodeURIComponent(userId)}`,
            {
                method: "GET",
                headers: {
                    "Authorization": `${tokenType} ${token}`,
                    "Accept": "application/json"
                }
            }
        );

        // Read response as text
        const responseText = await response.text();

        // Debug information
        console.log("API URL:", response.url);
        console.log("HTTP STATUS:", response.status);
        console.log("CONTENT TYPE:", response.headers.get("content-type"));
        console.log("API RESPONSE:", responseText);

        // Handle authentication errors
        if (response.status === 401) {
            localStorage.removeItem("token");
            localStorage.removeItem("tokenType");

            alert("Your session has expired. Please login again.");
            window.location.href = "login.html";
            return;
        }

        if (response.status === 403) {
            throw new Error(
                "Access denied. Please check your user permissions."
            );
        }

        // Handle other unsuccessful responses
        if (!response.ok) {
            throw new Error(
                `Server error: ${response.status} ${response.statusText}`
            );
        }

        // Handle empty response
        if (!responseText.trim()) {
            displayBookings([]);
            return;
        }

        // Prevent HTML from being parsed as JSON
        if (responseText.trim().startsWith("<")) {
            throw new Error(
                "Backend returned HTML instead of JSON. Check the API URL and Spring Boot configuration."
            );
        }

        // Convert response to JSON
        let bookings;

        try {
            bookings = JSON.parse(responseText);
        } catch (error) {
            throw new Error("Invalid JSON response received from backend.");
        }

        // Validate response
        if (!Array.isArray(bookings)) {
            throw new Error("Booking API did not return a list of bookings.");
        }

        displayBookings(bookings);

    } catch (error) {

        console.error("Booking History Error:", error);

        bookingsContainer.innerHTML = `
            <div class="error-message">
                <h3>Unable to load bookings</h3>
                <p>${escapeHTML(error.message)}</p>
            </div>
        `;
    }
}


// Display customer bookings
function displayBookings(bookings) {

    if (!bookings || bookings.length === 0) {

        bookingsContainer.innerHTML = `
            <div class="empty-message">
                <h2>No Bookings Found</h2>
                <p>You haven't booked any movie tickets yet.</p>

                <a href="movies.html" class="back-btn">
                    Explore Movies
                </a>
            </div>
        `;

        return;
    }

    bookingsContainer.innerHTML = bookings.map(booking => {

        const seats = booking.seatNumbers || [];

        const formattedDate = booking.showStartTime
            ? new Date(booking.showStartTime).toLocaleString()
            : "Not available";

        const bookingDate = booking.bookingDateTime
            ? new Date(booking.bookingDateTime).toLocaleString()
            : "Not available";

        return `
            <div class="booking-card">

                <div class="booking-header">

                    <div>
                        <div>Booking Reference</div>

                        <div class="booking-reference">
                            ${escapeHTML(booking.bookingReference || "N/A")}
                        </div>
                    </div>

                    <span class="status">
                        ${escapeHTML(booking.status || "UNKNOWN")}
                    </span>

                </div>

                <div class="booking-info">

                    <div class="info-item">
                        <small>Movie</small>
                        <strong>
                            ${escapeHTML(booking.movieTitle || "N/A")}
                        </strong>
                    </div>

                    <div class="info-item">
                        <small>Theatre</small>
                        <strong>
                            ${escapeHTML(booking.theatreName || "N/A")}
                        </strong>
                    </div>

                    <div class="info-item">
                        <small>Screen</small>
                        <strong>
                            ${escapeHTML(booking.screenName || "N/A")}
                        </strong>
                    </div>

                    <div class="info-item">
                        <small>Show Date & Time</small>
                        <strong>${escapeHTML(formattedDate)}</strong>
                    </div>

                    <div class="info-item">
                        <small>Booking Date</small>
                        <strong>${escapeHTML(bookingDate)}</strong>
                    </div>

                    <div class="info-item">
                        <small>Booking ID</small>
                        <strong>${escapeHTML(booking.id ?? "N/A")}</strong>
                    </div>

                </div>

                <div class="seat-section">

                    <strong>Selected Seats</strong>

                    <div>
                        ${
                            seats.length > 0
                                ? seats.map(seat => `
                                    <span class="seat-tag">
                                        ${escapeHTML(seat)}
                                    </span>
                                `).join("")
                                : "Seat details unavailable"
                        }
                    </div>

                </div>

                <div class="booking-footer">

                    <div>
                        <small>Total Amount</small>

                        <div class="total-amount">
                            ₹${Number(booking.totalAmount || 0).toFixed(2)}
                        </div>
                    </div>

                    <div>
                        <small>Booking Status</small>

                        <strong>
                            ${escapeHTML(booking.status || "UNKNOWN")}
                        </strong>
                    </div>

                </div>

            </div>
        `;

    }).join("");
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


// Load bookings when page opens
loadMyBookings();
