package com.itc.linkedin.connections_service.controller;

import com.itc.linkedin.connections_service.common.ApiResponse;
import com.itc.linkedin.connections_service.dto.ConnectionResponse;
import com.itc.linkedin.connections_service.dto.ConnectionStatusResponse;
import com.itc.linkedin.connections_service.dto.PageResponse;
import com.itc.linkedin.connections_service.dto.SendConnectionRequest;
import com.itc.linkedin.connections_service.security.CurrentUserProvider;
import com.itc.linkedin.connections_service.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/connections")
@RequiredArgsConstructor
@Tag(
        name = "Connection Management",
        description = "APIs for sending, accepting, and managing LinkedIn-style connection requests"
)
public class ConnectionController {
   @Autowired
   private final ConnectionService connectionService;
   @Autowired
   private final CurrentUserProvider currentUserProvider;
    //   private final CurrentUserProvider currentUserProvider;
    @Operation(
            summary = "Send connection request",
            description = "Allows the authenticated user to send a connection request to another user."
    )
    @PostMapping("/requests")
    public ApiResponse<ConnectionResponse> sendConnectionRequest(
            @Valid @RequestBody SendConnectionRequest request
    ) throws BadRequestException {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        ConnectionResponse response = connectionService.sendConnectionRequest(
                currentUserId,
                request.receiverId()
        );
        return ApiResponse.success("Connection request sent successfully", response);
    }

    @Operation(
            summary = "Accept connection request",
            description = "Allows the authenticated receiver to accept a pending connection request."
    )
    @PatchMapping("/requests/{requestId}/accept")
    public ApiResponse<ConnectionResponse> acceptConnectionRequest(
            @PathVariable UUID requestId
    ) throws BadRequestException {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        ConnectionResponse response = connectionService.acceptConnectionRequest(
                currentUserId,
                requestId
        );

        return ApiResponse.success("Connection request accepted successfully", response);
    }


    @Operation(
            summary = "Reject connection request",
            description = "Allows the authenticated receiver to reject a pending connection request."
    )
    @PatchMapping("/requests/{requestId}/reject")
    public ApiResponse<ConnectionResponse> rejectConnectionRequest(
            @PathVariable UUID requestId
    ) throws BadRequestException {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        ConnectionResponse response = connectionService.rejectConnectionRequest(
                currentUserId,
                requestId
        );

        return ApiResponse.success("Connection request rejected successfully", response);
    }

    @Operation(
            summary = "Cancel connection request",
            description = "Allows the authenticated requester to cancel their own pending connection request."
    )
    @PatchMapping("/requests/{requestId}/cancel")
    public ApiResponse<ConnectionResponse> cancelConnectionRequest(
            @PathVariable UUID requestId
    ) throws BadRequestException {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        ConnectionResponse response = connectionService.cancelConnectionRequest(
                currentUserId,
                requestId
        );

        return ApiResponse.success("Connection request cancelled successfully", response);
    }

    @Operation(
            summary = "Get my connections",
            description = "Returns paginated accepted connections for the authenticated user."
    )
    @GetMapping("/me")
    public ApiResponse<PageResponse<ConnectionResponse>> getMyConnections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        PageResponse<ConnectionResponse> response = connectionService.getMyConnections(
                currentUserId,
                page,
                size
        );

        return ApiResponse.success("Connections fetched successfully", response);
    }

    @Operation(
            summary = "Get received connection requests",
            description = "Returns paginated pending requests received by the authenticated user."
    )
    @GetMapping("/requests/received")
    public ApiResponse<PageResponse<ConnectionResponse>> getReceivedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        PageResponse<ConnectionResponse> response = connectionService.getReceivedRequests(
                currentUserId,
                page,
                size
        );

        return ApiResponse.success("Received requests fetched successfully", response);
    }

    @Operation(
            summary = "Get sent connection requests",
            description = "Returns paginated pending requests sent by the authenticated user."
    )
    @GetMapping("/requests/sent")
    public ApiResponse<PageResponse<ConnectionResponse>> getSentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        PageResponse<ConnectionResponse> response = connectionService.getSentRequests(
                currentUserId,
                page,
                size
        );

        return ApiResponse.success("Sent requests fetched successfully", response);
    }
    @Operation(
            summary = "Check connection status",
            description = "Returns the relationship status between the authenticated user and target user."
    )
    @GetMapping("/status/{targetUserId}")
    public ApiResponse<ConnectionStatusResponse> checkConnectionStatus(
            @PathVariable UUID targetUserId
    ) throws BadRequestException {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        ConnectionStatusResponse response = connectionService.checkConnectionStatus(
                currentUserId,
                targetUserId
        );

        return ApiResponse.success("Connection status fetched successfully", response);
    }


}
