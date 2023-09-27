package ru.knapp.simplesso.mapper;

import lombok.experimental.UtilityClass;
import ru.knapp.simplesso.domain.AuthProvider;
import ru.knapp.simplesso.domain.AuthorizedUser;
import ru.knapp.simplesso.domain.UserEntity;

import java.util.Collections;

@UtilityClass
public class AuthorizedUserMapper {

    public AuthorizedUser map(UserEntity entity, AuthProvider provider) {
        return AuthorizedUser.builder(entity.getEmail(), entity.getPasswordHash(), Collections.emptyList())
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .secondName(entity.getSecondName())
            .middleName(entity.getMiddleName())
            .birthday(entity.getBirthday())
            .avatarUrl(entity.getAvatarUrl())
            .build();
    }
}