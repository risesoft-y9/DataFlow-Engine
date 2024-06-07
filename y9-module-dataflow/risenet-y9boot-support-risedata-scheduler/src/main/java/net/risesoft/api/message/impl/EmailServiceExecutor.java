package net.risesoft.api.message.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.PatternMatchUtils;

import net.risesoft.api.message.MessageServiceExecutor;
import net.risesoft.api.utils.RegexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 用邮箱的形式发送
 * @ClassName EmailService
 * @Author lb
 * @Date 2022/8/30 10:24
 * @Version 1.0
 */
public class EmailServiceExecutor implements MessageServiceExecutor {


    @Value("${spring.mail.username:}")
    private String from;

    @Autowired()
    private JavaMailSender mailSender;

    /**
     * 发送简单文本的邮件方法
     *
     * @param tos
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String[] tos, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(tos);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    @Override
    public void sendMessage(String[] recipients, String title, String message, Object source) {
        List<String> emails = new ArrayList<>();
        for (String recipient : recipients) {
            if (RegexUtil.isEmail(recipient)) {
                emails.add(recipient);
            }
        }

        if (emails.size() > 0) {
            sendSimpleMail(emails.toArray(new String[emails.size()]), title, message);
        }
    }
}
