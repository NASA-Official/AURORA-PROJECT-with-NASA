package com.nassafy.api.dto.req;

import lombok.Builder;
import lombok.Getter;

//31번
@Getter
public class StampDTO {
    public String nation;
    public String attractionName;
    public String description;
    public Boolean certification;
    private String colorAuth;
    private String grayAuth;
    private String stamp;

    @Builder
    public StampDTO(String nation, String attractionName, String description, Boolean certification, String colorAuth, String grayAuth, String stamp) {
        this.nation = nation;
        this.attractionName = attractionName;
        this.description = description;
        this.certification = certification;
        this.colorAuth = colorAuth;
        this.grayAuth = grayAuth;
        this.stamp = stamp;
    }
}
