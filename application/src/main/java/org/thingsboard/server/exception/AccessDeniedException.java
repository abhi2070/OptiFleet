
package org.thingsboard.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccessDeniedException extends Exception implements ToErrorResponseEntity {

    public AccessDeniedException(String message) {
        super(message);
    }

    @Override
    public ResponseEntity<String> toErrorResponseEntity() {
        return new ResponseEntity<>(getMessage(), HttpStatus.FORBIDDEN);
    }
}
