/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package waterhole.miner.core.utils;

import android.os.Build;

/**
 * Class containing some static API utility methods.
 *
 * @author kzw on 2017/01/19.
 */
public final class APIUtils {

    /**
     * Private constructor to prohibit nonsense instance creation.
     */
    private APIUtils() {
    }

    /**
     * If platform is Froyo (level 8) or above.
     *
     * @return If platform SDK is above Froyo
     */
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * If platform is Gingerbread (level 9) or above.
     *
     * @return If platform SDK is above Gingerbread
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * If platform is Honeycomb (level 11) or above.
     *
     * @return If platform SDK is above Honeycomb
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * If platform is Honeycomb MR1 (level 12) or above.
     *
     * @return If platform SDK is above Honeycomb MR1
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * If platform is Ice Cream Sandwich (level 14) or above.
     *
     * @return If platform SDK is above Ice Cream Sandwich
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * If platform is Ice Cream Sandwich MR1 (level 15) or above.
     *
     * @return If platform SDK is above Ice Cream Sandwich MR1
     */
    public static boolean hasICSMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * If platform is JellyBean (level 16) or above.
     *
     * @return If platform SDK is above JellyBean
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * If platform is JellyBean MR1 (level 17) or above.
     *
     * @return If platform SDK is above JellyBean MR1
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * If platform is JellyBean MR2 (level 18) or above.
     *
     * @return If platform SDK is above JellyBean MR2
     */
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * If platform is KitKat (level 19) or above.
     *
     * @return If platform SDK is above KitKat
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * If platform is Lollipop (level 21) or above.
     *
     * @return If platform SDK is above Lollipop
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * If platform is LOLLIPOP_MR1 (level 22) or above.
     *
     * @return If platform SDK is above LOLLIPOP_MR1
     */
    public static boolean hasLOLLIPOP_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * If platform is LOLLIPOP_MR1 (level 23) or above.
     *
     * @return If platform SDK is above M
     */
    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
