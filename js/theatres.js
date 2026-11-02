
const THEATRE_API_URL = "http://localhost:8080/api/v1/theatres";

async function loadTheatres() {

    const theatreList = document.getElementById("theatreList");
    const theatreMessage = document.getElementById("theatreMessage");

    const selectedMovie = JSON.parse(
        localStorage.getItem("selectedMovie") || "null"
    );

    if (!selectedMovie) {
        theatreMessage.textContent = "Please select a movie first.";
        return;
    }

    document.getElementById("selectedMovieInfo").textContent =
        `Selected Movie: ${selectedMovie.title}`;

    try {

        const response = await fetch(THEATRE_API_URL);

        if (!response.ok) {
            throw new Error("Unable to fetch theatres.");
        }

        const theatres = await response.json();

        displayTheatres(theatres);

    } catch (error) {

        console.error("Theatre loading error:", error);

        theatreMessage.textContent =
            "Unable to load theatres. Please try again.";

        theatreList.innerHTML = "";
    }
}


function displayTheatres(theatres) {

    const theatreList = document.getElementById("theatreList");
    const theatreMessage = document.getElementById("theatreMessage");

    theatreList.innerHTML = "";

    if (!theatres || theatres.length === 0) {
        theatreMessage.textContent = "No theatres are currently available.";
        return;
    }

    theatreMessage.textContent =
        `${theatres.length} theatre(s) available`;

    theatres.forEach(theatre => {

        const card = document.createElement("div");
        card.className = "movie-card theatre-card";

        const poster = document.createElement("div");
        poster.className = "movie-poster theatre-poster";

        const icon = document.createElement("span");
        icon.textContent = "🎥";
        poster.appendChild(icon);

        const details = document.createElement("div");
        details.className = "movie-details";

        const name = document.createElement("h2");
        name.textContent = theatre.name || "Theatre";

        const address = document.createElement("p");
        address.textContent =
            `Address: ${theatre.address || "N/A"}`;

        const city = document.createElement("p");
        city.textContent =
            `City: ${theatre.city || "N/A"}`;

        const button = document.createElement("button");
        button.className = "book-movie-btn";
        button.textContent = "Select Theatre";

        // Select Theatre button click event
        button.addEventListener("click", () => {

            localStorage.setItem(
                "selectedTheatre",
                JSON.stringify(theatre)
            );

            window.location.href = "shows.html";
        });

        details.append(name, address, city, button);

        card.append(poster, details);

        theatreList.appendChild(card);
    });
}


document.addEventListener("DOMContentLoaded", loadTheatres);
