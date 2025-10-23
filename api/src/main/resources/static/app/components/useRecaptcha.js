import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import { useCallback } from 'react';

export const useRecaptcha = () => {
  const { executeRecaptcha } = useGoogleReCaptcha();

  const verifyRecaptcha = useCallback(async (action = 'submit') => {
    if (!executeRecaptcha) {
      console.log('Recaptcha not available');
      return null;
    }

    try {
      const token = await executeRecaptcha(action);
      return token;
    } catch (error) {
      console.error('Recaptcha execution error:', error);
      return null;
    }
  }, [executeRecaptcha]);

  return { verifyRecaptcha };
};