package com.webtoon.domain.User;

import java.util.Objects;

public class UserDto {
    private String id;
    private String pwd;
    private String first_name;
    private String last_name;
    private String email;
    private String reg_no;
    private String phone_no;
    private String state_code;
    private String social_accept_no;
    private String like_upload_alarm;
    private String created_at;
    private String created_id;
    private String updated_at;
    private String updated_id;

    private UserDto() {
    }

    public UserDto(Builder builder) {
        this.id = builder.id;
        this.pwd = builder.pwd;
        this.first_name = builder.first_name;
        this.last_name = builder.last_name;
        this.email = builder.email;
        this.reg_no = builder.reg_no;
        this.phone_no = builder.phone_no;
        this.state_code = builder.state_code;
        this.social_accept_no = builder.social_accept_no;
        this.like_upload_alarm = builder.like_upload_alarm;
        this.created_at = builder.created_at;
        this.created_id = builder.created_id;
        this.updated_at = builder.updated_at;
        this.updated_id = builder.updated_id;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id='" + id + '\'' +
                ", pwd='" + pwd + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", reg_no='" + reg_no + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", state_code='" + state_code + '\'' +
                ", social_accept_no='" + social_accept_no + '\'' +
                ", like_upload_alarm='" + like_upload_alarm + '\'' +
                ", created_at='" + created_at + '\'' +
                ", created_id='" + created_id + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", updated_id='" + updated_id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && Objects.equals(pwd, userDto.pwd) && Objects.equals(first_name, userDto.first_name) && Objects.equals(last_name, userDto.last_name) && Objects.equals(email, userDto.email) && Objects.equals(reg_no, userDto.reg_no) && Objects.equals(phone_no, userDto.phone_no) && Objects.equals(state_code, userDto.state_code) && Objects.equals(social_accept_no, userDto.social_accept_no) && Objects.equals(like_upload_alarm, userDto.like_upload_alarm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pwd, first_name, last_name, email, reg_no, phone_no, state_code, social_accept_no, like_upload_alarm);
    }

    public static class Builder {
        private String id;
        private String pwd;
        private String first_name;
        private String last_name;
        private String email;
        private String reg_no;
        private String phone_no;
        private String state_code;
        private String social_accept_no;
        private String like_upload_alarm;
        private String created_at;
        private String created_id;
        private String updated_at;
        private String updated_id;

        public Builder Id(String id) {
            this.id = id;
            return this;
        }

        public Builder Pwd(String pwd) {
            this.pwd = pwd;
            return this;
        }

        public Builder First_name(String first_name) {
            this.first_name = first_name;
            return this;
        }

        public Builder Last_name(String last_name) {
            this.last_name = last_name;
            return this;
        }

        public Builder Email(String email) {
            this.email = email;
            return this;
        }

        public Builder Reg_no(String reg_no) {
            this.reg_no = reg_no;
            return this;
        }

        public Builder Phone_no(String phone_no) {
            this.phone_no = phone_no;
            return this;
        }

        public Builder State_code(String state_code) {
            this.state_code = state_code;
            return this;
        }

        public Builder Social_accept_no(String social_accept_no) {
            this.social_accept_no = social_accept_no;
            return this;
        }

        public Builder Like_upload_alarm(String like_upload_alarm) {
            this.like_upload_alarm = like_upload_alarm;
            return this;
        }

        public Builder created_at(String created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder created_id(String created_id) {
            this.created_id = created_id;
            return this;
        }

        public Builder updated_at(String updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public Builder updated_id(String updated_id) {
            this.updated_id = updated_id;
            return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getPwd() {
        return pwd;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getReg_no() {
        return reg_no;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getState_code() {
        return state_code;
    }

    public String getSocial_accept_no() {
        return social_accept_no;
    }

    public String getLike_upload_alarm() {
        return like_upload_alarm;
    }
}
