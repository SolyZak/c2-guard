package com.eden.eden_crm_sec_crm_back.locks;

import com.eden.eden_crm_sec_crm_back.locks.config.CustomerEditLockProperties;
import com.eden.eden_crm_sec_crm_back.locks.dtos.AcquireLockResponse;
import com.eden.eden_crm_sec_crm_back.locks.dtos.LockStatusResponse;
import com.eden.eden_crm_sec_crm_back.locks.entities.CustomerEditLock;
import com.eden.eden_crm_sec_crm_back.locks.exception.LockedByOtherUserException;
import com.eden.eden_crm_sec_crm_back.locks.repositories.CustomerEditLockRepository;
import com.eden.eden_crm_sec_crm_back.locks.services.impl.CustomerEditLockServiceImpl;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerEditLockServiceTest {

    @Mock private CustomerEditLockRepository repository;
    @Mock private Utils utils;
    @InjectMocks private CustomerEditLockServiceImpl service;

    private final Long PATROL_ID = 75L;
    private final Long USER_A_ID = 100L;
    private final Long USER_B_ID = 200L;

    @BeforeEach
    void setUp() {
        // Inject properties manually since @InjectMocks can't do @ConfigurationProperties
        CustomerEditLockProperties props = new CustomerEditLockProperties();
        props.setTtlSeconds(1800);
        props.setIterationSeconds(600);
        service = new CustomerEditLockServiceImpl(repository, props, utils);
    }

    private UserData userA() {
        return UserData.builder().id(USER_A_ID.toString()).name("Ahmed").customerId(1L).type(UserType.CUSTOMER).build();
    }

    private UserData userB() {
        return UserData.builder().id(USER_B_ID.toString()).name("Mona").customerId(1L).type(UserType.CUSTOMER).build();
    }

    @Test
    void acquire_succeeds_when_no_lock() {
        when(utils.getLoggedInUser()).thenReturn(userA());
        when(repository.upsertIfFreeOrSameHolder(eq(PATROL_ID), eq(USER_A_ID), any(), any(), any())).thenReturn(1);

        AcquireLockResponse resp = service.acquire(PATROL_ID);

        assertThat(resp.getTtlSeconds()).isEqualTo(1800);
        assertThat(resp.getIterationSeconds()).isEqualTo(600);
        assertThat(resp.getTotalIterations()).isEqualTo(3);
        assertThat(resp.getExpiresAt()).isAfter(OffsetDateTime.now());
    }

    @Test
    void acquire_throws_423_when_held_by_other() {
        when(utils.getLoggedInUser()).thenReturn(userA());
        when(repository.upsertIfFreeOrSameHolder(any(), any(), any(), any(), any())).thenReturn(0);
        when(repository.findByPatrolId(PATROL_ID)).thenReturn(Optional.of(
                CustomerEditLock.builder()
                        .patrolId(PATROL_ID)
                        .holderUserId(USER_B_ID)
                        .holderUserName("Mona")
                        .expiresAt(OffsetDateTime.now().plusMinutes(20))
                        .build()));

        assertThatThrownBy(() -> service.acquire(PATROL_ID))
                .isInstanceOf(LockedByOtherUserException.class)
                .satisfies(ex -> {
                    var details = ((LockedByOtherUserException) ex).getDetails();
                    assertThat(details.getHolderName()).isEqualTo("Mona");
                });
    }

    @Test
    void status_returns_held_false_when_no_lock() {
        when(utils.getLoggedInUser()).thenReturn(userA());
        when(repository.findByPatrolId(PATROL_ID)).thenReturn(Optional.empty());

        LockStatusResponse resp = service.status(PATROL_ID);

        assertThat(resp.isHeld()).isFalse();
    }

    @Test
    void status_returns_held_true_with_iteration_info() {
        when(utils.getLoggedInUser()).thenReturn(userA());
        OffsetDateTime acquiredAt = OffsetDateTime.now().minusMinutes(12); // in iteration 2
        when(repository.findByPatrolId(PATROL_ID)).thenReturn(Optional.of(
                CustomerEditLock.builder()
                        .patrolId(PATROL_ID)
                        .holderUserId(USER_A_ID)
                        .holderUserName("Ahmed")
                        .acquiredAt(acquiredAt)
                        .expiresAt(acquiredAt.plusSeconds(1800))
                        .build()));

        LockStatusResponse resp = service.status(PATROL_ID);

        assertThat(resp.isHeld()).isTrue();
        assertThat(resp.getByCaller()).isTrue();
        assertThat(resp.getCurrentIteration()).isEqualTo(2); // 12 min / 10 min = iteration 2
    }

    @Test
    void requireHeldOrAbsent_passes_when_no_lock() {
        lenient().when(utils.getLoggedInUser()).thenReturn(userA());
        when(repository.findByPatrolId(PATROL_ID)).thenReturn(Optional.empty());

        service.requireHeldOrAbsent(PATROL_ID); // should not throw
    }

    @Test
    void requireHeldOrAbsent_throws_when_held_by_other() {
        when(utils.getLoggedInUser()).thenReturn(userA());
        when(repository.findByPatrolId(PATROL_ID)).thenReturn(Optional.of(
                CustomerEditLock.builder()
                        .patrolId(PATROL_ID)
                        .holderUserId(USER_B_ID)
                        .holderUserName("Mona")
                        .expiresAt(OffsetDateTime.now().plusMinutes(20))
                        .build()));

        assertThatThrownBy(() -> service.requireHeldOrAbsent(PATROL_ID))
                .isInstanceOf(LockedByOtherUserException.class);
    }

    @Test
    void requireHeldOrAbsent_passes_when_expired() {
        lenient().when(utils.getLoggedInUser()).thenReturn(userA());
        when(repository.findByPatrolId(PATROL_ID)).thenReturn(Optional.of(
                CustomerEditLock.builder()
                        .patrolId(PATROL_ID)
                        .holderUserId(USER_B_ID)
                        .holderUserName("Mona")
                        .expiresAt(OffsetDateTime.now().minusMinutes(1))
                        .build()));

        service.requireHeldOrAbsent(PATROL_ID); // should not throw — expired
    }
}
