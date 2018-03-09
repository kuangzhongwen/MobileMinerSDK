package waterhole.commonlibs.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * register some methods handling the user's choice to permanently deny permissions checking never ask again.
 *
 * @author kzw on 2016/09/30.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnMPermissionNeverAskAgain {
    int value();
}
