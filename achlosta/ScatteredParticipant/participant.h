#ifndef PARTICIPANT_H
#define PARTICIPANT_H

#include <QObject>

class Participant : public QObject {
    Q_OBJECT

signals:
    void voted(bool vote);

public:
    explicit Participant(QObject *parent = 0);

public slots:
    void beginTransaction();
    void setValue(int value);
    void commitTransaction();
    void rollbackTransaction();

private:
    bool m_inTransaction = false;
    int m_currentValue = 0;
    int m_tempValue;

};

#endif // PARTICIPANT_H
