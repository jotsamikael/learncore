package com.dodibo.learncore.elearningcore.leaderboard;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.leaderboard.dto.CreateLeaderboardEntryRequest;
import com.dodibo.learncore.elearningcore.leaderboard.dto.FindLeaderboardEntryQuery;
import com.dodibo.learncore.elearningcore.leaderboard.dto.LeaderboardEntryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LEADERBOARD_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LEADERBOARD_READ;

@RestController
@RequestMapping("leaderboard-entries")
@RequiredArgsConstructor
@Tag(name = "Leaderboard")
public class LeaderboardEntryController {

    private final LeaderboardEntryService leaderboardEntryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_LEADERBOARD_CREATE + "')")
    public ResponseEntity<LeaderboardEntryResponse> createLeaderboardEntry(
            @RequestBody @Valid CreateLeaderboardEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaderboardEntryService.createLeaderboardEntry(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_LEADERBOARD_READ + "')")
    public ResponseEntity<Page<LeaderboardEntryResponse>> getLeaderboardEntries(
            @ParameterObject FindLeaderboardEntryQuery query) {
        return ResponseEntity.ok(leaderboardEntryService.getLeaderboardEntries(query));
    }
}
