package com.itc.linkedin.connections_service.mapper;

import com.itc.linkedin.connections_service.dto.ConnectionResponse;
import com.itc.linkedin.connections_service.entity.Connection;
import org.springframework.stereotype.Component;

@Component
public class ConnectionMapper {

    public ConnectionResponse toResponse(Connection connection) {
        return new ConnectionResponse(
                connection.getId(),
                connection.getRequesterId(),
                connection.getReceiverId(),
                connection.getStatus().name(),
                connection.getRequestedAt(),
                connection.getRespondedAt()
        );
    }
}
