package com.example.oauth2session.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class CustomClientRegistrationRepo {

    private final SocialClientRegistration socialClientRegistration;

    public CustomClientRegistrationRepo(SocialClientRegistration socialClientRegistration) {
        this.socialClientRegistration = socialClientRegistration;
    }

    public ClientRegistrationRepository clientRegistrationRepository() {
        //인메모리 방식, JDBC 방식이 있는데 구글,네이버,깃허브 등 최대 10개 정도이므로 인메모리를 써도 무관
        return new InMemoryClientRegistrationRepository(
                socialClientRegistration.naverClientRegistration(),
                socialClientRegistration.googleClientRegistration());
    }

}
