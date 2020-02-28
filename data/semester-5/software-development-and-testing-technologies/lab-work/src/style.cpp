#include "style.h"

QString Style::getWindowStyleSheet()
{
    return "QWidget { "
           "background-color: #454545; "
           "border: 1px solid #454545; "
           "}";
}

QString Style::getLabelStyleSheet()
{
    return "QLabel { "
           "color: #8f8f8f; "
           "border: none; "
           "margin: 6px; "
           "}";
}

QString Style::getCloseStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/close-red.png);"
           "background-color: #292929; "
           "icon-size: 12px;"
           "padding-left: 12px;"
           "padding-right: 12px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover {"
           "image: url(:/buttons/close.png); "
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/close.png);"
           "background-color: #3575ff; "
           "}";
}

QString Style::getMaximizeStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/window-maximize-gray.png);"
           "background-color: #292929;"
           "icon-size: 12px;"
           "padding-left: 12px;"
           "padding-right: 12px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover {"
           "image: url(:/buttons/window-maximize.png); "
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/window-maximize.png);"
           "background-color: #3575ff; "
           "}";
}

QString Style::getRestoreStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/window-restore-gray.png);"
           "background-color: #292929;"
           "icon-size: 12px;"
           "padding-left: 12px;"
           "padding-right: 12px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover {"
           "image: url(:/buttons/window-restore.png); "
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/window-restore.png);"
           "background-color: #3575ff; "
           "}";
}

QString Style::getMinimizeStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/window-minimize-gray.png);"
           "background-color: #292929;"
           "icon-size: 12px;"
           "padding-left: 12px;"
           "padding-right: 12px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/window-minimize.png); "
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/window-minimize.png);"
           "background-color: #3575ff; "
           "}";
}

QString Style::getNextStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/skip-next.png);"
           "icon-size: 18px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/skip-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/skip-next-blue.png)"
           "}";
}

QString Style::getPreviousStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/skip-previous.png);"
           "icon-size: 18px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/skip-previous-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/skip-previous-blue.png)"
           "}";
}

QString Style::getStopStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/stop.png);"
           "icon-size: 18px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/stop-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/stop-blue.png)"
           "}";
}

QString Style::getPlayStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/play.png);"
           "icon-size: 24px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover{ "
           "image: url(:/buttons/play-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/play-blue.png)"
           "}";
}

QString Style::getRandomStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/rand-w.png);"
           "icon-size: 12px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/rand-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/rand-blue.png)"
           "}";
}

QString Style::getSequentialStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/seq.png);"
           "icon-size: 14px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/seq-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/seq-blue.png)"
           "}";
}

QString Style::getPauseStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/pause.png);"
           "icon-size: 18px;"
           "padding: 0px;"
           "margin: 0px;"
           "border: none;"
           "}"
           "QToolButton:hover { "
           "image: url(:/buttons/pause-gray.png)"
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/pause-blue.png)"
           "}";
}

QString Style::getMenuStyleSheet()
{
    return "QToolButton { "
           "color: #8f8f8f;"
           "background-color: #292929;"
           "icon-size: 12px;"
           "padding-left: 10px;"
           "padding-right: 10px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover {"
           "color: white;"
           "}"
           "QToolButton:pressed { "
           "color: white; "
           "background-color: #3575ff; "
           "}";
}

QString Style::getTableViewStyleSheet()
{
    return "QTableView { "
           "background-color: #454545; "
           "color: #adadad; "
           "border: 4px  #454545;"
           "}"
           "QTableView::item:selected {"
           "background-color: #3575ff;"
           "}"
           "QHeaderView::section:horizintal {"
           "background-color: #454545;"
           "border-style: none;"
           "color: black; "
           "border: 1px solid #454545; "
           "padding: 6px; "
           "}";
}

QString Style::getAddStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/plus-green.png);"
           "background-color: #292929; "
           "icon-size: 12px;"
           "padding-left: 12px;"
           "padding-right: 12px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover {"
           "image: url(:/buttons/plus.png); "
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/plus.png);"
           "background-color: #3575ff; "
           "}";
}

QString Style::getRemoveStyleSheet()
{
    return "QToolButton { "
           "image: url(:/buttons/remove-orange.png);"
           "background-color: #292929; "
           "icon-size: 12px;"
           "padding-left: 12px;"
           "padding-right: 12px;"
           "padding-top: 5px;"
           "padding-bottom: 5px;"
           "border: 1px solid #292929; "
           "}"
           "QToolButton:hover {"
           "image: url(:/buttons/remove.png); "
           "}"
           "QToolButton:pressed { "
           "image: url(:/buttons/remove.png);"
           "background-color: #3575ff; "
           "}";
}

QString Style::getSliderStyleSheet()
{    
 return
         "QSlider::groove:horizontal {"
         "border: 1px solid #454545;"
         "background: #454545;"
         "height: 3px;"
         "border-radius: 6px;"
         "}"

         "QSlider::sub-page:horizontal {"
         "background:black;"
         "height: 2px;"
         "border-radius: 6px;"
         "}"

         "QSlider::add-page:horizontal {"
         "background: #fff;"
         "height: 2px;"
         "border-radius: 6px;"
         "}"

         "QSlider::handle:horizontal {"
         "background: qlineargradient(x1:0, y1:0, x2:1, y2:1,"
             "stop:0 #eee, stop:1 #ccc);"
         "width: 10px;"
         "height: 20px;"
         "margin-top: -4px;"
         "margin-bottom: -4px;"
         "border-radius: 4px;"
         "}"

         "QSlider::handle:horizontal:hover {"
         "background: qlineargradient(x1:0, y1:0, x2:1, y2:1,"
             "stop:0 #3580ff, stop:1 #3575ff);"
         "border-radius: 4px;"
         "}"

         "QSlider::handle:horizontal:disabled {"
         "background: #eee;"
         "border-radius: 4px;"
         "}"

         "QSlider::sub-page:horizontal:disabled {"
         "background: #bbb;"
         "}"

         "QSlider::add-page:horizontal:disabled {"
         "background: #eee;"
         "}";
}

QString Style::getVerticalScrollBarStyleSheet()
{
 return
         "QScrollBar::groove:vertical {"
         "border: 0px solid #454545;"
         "background: white;"
         "height: 10px;"
         "border-radius: 0px;"
         "}"

         "QScrollBar::sub-page:vertical {"
         "background: #454545;"
         "border: 1px solid #454545;"
         "height: 10px;"
         "border-radius: 2px;"
         "}"

         "QScrollBar::add-page:vertical {"
         "background: #454545;"
         "border: 1px solid #454545;"
         "height: 10px;"
         "border-radius: 2px;"
         "}"

         "QScrollBar::handle:vertical {"
         "background: #292929;"
         "border: 0px solid qlineargradient(x1:0, y1:0, x2:1, y2:1,"
         "stop:0 #eee, stop:1 #ccc);"
         "width: 5px;"
         "margin-top: 2px;"
         "margin-bottom: 2px;"
         "border-radius: 2px;"
         "}"

         "QScrollBar::handle:vertical:hover{"
         "background: #3575ff;"
         "border: 0px solid #3575ff;"
         "border-radius: 2px;"
         "}"

        "QScrollBar::add-line:vertical {"
        "    background: qlineargradient(x1:0, y1:0, x2:1, y2:0,"
        "    stop: 0 rgb(32, 47, 130), stop: 0.5 rgb(32, 47, 130),  stop:1 rgb(32, 47, 130));"
        "    height: 0px;"
        "border-radius: 2px;"
        "    subcontrol-position: bottom;"
        "    subcontrol-origin: margin;"
        "}"

        "QScrollBar::sub-line:vertical{"
        "    background: qlineargradient(x1:0, y1:0, x2:1, y2:0,"
        "    stop: 0  rgb(32, 47, 130), stop: 0.5 rgb(32, 47, 130),  stop:1 rgb(32, 47, 130));"
        "    height: 0 px;"
        "border-radius: 2px;"
        "    subcontrol-position: top;"
        "    subcontrol-origin: margin;"
        "}";
}

QString Style::getHorizontalScrollBarStyleSheet()
{
 return
         "QScrollBar::groove:horizontal {"
         "border: 0px solid #292929;"
         "background: white;"
         "height: 10px;"
         "border-radius: 0px;"
         "}"

         "QScrollBar::sub-page:horizontal{"
         "background: #454545;"
         "border: 1px solid #454545;"
         "height: 10px;"
         "border-radius: 2px;"
         "}"

         "QScrollBar::add-page:horizontal {"
         "background: #454545;"
         "border: 1px solid #454545;"
         "height: 10px;"
         "border-radius: 2px;"
         "}"

         "QScrollBar::handle:horizontal {"
         "background: #292929;"
         "border: 0px solid qlineargradient(x1:0, y1:0, x2:1, y2:1,"
         "stop:0 #eee, stop:1 #ccc);"
         "width: 5px;"
         "margin-top: 2px;"
         "margin-bottom: 2px;"
         "border-radius: 2px;"
         "}"

         "QScrollBar::handle:horizontal:hover{"
         "background: #3575ff;"
         "border: 0px solid #3575ff;"
         "border-radius: 2px;"
         "}"

        "QScrollBar::add-line:horizontal {"
        "    background: qlineargradient(x1:0, y1:0, x2:1, y2:0,"
        "    stop: 0 rgb(32, 47, 130), stop: 0.5 rgb(32, 47, 130),  stop:1 rgb(32, 47, 130));"
        "    height: 0px;"
        "border-radius: 2px;"
        "    subcontrol-position: bottom;"
        "    subcontrol-origin: margin;"
        "}"

        "QScrollBar::sub-line:horizontal{"
        "    background: qlineargradient(x1:0, y1:0, x2:1, y2:0,"
        "    stop: 0  rgb(32, 47, 130), stop: 0.5 rgb(32, 47, 130),  stop:1 rgb(32, 47, 130));"
        "    height: 0 px;"
        "border-radius: 2px;"
        "    subcontrol-position: top;"
        "    subcontrol-origin: margin;"
        "}";
}
