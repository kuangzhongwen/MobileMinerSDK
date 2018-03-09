package waterhole.commonlibs.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * register a method invoked when permission requests are succeeded.
 *
 * @author kzw on 2016/09/30.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnMPermissionGranted {
    int value();
}
