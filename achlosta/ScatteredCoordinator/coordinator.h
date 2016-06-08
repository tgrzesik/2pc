#ifndef COORDINATOR_H
#define COORDINATOR_H

#include <QObject>
#include <QSet>

class Coordinator : public QObject
{
    Q_OBJECT
    Q_CLASSINFO("D-Bus Interface", "org.achlosta.transaction")

signals:
    void transactionBegun();
    void valueChanged(int value);
    void transactionCommited();
    void transactionRolledBack();

public:
    explicit Coordinator(QObject *parent = 0);

public slots:
    bool setValue(int value);
    bool addParticipant(QString name);
    void participantVoted(bool vote);
    void removeParticipant(QString name);

private:
    QSet<QString> m_participants;
    int m_votes = 0;
    bool m_falseVote = false;

};

#endif // COORDINATOR_H
