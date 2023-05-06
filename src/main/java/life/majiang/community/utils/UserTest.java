package life.majiang.community.utils;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * x
 *
 * @author ZhangJiaHang
 * @since 2023-05-05
 */
@Data
public class UserTest implements TreeEntity<UserTest> {
    private String id;
    private String parentId;
    private String name;
    private Integer age;
    private String phone;
    private String homeaddress;
    private Date time;
    public List<UserTest> children;

}