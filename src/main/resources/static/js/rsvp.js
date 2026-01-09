function handleRsvpClick(e) {
    e.preventDefault();
    const btn = e.currentTarget;
    console.log("The button works");

    // Read the data-concert-id attribute
    const concertId = btn.getAttribute('data-concert-id');

    const token = document.querySelector('meta[name="_csrf"]').content;
    const url = `/concerts/${concertId}/rsvp`;

     fetch(url, {
                method: 'POST',
                headers: {
                        'X-CSRF-TOKEN': token,
                        'Content-Type': 'application/json'
                    }
            })
            .then(response => {
                    if (response.ok) {
                        const currentUserCard = document.getElementById("attendee-current-user");
                        const noRsvpMessage = document.getElementById("no-rsvp-message");

                        btn.classList.toggle('active');

                        const isGoing = btn.classList.contains('active');

                        if (isGoing) {
                            currentUserCard.classList.remove('hidden');
                            currentUserCard.classList.add('active');
                            if (noRsvpMessage) {
                                noRsvpMessage.classList.add('hidden');
                                noRsvpMessage.classList.remove('active');
                            }
                        } else {
                            currentUserCard.classList.remove('active');
                            currentUserCard.classList.add('hidden');

                            const remainingAttendees = document.querySelectorAll('.user-list li:not(.hidden)');

                            if (remainingAttendees.length === 0) {
                                noRsvpMessage.classList.remove('hidden');
                                noRsvpMessage.classList.add('active');
                            }
                        }
                    } else {
                        alert("Something went wrong. Please try again.");
                    }
                })
            .catch(error => console.log('Error:', error));
}

const rsvpButton = document.querySelector('#rsvp-btn');
rsvpButton.addEventListener('click', handleRsvpClick);