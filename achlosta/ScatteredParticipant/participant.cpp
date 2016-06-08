#include "participant.h"
#include <QtCore>

Participant::Participant(QObject *parent) : QObject(parent)
{

}

void Participant::beginTransaction()
{
    m_inTransaction = true;
    qDebug() << "Transaction started";
}

void Participant::setValue(int value)
{
    bool vote = m_inTransaction;

    if (QCoreApplication::arguments().at(1) == "artur") {
        vote = false;
    }

    if (vote)
        m_tempValue = value;
    emit voted(vote);
}

void Participant::commitTransaction()
{
    if (m_inTransaction) {
        m_currentValue = m_tempValue;
        m_inTransaction = false;
        qDebug() << "Transaction commited";
    }
}

void Participant::rollbackTransaction()
{
    m_inTransaction = false;
    qDebug() << "Transaction rolled back";
}
