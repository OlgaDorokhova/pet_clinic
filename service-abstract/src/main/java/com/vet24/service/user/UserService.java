package com.vet24.service.user;

import com.vet24.models.user.User;
import com.vet24.service.ReadWriteService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService, ReadWriteService<Long, User> {

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;

    User getCurrentUser(); // temporary solution. Always returns User with id = 3
}
