# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\wangping\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

-keep public class waterhole.miner.monero.** extends android.app.Activity
-keep public class waterhole.miner.monero.** extends android.app.Application
-keep public class waterhole.miner.monero.** extends android.app.Service
-keep public class waterhole.miner.monero.** extends android.content.BroadcastReceiver
-keep public class waterhole.miner.monero.** extends android.content.ContentProvider
-keep public class waterhole.miner.monero.** extends android.app.backup.BackupAgentHelper
-keep public class waterhole.miner.monero.** extends android.preference.Preference

-keepattributes SourceFile,LineNumberTable,InnerClasses

-keepclasseswithmembernames class waterhole.miner.monero.** {
    native <methods>;
}

-keepclasseswithmembers class waterhole.miner.monero.** {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class waterhole.miner.monero.** {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class waterhole.miner.monero.** extends android.app.Activity {
    public void *(android.view.View);
}

-keepclassmembers enum waterhole.miner.monero.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class waterhole.miner.monero.** implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembernames class waterhole.miner.monero.** {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclassmembers class waterhole.miner.monero.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class waterhole.miner.monero.** extends waterhole.miner.core.AbstractMiner {
    public protected *;
}

-keep public class waterhole.miner.monero.** implements waterhole.miner.core.MinerCallback {
    public protected *;
}

-keep public class waterhole.miner.monero.** implements waterhole.miner.core.NoProguard {
    public protected *;
}