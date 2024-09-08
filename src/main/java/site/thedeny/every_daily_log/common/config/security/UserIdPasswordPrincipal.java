package site.thedeny.every_daily_log.common.config.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.thedeny.every_daily_log.common.member.entity.MemberEntity;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
public class UserIdPasswordPrincipal implements UserDetails {
    private String userId;
    private String password;
    private String enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(enabled));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled.equalsIgnoreCase("Y");
    }

    public static UserIdPasswordPrincipal fromEntity(MemberEntity entity) {
        return new UserIdPasswordPrincipal(entity.getId(), entity.getPassword(), entity.getEnabled());
    }
}
