
package org.thingsboard.server.exception;

import io.swagger.annotations.ApiModel;
import org.springframework.http.HttpStatus;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;

@ApiModel
public class ThingsboardCredentialsViolationResponse extends ThingsboardErrorResponse {

    protected ThingsboardCredentialsViolationResponse(String message) {
        super(message, ThingsboardErrorCode.PASSWORD_VIOLATION, HttpStatus.UNAUTHORIZED);
    }

    public static ThingsboardCredentialsViolationResponse of(final String message) {
        return new ThingsboardCredentialsViolationResponse(message);
    }

}
