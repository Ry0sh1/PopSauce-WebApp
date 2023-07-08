let guessTimer;
let resultTimer;
let category;

function setSettings(){
    guessTimer = document.getElementById('guess-timer').value;
    resultTimer = document.getElementById('result-timer').value;
    category = document.getElementById('category').value;

    const settings = {
        "guessTimer":guessTimer,
        "resultTimer":resultTimer,
        "category":category
    };

    const game = {
        "host":{
            "username":localStorage.getItem('username'),
            "points":0
        },
        "setting": settings,
    }

    fetch('/create', {
        method: 'POST',
        headers:{
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(game)
    })
    .then(response => response.text())
    .then(code => window.location.href = "/start-game/" + code);

}