# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/sunyuxin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in create.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontwarn
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-ignorewarnings

-keep public class waterhole.miner.core.** extends android.app.Activity
-keep public class waterhole.miner.core.** extends android.app.Application
-keep public class waterhole.miner.core.** extends android.app.Service
-keep public class waterhole.miner.core.** extends android.content.BroadcastReceiver
-keep public class waterhole.miner.core.** extends android.content.ContentProvider
-keep public class waterhole.miner.core.** extends android.app.backup.BackupAgentHelper
-keep public class waterhole.miner.core.** extends android.preference.Preference

-keepattributes SourceFile,LineNumberTable,InnerClasses

-keepclasseswithmembernames class waterhole.miner.core.** {
    native <methods>;
}

-keepclasseswithmembers class waterhole.miner.core.** {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class waterhole.miner.core.** {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class waterhole.miner.core.** extends android.app.Activity {
    public void *(android.view.View);
}

-keepclassmembers enum waterhole.miner.core.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class waterhole.miner.core.** implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembernames class waterhole.miner.core.** {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclassmembers class waterhole.miner.core.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class waterhole.miner.core.AbstractMiner
-keep public interface waterhole.miner.core.NoProguard
-keep public interface waterhole.miner.core.MinerCallback
-keep public class waterhole.miner.core.** implements waterhole.miner.core.NoProGuard
