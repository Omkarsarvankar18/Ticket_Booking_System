
const MOVIE_API_URL = "http://localhost:8080/api/v1/movies";

let allMovies = [];

// Default poster when no poster is available
const DEFAULT_POSTER =
    "https://placehold.co/500x750/252044/ffffff?text=Poster+Unavailable";

// OLD MOVIE POSTERS - Keep these as your fallback posters
const moviePosters = {
    "vikram": "https://www.behindwoods.com/uploads/62e27170ce3c0.jpeg",

    "rrr": "https://image.tmdb.org/t/p/w500/nEufeZlyAOLqO2brrs0yeF1lgXO.jpg",

    "3 idiots": "https://image.tmdb.org/t/p/w500/66A9MqXOyVFCssoloscw79z8Tew.jpg",

    "inception": "https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",

    "interstellar": "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",

    "drishyam": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRE7T7F4y7gL_rzFbpv48CdEitsaxyJmYYeanPA9DYKLa46KJnW_B2BYPtgj0EbdSEdbw4FfQ&s=10",

    "dangal": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrAj3X3l8pHj2TRRj7XL_6dz-pzxXeM6fZfKxhESWAKXnDJUMv7rA8BwMIbJSsfSHpHWrh&s=10",

    "the conjuring": "https://image.tmdb.org/t/p/w500/wVYREutTvI2tmxr6ujrHT704wGF.jpg",

    "leo": "https://i.pinimg.com/736x/46/37/db/4637dbeccdf457d9086f4e19e3737863.jpg"
};


// GET POSTER
// Priority 1: Poster URL added by Admin
// Priority 2: Old hardcoded poster
// Priority 3: Default poster

function getMoviePoster(movie) {

    if (movie.posterUrl && movie.posterUrl.trim() !== "") {
        return movie.posterUrl.trim();
    }

    const movieTitle = (movie.title || "").toLowerCase().trim();

    return moviePosters[movieTitle] || DEFAULT_POSTER;
}


// FETCH ALL MOVIES FROM BACKEND
async function loadMovies() {

    const movieList = document.getElementById("movieList");
    const movieMessage = document.getElementById("movieMessage");

    movieMessage.textContent = "Loading movies...";

    try {

        const response = await fetch(MOVIE_API_URL);

        if (!response.ok) {
            throw new Error("Unable to fetch movies.");
        }

        const data = await response.json();

        allMovies = Array.isArray(data)
            ? data
            : Array.isArray(data.content)
                ? data.content
                : Array.isArray(data.data)
                    ? data.data
                    : [];

        displayMovies(allMovies);

    } catch (error) {

        console.error("Movie loading error:", error);

        movieMessage.textContent =
            "Unable to load movies. Please check whether the backend is running.";

        movieList.innerHTML = "";

    }

}


// DISPLAY MOVIES
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


        // MOVIE POSTER

        const poster = document.createElement("div");

        poster.className = "movie-poster";

        const posterImage = document.createElement("img");

        posterImage.src = getMoviePoster(movie);

        posterImage.alt = `${movie.title || "Movie"} poster`;

        posterImage.loading = "lazy";

        posterImage.onerror = function () {

            this.onerror = null;

            this.src = DEFAULT_POSTER;

        };

        poster.appendChild(posterImage);


        // MOVIE DETAILS

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


        // BOOK TICKETS BUTTON

        const bookButton = document.createElement("button");

        bookButton.className = "book-movie-btn";

        bookButton.textContent = "Book Tickets";

        bookButton.addEventListener("click", () => {

            localStorage.setItem(
                "selectedMovie",
                JSON.stringify(movie)
            );

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


// SEARCH MOVIES

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


// LOAD MOVIES WHEN PAGE OPENS

document.addEventListener("DOMContentLoaded", loadMovies);
