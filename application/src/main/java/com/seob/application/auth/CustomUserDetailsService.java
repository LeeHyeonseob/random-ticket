package com.seob.application.auth;

import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDomain user = userRepository.findByEmail(Email.from(email))
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new CustomUserDetails(
                user.getUserId().getValue(),
                user.getEmail().getValue(),
                user.getRole().name()
        );



    }
}
