package waterhole.commonlibs.observer;

/**
 * 观察者监听
 *
 * @author kzw on 2017/03/20.
 */
public interface ObserverListener<Param> {

    /**
     * 通知所有注册者
     *
     * @param action 注册的action
     * @param params 回传的参数
     */
    void notifyAlls(String action, Param... params);
}
