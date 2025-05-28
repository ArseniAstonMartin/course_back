package udemy.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.model.user.UserDto;
import udemy.clone.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PutMapping("/avatar")
    public UserDto uploadAvatar(@RequestPart(name = "image") MultipartFile avatar) {
        return userService.uploadAvatar(avatar);
    }

    @GetMapping
    public UserDto getCurrentUser() {
        return userService.getCurrentUser();
    }
}
