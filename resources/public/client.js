function sendMessage(event) {
       event.preventDefault();
       const category = document.getElementById('category').value;
       const content = document.getElementById('content').value;
       const data = {
         category: category,
         content: content
       };
       fetch('/notify', {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json'
         },
         body: JSON.stringify(data)
       })
       .then(response => response.json())
       .then(result => {
         document.getElementById('response').innerText = JSON.stringify(result);
       })
       .then(e => {window.location.replace(window.location.href);})
       .catch(error => {
         document.getElementById('response').innerText = 'Error: ' + error.message;
       });

     }