

package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.UserRepository;
import com.milktea.backend.repository.UserAddressRepository;
import com.milktea.backend.util.SecurityUtils;
import com.milktea.milktea_backend.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.milktea.backend.repository.SysUserRepository sysUserRepository;
    private final com.milktea.backend.repository.SysRoleRepository sysRoleRepository;
    private final com.milktea.backend.repository.SysUserRoleRepository sysUserRoleRepository;

    public UserService(UserRepository userRepository,
                       UserAddressRepository userAddressRepository,
                       PasswordEncoder passwordEncoder,
                       com.milktea.backend.repository.SysUserRepository sysUserRepository,
                       com.milktea.backend.repository.SysRoleRepository sysRoleRepository,
                       com.milktea.backend.repository.SysUserRoleRepository sysUserRoleRepository) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.passwordEncoder = passwordEncoder;
        this.sysUserRepository = sysUserRepository;
        this.sysRoleRepository = sysRoleRepository;
        this.sysUserRoleRepository = sysUserRoleRepository;
    }

    /**
     * 用户登录（支持用户名或手机号）
     * @param account 用户名或手机号
     * @param password 密码
     * @return User
     */
    @Transactional
    public Optional<User> login(String account, String password) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new ServiceException("LOGIN_CREDENTIALS_REQUIRED", "用户名/手机号和密码不能为空");
        }
        
        System.out.println("[DEBUG] Login attempt - Account: " + account + ", Raw Password Length: " + password.length());
        
        // 先尝试按用户名查找，找不到再按手机号查找
        Optional<User> userOptional = userRepository.findByUsername(account);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByPhone(account);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = user.getPassword();
            
            System.out.println("[DEBUG] User found: " + user.getUsername());
            System.out.println("[DEBUG] DB Password Hash: " + encodedPassword);
            
            boolean matches = passwordEncoder.matches(password, encodedPassword);
            System.out.println("[DEBUG] Password matches: " + matches);

            if (StringUtils.hasText(encodedPassword) && matches) {
                // 更新最后登录时间
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        } else {
            System.out.println("[DEBUG] User not found for account: " + account);
        }
        return Optional.empty();
    }

    /**
     * 获取当前登录用户信息
     */
    @Transactional
    public User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return userRepository.findByIdWithLevel(((User) principal).getId()).orElse(null);
            }
            if (!"anonymousUser".equals(principal)) {
                String username = authentication.getName();
                return userRepository.findByUsername(username).orElse(null);
            }
        }
        // 开发阶段兜底
        return userRepository.findById(1L).orElseGet(() -> {
            User testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("123456"));
            testUser.setNickname("测试用户");
            testUser.setStatus("ACTIVE");
            testUser.setIsActive(true);
            testUser.setRegistrationTime(LocalDateTime.now());
            return userRepository.save(testUser);
        });
    }

    /**
     * 获取个人信息
     * @param userId 用户ID
     * @return User
     */
    public Optional<User> getUserProfile(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * 更新个人信息
     */
    @Transactional(readOnly = false)
    public User updateProfile(User userDetails) {
        User user = getCurrentUser();
        if (user == null) {
            throw new ServiceException("USER_NOT_FOUND", "用户未登录或不存在");
        }
        
        // 验证输入
        validateUserForUpdate(userDetails);
        
        System.out.println("[DEBUG] Current user before update: " + user);
        boolean changed = false;
        
        if (StringUtils.hasText(userDetails.getNickname()) && !userDetails.getNickname().equals(user.getNickname())) {
            System.out.println("[DEBUG] Updating nickname to: " + userDetails.getNickname());
            user.setNickname(userDetails.getNickname());
            changed = true;
        }
        if (StringUtils.hasText(userDetails.getAvatarUrl()) && !userDetails.getAvatarUrl().equals(user.getAvatarUrl())) {
            System.out.println("[DEBUG] Updating avatar to: " + userDetails.getAvatarUrl());
            user.setAvatarUrl(userDetails.getAvatarUrl());
            changed = true;
        }
        if (userDetails.getBirthday() != null && !userDetails.getBirthday().equals(user.getBirthday())) {
            System.out.println("[DEBUG] Updating birthday to: " + userDetails.getBirthday());
            user.setBirthday(userDetails.getBirthday());
            changed = true;
        }
        if (userDetails.getGender() != null && !userDetails.getGender().equals(user.getGender())) {
            System.out.println("[DEBUG] Updating gender to: " + userDetails.getGender());
            user.setGender(userDetails.getGender());
            changed = true;
        }
        
        if (changed) {
            User savedUser = userRepository.saveAndFlush(user);
            System.out.println("[DEBUG] User saved and flushed: " + savedUser);
            return savedUser;
        }
        return user;
    }

    /**
     * 绑定会员卡
     */
    @Transactional
    public void bindMemberCard(String cardNumber) {
        User user = getCurrentUser();
        user.setMemberCardNo(cardNumber);
        userRepository.save(user);
    }

    /**
     * 解绑会员卡
     */
    @Transactional
    public void unbindMemberCard() {
        User user = getCurrentUser();
        user.setMemberCardNo(null);
        userRepository.save(user);
    }

    /**
     * 申请电子会员卡
     */
    @Transactional
    public void applyMemberCard(Map<String, Object> data) {
        User user = getCurrentUser();
        // 模拟生成卡号：MT + 时间戳 + 随机数
        String newCardNo = "MT" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        user.setMemberCardNo(newCardNo);
        
        // 更新用户信息
        if (data.get("name") != null) {
            user.setNickname((String) data.get("name"));
        }
        if (data.get("birthday") != null) {
            user.setBirthday(java.time.LocalDate.parse((String) data.get("birthday")));
        }
        
        userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = getCurrentUser();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new ServiceException("INVALID_PASSWORD", "旧密码错误");
        }
    }

    /**
     * 注销前验证
     */
    public boolean verifyDeactivation() {
        // 检查是否有未完成订单等
        return true;
    }

    /**
     * 执行注销
     */
    @Transactional
    public void deactivate() {
        User user = getCurrentUser();
        user.setStatus("DEACTIVATED");
        userRepository.save(user);
    }

    /**
     * 用户注册
     * @param request 注册请求DTO
     * @return 注册成功的用户
     * @throws ServiceException 如果用户信息无效或已存在
     */
    @Transactional
    public User register(com.milktea.backend.dto.RegisterRequestDTO request) {
        if (request == null) {
            throw new ServiceException("USER_REQUIRED", "注册信息不能为空");
        }

        // 打印调试信息，确保后端收到了 adminCode
        System.out.println("=== 注册调试信息 ===");
        System.out.println("用户名: " + request.getUsername());
        System.out.println("角色: " + request.getRole());
        System.out.println("收到的密令: [" + request.getAdminCode() + "]");

        // 检查管理员进入后台权限判断的所有逻辑：
        // 1. 角色标识判断 (admin 或 ADMIN)
        // 2. 密令校验 (13603994106)
        // 3. 状态必须设为 ADMIN (对应后台 hasRole('ADMIN') 的判断)
        // 4. 同步创建后台系统账号 (SysUser)
        
        boolean isAdmin = "admin".equalsIgnoreCase(request.getRole());
        if (isAdmin) {
            String providedCode = request.getAdminCode() != null ? request.getAdminCode().trim() : "";
            // 强制匹配密令
            if (!"13603994106".equals(providedCode)) {
                System.out.println("[DEBUG] Admin code mismatch. Expected: 13603994106, Provided: [" + providedCode + "]");
                throw new ServiceException("INVALID_ADMIN_CODE", "管理员注册密令错误，请检查输入");
            }
            
            // 同步创建后台管理员账号 (内部会自动处理角色不存在的情况)
            createSysAdmin(request);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // 此时还是明文，将在 registerUser 中加密
        user.setNickname(request.getUsername());
        
        // 核心逻辑：将管理员权限判断逻辑应用到注册上
        if (isAdmin) {
            user.setStatus("ADMIN"); // 对应 User.java 中的 getAuthorities() 判断
            user.setIsActive(true);  // 对应 User.java 中的 isEnabled() 判断
        }

        return registerUser(user);
    }

    private void createSysAdmin(com.milktea.backend.dto.RegisterRequestDTO request) {
        if (sysUserRepository.existsByUsername(request.getUsername())) {
            throw new ServiceException("USERNAME_EXISTS", "管理员用户名已存在");
        }
        if (StringUtils.hasText(request.getPhone()) && sysUserRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ServiceException("PHONE_EXISTS", "管理员手机号已存在");
        }

        com.milktea.milktea_backend.model.entity.SysUser sysUser = new com.milktea.milktea_backend.model.entity.SysUser();
        sysUser.setUsername(request.getUsername());
        sysUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // 兼容旧数据库字段 password (如果存在)
        sysUser.setPassword(sysUser.getPasswordHash());
        sysUser.setPhone(request.getPhone());
        sysUser.setRealName(request.getUsername());
        sysUser.setStatus("ACTIVE");
        com.milktea.milktea_backend.model.entity.SysUser savedSysUser = sysUserRepository.save(sysUser);

        // 分配管理员角色
        com.milktea.milktea_backend.model.entity.SysRole adminRole = sysRoleRepository.findByCode("ADMIN")
                .orElseGet(() -> {
                    com.milktea.milktea_backend.model.entity.SysRole newRole = new com.milktea.milktea_backend.model.entity.SysRole();
                    newRole.setCode("ADMIN");
                    newRole.setName("系统管理员");
                    return sysRoleRepository.save(newRole);
                });

        com.milktea.milktea_backend.model.entity.SysUserRole userRole = new com.milktea.milktea_backend.model.entity.SysUserRole();
        userRole.setId(new com.milktea.milktea_backend.model.entity.SysUserRoleId(savedSysUser.getId(), adminRole.getId()));
        userRole.setUser(savedSysUser);
        userRole.setRole(adminRole);
        sysUserRoleRepository.save(userRole);
    }

    /**
     * 用户注册核心逻辑
     * 确保密码在此处仅加密一次
     */
    @Transactional
    public User registerUser(User user) {
        // 1. 验证输入（此时密码应为明文以满足长度校验）
        validateUserForRegistration(user);
        
        // 2. 唯一性检查
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ServiceException("USERNAME_EXISTS", "用户名已存在: " + user.getUsername());
        }
        if (user.getPhone() != null && userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new ServiceException("PHONE_EXISTS", "手机号已存在: " + user.getPhone());
        }
        
        // 3. 加密密码
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        
        // 4. 设置初始状态
        user.setRegistrationTime(LocalDateTime.now());
        // 如果是管理员注册，status 已经在 register 方法中设为 ADMIN
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
        user.setIsActive(true);
        user.setLastLoginAt(LocalDateTime.now());
        user.setGrowthValue(0);
        user.setPoints(0);
        user.setBalance(java.math.BigDecimal.ZERO);
        
        return userRepository.save(user);
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param userDetails 用户信息
     * @return 更新后的用户
     * @throws ServiceException 如果用户不存在或信息无效
     */
    @Transactional
    public Optional<User> updateUserProfile(Long userId, User userDetails) {
        // 验证输入
        validateUserForUpdate(userDetails);
        
        return userRepository.findById(userId).map(user -> {
            if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                    throw new ServiceException("EMAIL_EXISTS", "邮箱已存在: " + userDetails.getEmail());
                }
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPhone() != null && !userDetails.getPhone().equals(user.getPhone())) {
                if (userRepository.findByPhone(userDetails.getPhone()).isPresent()) {
                    throw new ServiceException("PHONE_EXISTS", "手机号已存在: " + userDetails.getPhone());
                }
                user.setPhone(userDetails.getPhone());
            }
            if (userDetails.getNickname() != null) {
                user.setNickname(userDetails.getNickname());
            }
            if (userDetails.getAvatarUrl() != null) {
                user.setAvatarUrl(userDetails.getAvatarUrl());
            }
            if (userDetails.getBirthday() != null) {
                user.setBirthday(userDetails.getBirthday());
            }
            return userRepository.save(user);
        });
    }

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     * @throws ServiceException 如果密码不符合要求
     */
    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 验证新密码
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new ServiceException("INVALID_PASSWORD", "新密码长度不能少于6位");
        }
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @throws ServiceException 如果用户不存在
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ServiceException("USER_NOT_FOUND", "用户不存在，ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据状态获取用户列表
     * @param status 状态
     * @return 用户列表
     */
    public List<User> findUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户Optional
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户Optional
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户Optional
     */
    public Optional<User> findUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    @Transactional
    public void updateLastLoginTime(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    public boolean existsByPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态
     * @return 更新后的用户Optional
     * @throws ServiceException 如果状态无效
     */
    @Transactional
    public Optional<User> updateUserStatus(Long userId, String status) {
        if (!isValidUserStatus(status)) {
            throw new ServiceException("INVALID_USER_STATUS", "无效的用户状态: " + status);
        }
        
        return userRepository.findById(userId).map(user -> {
            user.setStatus(status);
            return userRepository.save(user);
        });
    }

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     * @throws ServiceException 如果密码不符合要求
     */
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        // 验证新密码
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new ServiceException("INVALID_PASSWORD", "新密码长度不能少于6位");
        }
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * 根据微信OpenID查找用户
     * @param wechatOpenid 微信OpenID
     * @return 用户Optional
     */
    public Optional<User> findUserByWechatOpenid(String wechatOpenid) {
        return userRepository.findByWechatOpenid(wechatOpenid);
    }

    /**
     * 根据微信UnionID查找用户
     * @param wechatUnionid 微信UnionID
     * @return 用户Optional
     */
    public Optional<User> findUserByWechatUnionid(String wechatUnionid) {
        return userRepository.findByWechatUnionid(wechatUnionid);
    }

    /**
     * 根据昵称搜索用户
     * @param nickname 昵称
     * @return 用户列表
     */
    public List<User> searchUsersByNickname(String nickname) {
        return userRepository.findByNicknameContaining(nickname);
    }

    /**
     * 根据会员等级查找用户
     * @param memberLevelId 会员等级ID
     * @return 用户列表
     */
    public List<User> findUsersByMemberLevel(Long memberLevelId) {
        return userRepository.findByMemberLevelId(memberLevelId);
    }

    /**
     * 根据邀请人查找用户
     * @param inviterId 邀请人ID
     * @return 用户列表
     */
    public List<User> findUsersByInviter(Long inviterId) {
        return userRepository.findByInviterId(inviterId);
    }

    // ========== 私有方法 ==========
    
    /**
     * 验证用户注册信息
     */
    private void validateUserForRegistration(User user) {
        if (user == null) {
            throw new ServiceException("USER_REQUIRED", "用户信息不能为空");
        }
        if (!StringUtils.hasText(user.getUsername())) {
            throw new ServiceException("USERNAME_REQUIRED", "用户名不能为空");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new ServiceException("PASSWORD_REQUIRED", "密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            throw new ServiceException("PASSWORD_TOO_SHORT", "密码长度不能少于6位");
        }
        
        // 验证用户名格式（字母数字下划线）
        if (!user.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new ServiceException("INVALID_USERNAME_FORMAT", "用户名只能包含字母、数字和下划线，长度3-20位");
        }
        
        // 验证邮箱格式（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new ServiceException("INVALID_EMAIL_FORMAT", "邮箱格式不正确");
        }
        
        // 验证手机号格式（如果提供了手机号）
        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new ServiceException("INVALID_PHONE_FORMAT", "手机号格式不正确");
        }
    }
    
    /**
     * 验证用户更新信息
     */
    private void validateUserForUpdate(User user) {
        if (user == null) {
            throw new ServiceException("USER_REQUIRED", "用户信息不能为空");
        }
        
        // 验证邮箱格式（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new ServiceException("INVALID_EMAIL_FORMAT", "邮箱格式不正确");
        }
        
        // 验证手机号格式（如果提供了手机号）
        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new ServiceException("INVALID_PHONE_FORMAT", "手机号格式不正确");
        }
        
        // 验证昵称长度
        if (user.getNickname() != null && user.getNickname().length() > 50) {
            throw new ServiceException("NICKNAME_TOO_LONG", "昵称长度不能超过50个字符");
        }
        
        // 验证性别合法性
        if (user.getGender() != null && (user.getGender() < 0 || user.getGender() > 2)) {
            throw new ServiceException("INVALID_GENDER", "无效的性别值");
        }
    }
    
    /**
     * 验证用户状态是否有效
     */
    private boolean isValidUserStatus(String status) {
        return status != null && 
               (status.equals("ACTIVE") || status.equals("INACTIVE") || 
                status.equals("SUSPENDED") || status.equals("DELETED"));
    }
}