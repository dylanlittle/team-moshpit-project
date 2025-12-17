// Set user timezone cookie if not exists, reload once
(function() {
    function getCookie(name) {
        const nameEQ = name + '=';
        return document.cookie.split(';').find(row => row.trim().startsWith(nameEQ))?.split('=')[1];
    }

    if (!getCookie('userTz')) {
        const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
        document.cookie = 'userTz=' + encodeURIComponent(tz) + '; path=/; max-age=3600'; // cookie expires in one hour
        location.reload();
    }
})();
