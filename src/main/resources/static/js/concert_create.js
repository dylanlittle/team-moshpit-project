    const countryInput = document.getElementById('country');
    const cityInput = document.getElementById('city');
    const venueNameInput = document.getElementById('venue-name');
    const venueAddressInput = document.getElementById('venue-address');
    const citiesList = document.getElementById('cities-list');
    const venuesList = document.getElementById('venues-list');

    let venueTimeout;

    // Helper functions for cascaded input clearing
    function clearCityAndBelow() {
        cityInput.value = '';
        citiesList.innerHTML = '';

        clearVenueAndBelow();
    }

    function clearVenueAndBelow() {
        venueNameInput.value = '';
        venuesList.innerHTML = '';

        clearAddress();
    }

    function clearAddress() {
        venueAddressInput.value = '';
        venueAddressInput.readOnly = false;
    }

    // When country changes or is typed, fetch cities. If country changes to empty, clear city and below
    countryInput.addEventListener('input', function() {
        const country = this.value.trim();

        if (country === '') {
            clearCityAndBelow();
            return;
        }

        clearTimeout(venueTimeout);
        venueTimeout = setTimeout(async () => {
            if (country.length >= 2) {
                try {
                    const response = await fetch(`/venues/cities?country=${encodeURIComponent(country)}`);
                    const cities = await response.json();

                    // Clear
                    citiesList.innerHTML = '';

                    // Populate cities datalist
                    cities.forEach(city => {
                        const option = document.createElement('option');
                        option.value = city;
                        citiesList.appendChild(option);
                    });
                } catch (error) {
                    console.error('Error fetching cities:', error);
                }
            }
        }, 300); // Wait 300ms after user stops typing
    });

    // When city changes, fetch venues. If city changes to empty, clear venue and below
    cityInput.addEventListener('input', function() {
        const city = this.value.trim();

        if (city === '') {
            clearVenueAndBelow();
            return;
        }

        clearTimeout(venueTimeout);
        venueTimeout = setTimeout(async () => {
            const country = countryInput.value.trim();

            if (city.length >= 2 && country.length >= 2) {
                try {
                    const response = await fetch(`/venues/by-location?country=${encodeURIComponent(country)}&city=${encodeURIComponent(city)}`
                    );
                    const venues = await response.json();

                    // clear
                    venuesList.innerHTML = '';

                    // Populate venues dataList
                    venues.forEach(venue => {
                        const option = document.createElement('option');
                        option.value = venue;
                        venuesList.appendChild(option);
                    });
                } catch (error) {
                    console.error('Error fetching venues:', error);
                }
            }
        }, 300);
    });

    // When venue name changes, fetch address. If venue name changes to empty, clear venue address.
    venueNameInput.addEventListener('input', function() {
        const venueName = this.value.trim();

        if (venueName === '') {
            clearAddress();
            return;
        }

        clearTimeout(venueTimeout);
        venueTimeout = setTimeout(async () => {
            const city = cityInput.value.trim();
            const country = countryInput.value.trim();

            clearAddress();

            if (city.length >= 2 && country.length >= 2 && venueName.length >= 2) {
                try {
                    const response = await fetch(`/venue-address/by-location?venueName=${encodeURIComponent(venueName)}&country=${encodeURIComponent(country)}&city=${encodeURIComponent(city)}`);

                    // Venue exists → autofill
                    if (response.ok) {
                        const address = await response.text();

                        if (address) {
                            venueAddressInput.value = address;
                            venueAddressInput.readOnly = true; // lock if from DB
                        }
                    }
                    // If venue does NOT exist → user types freely (no action needed)
                } catch (error) {
                    console.error('Error fetching venue address:', error);
                }
            }
        }, 300);
    });