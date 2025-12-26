package com.milktea.backend.service;

import com.milktea.backend.dto.NotificationDTO;
import com.milktea.backend.dto.PushTaskDTO;
import com.milktea.backend.repository.NotificationRepository;
import com.milktea.milktea_backend.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * 获取用户通知列表
     */
    public List<NotificationDTO> getUserNotifications(Long userId) {
        // 实际应根据用户ID查询，这里简化处理
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        // 实际应更新用户通知关联表的状态
    }

    @Transactional
    public PushTaskDTO createPushTask(PushTaskDTO taskDTO) {
        Notification notification = new Notification();
        notification.setTitle(taskDTO.getTitle());
        notification.setContent(taskDTO.getContent());
        notification.setTargetType(taskDTO.getTargetType());
        notification.setTargetValue(taskDTO.getTargetValue());
        notification.setSentTime(LocalDateTime.now());
        notification.setStatus("SENT");
        
        notification = notificationRepository.save(notification);
        return convertToPushTaskDTO(notification);
    }

    public Page<PushTaskDTO> getPushTasks(Pageable pageable) {
        Page<Notification> page = notificationRepository.findAll(pageable);
        List<PushTaskDTO> dtos = page.getContent().stream()
                .map(this::convertToPushTaskDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public Map<String, Object> getPushStatistics(Long id) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("sentCount", 1000);
        stats.put("reachCount", 950);
        stats.put("readCount", 450);
        stats.put("clickCount", 120);
        return stats;
    }

    @Transactional
    public void sendActivityNotice(Map<String, Object> notice) {
        Notification notification = new Notification();
        notification.setTitle((String) notice.get("title"));
        notification.setContent((String) notice.get("content"));
        notification.setTargetType("ALL");
        notification.setSentTime(LocalDateTime.now());
        notification.setStatus("SENT");
        notificationRepository.save(notification);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getTargetType())
                .createdAt(notification.getSentTime())
                .isRead(false)
                .build();
    }

    private PushTaskDTO convertToPushTaskDTO(Notification notification) {
        PushTaskDTO dto = new PushTaskDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setTargetType(notification.getTargetType());
        dto.setTargetValue(notification.getTargetValue());
        dto.setSentTime(notification.getSentTime());
        dto.setStatus(notification.getStatus());
        return dto;
    }
}