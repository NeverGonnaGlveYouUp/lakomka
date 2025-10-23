import React, { useState } from 'react';
import { GoogleReCaptchaProvider } from 'react-google-recaptcha-v3';
import { useRecaptcha } from './useRecaptcha';
import { recaptchaApi } from './recaptchaApi';

const ContactFormInner = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    message: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [result, setResult] = useState(null);

  const { verifyRecaptcha } = useRecaptcha();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setResult(null);

    try {
      // Get reCAPTCHA token
      const recaptchaToken = await verifyRecaptcha('contact_form');

      if (!recaptchaToken) {
        throw new Error('Failed to get reCAPTCHA token');
      }

      // Verify with backend
      const verificationResult = await recaptchaApi.verifyDetailed(recaptchaToken);

      if (verificationResult.success) {
        // Proceed with form submission
        console.log('Form submitted successfully:', formData);
        setResult({
          type: 'success',
          message: `Form submitted successfully! Score: ${verificationResult.score}`
        });

        // Reset form
        setFormData({ name: '', email: '', message: '' });
      } else {
        throw new Error(`reCAPTCHA verification failed. Score: ${verificationResult.score}`);
      }
    } catch (error) {
      console.error('Submission error:', error);
      setResult({
        type: 'error',
        message: error.message || 'Submission failed. Please try again.'
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="contact-form">
      <h2>Contact Us</h2>

      {result && (
        <div className={`alert ${result.type === 'success' ? 'alert-success' : 'alert-error'}`}>
          {result.message}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Name:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="message">Message:</label>
          <textarea
            id="message"
            name="message"
            value={formData.message}
            onChange={handleChange}
            required
          />
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="submit-button"
        >
          {isSubmitting ? 'Submitting...' : 'Submit'}
        </button>
      </form>

      <p className="recaptcha-notice">
        This site is protected by reCAPTCHA and the Google
        <a href="https://policies.google.com/privacy">Privacy Policy</a> and
        <a href="https://policies.google.com/terms">Terms of Service</a> apply.
      </p>
    </div>
  );
};

// Main component with reCAPTCHA provider
const ContactForm = () => {
  return (
    <GoogleReCaptchaProvider
      reCaptchaKey="6Lf1gPQrAAAAAG_tjJ1Jy4QuHJjKy5uBEZZc0z3y"
      language="en"
      scriptProps={{
        async: false,
        defer: false,
        appendTo: 'head',
        nonce: undefined,
      }}
    >
      <ContactFormInner />
    </GoogleReCaptchaProvider>
  );
};

export default ContactForm;