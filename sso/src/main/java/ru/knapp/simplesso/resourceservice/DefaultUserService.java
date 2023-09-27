package ru.knapp.simplesso.resourceservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.knapp.simplesso.domain.AuthErrorCode;
import ru.knapp.simplesso.domain.AuthProvider;
import ru.knapp.simplesso.domain.AuthorizedUser;
import ru.knapp.simplesso.domain.UserEntity;
import ru.knapp.simplesso.exception.AuthException;
import ru.knapp.simplesso.mapper.AuthorizedUserMapper;
import ru.knapp.simplesso.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    /**
     * Создание или обновление пользователя
     */
    @Override
    public UserEntity save(OAuth2User userDto, AuthProvider provider) {
        return switch (provider) {
            case GITHUB -> this.saveUserFromGithab(userDto);
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
    private UserEntity saveUserFromGithab(OAuth2User userDto) {
        String email = userDto.getAttribute("email");           // пытаемся получить атрибут email
        if (email == null) {                                          // если данного атрибута нет или он пустой, то генерируем исключение с указанием того, что нет email
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = this.userRepository.findByEmail(email);     // пытаемся найти пользователя в нашем хранилище по email
        if (user == null) {                                           // если пользователя не существует у нас, то создаём новую сущность UserEntity
            user = new UserEntity();
            user.setEmail(email);
            user.setActive(true);                                     // пока пусть все созданные пользователи будут активными
        }

        if (userDto.getAttribute("name") != null) {             // получаем firstName, lastName и middleName
            String[] splitted = ((String) userDto.getAttribute("name")).split(" ");
            user.setFirstName(splitted[0]);
            if (splitted.length > 1) {
                user.setSecondName(splitted[1]);
            }
            if (splitted.length > 2) {
                user.setMiddleName(splitted[2]);
            }
        } else {                                                      // иначе устанавливаем в эти поля значение email
            user.setFirstName(userDto.getAttribute("login"));   // конечно в реальных проектах так делать не надо, здесь это сделано для упрощения логики
            user.setSecondName(userDto.getAttribute("login"));
        }

        if (userDto.getAttribute("avatar_url") != null) {       // если есть аватар, то устанавливаем значение в поле avatarUrl
            user.setAvatarUrl(userDto.getAttribute("avatar_url"));
        }
        return userRepository.save(user);                             // сохраняем сущность UserEntity
    }

    /**
     * Метод описывающий создание/обновление UserEntity на основе OAuth2User полученного из провайдера Google
     */
    private UserEntity saveUserFromGoogle(OAuth2User userDto) {
        String email = userDto.getAttribute("email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = this.userRepository.findByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setActive(true);
        }

        if (userDto.getAttribute("given_name") != null) {
            user.setFirstName(userDto.getAttribute("given_name"));
        }

        if (userDto.getAttribute("family_name") != null) {
            user.setSecondName(userDto.getAttribute("family_name"));
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
        UserEntity user = this.userRepository.findByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setActive(true);
        }

        if (userDto.getAttribute("first_name") != null) {
            user.setFirstName(userDto.getAttribute("first_name"));
        }

        if (userDto.getAttribute("last_name") != null) {
            user.setSecondName(userDto.getAttribute("last_name"));
        }

        if (userDto.getAttribute("default_avatar_id") != null) {
            user.setAvatarUrl(userDto.getAttribute("default_avatar_id"));
        }

        return userRepository.save(user);
    }
}