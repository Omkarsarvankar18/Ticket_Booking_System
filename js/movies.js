
const MOVIE_API_URL = "http://localhost:8080/api/v1/movies";

let allMovies = [];

// Online movie poster URLs
const moviePosters = {
    "vikram": "https://www.behindwoods.com/uploads/62e27170ce3c0.jpeg",

    "rrr": "https://m.media-amazon.com/images/M/MV5BODUwNDNjYzctODUxNy00ZTA2LWIyYTEtMDc5Y2E5ZjBmNTMzXkEyXkFqcGdeQXVyODE5NzE3OTE%40._V1_FMjpg_UX1000_.jpg",

    "3 idiots": "https://www.cinema.de/sites/default/files/styles/schema_org/public/sync/img.tvspielfilm.de/0c/fe/12/59ae7997f033af223612fe0c.jpeg?itok=5PzFNVor",

    "inception": "https://artofthemovies.co.uk/cdn/shop/files/inception_advance_SD18120_B-731214_920c5dfe-8d66-47c7-af74-11b98a83ebbf_1024x1024%402x.jpg?v=1737545739",

    "interstellar": "https://cdn.ome.lt/legacy/images/galerias/Interstellar/Interstellar-Poster-18Set2014-1.jpg",

    "drishyam": "https://m.media-amazon.com/images/M/MV5BZDZkM2U0Y2ItMTAwOC00MTc3LWI5NTQtZTk3Yjc2NGI5MWYzXkEyXkFqcGc%40._V1_FMjpg_UX1000_.jpg",

    "dangal": "https://images.fandango.com/ImageRenderer/0/0/redesign/static/img/default_poster.png/0/images/masterrepository/fandango/198210/dangal-posterart.jpg",

    "the conjuring": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQHSnZhpr2P_xN0GmbSlbaRUN_c7TQp1_uhKdIpbjG7WlZ3Jk_gD048-QQC&s=1024",
    
    "leo": "https://mir-s3-cdn-cf.behance.net/project_modules/1400_webp/60c78e182523101.652f4bf99216b.jpg"
};

// Default poster
const DEFAULT_POSTER =
    "https://placehold.co/500x750/252044/ffffff?text=Poster+Unavailable";


// Get poster based on movie title
function getMoviePoster(title) {

    const movieTitle = (title || "").toLowerCase().trim();

    return moviePosters[movieTitle] || DEFAULT_POSTER;
}


// Fetch all movies from backend
async function loadMovies() {

    const movieList = document.getElementById("movieList");
    const movieMessage = document.getElementById("movieMessage");

    movieMessage.textContent = "Loading movies...";

    try {

        const response = await fetch(MOVIE_API_URL);

        if (!response.ok) {
            throw new Error("Unable to fetch movies.");
        }

        allMovies = await response.json();

        displayMovies(allMovies);

    } catch (error) {

        console.error("Movie loading error:", error);

        movieMessage.textContent =
            "Unable to load movies. Please check whether the backend is running.";

        movieList.innerHTML = "";
    }
}


// Display movies
function displayMovies(movies) {

    const movieList = document.getElementById("movieList");
    const movieMessage = document.getElementById("movieMessage");

    movieList.innerHTML = "";

    if (!movies || movies.length === 0) {

        movieMessage.textContent = "No movies found.";
        return;
    }

    movieMessage.textContent = `${movies.length} movie(s) found`;

    movies.forEach(movie => {

        const movieCard = document.createElement("div");
        movieCard.className = "movie-card";

        // Movie poster section
        const poster = document.createElement("div");
        poster.className = "movie-poster";

        const posterImage = document.createElement("img");
        posterImage.src = getMoviePoster(movie.title);
        posterImage.alt = `${movie.title || "Movie"} poster`;

        // Fallback if poster URL fails
        posterImage.onerror = function () {
            this.onerror = null;
            this.src = DEFAULT_POSTER;
        };

        poster.appendChild(posterImage);

        // Movie details section
        const details = document.createElement("div");
        details.className = "movie-details";

        const title = document.createElement("h2");
        title.textContent = movie.title || "Untitled Movie";

        const description = document.createElement("p");
        description.className = "movie-description";
        description.textContent =
            movie.description || "No description available.";

        const genre = document.createElement("p");
        genre.textContent = `Genre: ${movie.genre || "N/A"}`;

        const language = document.createElement("p");
        language.textContent = `Language: ${movie.language || "N/A"}`;

        const duration = document.createElement("p");
        duration.textContent =
            `Duration: ${movie.duration ?? "N/A"} minutes`;

        const certificate = document.createElement("p");
        certificate.textContent =
            `Certificate: ${movie.certificate || "N/A"}`;

        const releaseDate = document.createElement("p");
        releaseDate.textContent =
            `Release Date: ${movie.releaseDate || "N/A"}`;

        const status = document.createElement("span");
        status.className = "movie-status";
        status.textContent = movie.status || "UNKNOWN";

        const bookButton = document.createElement("button");
        bookButton.className = "book-movie-btn";
        bookButton.textContent = "Book Tickets";

        // Save selected movie and navigate to theatres
        bookButton.addEventListener("click", () => {

            localStorage.setItem("selectedMovie", JSON.stringify(movie));

            window.location.href = "theatres.html";
        });

        details.append(
            title,
            description,
            genre,
            language,
            duration,
            certificate,
            releaseDate,
            status,
            bookButton
        );

        movieCard.append(poster, details);
        movieList.appendChild(movieCard);
    });
}


// Search movies
document.getElementById("searchInput").addEventListener("input", function () {

    const searchValue = this.value.toLowerCase().trim();

    const filteredMovies = allMovies.filter(movie => {

        return (
            (movie.title || "").toLowerCase().includes(searchValue) ||
            (movie.genre || "").toLowerCase().includes(searchValue) ||
            (movie.language || "").toLowerCase().includes(searchValue)
        );
    });

    displayMovies(filteredMovies);
});


// Load movies when page opens
document.addEventListener("DOMContentLoaded", loadMovies);
