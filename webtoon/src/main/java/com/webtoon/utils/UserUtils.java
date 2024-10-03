package com.webtoon.utils;

import org.springframework.data.jpa.repository.JpaRepository;

public class UserUtils {
    public static <T, ID> void saveIfNullId(ID id, JpaRepository<T, ID> repository, T entity) {
        if (id == null) {
            repository.save(entity);
        }
    }
}
