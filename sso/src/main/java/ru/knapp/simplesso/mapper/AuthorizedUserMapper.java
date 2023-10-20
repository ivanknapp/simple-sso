package ru.knapp.simplesso.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.knapp.simplesso.dao.entity.AuthorityEntity;
import ru.knapp.simplesso.dao.entity.RoleEntity;
import ru.knapp.simplesso.dao.entity.UserEntity;
import ru.knapp.simplesso.domain.AuthProvider;
import ru.knapp.simplesso.domain.AuthorizedUser;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AuthorizedUserMapper {

    public AuthorizedUser map(UserEntity entity, AuthProvider provider) {
        return AuthorizedUser.builder(entity.getEmail(), entity.getPasswordHash(), getUserAuthorities(entity))
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .secondName(entity.getLastName())
            .middleName(entity.getMiddleName())
            .birthday(entity.getBirthday())
            .avatarUrl(entity.getAvatarUrl())
            .build();
    }

    // получаем список привилегий из сущности и преобразовываем каждый код привилегии в объект SimpleGrantedAuthority
    public List<GrantedAuthority> getUserAuthorities(UserEntity entity) {
        return entity.getRoles().stream()
            .filter(RoleEntity::getActive)
            .flatMap(role -> role.getAuthorities().stream())
            .filter(AuthorityEntity::getActive)
            .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
            .collect(Collectors.toList());
    }
}