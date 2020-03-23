/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: handleLogin
--
-- DATE: March 20, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:       handleLogin()
--
-- RETURNS: void.
-- 
-- NOTES:
-- Opens a simple prompt in the web browser to prompt for user authentication. If the user fails to pass in the correct
-- credentials, the loading of the window will be stopped and no resources will be shown to the user.
----------------------------------------------------------------------------------------------------------------------*/
const user = "phoenix";
const pwd = "password";

const handleLogin = () => {
    const username = prompt("Username:", "");
    const password = prompt("Password:", "");
    if (username !== user || password !== pwd) {
        window.stop();
    }
};

window.onload = handleLogin();
