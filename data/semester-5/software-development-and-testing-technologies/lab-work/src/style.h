#ifndef STYLE_H
#define STYLE_H

#include <QString>

class Style
{
public:
    static QString getWindowStyleSheet();
    static QString getLabelStyleSheet();
    static QString getCloseStyleSheet();
    static QString getMaximizeStyleSheet();
    static QString getRestoreStyleSheet();
    static QString getMinimizeStyleSheet();
    static QString getNextStyleSheet();
    static QString getPreviousStyleSheet();
    static QString getStopStyleSheet();
    static QString getPlayStyleSheet();
    static QString getRandomStyleSheet();
    static QString getSequentialStyleSheet();
    static QString getPauseStyleSheet();
    static QString getMenuStyleSheet();
    static QString getTableViewStyleSheet();
    static QString getAddStyleSheet();
    static QString getRemoveStyleSheet();
    static QString getSliderStyleSheet();
    static QString getVerticalScrollBarStyleSheet();
    static QString getHorizontalScrollBarStyleSheet();
};

#endif // STYLE_H
