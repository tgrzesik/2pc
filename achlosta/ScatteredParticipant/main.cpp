#include <QCoreApplication>
#include <QtDBus>
#include "participant.h"

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);

    if (app.arguments().size() < 2) {
        qFatal("Usage: %s <name>", qPrintable(app.arguments().at(0)));
        return 1;
    }

    if (!QDBusConnection::sessionBus().isConnected()) {
        fprintf(stderr, "Cannot connect to the D-Bus session bus.\n"
            "To start it, run:\n"
            "\teval `dbus-launch --auto-syntax`\n");
        return 1;
    }

    Participant participant;

    QDBusInterface iface("org.achlosta.transaction", "/Coordinator", "", QDBusConnection::sessionBus());
    if (iface.isValid()) {
        QString name = app.arguments().at(1);
        QDBusReply<bool> reply = iface.call("addParticipant", name);
        if (reply) {
            QObject::connect(&iface, SIGNAL(transactionBegun()), &participant, SLOT(beginTransaction()));
            QObject::connect(&iface, SIGNAL(valueChanged(int)), &participant, SLOT(setValue(int)));
            QObject::connect(&iface, SIGNAL(transactionCommited()), &participant, SLOT(commitTransaction()));
            QObject::connect(&iface, SIGNAL(transactionRolledBack()), &participant, SLOT(rollbackTransaction()));
            QObject::connect(&participant, SIGNAL(voted(bool)), &iface, SLOT(participantVoted(bool)));

            qDebug() << "Succesfully added to the coordinator";
        }
    } else {
        qFatal("No running coordinator");
        app.exit(1);
    }

    return app.exec();
}
