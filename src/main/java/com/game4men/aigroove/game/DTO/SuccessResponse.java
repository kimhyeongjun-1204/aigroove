package com.game4men.aigroove.game.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse<T> extends BaseResponse {
    private T data;

    public SuccessResponse(String message, T data) {
        super(true, message);
        this.data = data;
    }
}