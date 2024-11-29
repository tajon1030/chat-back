package com.example.demo.security;

import com.example.demo.entity.ERole;
import com.example.demo.entity.Role;
import com.example.demo.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
//        Users user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        Users user = new Users(username, "test@gmail.com", BCrypt.hashpw("1234", BCrypt.gensalt()));
        user.setRoles(Set.of(new Role(ERole.ROLE_USER)));
        user.setSeq(1L);

        return UserDetailsImpl.build(user);
    }


}
