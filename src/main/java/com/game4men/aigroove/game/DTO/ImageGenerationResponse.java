package com.game4men.aigroove.game.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageGenerationResponse {
    private String imageId;
    private String imageUrl;
    private String status;
    
    public ImageGenerationResponse(String imageId, String imageUrl, String status) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.status = status;
    }
}