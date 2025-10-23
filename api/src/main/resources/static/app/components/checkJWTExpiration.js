export function checkJWTExpiration() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Math.floor(Date.now() / 1000);

        if (payload.exp < currentTime) {
            localStorage.removeItem('jwtToken');
            console.log('Token has expired and was removed from local storage.');
        } else {
            console.log('Token is still valid.');
        }
    } else {
        console.log('No token found in local storage.');
    }
}