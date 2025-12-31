package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsernameAndPassword(String username, String password);
    

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"memberLevel"})
    Optional<User> findByUsername(String username);


    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"memberLevel"})
    @org.springframework.data.jpa.repository.Query("select u from User u where u.id = :id")
    Optional<User> findByIdWithLevel(@org.springframework.data.repository.query.Param("id") Long id);
    

    Optional<User> findByEmail(String email);
    

    Optional<User> findByPhone(String phone);
    

    List<User> findByStatus(String status);
    

    Optional<User> findByWechatOpenid(String wechatOpenid);
    

    Optional<User> findByWechatUnionid(String wechatUnionid);
    

    List<User> findByNicknameContaining(String nickname);
    

    boolean existsByUsername(String username);
    

    boolean existsByEmail(String email);
    

    boolean existsByPhone(String phone);
    

    List<User> findByMemberLevelId(Long memberLevelId);
    

    List<User> findByInviterId(Long inviterId);


    long countByRegistrationTimeBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    long countByRegistrationTimeBefore(java.time.LocalDateTime time);


    long countByCreatedAtAfter(java.time.LocalDateTime time);
}
