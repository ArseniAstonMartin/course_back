package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
}
