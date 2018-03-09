package waterhole.commonlibs.observer;


import java.io.ObjectStreamException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 观察者
 * <p>
 * <p>实现就是一个轻量级支持并发的单例类，内部维护一个链表，通过register和unregister往链表中添加和
 * 移除观察对象。注意，因为实现原理是在某个场景下通知所有的被观察者，即遍历链表，执行链表里的回调方法，
 * 这个是串形过程，如果中间某个回调执行时间过长，会影响整个的观察者速度。所以对于响应速度要求过高的场景，
 * 最好不要使用observer。同时observer太过于解耦，在一定程度上对于软件的设计是有破坏性的。</p>
 * <p>
 * <p>那么为什么当初不选择EventBus等做了性能优化的事件总线呢，主要是使用场景较少，而且作为一个sdk，应该
 * 尽量少的依赖第三方库，否则会造成方法数增多，同时还得维护和接入方的相同依赖库的版本统一。</p>
 *
 * @author kzw on 2017/03/20.
 */
public class Observer<Listener extends ObserverListener<Param>, Param> {

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    private Observer() {
    }

    /**
     * <p>
     * 使用延迟加载引入的同步关键字会降低系统性能，而使用内部类来维护单例的实例，
     * 当MtbConfigObserver被加载时，其内部类并不会被初始化，所以可以保证MtbConfigObserver
     * 类被载入JVM时，不会初始化单例类，而只有调用 @see #getInstance()方法时，才会加载
     * MtbConfigObserverHolder，从而初始化instance，而且实例的建立是在类加载时完成，所以
     * 天生对多线程友好。
     * </p>
     */
    private static final class ObserverHolder {
        private static Observer instance = new Observer();
    }

    /**
     * 避免反序列化
     */
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }

    public static Observer getInstance() {
        return ObserverHolder.instance;
    }

    @SuppressWarnings("unchecked")
    public final void fireUpdate(String action, final Param... param) {
        if (mListeners.isEmpty()) {
            return;
        }
        synchronized (this) {
            for (Listener listener : mListeners) {
                if (listener != null) {
                    listener.notifyAlls(action, param);
                }
            }
        }
    }

    public final void register(final Listener listener) {
        if (listener == null) {
            return;
        }
        synchronized (this) {
            mListeners.add(listener);
        }
    }

    public final void unregister(final Listener listener) {
        if (listener == null) {
            return;
        }
        synchronized (this) {
            mListeners.remove(listener);
        }
    }
}
