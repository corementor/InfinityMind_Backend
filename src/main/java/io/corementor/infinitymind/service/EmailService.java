package io.corementor.infinitymind.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * The Class EmailService.
 * @author Blaise Mugisha.
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class EmailService {
    /**
     * The Java mail sender.
     */
    private final JavaMailSender mailSender;

    /**
     * Send welcome email.
     * @param toEmail String
     * @param username String
     * @param firstName String
     * @throws MessagingException Exception
     */
    public void sendWelcomeEmail(String toEmail, String username, String firstName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Welcome to Mind Expanse!");

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        font-family: Arial, sans-serif;
                        padding: 20px;
                    }
                    .header {
                        background-color: #4F46E5;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 10px 10px 0 0;
                    }
                    .content {
                        background-color: #ffffff;
                        padding: 20px;
                        border-radius: 0 0 10px 10px;
                        border: 1px solid #e0e0e0;
                    }
                    .username-box {
                        background-color: #f3f4f6;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                        text-align: center;
                        font-size: 18px;
                        font-weight: bold;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 20px;
                        color: #666666;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <h1>Welcome to Mind Expanse!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Thank you for joining Mind Expanse! An online math worksheet, We're excited to have you as part of our community.</p>
                        <p>Your username has been successfully created:</p>
                        <div class="username-box">
                            %s
                        </div>
                        <p>You can use this username to log in to your account. Keep it safe!</p>
                        <p>If you have any questions or need assistance, don't hesitate to contact our support team.</p>
                        <p>Best regards,<br>The Core Mentor Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message, please do not reply directly to this email.</p>
                        <p>© 2025 Core Mentor. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(firstName, username);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
