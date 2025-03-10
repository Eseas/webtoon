//package com.webtoon.sociallogin.User;
//
//import com.webtoon.domain.User.Member;
//import com.webtoon.dto.SignUp.IdCheckDto;
//import com.webtoon.dto.SignUp.SignUpDto;
//import com.webtoon.service.User.UserService;
//import com.webtoon.validator.SignupValidator;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Slf4j
//public class UserRestController {
//    private final UserService userService;
//
//    private final SignupValidator signupValidator;
//
//    private final PasswordEncoder passwordEncoder;
//
//    @InitBinder("idCheckDto")
//    public void disableValidatorForThisMethod(WebDataBinder binder) {
//        binder.setValidator(null);  // 특정 메서드에서 Validator 비활성화
//    }
//
//    @InitBinder("signUpDto")
//    public void initBinder(WebDataBinder binder) {
//        binder.addValidators(signupValidator);
//    }
//
//    @PostMapping("/signup/checkid")
//    public ResponseEntity<Map<String, Boolean>> checkId(@RequestBody IdCheckDto idCheckDto) throws Exception {
//        /**
//         * 1. id를 통해 member 테이블 조회
//         * 2. 같은 id를 가진 row가 존재할 경우, 에러 메시지와 함께 BAD_REQUEST 반환.
//         */
//        Optional<Member> member = userService.getMemberInDB(idCheckDto.getId());
//        Map<String, Boolean> response = new HashMap<>();
//
//        if (member.isPresent()) {
//            response.put("available", false);
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        } else {
//            response.put("available", true);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
//    }
//
//    @PutMapping("/signup/process")
//    public ResponseEntity<Map<String, Boolean>> signup(
//                        @RequestBody @Validated SignUpDto signUpDto,
//                         BindingResult bindingResult,
//                         Model model,
//                         HttpServletResponse response
//    ) throws Exception {
//        Map<String, Boolean> map = new HashMap<>();
//
//        if(bindingResult.hasErrors()) {
//            for(FieldError fieldError : bindingResult.getFieldErrors()) {
//                map.put(fieldError.getField(), false);
//            }
//            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//        }
//
//        userService.saveMember(Member.builder()
//                        .loginId(signUpDto.getId())
//                        .pwd(passwordEncoder.encode(signUpDto.getPwd()))
//                        .name(signUpDto.getName())
//                        .birth(signUpDto.getBirth())
//                        .build());
//
//        return ResponseEntity.ok(map);
//    }
//}
