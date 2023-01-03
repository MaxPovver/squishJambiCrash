package crash;

import io.qt.QtUtilities;
import io.qt.core.*;
import io.qt.qml.QQmlApplicationEngine;
import io.qt.webengine.core.QWebEngineUrlScheme;
import io.qt.webengine.quick.QtWebEngineQuick;
import io.qt.webengine.widgets.QWebEngineView;
import io.qt.widgets.QApplication;
import io.qt.widgets.QWidget;

import java.time.Instant;


public class Main extends QWidget {
    /** Creates the GUI shown inside the frame's content pane. */

    /** Sets the text displayed at the bottom of the frame. */


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI(QQmlApplicationEngine engine) {
        //Create and set up the window.
        QWidget frame = new QWidget();
        System.out.println("Step 8.1");
        frame.setWindowTitle("Test window title");
        System.out.println("Step 8.2");
        QWebEngineView webEngineView = new QWebEngineView(frame);
        System.out.println("Step 8.3");
        webEngineView.setUrl(QUrl.fromEncoded(new QByteArray("https://google.com")));
        System.out.println("Step 8.4");
        webEngineView.setFixedSize(500, 500);
        System.out.println("Step 8.5");
        frame.setFixedSize(500, 500);
        System.out.println("Step 8.6");
        frame.show();
        System.out.println("Step 8.7");
        engine.loadData(new QByteArray("import QtQml\n" +
                "import QtQuick\n" +
                "import QtQuick.Window\n" +
                "import QtWebEngine\n" +
                "Window {\n" +
                " width: 900\n" +
                " visible: true\n" +
                " height: 600\n"+
                " title: \"Qml Window\"\n"+
                " WebEngineView {\n" +
                "        id: webView\n" +
                "        anchors.fill: parent\n" +
                "        url: \"https://facebook.com\" \n" +
                "}\n" +
                " }"));
    }

    public static void main(String[] args) {
        System.out.println("Step " + 1);
        QWebEngineUrlScheme urlScheme = new QWebEngineUrlScheme(new QByteArray("embedded"));
        urlScheme.setSyntax(QWebEngineUrlScheme.Syntax.HostAndPort);
        urlScheme.setDefaultPort(80);
        urlScheme.setFlags(QWebEngineUrlScheme.Flag.LocalScheme);
        System.out.println("Step " + 2);
        QWebEngineUrlScheme.registerScheme(urlScheme);
        System.out.println("Step " + 3);
        QtWebEngineQuick.initialize();
        System.out.println("Step " + 4);


        QtUtilities.initializePackage("io.qt.quick");
        System.out.println("Step " + 5);

        //
        QtUtilities.loadQtLibrary("QuickControls2");
        System.out.println("Step " + 6);



        QApplication.initialize(args);


        System.out.println("Step 6.5");
        var app = QApplication.instance();
        System.out.println("Step 6.5.2"); // without installBuiltinHook it doesn't even get to here, crashes on previous line
        UiThreadPing.start(app);
        System.out.println("Step 6.6");
        var s_bgQueueThread = new QThread("InvokeLaterBgSupport");
        System.out.println("Step 6.6.1");
        QApplication.instance().aboutToQuit.connect(s_bgQueueThread::quit); // crashes here with squish
        System.out.println("Step 6.6.2");
        s_bgQueueThread.start();
        System.out.println("Step 6.6.3");
        //var thread = new QThread("InvokeLaterBgSupport");
        //System.out.println(thread.getName());

        System.out.println("Step " + 7);

        System.out.println("Step " + 8);
        QQmlApplicationEngine engine = new QQmlApplicationEngine();
        createAndShowGUI(engine);
        System.out.println("Step " + 9);
        if (Squish.squishPrefixSet()) {
            if (Squish.installBuiltinHook()) {
                System.out.println("squish hook loaded ok");
            } else {
                System.err.println("Failed to attach squish hook");
            }
        } else {
            System.out.println("crash.Squish prefix not set, skipping hook");
        }
        QtUtilities.reinstallEventNotifyCallback();
        QApplication.exec();
        QApplication.shutdown();
    }


    public static class UiThreadPing extends QObject {
        //private final static Logger LOG = LogManager.getLogger();
        private final QThread m_uiPingThread;
        private final QTimer m_pingTimer;

        public final Signal1<Long> eventDelay = new Signal1<>(); // this signal will emit even processing delays

        private static UiThreadPing s_instance;

        // call this once when QApplications instance is already created
        public static void start(QObject parent) {
            System.out.println("Step 6.5.2.5");
            if (s_instance != null || parent == null) {
                //LOG.error("Failed to create new UiThreadPing instance");
                return;
            }
            System.out.println("Step 6.5.3");
            s_instance = new UiThreadPing(parent);
        }

        // you can check this signal in some health monitoring system
        public static Signal1<Long> delaySignal() { return s_instance.eventDelay; }

        private final long MAX_NORMAL_DELAY = 200; // up to 200 ms is a normal delay
        private final long NORMAL_DELAY_MESSAGES_DELAY = 1000 * 60; // post message for normal delay only once a minute
        private long m_lastNormalDelayMessage = 0;

        private UiThreadPing(QObject parent) {
            super(parent);
            System.out.println("Step 6.5.4");
            m_uiPingThread = new QThread("UiPingThread", this);
            m_pingTimer = new QTimer();
            System.out.println("Step 6.5.5");
            m_pingTimer.setInterval(1000*60);
            m_pingTimer.moveToThread(m_uiPingThread);
            m_pingTimer.timeout.connect(() -> {
                long sendingTime = Instant.now().toEpochMilli();

                System.out.println("Step 6.5.11");
                //SUtils.invokeLater(() -> {
                //S.invokeLater emits a new event for the main thread that has this lambda in it

                //    System.out.println("Step 6.5.12");
                //    eventDelay.emit(Instant.now().toEpochMilli() - sendingTime);
                //});
            });
            m_uiPingThread.started.connect(m_pingTimer::start);
            m_uiPingThread.finished.connect(m_pingTimer::stop);
            System.out.println("Step 6.5.6");
            QApplication.instance().aboutToQuit.connect(m_uiPingThread::quit);
            System.out.println("Step 6.5.7");
            m_uiPingThread.start(QThread.Priority.LowPriority);
            System.out.println("Step 6.5.8");
            eventDelay.connect(this::onUiEventDelay);  // crashes on this step if squish is attached
            System.out.println("Step 6.5.9");
        }

        private void onUiEventDelay(long delay) {

            System.out.println("Step 6.5.10");
            // report every too big delay
            if (delay > MAX_NORMAL_DELAY) {
                System.err.println("UiThreadPing: Too big event processing delay! Took " + delay + " ms to process an event.");
            } else {
                long now = Instant.now().toEpochMilli();
                if (now - m_lastNormalDelayMessage > NORMAL_DELAY_MESSAGES_DELAY) {
                    m_lastNormalDelayMessage = now;
                    System.out.println("UiThreadPing: Event processing took " + delay + " ms");
                }
            }
        }
    }
}