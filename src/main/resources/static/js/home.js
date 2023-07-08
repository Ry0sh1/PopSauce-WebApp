//Username
let username;
function createGame(){
    username = document.getElementById('input-username').value;
    localStorage.setItem('username',username);
    window.location.href = "/create-game";
}