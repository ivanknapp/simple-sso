package ru.knapp.simplesso.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class IntrospectionPrincipal {

    private UUID id;
    private String firstName;
    private String secondName;
    private String middleName;
    private LocalDate birthday;
    private String avatarUrl;
    private String username;
    private String email;
    private Collection<String> authorities;

    public static IntrospectionPrincipal build(AuthorizedUser authorizedUser) {
        if (authorizedUser == null) {
            return null;
        }

        // создаём список строк из authorities в AuthorizedUser
        List<String> authorities = Collections.emptyList();
        if (authorizedUser.getAuthorities() != null) {
            authorities = authorizedUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        }

        return IntrospectionPrincipal.builder()
            .id(authorizedUser.getId())
            .firstName(authorizedUser.getFirstName())
            .secondName(authorizedUser.getSecondName())
            .middleName(authorizedUser.getMiddleName())
            .birthday(authorizedUser.getBirthday())
            .avatarUrl(authorizedUser.getAvatarUrl())
            .username(authorizedUser.getUsername())
            .email(authorizedUser.getEmail())
            .authorities(authorities)
            .build();
    }
}