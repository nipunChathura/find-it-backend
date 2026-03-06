package lk.icbt.findit.security;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        boolean enabled = !Constants.USER_DELETED_STATUS.equals(user.getStatus())
                && (Constants.USER_APPROVED_STATUS.equals(user.getStatus())
                || Constants.USER_ACTIVE_STATUS.equals(user.getStatus()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                enabled,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
