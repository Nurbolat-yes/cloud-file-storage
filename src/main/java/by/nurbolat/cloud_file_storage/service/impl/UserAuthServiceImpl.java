package by.nurbolat.cloud_file_storage.service.impl;

import by.nurbolat.cloud_file_storage.dto.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.UserReadDto;
import by.nurbolat.cloud_file_storage.entity.Roles;
import by.nurbolat.cloud_file_storage.entity.User;
import by.nurbolat.cloud_file_storage.exception.custom.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.mapper.UserMapper;
import by.nurbolat.cloud_file_storage.repository.UserRepository;
import by.nurbolat.cloud_file_storage.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserReadDto register(UserCreateDto userCreateDto, HttpServletRequest request) throws UserAlreadyExistsException {

        var maybeExistingUser = userRepository.findUserByEmail(userCreateDto.getEmail());

        if (maybeExistingUser.isPresent()){
            throw new UserAlreadyExistsException("User with email: "+userCreateDto.getEmail()+" already exists!");
        }

        User user = userMapper.toEntity(userCreateDto);

        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setRole(Roles.USER);

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userCreateDto.getEmail(),
                        userCreateDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        return userMapper.toReadDto(savedUser);
    }

    @Override
    public UserReadDto login(UserLoginDto userLoginDto, HttpServletRequest request) throws UsernameNotFoundException, EmailOrPasswordIncorrect, UserNotFoundException {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDto.getEmail(),
                            userLoginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.getSession().setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            var user = userRepository.findUserByEmail(authentication.getName());

            if (user.isEmpty()){
                throw new UserNotFoundException("User not found");
            }

            return UserReadDto.builder()
                    .email(user.get().getEmail())
                    .name(user.get().getName())
                    .build();
        }catch (BadCredentialsException | UsernameNotFoundException e){
            throw new EmailOrPasswordIncorrect();
        }

    }

    @Override
    public void logout(HttpServletRequest request)  {

        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();

    }

}
