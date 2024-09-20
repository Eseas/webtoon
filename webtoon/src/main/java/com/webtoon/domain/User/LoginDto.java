package com.webtoon.domain.User;

public class LoginDto {
    private String id;
    private String pwd;
    private String username;
    private boolean remember_id;

    private LoginDto() {
    }

    public LoginDto(Builder builder) {
        this.id = builder.id;
        this.pwd = builder.pwd;
        this.username = builder.username;
        this.remember_id = builder.remember_id;
    }

    public static class Builder {
        private String id;
        private String pwd;
        private String username;
        private boolean remember_id = false;

        public Builder Id(String id) {
            this.id = id;
            return this;
        }

        public Builder Pwd(String pwd) {
            this.pwd = pwd;
            return this;
        }

        public Builder Username(String username) {
            this.username = username;
            return this;
        }

        public Builder Remember_id(boolean remember_id) {
            this.remember_id = remember_id;
            return this;
        }

        public LoginDto build() {
            return new LoginDto(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUsername() {
        return username;
    }

    public boolean getRemember_id() {
        return remember_id;
    }
}
