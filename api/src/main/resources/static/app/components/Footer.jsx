import React from 'react';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="about-section">
          <h3>О нас</h3>
          <p>
            We are a leading company in our industry, dedicated to providing high-quality products and services to our customers. Our mission is to innovate and deliver exceptional value while maintaining a commitment to sustainability and community engagement.
          </p>
        </div>
        <div className="links-section">
          <h3>Полезные ссылки</h3>
          <ul>
            <li><a href="/about">Our Story</a></li>
            <li><a href="/team">Our Team</a></li>
            <li><a href="/careers">Careers</a></li>
          </ul>
        </div>
        <div className="contact-section">
          <h3>Контакты</h3>
          <p>Email: <a href="mailto:info@example.com">info@example.com</a></p>
          <p>Телефон: <a href="tel:+1234567890">+1 234 567 890</a></p>
          <p>Почта: 123 Main Street, City, Country</p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>&copy; {new Date().getFullYear()} Your Company Name. All rights reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;
