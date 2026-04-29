// security.js - Global Authentication Helper

// 1. Instantly check if user is logged in (Skip if on login/register pages)
const currentPath = window.location.pathname;
if (currentPath !== '/login' && currentPath !== '/register' && currentPath !== '/') {
    const token = localStorage.getItem("jwt_token");
    if (!token) {
        window.location.replace("/login");
    }
}

// 2. Global function to generate headers for fetch calls
function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('jwt_token')
    };
}

// 3. Global function to handle expired tokens and standard JSON parsing
function handleSecureResponse(res) {
    if (res.status === 401 || res.status === 403) {
        console.warn("Unauthorized request. Token may be expired.");
        localStorage.clear();
        window.location.replace('/login');
        throw new Error("Unauthorized");
    }
    return res.json();
}

// 4. Global Logout function (so you can attach it to your navbar!)
function logout() {
    localStorage.clear();
    window.location.replace('/login');
}