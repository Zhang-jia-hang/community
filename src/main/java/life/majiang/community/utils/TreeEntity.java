package life.majiang.community.utils;

import java.util.List;

/**
 * 树形数据实体接口
 *
 * @author ZhangJiaHang
 * @since 2023-05-05
 */

public interface TreeEntity<E> {
    String getId();

    String getParentId();

    void setChildren(List<E> children);
}

