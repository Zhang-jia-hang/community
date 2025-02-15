package life.majiang.community.enums;

import java.util.Objects;

/**
 * Created by codedrinker on 2019/5/31.
 */
public enum CommentTypeEnum {
    QUESTION(1),
    COMMENT(2);
    private Integer type;


    public Integer getType() {
        return type;
    }

    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (Objects.equals(commentTypeEnum.getType(), type)) {
                return true;
            }
        }
        return false;
    }
}
