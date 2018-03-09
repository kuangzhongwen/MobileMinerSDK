package waterhole.commonlibs;

/**
 * 不需要混淆的类，可以实现此接口，在混淆配置中(proguard-rules.pro)需要加上:
 * <code>
 * -keep public class waterhole.commonlibs.NoProGuard
 * -keep class * implements waterhole.commonlibs.NoProGuard {*;}
 * </code>
 *
 * @author kzw on 2017/07/27.
 */
public interface NoProGuard {
}
