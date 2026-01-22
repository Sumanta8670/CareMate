package com.Sumanta.caremate.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Async
    public void sendNurseRegistrationEmail(String toEmail, String nurseName) {
        String subject = "Welcome to CareMate - Nurse Registration Successful! 🏥";
        String htmlContent = buildNurseRegistrationTemplate(nurseName);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Async
    public void sendPatientRegistrationEmail(String toEmail, String patientName) {
        String subject = "Welcome to CareMate - Patient Registration Successful! 💙";
        String htmlContent = buildPatientRegistrationTemplate(patientName);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Async
    public void sendFamilyNotificationEmail(String toEmail, String patientName) {
        String subject = "CareMate - Your Family Member Has Registered";
        String htmlContent = buildFamilyNotificationTemplate(patientName);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildNurseRegistrationTemplate(String nurseName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    }
                    .email-container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: white;
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        padding: 40px 20px;
                        text-align: center;
                    }
                    .logo {
                        font-size: 48px;
                        color: white;
                        margin-bottom: 10px;
                    }
                    .header h1 {
                        color: white;
                        margin: 0;
                        font-size: 32px;
                        font-weight: 700;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .welcome-text {
                        font-size: 24px;
                        color: #333;
                        margin-bottom: 20px;
                        font-weight: 600;
                    }
                    .message {
                        font-size: 16px;
                        color: #666;
                        line-height: 1.8;
                        margin-bottom: 30px;
                    }
                    .info-box {
                        background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                        border-left: 4px solid #667eea;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 8px;
                    }
                    .info-box h3 {
                        color: #667eea;
                        margin-top: 0;
                        font-size: 18px;
                    }
                    .info-box ul {
                        margin: 10px 0;
                        padding-left: 20px;
                    }
                    .info-box li {
                        color: #555;
                        margin: 8px 0;
                    }
                    .cta-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 15px 40px;
                        text-decoration: none;
                        border-radius: 50px;
                        font-weight: 600;
                        margin: 20px 0;
                        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 30px;
                        text-align: center;
                        color: #888;
                        font-size: 14px;
                    }
                    .footer-icon {
                        font-size: 24px;
                        margin: 10px 0;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="logo">🏥</div>
                        <h1>CareMate</h1>
                    </div>
                    
                    <div class="content">
                        <div class="welcome-text">Welcome to CareMate, %s! 👋</div>
                        
                        <div class="message">
                            Congratulations! Your registration as a <strong>Nurse/Caretaker</strong> has been successfully completed. We're thrilled to have you join our community of healthcare professionals dedicated to providing exceptional care.
                        </div>
                        
                        <div class="info-box">
                            <h3>✨ What's Next?</h3>
                            <ul>
                                <li>Complete your profile to increase visibility</li>
                                <li>Set your availability and working hours</li>
                                <li>Start accepting patient care requests</li>
                                <li>Maintain your professional status with quality care</li>
                            </ul>
                        </div>
                        
                        <div class="info-box">
                            <h3>💡 Benefits of Being a CareMate Professional</h3>
                            <ul>
                                <li>Flexible working hours</li>
                                <li>Direct connection with patients</li>
                                <li>Competitive compensation</li>
                                <li>Professional growth opportunities</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="#" class="cta-button">Access Your Dashboard</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-icon">💙</div>
                        <p><strong>CareMate</strong> - Compassionate Care at Your Doorstep</p>
                        <p>For support, contact us at support@caremate.com</p>
                        <p style="margin-top: 20px; font-size: 12px; color: #aaa;">
                            © 2026 CareMate. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nurseName);
    }

    private String buildPatientRegistrationTemplate(String patientName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #1e3c72 0%%, #2a5298 100%%);
                    }
                    .email-container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: white;
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    }
                    .header {
                        background: linear-gradient(135deg, #1e3c72 0%%, #2a5298 100%%);
                        padding: 40px 20px;
                        text-align: center;
                    }
                    .logo {
                        font-size: 48px;
                        color: white;
                        margin-bottom: 10px;
                    }
                    .header h1 {
                        color: white;
                        margin: 0;
                        font-size: 32px;
                        font-weight: 700;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .welcome-text {
                        font-size: 24px;
                        color: #333;
                        margin-bottom: 20px;
                        font-weight: 600;
                    }
                    .message {
                        font-size: 16px;
                        color: #666;
                        line-height: 1.8;
                        margin-bottom: 30px;
                    }
                    .info-box {
                        background: linear-gradient(135deg, #e0f7fa 0%%, #b2ebf2 100%%);
                        border-left: 4px solid #2a5298;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 8px;
                    }
                    .info-box h3 {
                        color: #2a5298;
                        margin-top: 0;
                        font-size: 18px;
                    }
                    .info-box ul {
                        margin: 10px 0;
                        padding-left: 20px;
                    }
                    .info-box li {
                        color: #555;
                        margin: 8px 0;
                    }
                    .cta-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #1e3c72 0%%, #2a5298 100%%);
                        color: white;
                        padding: 15px 40px;
                        text-decoration: none;
                        border-radius: 50px;
                        font-weight: 600;
                        margin: 20px 0;
                        box-shadow: 0 10px 30px rgba(30, 60, 114, 0.4);
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 30px;
                        text-align: center;
                        color: #888;
                        font-size: 14px;
                    }
                    .footer-icon {
                        font-size: 24px;
                        margin: 10px 0;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="logo">🏥</div>
                        <h1>CareMate</h1>
                    </div>
                    
                    <div class="content">
                        <div class="welcome-text">Welcome to CareMate, %s! 🌟</div>
                        
                        <div class="message">
                            Thank you for choosing <strong>CareMate</strong> for your healthcare needs. Your registration has been successfully completed, and you're now part of our caring community. We're committed to providing you with the best possible care and support.
                        </div>
                        
                        <div class="info-box">
                            <h3>🎯 Next Steps</h3>
                            <ul>
                                <li>Browse available nurses and caretakers</li>
                                <li>Select your preferred care professional</li>
                                <li>Choose a subscription plan that fits your needs</li>
                                <li>Schedule your first care session</li>
                            </ul>
                        </div>
                        
                        <div class="info-box">
                            <h3>💙 Why CareMate?</h3>
                            <ul>
                                <li>Verified and experienced healthcare professionals</li>
                                <li>24/7 customer support</li>
                                <li>Flexible subscription plans</li>
                                <li>Quality care at your doorstep</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="#" class="cta-button">Browse Nurses</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-icon">❤️</div>
                        <p><strong>CareMate</strong> - Your Health, Our Priority</p>
                        <p>Need help? Contact us at support@caremate.com</p>
                        <p style="margin-top: 20px; font-size: 12px; color: #aaa;">
                            © 2026 CareMate. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName);
    }

    private String buildFamilyNotificationTemplate(String patientName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #43cea2 0%%, #185a9d 100%%);
                    }
                    .email-container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: white;
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    }
                    .header {
                        background: linear-gradient(135deg, #43cea2 0%%, #185a9d 100%%);
                        padding: 40px 20px;
                        text-align: center;
                    }
                    .logo {
                        font-size: 48px;
                        color: white;
                        margin-bottom: 10px;
                    }
                    .header h1 {
                        color: white;
                        margin: 0;
                        font-size: 32px;
                        font-weight: 700;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .welcome-text {
                        font-size: 24px;
                        color: #333;
                        margin-bottom: 20px;
                        font-weight: 600;
                    }
                    .message {
                        font-size: 16px;
                        color: #666;
                        line-height: 1.8;
                        margin-bottom: 30px;
                    }
                    .info-box {
                        background: linear-gradient(135deg, #e8f5e9 0%%, #c8e6c9 100%%);
                        border-left: 4px solid #43cea2;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 8px;
                    }
                    .info-box h3 {
                        color: #185a9d;
                        margin-top: 0;
                        font-size: 18px;
                    }
                    .info-box p {
                        color: #555;
                        margin: 10px 0;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 30px;
                        text-align: center;
                        color: #888;
                        font-size: 14px;
                    }
                    .footer-icon {
                        font-size: 24px;
                        margin: 10px 0;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="logo">🏥</div>
                        <h1>CareMate</h1>
                    </div>
                    
                    <div class="content">
                        <div class="welcome-text">Important Notification 📋</div>
                        
                        <div class="message">
                            This is to inform you that <strong>%s</strong> has registered on CareMate to receive professional healthcare services at home.
                        </div>
                        
                        <div class="info-box">
                            <h3>📌 What This Means</h3>
                            <p>Your family member will now have access to verified nurses and caretakers who can provide quality care at home. You've been added as a family contact for important updates and notifications.</p>
                        </div>
                        
                        <div class="info-box">
                            <h3>🔔 You'll Receive Notifications About</h3>
                            <p>• Care session schedules<br>
                            • Subscription renewals<br>
                            • Health updates and reports<br>
                            • Emergency alerts (if any)</p>
                        </div>
                        
                        <div class="message">
                            If you have any questions or concerns, please don't hesitate to contact our support team.
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-icon">💚</div>
                        <p><strong>CareMate</strong> - Caring Together</p>
                        <p>Contact us: support@caremate.com | +91 1234567890</p>
                        <p style="margin-top: 20px; font-size: 12px; color: #aaa;">
                            © 2026 CareMate. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName);
    }
}