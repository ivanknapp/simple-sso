package ru.knapp.simplesso.resourceservice;

import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.knapp.simplesso.domain.AuthProvider;
import ru.knapp.simplesso.domain.AuthorizedUser;
import ru.knapp.simplesso.dao.entity.UserEntity;

public interface UserService {

    UserEntity save(OAuth2User userDto, AuthProvider provider);

    AuthorizedUser saveAndMap(OAuth2User userDto, AuthProvider provider);

}