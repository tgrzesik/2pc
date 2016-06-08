#include "coordinator.h"
#include <QtCore>
#include <QtDBus>

Coordinator::Coordinator(QObject *parent) : QObject(parent)
{

}

bool Coordinator::setValue(int value)
{
    qDebug() << "Starting transaction with new value =" << value;

    m_votes = 0;
    m_falseVote = false;

    emit transactionBegun();
    emit valueChanged(value);

    while (m_votes < m_participants.size()) {
        QCoreApplication::sendPostedEvents();
        QCoreApplication::processEvents();

        QThread::sleep(1);
    }

    if (!m_falseVote) {
        qDebug() << "Commit successful";
        emit transactionCommited();
        return true;
    } else {
        qDebug() << "Somebody voted FALSE, rolling back";
        emit transactionRolledBack();
        return false;
    }
}

bool Coordinator::addParticipant(QString name)
{
    if (!m_participants.contains(name)) {
        m_participants.insert(name);
        qDebug() << "Participant added:" << name;
        return true;
    } else {
        qWarning("Participant %s already exists", qPrintable(name));
        return false;
    }
}

void Coordinator::participantVoted(bool vote)
{
    m_votes += 1;
    if (!vote)
        m_falseVote = true;
}

void Coordinator::removeParticipant(QString name)
{
    m_participants.remove(name);
    qDebug("Participant removed: %s", qPrintable(name));
}
