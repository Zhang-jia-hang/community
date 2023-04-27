package life.majiang.community.dto;

import lombok.Data;

/**
 * 授权获得的token传输对象
 */
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
