let guessTime;
let resultTime;
let category;

function setSettings(){
    guessTime = document.getElementById('guess-timer').value;
    resultTime = document.getElementById('result-timer').value;
    const categoryElement = document.getElementById('category');
    category = categoryElement.options[categoryElement.selectedIndex].text;

    //Break Protection
    if (guessTime < 7 || guessTime > 15){
        guessTime = 7;
    }
    if (resultTime < 2 && guessTime > 5){
        resultTime = 2;
    }

    const settings = {
        "guessTimer":guessTime,
        "resultTimer":resultTime,
        "category":category
    };

    const game = {
        "host":{
            "username":localStorage.getItem('username'),
            "points":0
        },
        "setting": settings,
    }

    fetch('/popsauce/create', {
        method: 'POST',
        headers:{
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(game)
    })
    .then(response => response.text())
    .then(code => window.location.href = "/popsauce/start-game/" + code);

}

document.getElementById('guess-timer').value = 7;
document.getElementById('result-timer').value = 2;