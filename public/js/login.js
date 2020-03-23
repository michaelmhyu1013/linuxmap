const handleLogin = () => {
    const username = prompt("Username:", "");
    const password = prompt("Password:", "");
    if (username !== "phoenix" || password !== "password") {
        window.stop();
    }
};

window.onload = handleLogin();
