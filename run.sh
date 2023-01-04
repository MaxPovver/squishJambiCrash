#!/bin/zsh
if [ $# -lt 3 ]
  then
    echo "Not enough arguments supplied. How to use: ./run.sh -j *your jvm* -q *your qt*  -p *your project*\n"
    echo "For example: ./run.sh -j /Users/max/Library/Java/JavaVirtualMachines/azul-11.0.13/Contents/Home -q /Users/max/Qt/6.4.0/macos/lib -p /Users/max/IdeaProjects/crashdemo"
    exit 0
fi
while getopts j:q:p: flag
do
    case "${flag}" in
        j) jvm=${OPTARG};;
        q) qt_path=${OPTARG};;
        p) project_path=${OPTARG};;
    esac
done
"$jvm"/bin/java -Djava.library.path="$qt_path" -Dproject_path="$project_path" -Dio.qt.log-messages=ALL -XstartOnFirstThread -Ddebug=true -Dfile.encoding=UTF-8 -classpath "$project_path"/out/production/crashdemo:"$project_path"/lib/qtjambi-6.4.2.jar:"$project_path"/lib/qtjambi-qml-6.4.2.jar:"$project_path"/lib/qtjambi-svg-6.4.2.jar:"$project_path"/lib/qtjambi-quick-6.4.2.jar:"$project_path"/lib/qtjambi-charts-6.4.2.jar:"$project_path"/lib/qtjambi-network-6.4.2.jar:"$project_path"/lib/qtjambi-uitools-6.4.2.jar:"$project_path"/lib/qtjambi-webview-6.4.2.jar:"$project_path"/lib/qtjambi-multimedia-6.4.2.jar:"$project_path"/lib/qtjambi-svgwidgets-6.4.2.jar:"$project_path"/lib/qtjambi-webchannel-6.4.2.jar:"$project_path"/lib/qtjambi-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-printsupport-6.4.2.jar:"$project_path"/lib/qtjambi-webenginecore-6.4.2.jar:"$project_path"/lib/qtjambi-webenginequick-6.4.2.jar:"$project_path"/lib/qtjambi-qml-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-svg-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-webenginewidgets-6.4.2.jar:"$project_path"/lib/qtjambi-multimediawidgets-6.4.2.jar:"$project_path"/lib/qtjambi-quick-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-charts-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-network-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-uitools-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-webview-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-multimedia-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-svgwidgets-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-webchannel-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-printsupport-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-webenginecore-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-webenginequick-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-webenginewidgets-native-macos-6.4.2.jar:"$project_path"/lib/qtjambi-multimediawidgets-native-macos-6.4.2.jar crash.Main
