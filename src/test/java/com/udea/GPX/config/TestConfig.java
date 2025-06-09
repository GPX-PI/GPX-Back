package com.udea.GPX.config;

import com.udea.GPX.JwtUtil;
import com.udea.GPX.service.OAuth2Service;
import com.udea.GPX.service.UserService;
import com.udea.GPX.util.AuthUtils;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * Configuración específica para tests que proporciona beans mock
 * para evitar problemas de carga de ApplicationContext
 */
@TestConfiguration
public class TestConfig {

  @Bean
  @Primary
  public JwtUtil jwtUtil() {
    return Mockito.mock(JwtUtil.class);
  }

  @Bean
  @Primary
  public OAuth2Service oAuth2Service() {
    return Mockito.mock(OAuth2Service.class);
  }

  @Bean
  @Primary
  public UserService userService() {
    return Mockito.mock(UserService.class);
  }

  @Bean
  @Primary
  public AuthUtils authUtils() {
    return Mockito.mock(AuthUtils.class);
  }

  @Bean
  @Primary
  public ClientRegistrationRepository clientRegistrationRepository() {
    return Mockito.mock(ClientRegistrationRepository.class);
  }

  @Bean
  @Primary
  public OAuth2AuthorizedClientRepository authorizedClientRepository() {
    return Mockito.mock(OAuth2AuthorizedClientRepository.class);
  }
}