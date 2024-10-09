package com.university.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.university.models.noBD.AppProperties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.List;

@Component
public class MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private AppProperties appProperties;

    /**
     *
     * @param correo correo electronico destinatario
     * @param parametro parametro que vaya a ir en el reporte
     * @param tipoDeCorreo 1: confirmacion, 2.recuperacion, 3. Bajo Stock
     */
    public void enviarCorreoEnSegundoPlano(String correo, String parametro, int tipoDeCorreo) {
        Thread hiloMail = new Thread() {
            @Override
            public void run() {
                switch (tipoDeCorreo) {
                    case 1:
                        enviarCorreoTwoFactorToken(correo, parametro);
                        break;
                    case 2:
                        enviarCorreoDeRecuperacion(correo, parametro);
                        break;
                    case 3:
                        enviarCorreoDeRecuperacion(correo, parametro);
                        break;
                }
            }
        };
        hiloMail.start();
    }

    private void enviarCorreoDeRecuperacion(String correo, String codigoActivacion) {
        try {
            Context context = new Context();//crear nuevo contexto
            String url = String.format("http://localhost:%s/password_reset/form?c=%s",
                    appProperties.getHostFront1(), codigoActivacion);

            context.setVariable("url", url);//adjuntar las variables
            String html = templateEngine.process("CorreoDeRecuperacion", context);
            //mandamos el correo electronico
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(html, true);//adjuntamos el mansaje indicando que sera un html
            helper.setTo(correo);
            helper.setSubject("Recuperación de cuenta P1.");
            helper.setFrom("P1 <namenotfound4004@gmail.com>");
            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

    private void enviarCorreoTwoFactorToken(String correo, String codigo) {
        try {
            Context context = new Context();//crear nuevo contexto
            context.setVariable("codigo", codigo);//adjuntar las variables
            String html = templateEngine.process("CorreoDosPasos", context);
            //mandamos el correo electronico
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(html, true);//adjuntamos el mansaje indicando que sera un html
            helper.setTo(correo);
            helper.setSubject("Token de verificación P1.");
            helper.setFrom("P1 <namenotfound4004@gmail.com>");
            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
}
