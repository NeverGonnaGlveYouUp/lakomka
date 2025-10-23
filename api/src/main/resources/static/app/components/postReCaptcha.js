export async function postReCaptcha(e, expectedAction) {
    e.preventDefault();
    return new Promise((resolve, reject) => {
        grecaptcha.enterprise.ready(async () => {
            try {
                const token = await grecaptcha.enterprise.execute('6Lf3LuYrAAAAAJqGCS8WfdcmtAl-RsYvSvHEXW94', {action: expectedAction});
                resolve(token);
            } catch (error) {
                reject(error);
            }
        });
    });
}