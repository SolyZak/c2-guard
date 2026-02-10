package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.dto.rbac.PredefinedData.*;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import com.eden.eden_crm_sec_crm_back.repository.PermissionClassRepository;
import com.eden.eden_crm_sec_crm_back.repository.PermissionRepository;
import com.eden.eden_crm_sec_crm_back.repository.PermissionScreenRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PredefinedDataService {

    private final PermissionClassRepository classRepo;
    private final PermissionScreenRepository screenRepo;
    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;

    public List<PredefinedClassDto> predefinedNoChecks() {
        var classes = classRepo.findByDeletedFalseOrderByIdAsc();
        var screens = screenRepo.findByDeletedFalseOrderByIdAsc();
        var permissions = permissionRepo.findByDeletedFalseOrderByKeycloakRoleNameAsc();

        Map<Integer, List<PredefinedPermissionNoCheckDto>> permsByScreenId = new HashMap<>();
        for (var p : permissions) {
            if (p.getPermissionScreen() == null) continue; // no unclassified group
            Integer screenId = p.getPermissionScreen().getId();
            permsByScreenId.computeIfAbsent(screenId, k -> new ArrayList<>())
                    .add(new PredefinedPermissionNoCheckDto(p.getId(), p.getNameEn(), p.getNameAr()));
        }

        Map<Integer, List<PredefinedActivityDto>> activitiesByClassId = new HashMap<>();
        for (var s : screens) {
            Integer classId = s.getPermissionClass().getId();
            var perms = permsByScreenId.getOrDefault(s.getId(), List.of());
            activitiesByClassId.computeIfAbsent(classId, k -> new ArrayList<>())
                    .add(new PredefinedActivityDto(s.getId(), s.getNameEn(), s.getNameAr(), perms));
        }

        List<PredefinedClassDto> result = new ArrayList<>();
        for (var c : classes) {
            result.add(new PredefinedClassDto(
                    c.getId(),
                    c.getNameEn(),
                    c.getNameAr(),
                    activitiesByClassId.getOrDefault(c.getId(), List.of())
            ));
        }

        return result;
    }

    public PredefinedRoleWithChecksDto predefinedWithChecks(Integer roleId) {
        RoleEntity role = roleRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        Set<Long> rolePermIds = new HashSet<>();
        if (role.getPermissions() != null) {
            role.getPermissions().forEach(p -> rolePermIds.add(p.getId()));
        }

        var classes = classRepo.findByDeletedFalseOrderByIdAsc();
        var screens = screenRepo.findByDeletedFalseOrderByIdAsc();
        var permissions = permissionRepo.findByDeletedFalseOrderByKeycloakRoleNameAsc();

        Map<Integer, List<PredefinedPermissionCheckedDto>> permsByScreenId = new HashMap<>();
        for (var p : permissions) {
            if (p.getPermissionScreen() == null) continue;

            Integer screenId = p.getPermissionScreen().getId();
            boolean checked = rolePermIds.contains(p.getId());

            permsByScreenId.computeIfAbsent(screenId, k -> new ArrayList<>())
                    .add(new PredefinedPermissionCheckedDto(
                            p.getId(), p.getNameEn(), p.getNameAr(), checked
                    ));
        }

        Map<Integer, List<PredefinedActivityCheckedDto>> activitiesByClassId = new HashMap<>();
        for (var s : screens) {
            Integer classId = s.getPermissionClass().getId();
            var perms = permsByScreenId.getOrDefault(s.getId(), List.of());

            activitiesByClassId.computeIfAbsent(classId, k -> new ArrayList<>())
                    .add(new PredefinedActivityCheckedDto(
                            s.getId(), s.getNameEn(), s.getNameAr(), perms
                    ));
        }

        List<PredefinedClassCheckedDto> tree = new ArrayList<>();
        for (var c : classes) {
            tree.add(new PredefinedClassCheckedDto(
                    c.getId(),
                    c.getNameEn(),
                    c.getNameAr(),
                    activitiesByClassId.getOrDefault(c.getId(), List.of())
            ));
        }

        return new PredefinedRoleWithChecksDto(
                role.getId(),
                role.getName(),
                role.getDescription(),
                tree
        );
    }
}