package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.TriggerRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import com.eden.eden_crm_sec_crm_back.service.TriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/triggers")
public class TriggerController {
    private final TriggerService triggerService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TriggerResponse>> getAllTriggers() {
        return ResponseEntity.ok(triggerService.getAllTriggers());
    }

    @GetMapping(value = "/{triggerId}", produces = "application/json")
    public ResponseEntity<TriggerResponse> getById(final @PathVariable("triggerId") Long triggerId) {
        return ResponseEntity.ok(triggerService.getTriggerById(triggerId));
    }

    @PostMapping(consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<TriggerResponse> addNewTrigger(final @RequestBody TriggerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(triggerService.addNewTrigger(request));
    }

    @PutMapping(value = "/{triggerId}",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<TriggerResponse> updateTrigger(final @PathVariable("triggerId") Long triggerId,
                                                         final @RequestBody TriggerRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(triggerService.updateTrigger(triggerId, request));
    }

    @DeleteMapping("/{triggerId}")
    public ResponseEntity<Void> deleteTrigger(final @PathVariable("triggerId") Long triggerId) {
        triggerService.deleteTrigger(triggerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
