package com.milktea.backend.service;

import com.milktea.backend.dto.NotificationDTO;
import com.milktea.backend.dto.PushTaskDTO;
import com.milktea.backend.repository.NotificationRepository;
import com.milktea.backend.repository.SysAnnouncementRepository;
import com.milktea.milktea_backend.model.entity.Notification;
import com.milktea.milktea_backend.model.entity.SysAnnouncement;
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
    private final SysAnnouncementRepository sysAnnouncementRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               SysAnnouncementRepository sysAnnouncementRepository) {
        this.notificationRepository = notificationRepository;
        this.sysAnnouncementRepository = sysAnnouncementRepository;
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
        notification.setPushType(taskDTO.getPushType());
        notification.setTriggerType(taskDTO.getTriggerType());
        notification.setTriggerCondition(taskDTO.getTriggerCondition());
        notification.setImageUrl(taskDTO.getImageUrl());
        notification.setLinkUrl(taskDTO.getLinkUrl());
        notification.setScheduledTime(taskDTO.getScheduledTime());
        
        if ("IMMEDIATE".equals(taskDTO.getTriggerType()) || taskDTO.getTriggerType() == null) {
            notification.setSentTime(LocalDateTime.now());
            notification.setStatus("SENT");
        } else {
            notification.setStatus("PENDING");
        }
        
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
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("推送任务不存在"));
        
        Map<String, Object> stats = new HashMap<>();
        // 真实场景下应从埋点系统或日志系统统计，这里根据状态模拟一些合理数据
        if ("SENT".equals(notification.getStatus())) {
            stats.put("sentCount", 1000);
            stats.put("reachCount", 950);
            stats.put("readCount", 450);
            stats.put("clickCount", 120);
        } else {
            stats.put("sentCount", 0);
            stats.put("reachCount", 0);
            stats.put("readCount", 0);
            stats.put("clickCount", 0);
        }
        return stats;
    }

    @Transactional
    public void sendActivityNotice(Map<String, Object> notice) {
        // 11. 把后台增加通知的功能也改成在通知列表里面增加
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setTitle((String) notice.get("title"));
        announcement.setContent((String) notice.get("content"));
        announcement.setImageUrl((String) notice.get("imageUrl"));
        announcement.setLinkUrl((String) notice.get("linkUrl"));
        announcement.setIsActive(true);
        announcement.setStartTime(LocalDateTime.now());
        
        sysAnnouncementRepository.save(announcement);
    }

    /**
     * 获取当前有效的全局公告
     */
    public List<SysAnnouncement> getActiveAnnouncements() {
        return sysAnnouncementRepository.findActiveAnnouncements(LocalDateTime.now());
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getTargetType())
                .createdAt(notification.getSentTime())
                .isRead(false)
                .pushType(notification.getPushType())
                .imageUrl(notification.getImageUrl())
                .linkUrl(notification.getLinkUrl())
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
        dto.setPushType(notification.getPushType());
        dto.setTriggerType(notification.getTriggerType());
        dto.setTriggerCondition(notification.getTriggerCondition());
        dto.setImageUrl(notification.getImageUrl());
        dto.setLinkUrl(notification.getLinkUrl());
        dto.setScheduledTime(notification.getScheduledTime());
        return dto;
    }
}