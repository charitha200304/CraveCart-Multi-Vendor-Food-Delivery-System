package com.cravecart.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.web.client.RestTemplate;
import com.cravecart.backend.util.ResendRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${mailjet.sender.name:CraveCart}")
    private String senderName;

    @Value("${RESEND_API_KEY:}")
private String resendApiKey;

@Value("${RESEND_FROM_EMAIL:}")
private String resendFromEmail;

private final RestTemplate restTemplate = new RestTemplate();

    @Async
public void sendVerificationEmail(String toEmail, String name, String verificationCode) {
    System.out.println(">> Sending verification email to: [" + toEmail + "]");

    String frontendUrl = System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:5173");
    String verifyURL = frontendUrl + "/verify?code=" + verificationCode + "&email=" + toEmail;

    String htmlContent = String.format(
        "<h3>Welcome to CraveCart, %s!</h3>" +
        "<p>Please click the link below to verify your registration:</p>" +
        "<h4><a href='%s'>VERIFY MY ACCOUNT</a></h4>" +
        "<p>Thank you,<br>The CraveCart Team</p>",
        name, verifyURL
    );

    if (resendApiKey != null && !resendApiKey.isBlank()) {
        sendViaResend(toEmail, "Please verify your registration", htmlContent);
    } else {
        sendHtmlEmail(toEmail, "Please verify your registration", htmlContent);
    }
}

    @Async
public void sendVerificationOtpEmail(String toEmail, String otpCode) {
    System.out.println(">> Sending OTP email to: [" + toEmail + "]");

    String htmlContent = String.format(
        "<h3>CraveCart Registration OTP</h3>" +
        "<p>Your email verification code is:</p>" +
        "<h2><strong>%s</strong></h2>" +
        "<p>This code is valid for 5 minutes. Please do not share it with anyone.</p>" +
        "<p>Thank you,<br>The CraveCart Team</p>",
        otpCode
    );

    if (resendApiKey != null && !resendApiKey.isBlank()) {
        sendViaResend(toEmail, "Your Email Verification Code - CraveCart", htmlContent);
    } else {
        sendHtmlEmail(toEmail, "Your Email Verification Code - CraveCart", htmlContent);
    }
}

    @Async
    public void sendPasswordResetEmail(String toEmail, String name, String resetToken) {
        System.out.println(">> Sending Mailjet HTTP password reset email to: [" + toEmail + "]");
        
        String frontendUrl = System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:5173");
        String resetURL = frontendUrl + "/reset-password?token=" + resetToken;

        String htmlContent = String.format(
            "<h3>Reset Your Password</h3>" +
            "<p>Dear %s, click the link below to set a new password:</p>" +
            "<h4><a href='%s'>RESET MY PASSWORD</a></h4>" +
            "<p>If you did not request this, please ignore this email.</p>", 
            name, resetURL
        );

        sendHtmlEmail(toEmail, "Reset Your Password - CraveCart", htmlContent);
    }

    @Async
    public void sendOrderReceiptEmail(String toEmail, String name, com.cravecart.backend.entity.Order order) {
        System.out.println(">> Sending Mailjet HTTP order receipt email to: [" + toEmail + "]");

        StringBuilder itemsHtml = new StringBuilder();
        if (order.getItems() != null) {
            for (com.cravecart.backend.entity.OrderItem item : order.getItems()) {
                itemsHtml.append(String.format(
                    "<tr>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>%s</td>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #ddd; text-align: center;'>$%.2f</td>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #ddd; text-align: center;'>%d</td>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #ddd; text-align: right;'>$%.2f</td>" +
                    "</tr>",
                    item.getFoodItem().getName(),
                    item.getFoodItem().getPrice(),
                    item.getQuantity(),
                    item.getPrice()
                ));
            }
        }

        String htmlContent = String.format(
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 8px;'>" +
            "  <div style='text-align: center; margin-bottom: 20px;'>" +
            "    <h2 style='color: #E23744; margin: 0;'>CraveCart</h2>" +
            "    <p style='color: #666; margin: 5px 0 0 0;'>Your Order Confirmation</p>" +
            "  </div>" +
            "  <p>Dear <strong>%s</strong>,</p>" +
            "  <p>Thank you for ordering from <strong>%s</strong>! Your order has been placed successfully. Here is your receipt:</p>" +
            "  <div style='margin: 20px 0;'>" +
            "    <strong>Order ID:</strong> #%d<br>" +
            "    <strong>Order Date:</strong> %s" +
            "  </div>" +
            "  <table style='width: 100%%; border-collapse: collapse; margin-bottom: 20px;'>" +
            "    <thead>" +
            "      <tr style='background-color: #f8f8f8;'>" +
            "        <th style='padding: 10px; border-bottom: 2px solid #ddd; text-align: left;'>Item</th>" +
            "        <th style='padding: 10px; border-bottom: 2px solid #ddd; text-align: center;'>Unit Price</th>" +
            "        <th style='padding: 10px; border-bottom: 2px solid #ddd; text-align: center;'>Qty</th>" +
            "        <th style='padding: 10px; border-bottom: 2px solid #ddd; text-align: right;'>Total</th>" +
            "      </tr>" +
            "    </thead>" +
            "    <tbody>" +
            "      %s" +
            "    </tbody>" +
            "  </table>" +
            "  <div style='text-align: right; margin-bottom: 20px; font-size: 16px;'>" +
            "    <strong>Total Amount:</strong> <span style='font-size: 18px; color: #E23744;'>$%.2f</span>" +
            "  </div>" +
            "  <div style='background-color: #f9f9f9; padding: 15px; border-radius: 6px; margin-bottom: 20px;'>" +
            "    <strong style='display: block; margin-bottom: 5px;'>Delivery Information:</strong>" +
            "    <strong>Address:</strong> %s<br>" +
            "    <strong>Contact:</strong> %s<br>" +
            "    <strong>Payment Method:</strong> %s" +
            "  </div>" +
            "  <p style='color: #666; font-size: 13px; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px;'>" +
            "    We hope you enjoy your meal! If you have any questions, please contact our support team.<br>" +
            "    &copy; CraveCart. All Rights Reserved." +
            "  </p>" +
            "</div>",
            name,
            order.getRestaurant() != null ? order.getRestaurant().getName() : "CraveCart Partner",
            order.getId(),
            order.getOrderDate() != null ? order.getOrderDate().toString() : "Just now",
            itemsHtml.toString(),
            order.getTotalAmount(),
            order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A",
            order.getContactNumber() != null ? order.getContactNumber() : "N/A",
            order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A"
        );

        sendHtmlEmail(toEmail, "Your CraveCart Order Receipt #" + order.getId(), htmlContent);
    }

    /**
     * Sends a generic HTML email.
     */
    /**
     * Sends an HTML email with simple retry mechanism (max 3 attempts).
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(senderEmail, senderName);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);
                System.out.println(">> Gmail SMTP email sent to: [" + to + "] (attempt " + attempt + ")");
                break; // success, exit loop
            } catch (Exception e) {
                System.err.println(">> Failed to send email via Gmail SMTP (attempt " + attempt + "): " + e.getMessage());
                if (attempt == maxAttempts) {
                    // All attempts failed
                    System.err.println(">> All retry attempts exhausted for email to: [" + to + "]");
                } else {
                    try {
                        Thread.sleep(2000); // wait before next retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    
/**
     * Sends an email using Resend's HTTP API.
     */
    private void sendViaResend(String to, String subject, String htmlContent) {
        System.out.println(">> Sending Resend email to: [" + to + "]");
        try {
            String apiUrl = "https://api.resend.com/emails";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (resendApiKey != null && !resendApiKey.isBlank()) {
                headers.setBearerAuth(resendApiKey);
            }
            ResendRequest payload = new ResendRequest(resendFromEmail, to, subject, htmlContent);
            HttpEntity<ResendRequest> request = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(apiUrl, request, String.class);
            System.out.println(">> Resend email sent to: [" + to + "]");
        } catch (Exception e) {
            System.err.println(">> Failed to send email via Resend: " + e.getMessage());
            // Fallback to SMTP
            sendHtmlEmail(to, subject, htmlContent);
        }
    }

}
