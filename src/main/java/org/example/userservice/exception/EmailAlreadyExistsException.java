package org.example.userservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailAlreadyExistsException extends RuntimeException {
    private final String message;
}
