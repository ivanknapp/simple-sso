package ru.knapp.simplesso.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.knapp.simplesso.dao.entity.common.VersionedBusinessEntity;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "sso", name = "authorities")
public class AuthorityEntity extends VersionedBusinessEntity<String> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "system_code", nullable = false)
    private String systemCode;

    @Column(name = "active")
    private Boolean active;

    @Override
    public String getId() {
        return this.code;
    }

    @Override
    public void setId(String code) {
        this.code = code;
    }

}