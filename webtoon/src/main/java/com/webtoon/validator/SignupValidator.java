package com.webtoon.validator;

import com.webtoon.dto.SignUp.SignUpDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignupValidator implements Validator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpDto signUpDto = (SignUpDto) target;

        if(signUpDto.getId() == null || signUpDto.getId().isEmpty()) {
            errors.rejectValue("id", "Id.null", "아이디를 입력해주세요.");
        }
        else if(signUpDto.getId().split("@").length < 2) {
            errors.rejectValue("id", "Id.invalidChar", "Id의 형식을 제대로 입력해주세요. ex) Id@Domain.com");
        }
        else if(signUpDto.getId().split("@")[0].length() > 12 || signUpDto.getId().split("@")[0].length() < 6) {
            errors.rejectValue("id", "Id.invalidLength", "Id의 길이를 다시 확인해주세요.");
        }
        if(signUpDto.getPwd() == null || signUpDto.getPwd().isEmpty()) {
            errors.rejectValue("Pwd", "password.null", "비밀번호를 입력해주세요.");
        }
        else if(signUpDto.getPwd().length() > 20 || signUpDto.getPwd().length() < 6) {
            errors.rejectValue("Pwd", "password.invalidLength","비밀번호의 길이를 다시 확인해주세요");
        }
        else if (!signUpDto.getPwd().matches(PASSWORD_PATTERN)) {
            errors.rejectValue("Pwd", "password.invalid", "비밀번호는 대문자, 소문자, 특수문자를 각각 하나 이상 포함해야 하며 숫자와 영어 외 다른 문자는 허용되지 않습니다.");
        }
        else if (!signUpDto.getPwd().matches("^[a-zA-Z0-9@#$%^&+=!]*$")) {
            errors.rejectValue("Pwd", "password.invalidChars", "비밀번호는 오직 영어 알파벳과 숫자, 특수 문자만 포함할 수 있습니다.");
        }
        if(signUpDto.getName().isEmpty()) {
            errors.rejectValue("name", "name.null", "이름을 입력해주세요.");
        }
        if(signUpDto.getBirth().isEmpty()) {
            errors.rejectValue("birth", "birth.null", "생년월일을 입력해주세요.");
        }
        else if(signUpDto.getBirth().replace("-", "").length() != 8) {
            errors.rejectValue("birth", "birth.invalidLength", "생년월일의 길이를 다시 확인해주세요.");
        }
        else if(checkBirth(signUpDto.getBirth().replace("-", ""))) {
            errors.rejectValue("birth", "birth.invalid", "생년월일을 다시 확인해주세요.");
        }
    }

    private boolean checkBirth(String Birth) {
        int year = Integer.parseInt(Birth.substring(1, 5));
        int month = Integer.parseInt(Birth.substring(5, 7));
        int day = Integer.parseInt(Birth.substring(7, 9));

        if(month < 1 || month > 12) {
            return true;
        }
        if(day < 1 || day > 31) {
            return true;
        }
        if(((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) && month == 2 && (day > 29)) {
            return true;
        }
        return false;
    }
}
