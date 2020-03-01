# Protokoll 22.02.2020: Android Into und Setup

## Allgemeines
Android ist ein Open-Source Betriebssystem für mobile und Touchgeräte und wird
aktiv von Google entwickelt. Der erste Release von Android fand im Jahr 2007
statt und hat aktuell einen Marktanteil von zirka 75%. Der Nachfolger ist iOS
mit knapp 25%.


## Versionierung und Verbreitung
Die Codenamen der einzelnen Android-Versionen werden im Allgemeinen nach
Süßigkeiten benannt. Seit Android-Version 10 (und auch 1.0) werden keine dieser
Codenamen mehr verwendet. Zudem ist die Adaptionsrate der neuen Android-Version
auch nicht sonderlich hoch. Momentan sind viele unterschiedliche
Android-Versionen auf den Geräten installiert und somit sollten die entwickelten
Applikationen ein hohes Spektrum an Android-Versionen unterstützen.

## Architektur
Android nutzt Linux als Kernel und die APIs dazu wurde in C++ entwickelt. Diese
geben den Entwickler/-innen die Möglichkeit, auf low-level Funktionalitäten
zurückzugreifen, z.B. OpenGL.

Es gibt diverse Runtimes die von den Android-Geräten unterstützt werden, unter
anderem ```ART```, ```.dex```, ```.apk```. Deren Aufgabe ist das Ausführen von
Applikationen unter Android und jede Applikation wird innerhalb einer virtuellen
Maschine ausgeführt.

Android liefert auch alle APIs für Java und Kotlin aus um die Entwicklung der
Apps zu vereinfachen. Diese APIs bieten Zugriff auf den Notification Manager
usw. Jede App besteht aus sogenannten Komponenten. Es gibt Activities, die für
das Anzeigen von User Interfaces verwendet werden, Services um Prozesse im
Hintergrund auszuführen, Broadcast Receivers, die systemweite Events verarbeiten
und Content Providers, die Daten mit anderen Apps teilen.

## Entwicklung
Die Entwicklung der Apps kann sehr einfach mit Android-Studio durchgführt
werden. Diese IDE liefert alles mit, was für die Entwicklung erforderlich ist.
Es werden Emulatoren für diverse Geräte und Android-Versionen mitgeliefert und
unter anderem auch ```adb```, welches sich um das Installieren und Debuggen von
Applikationen auf echten Geräten auseinandersetzt.



