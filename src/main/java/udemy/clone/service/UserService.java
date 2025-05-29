package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import udemy.clone.exceptions.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.mapper.UserMapper;
import udemy.clone.model.User;
import udemy.clone.model.user.UserDto;
import udemy.clone.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserMapper userMapper;

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found, ID: " + id));
    }

    public User findUserById(String id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserDto findUserDtoById(String id){
        User user = findUserById(id);
        return userMapper.toDto(user);
    }

    public UserDto uploadAvatar(MultipartFile avatar) {
        String id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = findById(id);
        if (user.getFilename() != null) {
            imageService.deleteImage(user.getFilename());
        }
        user.setFilename(imageService.upload(avatar));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto getCurrentUser() {
        String id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return userMapper.toDto(findById(id));
    }
}
