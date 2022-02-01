package com.devdojo.valhallaproject.securityopenapi.validateaccess;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class GrantAccess {

  @Autowired
  private WebClient.Builder webClient;
  @Value("${spring.application.name}")
  private String applicationName;


  public boolean grantAccessForUser(String functionName, String permission) {

    String requestParams = "?applicationName=" + applicationName
        + "&functionName=" + functionName
        + "&permission=" + permission;

    log.info("assuring user is authorized");
    return webClient.baseUrl("http://access-control-service")
        .build()
        .get()
        .uri("/access-control-validation" + requestParams)
        .headers(h -> h.setBearerAuth(getCurrentUserToken()))
        .retrieve()
        .bodyToMono(Boolean.class).block();
  }

  private String getCurrentUserToken(){
    KeycloakPrincipal<KeycloakSecurityContext> principal =
        (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    return principal.getKeycloakSecurityContext().getTokenString();
  }
}

