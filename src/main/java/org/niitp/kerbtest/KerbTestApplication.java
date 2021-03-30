package org.niitp.kerbtest;

import com.kerb4j.client.SpnegoClient;
import com.kerb4j.client.SpnegoContext;
import com.kerb4j.client.spring.KerberosRestTemplate;
import com.kerb4j.client.spring.SpnegoRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@SpringBootApplication
public class KerbTestApplication implements CommandLineRunner {

    @Value("${app.user-principal}")
    private String userPrincipal;

    @Value("${app.access-url}")
    private String accessUrl;

    @Override
    public void run(String... args) {
        try {
            SpnegoClient spnegoClientCache = SpnegoClient.loginWithTicketCache(userPrincipal);
            SpnegoRestTemplate spnegoRestTemplateCache = new SpnegoRestTemplate(spnegoClientCache);
            SpnegoContext context = spnegoClientCache.createContext(new URL(accessUrl));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", context.createTokenAsAuthroizationHeader());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = spnegoRestTemplateCache.exchange(accessUrl, HttpMethod.GET, entity, String.class);
            log.info("response.StatusCodeValue {}", response.getStatusCodeValue());
            log.info("response.Body {}", response.getBody());

        } catch (Exception e) {
            log.info("", e);
        }
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(KerbTestApplication.class).web(WebApplicationType.NONE).run(args);
    }

}
