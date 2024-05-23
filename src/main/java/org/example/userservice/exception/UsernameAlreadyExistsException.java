package org.example.userservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsernameAlreadyExistsException extends RuntimeException {
    private final String message;
}
