package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名和密码查找用户
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 根据状态查找用户
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByStatus(String status);
    
    /**
     * 根据微信OpenID查找用户
     * @param wechatOpenid 微信OpenID
     * @return 用户
     */
    Optional<User> findByWechatOpenid(String wechatOpenid);
    
    /**
     * 根据微信UnionID查找用户
     * @param wechatUnionid 微信UnionID
     * @return 用户
     */
    Optional<User> findByWechatUnionid(String wechatUnionid);
    
    /**
     * 根据昵称查找用户
     * @param nickname 昵称
     * @return 用户列表
     */
    List<User> findByNicknameContaining(String nickname);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 根据会员等级查找用户
     * @param memberLevelId 会员等级ID
     * @return 用户列表
     */
    List<User> findByMemberLevelId(Long memberLevelId);
    
    /**
     * 根据邀请人查找用户
     * @param inviterId 邀请人ID
     * @return 用户列表
     */
    List<User> findByInviterId(Long inviterId);

    /**
     * 统计指定时间范围内注册的用户数
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户数
     */
    long countByRegistrationTimeBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    /**
     * 统计在指定时间之前注册的用户总数
     */
    long countByRegistrationTimeBefore(java.time.LocalDateTime time);
}
