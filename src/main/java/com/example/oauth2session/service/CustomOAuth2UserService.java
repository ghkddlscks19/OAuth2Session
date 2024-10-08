package com.example.oauth2session.service;

import com.example.oauth2session.dto.CustomOAuth2User;
import com.example.oauth2session.dto.GoogleResponse;
import com.example.oauth2session.dto.NaverResponse;
import com.example.oauth2session.dto.OAuth2Response;
import com.example.oauth2session.entity.User;
import com.example.oauth2session.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    //DefaultOAuth2UserService OAuth2UserService의 구현체

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        //registrationId를 통해 구글, 네이버 등 판별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User findUser = userRepository.findByUsername(username);
        String role = null;
        if (findUser == null) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(oAuth2Response.getEmail());
            user.setRole("ROLE_USER");
            userRepository.save(user);
        } else {
            role = findUser.getRole();
            findUser.setEmail(oAuth2Response.getEmail());
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
