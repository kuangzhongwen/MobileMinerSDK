# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Elegreen-Tech PC\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# proguard的作用除了混淆，还有压缩（侦测并移除代码中无用的类，字段，方法和特性），优化（对字节码进行优化，
# 移除无用指令），预检（在java平台上对处理后的代码进行预检），它是一个开源项目
-optimizationpasses 7
-dontshrink
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保持异常，内部类，泛型
-keepattributes Exceptions,InnerClasses,Signature
# 保持注解
-keepattributes *Annotation*
# 保持文件名和行号
-keepattributes SourceFile,LineNumberTable
# 保持javax包
-keep public class javax.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * implements android.view.View.OnTouchListener

-keep public class waterhole.commonlibs.NoProGuard
-keep class * implements waterhole.commonlibs.NoProGuard {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * implements java.io.Serializable{*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class android.support.** { public *; }

-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

# 如果要使用okHttp请求网络，请打开下面的混淆注释
# okhttp3
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-keep interface okhttp3.**{*;}

# okhttputils
-dontwarn waterhole.commonlibs.net.okhttp.**
-keep class waterhole.commonlibs.net.okhttp.**{*;}
-keep interface waterhole.commonlibs.net.okhttp.**{*;}
