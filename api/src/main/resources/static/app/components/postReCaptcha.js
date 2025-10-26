export async function postReCaptcha(executeRecaptcha, expectedAction) {
  if (typeof executeRecaptcha !== 'function') {
    throw new Error('Recaptcha not available - executeRecaptcha must be provided');
  }

  if (!expectedAction || typeof expectedAction !== 'string') {
    throw new Error('Invalid action provided for recaptcha');
  }

  try {
    const token = await executeRecaptcha(expectedAction);
    return token;
  } catch (error) {
    console.error('Recaptcha execution error:', error);
    throw error;
  }
}