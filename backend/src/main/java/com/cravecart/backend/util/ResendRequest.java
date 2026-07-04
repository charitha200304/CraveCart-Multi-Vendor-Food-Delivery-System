package com.cravecart.backend.util;

public class ResendRequest {
    private String from;
    private String to;
    private String subject;
    private String html;

    public ResendRequest() {}

    public ResendRequest(String from, String to, String subject, String html) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.html = html;
    }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getHtml() { return html; }
    public void setHtml(String html) { this.html = html; }
}
