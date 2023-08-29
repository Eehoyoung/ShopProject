package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    @Query("select u from User u where u.name = :username OR u.loginId = :loginId")
    Optional<User> findByUsernameOrLoginId(@Param("username") String username, @Param("loginId") String loginId);

    Optional<User> findByLoginId(String kakaoId);

    void deleteByLoginId(String loginId);

    @Query("select u.id FROM User u where u.loginId = :loginId")
    Long findUserId(@Param("loginId") String LoginId);

    @Query("SELECT m.visitCount FROM Market m WHERE m.marketId = :marketId")
    int visitCountResultByMarketId(@Param("marketId") Long marketId);

    List<User> findByBirthday(LocalDate now);

    @Query("select u.loginId from User u where u.name = :name and u.phoneNumber like :phoneNum")
    String findByFindLoginId(@Param("name") String name, @Param("phoneNum") String phoneNum);

    @Query("select u.id from User u where u.loginId = :loginId and u.name = :name and u.phoneNumber = :phoneNum")
    Long checkUserInfo(@Param("loginId") String loginId, @Param("name") String name, @Param("phoneNum") String phoneNum);

    @Query("select u from User u where u.userGrade = 'ADMIN'")
    Optional<User> findAdmin();

    @Query("select u.name from User u where u.loginId = :loginId")
    String findUserNameByLoginId(@Param("loginId") String loginId);

    @Query("select u.name from User u where u.id = :userId")
    String findUserNameByUserId(@Param("userId") Long userId);

    Page<User> findAllByOrderByCreatedAt(Pageable pageable);

    @Query("select sum(u.visitCount) from User u")
    int visitCountResult();

    @Query("select u.loginId from User u where u.id = (select cr.sender.id from ChatRoom cr where cr.roomId = :roomId)")
    String findLoginIdByRoom(@Param("roomId") Long roomId);
}
