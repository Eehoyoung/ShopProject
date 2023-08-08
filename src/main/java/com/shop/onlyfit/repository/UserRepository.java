package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.name = :username OR u.loginId = :loginId")
    Optional<User> findbyusernameorloginid(@Param("username") String username, @Param("loginId") String loginId);

    Optional<User> findByLoginId(String kakaoId);

    void deleteByLoginId(String loginId);

}
