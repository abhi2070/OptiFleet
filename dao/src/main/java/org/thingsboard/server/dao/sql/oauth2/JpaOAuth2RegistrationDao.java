
package org.thingsboard.server.dao.sql.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.oauth2.OAuth2Registration;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.oauth2.SchemeType;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.OAuth2RegistrationEntity;
import org.thingsboard.server.dao.oauth2.OAuth2RegistrationDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@SqlDao
public class JpaOAuth2RegistrationDao extends JpaAbstractDao<OAuth2RegistrationEntity, OAuth2Registration> implements OAuth2RegistrationDao {

    private final OAuth2RegistrationRepository repository;

    @Override
    protected Class<OAuth2RegistrationEntity> getEntityClass() {
        return OAuth2RegistrationEntity.class;
    }

    @Override
    protected JpaRepository<OAuth2RegistrationEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    public List<OAuth2Registration> findEnabledByDomainSchemesDomainNameAndPkgNameAndPlatformType(List<SchemeType> domainSchemes, String domainName, String pkgName, PlatformType platformType) {
        return DaoUtil.convertDataList(repository.findEnabledByDomainSchemesDomainNameAndPkgNameAndPlatformType(domainSchemes, domainName, pkgName,
                platformType != null ? "%" + platformType.name() + "%" : null));
    }

    @Override
    public List<OAuth2Registration> findByOAuth2ParamsId(UUID oauth2ParamsId) {
        return DaoUtil.convertDataList(repository.findByOauth2ParamsId(oauth2ParamsId));
    }

    @Override
    public String findAppSecret(UUID id, String pkgName) {
        return repository.findAppSecret(id, pkgName);
    }

}
