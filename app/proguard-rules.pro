# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

#ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#Gson Hawk
-keep class com.google.gson.** { *; }
-keepattributes Signature
-dontwarn rx.**

#lib
-libraryjars libs/jxl-2.6.jar.jar

#Android 已经在proguard-android-optimize.txt 文件中,默认存在了
#-optimizationpasses 5
#-allowaccessmodification
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-dontpreverify
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose
#
#-keepattributes *Annotation*
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#-keepclassmembers public class * extends android.view.View {
#   void set*(***);
#   *** get*();
#}
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#-keepclassmembers class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator CREATOR;
#}
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-dontwarn android.support.**

