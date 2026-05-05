package az.idtech.bookmanagement.model.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<String> validationErrors;
}
