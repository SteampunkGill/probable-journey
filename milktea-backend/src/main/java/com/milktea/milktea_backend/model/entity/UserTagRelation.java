package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "user_tag_relation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@IdClass(UserTagRelation.UserTagRelationId.class)
public class UserTagRelation {
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private UserTag tag;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTagRelationId implements Serializable {
        private Long user;
        private Long tag;
    }
}