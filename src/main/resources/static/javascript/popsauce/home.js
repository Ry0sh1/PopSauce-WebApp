//Username
let username;
function createGame(){
    username = document.getElementById('input-username').value;
    fetch("/popsauce/is-username-valid/"+username)
        .then(response=> response.text())
        .then(data => {
            if (data === "true"){
                localStorage.setItem('username',username);
                window.location.href = "/popsauce/create-game";
            }else {
                document.getElementById('invalid-username').classList.remove('invisible');
                document.getElementById('input-username').value = '';
            }
        })
}
function joinGame(){
    let code = document.getElementById('input-code').value;
    username = document.getElementById('input-username').value;
    fetch("/popsauce/is-username-valid/"+username)
        .then(response=> response.text())
        .then(data => {
            if (data === "true"){
                fetch("/popsauce/is-code-valid/"+code)
                    .then(response=> response.text())
                    .then(data => {
                        if (data==="false"){
                            document.getElementById('invalid-code').classList.remove('invisible');
                            document.getElementById('input-code').value = '';
                            document.getElementById('invalid-username').classList.add('invisible');
                        }else {
                            localStorage.setItem('username',username);
                            window.location.href = "/popsauce/start-game/" + code;
                        }
                    })
            }else {
                document.getElementById('invalid-username').classList.remove('invisible');
                document.getElementById('input-username').value = '';
            }
        })
}

document.getElementById("input-code").addEventListener('keydown', (event)=>{
    if (event.key==="Enter"){
        joinGame();
    }
})
document.getElementById("input-username").addEventListener('keydown', (event)=>{
    if (event.key==="Enter"){
        createGame();
    }
})