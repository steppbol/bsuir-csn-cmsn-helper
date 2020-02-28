#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include <QStandardItemModel>
#include <QMediaPlayer>
#include <QMediaPlaylist>
#include <QMouseEvent>
#include <QFileDialog>
#include <QDir>
#include <QGraphicsDropShadowEffect>
#include <QMediaMetaData>

#include "volumebutton.h"
#include "style.h"

QT_FORWARD_DECLARE_CLASS(QSlider)
QT_FORWARD_DECLARE_CLASS(QWinTaskbarButton)
QT_FORWARD_DECLARE_CLASS(QWinTaskbarProgress)

class VolumeButton;

namespace Ui {
class Widget;
}

class Widget : public QWidget
{
    Q_OBJECT
    // Свойство с точкой предыдущей позиции мыши
    // Относительно данной точки идёт пересчёт позиции окна
    // Или размеров окна. При этом свойство устанавливается при нажатии мыши
    // по окну и в ряде иных случаев
    Q_PROPERTY(QPoint previousPosition READ previousPosition WRITE setPreviousPosition NOTIFY previousPositionChanged)

    // тип клика мыши, при перемещении курсора по этому типу будем определять
    // что именно нужно сделать, перенести окно, или изменить его размер с одной из сторон
    enum MouseType {
        None = 0,
        Top,
        Bottom,
        Left,
        Right,
        Move
    };

private:
    Ui::Widget *ui = nullptr;

    QStandardItemModel *m_playListModel = nullptr;
    QMediaPlayer *m_player = nullptr;
    QMediaPlaylist *m_playlist = nullptr;

    VolumeButton *m_volumeButton = nullptr;

    QWinTaskbarProgress *m_taskbarProgress = nullptr;
    QWinTaskbarButton *m_taskbarButton = nullptr;

    QWinThumbnailToolBar *thumbnailToolBar = nullptr;
    QWinThumbnailToolButton *playToolButton = nullptr;
    QWinThumbnailToolButton *forwardToolButton = nullptr;
    QWinThumbnailToolButton *backwardToolButton = nullptr;

    // Переменная, от которой будем отталкиваться при работе с перемещением и изменением размера окна
    MouseType m_leftMouseButtonPressed;
    QPoint m_previousPosition;

    MouseType checkResizableField(QMouseEvent*);

    void createTaskbar();
    void createThumbnailToolBar();

private slots:
    void on_btn_add_clicked();
    void updatePosition(qint64);
    void updateDuration(qint64);
    void setPosition(int);

    void updateTaskbar();
    void updateThumbnailToolBar();

    void on_btn_del_clicked();
    void on_btn_random_clicked();

protected:
    void mousePressEvent(QMouseEvent*);
    void mouseReleaseEvent(QMouseEvent*);
    void mouseMoveEvent(QMouseEvent*);

public:
    explicit Widget(QWidget *parent = nullptr);
    ~Widget();
    QPoint previousPosition() const;

public slots:
    void setPreviousPosition(QPoint);
    void togglePlayback();
    void seekForward();
    void seekBackward();

signals:
    void previousPositionChanged(QPoint);
};

#endif // WIDGET_H
