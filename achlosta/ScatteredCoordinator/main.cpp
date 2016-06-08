#include <QtCore>
#include <QtDBus>
#include "coordinator.h"

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);

    if (!QDBusConnection::sessionBus().isConnected()) {
        fprintf(stderr, "Cannot connect to the D-Bus session bus.\n"
            "To start it, run:\n"
            "\teval `dbus-launch --auto-syntax`\n");
        return 1;
    }

    if (!QDBusConnection::sessionBus().registerService("org.achlosta.transaction")) {
        fprintf(stderr, "%s\n",
            qPrintable(QDBusConnection::sessionBus().lastError().message()));
        exit(1);
    }

    Coordinator coordinator;
    QDBusConnection::sessionBus().registerObject("/Coordinator",
                                                 &coordinator,
                                                 QDBusConnection::ExportAllSlots | QDBusConnection::ExportAllSignals);

    return app.exec();
}
