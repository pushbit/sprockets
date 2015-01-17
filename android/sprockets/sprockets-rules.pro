# Sprockets ProGuard rules

# butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *; }

# commons-configuration
-dontwarn java.applet.**
-dontwarn java.awt.**
-dontwarn java.beans.**
-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.apache.commons.beanutils.**
-dontwarn org.apache.commons.codec.**
-dontwarn org.apache.commons.configuration.resolver.**
-dontwarn org.apache.commons.digester.**
-dontwarn org.apache.commons.jexl2.**
-dontwarn org.apache.commons.jxpath.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.vfs2.**

# commons-logging
-dontwarn org.apache.avalon.**
-dontwarn org.apache.log.**
-dontwarn org.apache.log4j.**

# guava
-dontwarn javax.annotation.**
-dontwarn sun.misc.**

# icepick
-dontwarn icepick.processor.**
-keep class **$$Icicle { *; }
-keepnames class * { @icepick.Icicle *; }

# okhttp
-dontwarn com.squareup.okhttp.internal.**

# okio
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

# play-services
-keep class * extends java.util.ListResourceBundle { protected Object[][] getContents(); }
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * { @com.google.android.gms.common.annotation.KeepName *; }
-keepnames class * implements android.os.Parcelable { public static final ** CREATOR; }
