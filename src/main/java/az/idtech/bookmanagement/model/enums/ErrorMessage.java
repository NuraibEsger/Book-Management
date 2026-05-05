package az.idtech.bookmanagement.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    AUTHOR_NOT_FOUND("Author not found with id: %d"),
    BOOK_NOT_FOUND("Book not found with isbn: %s"),
    INTERNAL_ERROR("An internal error occurred. Please try again later.");

    private final String message;
}
