package crash;

import io.qt.QtUtilities;
import io.qt.core.QLibrary;

import java.util.function.Function;
import java.util.stream.Collectors;

public class Squish {
    //squish prefix and qt bin/lib dir must be in jna.library.path
    // for example -Djna.library.path="C:\Qt\6.4.1\msvc2019_64\bin;C:\Users\max\crash.Squish for Qt 7.1.0\bin"
    // macos -Djna.library.path="/Users/max/Qt/6.4.0/macos/lib:/Applications/crash.Squish for Qt 7.1.0/lib"

    private static String libUrl(String lib) {
        // squish prefix is crash.Squish location, should be like this: /Applications/crash.Squish for Qt 7.1.0 (absolute path)
        var squishPrefix = prefix().endsWith("/") ? prefix().substring(0, prefix().length() - 2) : prefix();
        boolean isWindows = System.getProperty( "os.name").startsWith("Windows");
        var folder = isWindows ? "/bin/":"/lib/";
        var extension = isWindows ? ".dll" : ".dylib";
        return squishPrefix + folder + lib + extension;
    }

    private static QLibrary loadBuiltinhookLib() {
        System.out.println("Loading lib...");

        QLibrary qtwrapperLib = new QLibrary(libUrl("libsquishqtwrapper"));
        if (!qtwrapperLib.load()) {
            System.err.println("Failed to load lib: " + qtwrapperLib.errorString());
            System.exit(0);
        }
        System.out.println("loaded lib" + qtwrapperLib.fileName());
        System.out.println("load extra lib");
        // load extra libs just in case it's them that break this
        QLibrary libsquishqtquickcommon = new QLibrary( libUrl("libsquishqtquickcommon"));
        if (!libsquishqtquickcommon.load()) {
            System.err.println("Failed to load extra lib" + libsquishqtquickcommon.errorString());
        } else {
            System.out.println("Loaded extra lib " + libsquishqtquickcommon.fileName());
        }
        System.out.println("load extra lib");
        QLibrary libsquishqtwebkitcommon = new QLibrary( libUrl("libsquishqtwebkitcommon"));
        if (!libsquishqtwebkitcommon.load()) {
            System.err.println("Failed to load extra lib " + libsquishqtwebkitcommon.errorString());
        } else {
            System.out.println("Loaded extra lib " + libsquishqtwebkitcommon.fileName());
        }

        return qtwrapperLib;
    }

    private static Function<Short, Boolean> loadBuiltinhookLibAndResolve(){
        // existing functions
        // 00000000001e0b00 T _qtwrapper_cleanup
        //00000000001dfd10 T _qtwrapper_initialize
        //00000000001e0b30 T _qtwrapper_initialize_builtin_hook
        //00000000001e0a80 T _qtwrapper_isguithread
        //00000000001e9c40 T _qtwrapper_notify
        //00000000001e0670 T _qtwrapper_postinitialize
        //00000000001e0a70 T _qtwrapper_preinitialize
        //000000000025c980 T _qtwrapper_removeWrappedQObject
        //0000000000584c40 T _squishqtwrapper_init
        //00000000001dfc40 T _write_event_queue
        var qtwrapperLib = loadBuiltinhookLib();
        var function = qtwrapperLib.resolve("qtwrapper_initialize_builtin_hook" );
        System.out.println("loaded function " + function);
        var function2 = qtwrapperLib.resolve("squishqtwrapper_init" );
        System.out.println("loaded function " + function2);
        //function2.invoke();
        function2 = qtwrapperLib.resolve("squishqtwrapper_init" );
        System.out.println("loaded function " + function2);
        //function2.invoke();
        function2 = qtwrapperLib.resolve("squishqtwrapper_init" );
        System.out.println("loaded function " + function2);
        //function2.invoke();
        return (Short value) -> (Boolean) function.invoke(Boolean.class, new Object[] { (short)0});
    }

    private static String prefix() {
        return System.getenv("SQUISH_PREFIX");
    }

    public static boolean squishPrefixSet() {
        return prefix() != null && !prefix().trim().isEmpty();
    }

    public static boolean installBuiltinHook() {
        var initFunc = loadBuiltinhookLibAndResolve();
        //return initFunc.map(p -> p.invoke(boolean.class, (short) 0)).orElse(false);
        var result = initFunc.apply((short) 0);
        QtUtilities.reinstallEventNotifyCallback();
        return result;
    }

    public static boolean allowAttaching(short port) {
        var initFunc = loadBuiltinhookLibAndResolve();
        //return initFunc.map(p -> p.invoke(boolean.class, port)).orElse(false);
        var result = initFunc.apply(port);
        QtUtilities.reinstallEventNotifyCallback();
        return result;
    }
}


