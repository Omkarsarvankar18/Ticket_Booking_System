
const SHOW_API_URL = "http://localhost:8080/api/v1/shows";
const SCREEN_API_URL = "http://localhost:8080/api/v1/screens";

let selectedMovie;
let selectedTheatre;

async function loadShows() {

    const bookingInfo = document.getElementById("bookingInfo");
    const showMessage = document.getElementById("showMessage");
    const showList = document.getElementById("showList");

    selectedMovie = JSON.parse(
        localStorage.getItem("selectedMovie") || "null"
    );

    selectedTheatre = JSON.parse(
        localStorage.getItem("selectedTheatre") || "null"
    );

    if (!selectedMovie || !selectedTheatre) {
        bookingInfo.textContent =
            "Please select a movie and theatre first.";
        return;
    }

    bookingInfo.textContent =
        `Movie: ${selectedMovie.title} | Theatre: ${selectedTheatre.name}`;

    console.log("Selected Movie:", selectedMovie);
    console.log("Selected Theatre:", selectedTheatre);

    if (!selectedMovie.id || !selectedTheatre.id) {
        showMessage.textContent =
            "Movie ID or Theatre ID is missing.";
        return;
    }

    try {

        const token = localStorage.getItem("token");
        const tokenType = localStorage.getItem("tokenType") || "Bearer";

        const headers = {
            "Accept": "application/json"
        };

        if (token) {
            headers["Authorization"] = `${tokenType} ${token}`;
        }

        // 1. Fetch screens using the correct backend endpoint
        const screenUrl =
            `${SCREEN_API_URL}/theatre/${selectedTheatre.id}`;

        console.log("Screen API URL:", screenUrl);

        const screenResponse = await fetch(screenUrl, { headers });

        if (!screenResponse.ok) {
            const errorText = await screenResponse.text();
            console.error("Screen API error:", errorText);

            throw new Error(
                `Unable to fetch screens. HTTP ${screenResponse.status}`
            );
        }

        const screens = await screenResponse.json();

        console.log("Screens received:", screens);

        if (!Array.isArray(screens) || screens.length === 0) {
            showMessage.textContent =
                "No screens available in this theatre.";
            showList.innerHTML = "";
            return;
        }

        const screenIds = screens.map(screen => Number(screen.id));

        // 2. Fetch all shows
        const showResponse = await fetch(SHOW_API_URL, { headers });

        if (!showResponse.ok) {
            const errorText = await showResponse.text();
            console.error("Show API error:", errorText);

            throw new Error(
                `Unable to fetch shows. HTTP ${showResponse.status}`
            );
        }

        const shows = await showResponse.json();

        console.log("All shows received:", shows);

        // 3. Filter shows by selected movie and theatre
        const filteredShows = shows.filter(show => {

            const movieId =
                show.movieId ?? show.movie?.id;

            const screenId =
                show.screenId ?? show.screen?.id;

            const movieMatches =
                Number(movieId) === Number(selectedMovie.id);

            const screenMatches =
                screenIds.includes(Number(screenId));

            return movieMatches && screenMatches;
        });

        console.log("Filtered Shows:", filteredShows);

        // 4. Display matching shows
        displayShows(filteredShows, screens);

    } catch (error) {

        console.error("Show loading error:", error);

        showMessage.textContent =
            error.message + " Please check your backend and login session.";
    }
}


// Display matching shows
function displayShows(shows, screens) {

    const showList = document.getElementById("showList");
    const showMessage = document.getElementById("showMessage");

    showList.innerHTML = "";

    if (!shows || shows.length === 0) {
        showMessage.textContent =
            "No shows available for this movie and theatre.";
        return;
    }

    showMessage.textContent =
        `${shows.length} show(s) available`;

    shows.forEach(show => {

        const screenId = show.screenId ?? show.screen?.id;

        const screen = screens.find(
            item => Number(item.id) === Number(screenId)
        );

        const card = document.createElement("div");
        card.className = "show-card";

        const screenName = document.createElement("h2");
        screenName.textContent =
            show.screenName || screen?.screenName || "Screen";

        const movieName = document.createElement("p");
        movieName.textContent =
            show.movieTitle || show.movie?.title || selectedMovie.title;

        const startTime = document.createElement("p");
        startTime.textContent =
            `Start: ${formatDateTime(show.startTime)}`;

        const endTime = document.createElement("p");
        endTime.textContent =
            `End: ${formatDateTime(show.endTime)}`;

        const price = document.createElement("h3");
        price.textContent = `₹${show.basePrice}`;

        const status = document.createElement("span");
        status.className = "movie-status";
        status.textContent = show.status || "UNKNOWN";

        const selectButton = document.createElement("button");
        selectButton.className = "book-movie-btn";
        selectButton.textContent = "Select Show";

        selectButton.addEventListener("click", () => {

            localStorage.setItem(
                "selectedShow",
                JSON.stringify(show)
            );

            window.location.href = "seats.html";
        });

        card.append(
            screenName,
            movieName,
            startTime,
            endTime,
            price,
            status,
            selectButton
        );

        showList.appendChild(card);
    });
}


// Format backend LocalDateTime
function formatDateTime(dateTime) {

    if (!dateTime) {
        return "Not available";
    }

    const date = new Date(dateTime);

    if (Number.isNaN(date.getTime())) {
        return dateTime;
    }

    return date.toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

document.addEventListener("DOMContentLoaded", loadShows);
