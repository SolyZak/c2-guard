package com.eden.eden_crm_sec_crm_back.locks.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * One row per patrol being actively edited. Keyed by {@code patrol_id} so
 * different patrols can be edited in parallel by different users. Guards both
 * US1 (edit patrol definition) and US2 (edit that patrol's per-service
 * distributions). Hard TTL with no extension; "iterations" are cosmetic FE
 * banners.
 */
@Entity
@Table(name = "patrol_edit_lock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEditLock {

    @Id
    @Column(name = "patrol_id")
    private Long patrolId;

    @Column(name = "holder_user_id", nullable = false)
    private Long holderUserId;

    @Column(name = "holder_user_name", nullable = false)
    private String holderUserName;

    @Column(name = "acquired_at", nullable = false)
    private OffsetDateTime acquiredAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
}
