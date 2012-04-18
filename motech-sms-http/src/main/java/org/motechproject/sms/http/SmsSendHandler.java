package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
public class SmsSendHandler implements SmsEventHandler {
	
	public static final String CREDENTIALS_USERNAME = "username";
    public static final String CREDENTIALS_PASSWORD = "password";
    private Properties properties;

    private SmsSendTemplate template;
    private HttpClient commonsHttpClient;
    private static Logger log = LoggerFactory.getLogger(SmsSendHandler.class);

    @Autowired
    public SmsSendHandler(TemplateReader templateReader, HttpClient commonsHttpClient, @Qualifier(value = "credentialProperties") Properties properties) {
        String templateFile = "/sms-http-template.json";
        this.template = templateReader.getTemplate(templateFile);
        this.commonsHttpClient = commonsHttpClient;
        this.properties = properties;
    }

    @Override
    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) throws IOException, SmsDeliveryFailureException {
    	System.out.println("Handling");
    	List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        HttpMethod httpMethod = template.generateRequestFor(recipients, message);
        
        if (username() != null && password() != null) {
            commonsHttpClient.getState().setCredentials(new AuthScope(httpMethod.getURI().getHost(), 80), new UsernamePasswordCredentials(username(), password())); //the host is implementation specific right now
        }
        
        int status = commonsHttpClient.executeMethod(httpMethod);
        
        System.out.println("Status: " + status);
        String response = httpMethod.getResponseBodyAsString();
        System.out.println("Response: " + response);
        log.info("HTTP Status:" + status + "|Response:" + response);

        if (response != null && !response.contains(template.getResponse().success)) {
            log.info("delivery failed, retrying...");
            throw new SmsDeliveryFailureException();
        }
    }
    
    private String username() {
        return this.properties.getProperty(CREDENTIALS_USERNAME);
    }
    private String password() {
        return this.properties.getProperty(CREDENTIALS_PASSWORD);
    }
}
