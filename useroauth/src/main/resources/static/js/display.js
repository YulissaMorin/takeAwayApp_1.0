window.onload = function changeStyle() {
    var username = document.getElementById("username_span").innerText;
    var login = document.getElementsByClassName("display_on_login");
    for (let i = 0; i < login.length; i++) {
        if (username != "") {
            login[i].style.display = "";
        } else {
            login[i].style.display = "none";
        }
    }
    var logout = document.getElementsByClassName("display_on_logout");
    for (let j = 0; j < logout.length; j++) {
        if (username != "") {
            logout[j].style.display = "none";
        } else {
            logout[j].style.display = "";
        }
    }
}