package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import udemy.clone.exceptions.EntityNotFoundException;
import udemy.clone.mapper.UserMapper;
import udemy.clone.model.User;
import udemy.clone.model.user.UserDto;
import udemy.clone.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findUserById(String id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserDto findUserDtoById(String id){
        User user = findUserById(id);
        return userMapper.toDto(user);
    }

}
