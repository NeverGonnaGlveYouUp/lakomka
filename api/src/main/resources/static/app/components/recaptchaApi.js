const API_BASE_URL = 'http://localhost:8080/api';

export const recaptchaApi = {
  verify: async (recaptchaToken) => {
    const response = await fetch(`${API_BASE_URL}/recaptcha/verify`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ recaptchaToken }),
    });

    if (!response.ok) {
      throw new Error('Verification failed');
    }

    return response.json();
  },

  verifyDetailed: async (recaptchaToken) => {
    const response = await fetch(`${API_BASE_URL}/recaptcha/verify-detailed`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ recaptchaToken }),
    });

    if (!response.ok) {
      throw new Error('Verification failed');
    }

    return response.json();
  }
};