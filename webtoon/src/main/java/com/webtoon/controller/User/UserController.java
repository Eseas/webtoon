package com.webtoon.controller.User;

import com.webtoon.dto.LoginDto;
import com.webtoon.domain.User.Member;
import com.webtoon.service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @PostMapping("/signin")
    public ResponseEntity<String> login(LoginDto loginDto,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request,
                                        HttpServletResponse response
    ) throws Exception {
        
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PutMapping("/signup")
    public ResponseEntity<String> signup(Member member,
                                         HttpServletResponse response
    ) throws Exception {

    }
}
