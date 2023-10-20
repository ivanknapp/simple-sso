package ru.knapp.simplesso.resourceservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.knapp.simplesso.dao.entity.UserEntity;
import ru.knapp.simplesso.dao.repository.RoleRepository;
import ru.knapp.simplesso.dao.repository.UserRepository;
import ru.knapp.simplesso.domain.AuthErrorCode;
import ru.knapp.simplesso.domain.AuthProvider;
import ru.knapp.simplesso.domain.AuthorizedUser;
import ru.knapp.simplesso.exception.AuthException;
import ru.knapp.simplesso.mapper.AuthorizedUserMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Создание или обновление пользователя
     */
    @Override
    @Transactional
    public UserEntity save(OAuth2User userDto, AuthProvider provider) {
        return switch (provider) {
            case GITHUB -> this.saveUserFromGithub(userDto);
            case GOOGLE -> this.saveUserFromGoogle(userDto);
            case YANDEX -> this.saveUserFromYandex(userDto);
        };
    }

    /**
     * Создание или обновление пользователя с последующим маппингом в сущность AuthorizedUser
     */
    @Override
    public AuthorizedUser saveAndMap(OAuth2User userDto, AuthProvider provider) {
        UserEntity entity = this.save(userDto, provider);
        return AuthorizedUserMapper.map(entity, provider);
    }

    /**
     * Метод описывающий создание/обновление UserEntity на основе OAuth2User полученного из провайдера Github
     */
    private UserEntity saveUserFromGithub(OAuth2User userDto) {
        String email = userDto.getAttribute("email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = getEntityByEmail(email);

        if (userDto.getAttribute("name") != null) {
            String[] splitted = ((String) userDto.getAttribute("name")).split(" ");
            user.setFirstName(splitted[0]);
            if (splitted.length > 1) {
                user.setLastName(splitted[1]);
            }
            if (splitted.length > 2) {
                user.setMiddleName(splitted[2]);
            }
        } else {
            user.setFirstName(userDto.getAttribute("login"));
            user.setLastName(userDto.getAttribute("login"));
        }

        if (userDto.getAttribute("avatar_url") != null) {
            user.setAvatarUrl(userDto.getAttribute("avatar_url"));
        }
        return userRepository.save(user);
    }

    /**
     * Метод описывающий создание/обновление UserEntity на основе OAuth2User полученного из провайдера Google
     */
    private UserEntity saveUserFromGoogle(OAuth2User userDto) {
        String email = userDto.getAttribute("email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = getEntityByEmail(email);

        if (userDto.getAttribute("given_name") != null) {
            user.setFirstName(userDto.getAttribute("given_name"));
        }

        if (userDto.getAttribute("family_name") != null) {
            user.setLastName(userDto.getAttribute("family_name"));
        }

        if (userDto.getAttribute("picture") != null) {
            user.setAvatarUrl(userDto.getAttribute("picture"));
        }

        return userRepository.save(user);
    }

    /**
     * Метод описывающий создание/обновление UserEntity на основе OAuth2User полученного из провайдера Google
     */
    private UserEntity saveUserFromYandex(OAuth2User userDto) {
        String email = userDto.getAttribute("default_email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = getEntityByEmail(email);

        if (userDto.getAttribute("first_name") != null) {
            user.setFirstName(userDto.getAttribute("first_name"));
        }

        if (userDto.getAttribute("last_name") != null) {
            user.setLastName(userDto.getAttribute("last_name"));
        }

        if (userDto.getAttribute("default_avatar_id") != null) {
            user.setAvatarUrl(userDto.getAttribute("default_avatar_id"));
        }

        return userRepository.save(user);
    }

    /**
     * Метод получения сущности UserEntity по email
     * Если пользователь с данным email не найден в БД, то создаём новую сущность
     */
    private UserEntity getEntityByEmail(String email) {
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = this.userRepository.findByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setActive(true);
            // добавляем роль по умолчанию
            user.setRoles(List.of(roleRepository.getDefaultRole()));
        }
        return user;
    }

}