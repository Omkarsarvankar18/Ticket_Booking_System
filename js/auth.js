
const API_BASE_URL = "http://localhost:8080/api/v1/auth";

// ==============================
// REGISTER USER
// ==============================
async function registerUser(event) {
    event.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return;
    }

    if (password.length < 8) {
        alert("Password must contain at least 8 characters!");
        return;
    }

    const userData = {
        name: name,
        email: email,
        password: password
    };

    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Registration failed!");
        }

        alert("Registration successful! Please login.");
        window.location.href = "login.html";

    } catch (error) {
        console.error("Registration Error:", error);
        alert(error.message);
    }
}


// ==============================
// LOGIN USER - ADMIN & CUSTOMER
// ==============================
async function loginUser(event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    const loginData = {
        email: email,
        password: password
    };

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginData)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Invalid email or password!");
        }

        // Validate login response
        if (!data.token || data.userId == null) {
            throw new Error("Login response is missing token or user ID!");
        }

        // Check role
        const userRole = (data.role || "").toUpperCase();

        if (userRole !== "ADMIN" && userRole !== "CUSTOMER") {
            throw new Error("Invalid user role returned by server!");
        }

        // Clear previous login information
        localStorage.removeItem("redirectAfterLogin");

        // Store authenticated user information
        localStorage.setItem("token", data.token);
        localStorage.setItem("tokenType", data.tokenType || "Bearer");
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("userName", data.name || "");
        localStorage.setItem("userEmail", data.email || "");
        localStorage.setItem("userRole", userRole);

        console.log("Login successful");
        console.log("Email:", data.email);
        console.log("Role:", userRole);

        // ADMIN REDIRECTION
        if (userRole === "ADMIN") {
            window.location.replace("./admin-dashboard.html");
            return;
        }

        // CUSTOMER REDIRECTION
        if (userRole === "CUSTOMER") {
            window.location.replace("./movies.html");
            return;
        }

    } catch (error) {
        console.error("Login Error:", error);
        alert(error.message);
    }
}


// ==============================
// LOGOUT USER
// ==============================
function logoutUser() {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenType");
    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userRole");
    localStorage.removeItem("redirectAfterLogin");

    window.location.href = "login.html";
}


// ==============================
// CHECK LOGIN STATUS
// ==============================
function isLoggedIn() {
    return localStorage.getItem("token") !== null;
}


// ==============================
// OPEN PROTECTED PAGE
// ==============================
function openProtectedPage(page) {
    if (isLoggedIn()) {
        window.location.href = page;
    } else {
        alert("Please login first to continue.");

        localStorage.setItem("redirectAfterLogin", page);
        window.location.href = "login.html";
    }
}
