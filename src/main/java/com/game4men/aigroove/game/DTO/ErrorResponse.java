package com.game4men.aigroove.game.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends BaseResponse {
    private String errorCode;

    public ErrorResponse(String message, String errorCode) {
        super(false, message);
        this.errorCode = errorCode;
    }
}