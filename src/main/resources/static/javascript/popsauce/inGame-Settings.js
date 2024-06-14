document.getElementById('chat-input').addEventListener('keydown',(event)=>{
    if (event.key==='Enter'){
        sendMessage();
    }
})

inputElement.addEventListener('keydown', (event)=>{
    if (event.key==="Enter"){
        if (!alreadyGuessedRight){
            rightAnswer();
        }
    }
})
