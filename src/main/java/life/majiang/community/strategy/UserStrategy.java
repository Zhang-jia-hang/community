package life.majiang.community.strategy;

/**
 * 第三方授权登录接口，使用策略模式，可以扩展其他第三方授权登录方式
 */
public interface UserStrategy {

    /**
     * 第三方登录
     * @param code
     * @param state
     * @return
     */
    LoginUserInfo getUser(String code, String state);

    /**
     * 获取可以登录的方式，gitee、github。。。等
     * @return 返回登录方式的名称，例github
     */
    String getSupportedType();
}
