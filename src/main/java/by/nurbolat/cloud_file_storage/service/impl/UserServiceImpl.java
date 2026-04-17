package by.nurbolat.cloud_file_storage.service.impl;

import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.entity.User;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.repository.UserRepository;
import by.nurbolat.cloud_file_storage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var maybeUser = userRepository.findUserByEmail(username);

        if (maybeUser.isEmpty())
            throw new UsernameNotFoundException("User with email: "+username+" not found!");

        User user = maybeUser.get();

        List<GrantedAuthority> roles  = List.of(
                new SimpleGrantedAuthority("ROLE_"+user.getRole().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                roles
        );
    }

    @Override
    public UserReadDto getCurrentUser() throws UserNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null){
            throw new UserNotFoundException("User not found");
        }

        var user = userRepository.findUserByEmail(authentication.getName());

        if (user.isEmpty()){
            throw new UsernameNotFoundException("User by username not found!");
        }

        return UserReadDto.builder()
                .name(user.get().getName())
                .email(user.get().getEmail())
                .build();
    }

    @Override
    public Long getCurrentUserId() throws UserNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null){
            throw new UserNotFoundException("User not found");
        }

        var user = userRepository.findUserByEmail(authentication.getName());

        if (user.isEmpty()){
            throw new UsernameNotFoundException("User by username not found!");
        }


        return user.get().getId();
    }


}
