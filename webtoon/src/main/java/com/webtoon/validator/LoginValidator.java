package com.webtoon.validator;

import com.webtoon.dto.Login.LoginFormDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LoginValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginFormDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginFormDto loginDto = (LoginFormDto) target;

        if(loginDto.getId() == null || loginDto.getId().isEmpty()) {
            errors.rejectValue("id", "id.required");
        }

        if(loginDto.getPwd() == null || loginDto.getPwd().isEmpty()) {
            errors.rejectValue("pwd", "pwd.required");
        }
    }
}
