QT += core dbus

TARGET = ScatteredCoordinator

CONFIG += c++11
CONFIG += console
CONFIG -= app_bundle

TEMPLATE = app

SOURCES += main.cpp \
    coordinator.cpp

HEADERS  += coordinator.h
