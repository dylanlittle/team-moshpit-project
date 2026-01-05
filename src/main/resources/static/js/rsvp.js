function handleRsvpClick(e) {
    e.preventDefault();
    const btn = e.currentTarget;
    console.log("The button works");

    // 1. Read the data-concert-id attribute
    const concertId = btn.getAttribute('data-concert-id');
    console.log(concertId);

    const token = document.querySelector('meta[name="_csrf"]').content;
    const url = `/concerts/${concertId}/rsvp`;
    const isGoing = btn.classList.contains('active');

     fetch(url, {
                method: 'POST',
                headers: {
                        'X-CSRF-TOKEN': token,
                        'Content-Type': 'application/json'
                    }
            })
            .then(response => {
                    if (response.ok) {
                        // Logic to toggle the text based on what it currently is
                        if (isGoing) {
                            btn.classList.remove('active');
                        } else {
                            btn.classList.add('active');
                        }
                    } else {
                        alert("Something went wrong. Please try again.");
                    }
                })
            .catch(error => console.log('Error:', error));
}

const rsvpButton = document.querySelector('#rsvp-btn');
rsvpButton.addEventListener('click', handleRsvpClick);