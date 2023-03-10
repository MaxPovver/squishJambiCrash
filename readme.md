# Demo for Squish 7.1 crashing Qt when running  the Java app

## Steps to reproduce the issue

1. Install Qt 6.4.0 
2. Install Squish 7.1 for Mac, using Qt you've installed on the last step
3. make run.sh of this repository executable with `chmod +x run.sh`
4. Start the app using command like this `./run.sh -j /Users/max/Library/Java/JavaVirtualMachines/azul-11.0.13/Contents/Home -q /Users/max/Qt/6.4.0/macos/lib -p /Users/max/IdeaProjects/crashdemo`
5. Enter something in search, verify that you see no crashes, close the app. 
   - Note that Squish hook is not installed because you don't set SQUISH_PREFIX env variable, like squish app does.
6. Create new Squish Test Suite with run.sh AUT
7. Add these arguments as program arguments there: `-j /Users/max/Library/Java/JavaVirtualMachines/azul-11.0.13/Contents/Home -q /Users/max/Qt/6.4.0/macos/lib -p /Users/max/IdeaProjects/crashdemo`
   - Make sure that arguments point to your locations of Zulu JDK11(download from [here](https://www.azul.com/downloads/?version=java-11-lts&os=macos&architecture=x86-64-bit&package=jdk)), Qt 6.4.0 and this project location
8. In Squish /bin run `squishserver --config setUsesBuiltinHook run.sh on`
   - this will allow to attach squish only after squish hook was installed in Main.java
   - Without this squish would just fail to connect
9. Now run AUT `run.sh` with parameters from step 6.
   1. You will observe that Squish loads first, but then crashes on `eventDelay.connect(this::onUiEventDelay)`
      1. this is just a normal signal subscription that works perfectly without squish attached.
   2. If you comment out that line, it will crash on line `QApplication.instance().thread().finished.connect(s_bgQueueThread::quit);`
      2. this is just a normal signal subscription that works perfectly without squish attached.
   3. If you comment out that line, it will crash on line `QApplication.instance().thread().finished.connect(m_uiPingThread::quit)`
   4. If you comment out that line too, it will crash on `QWidget frame = new QWidget()` line, which seems to be the moment Squish app tries to join the hook

In folder `src/crash/crashlogs` you can see what is in logs when some of these crashes happen