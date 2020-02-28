#include "widget.h"
#include "ui_widget.h"

Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget),
    m_leftMouseButtonPressed(None)
{
    ui->setupUi(this);

    /// Настройка UI
    this->setWindowFlags(Qt::FramelessWindowHint);      // Отключаем оформление окна
    this->setAttribute(Qt::WA_TranslucentBackground);   // Делаем фон главного виджета прозрачным
    this->setStyleSheet(Style::getWindowStyleSheet());    // Устанавливаем стиль виджета
    this->setMouseTracking(true);   // Включаем отслеживание курсора без нажатых кнопок

    /// Создаём эффект тени
    QGraphicsDropShadowEffect *shadowEffect = new QGraphicsDropShadowEffect(this);
    shadowEffect->setBlurRadius(9); // Устанавливаем радиус размытия
    shadowEffect->setOffset(0);     // Устанавливаем смещение тени
    ui->widgetInterface->setGraphicsEffect(shadowEffect);   // Устанавливаем эффект тени на окно
    ui->widgetInterface->layout()->setMargin(0);            // Устанавливаем размер полей
    ui->widgetInterface->layout()->setSpacing(0);
    ui->label->setAlignment(Qt::AlignHCenter | Qt::AlignVCenter);

    /// Установка стилей для всех элементов
    ui->currentTrack->setStyleSheet("color:#c1c1c1;");
    ui->btn_close->setStyleSheet(Style::getCloseStyleSheet());
    ui->btn_maximize->setStyleSheet(Style::getMaximizeStyleSheet());
    ui->btn_minimize->setStyleSheet(Style::getMinimizeStyleSheet());
    ui->btn_next->setStyleSheet(Style::getNextStyleSheet());
    ui->btn_previous->setStyleSheet(Style::getPreviousStyleSheet());
    ui->btn_stop->setStyleSheet(Style::getStopStyleSheet());
    ui->btn_play->setStyleSheet(Style::getPlayStyleSheet());
    ui->btn_pause->setStyleSheet(Style::getPauseStyleSheet());
    ui->btn_add->setStyleSheet(Style::getMenuStyleSheet());
    ui->btn_del->setStyleSheet(Style::getRemoveStyleSheet());
    ui->btn_add->setStyleSheet(Style::getAddStyleSheet());
    ui->btn_random->setStyleSheet(Style::getRandomStyleSheet());

    ui->playlistView->setStyleSheet(Style::getTableViewStyleSheet());
    ui->positionSlider->setStyleSheet(Style::getSliderStyleSheet());
    ui->playlistView->verticalScrollBar()->setStyleSheet(Style::getVerticalScrollBarStyleSheet());
    ui->playlistView->horizontalScrollBar()->setStyleSheet(Style::getHorizontalScrollBarStyleSheet());

    ui->btn_add->setToolTip("Add track");
    ui->btn_del->setToolTip("Remove track");
    ui->btn_next->setToolTip("Next track");
    ui->btn_pause->setToolTip("Pause");
    ui->btn_play->setToolTip("Play");
    ui->btn_previous->setToolTip("Previous track");
    ui->btn_stop->setToolTip("Stop");
    ui->btn_random->setToolTip("Type of payback");

    ui->btn_next->setCursor(Qt::PointingHandCursor);
    ui->btn_previous->setCursor(Qt::PointingHandCursor);
    ui->btn_stop->setCursor(Qt::PointingHandCursor);
    ui->btn_play->setCursor(Qt::PointingHandCursor);
    ui->btn_pause->setCursor(Qt::PointingHandCursor);

    m_playListModel = new QStandardItemModel(this);
    ui->playlistView->setModel(m_playListModel);

    ui->horizontalLayout->setSpacing(0);
    ui->horizontalLayout_2->setSpacing(0);
    ui->playlistView->verticalHeader()->setVisible(false);

    ui->playlistView->setSelectionBehavior(QAbstractItemView::SelectRows);
    ui->playlistView->setSelectionMode(QAbstractItemView::SingleSelection);
    ui->playlistView->setEditTriggers(QAbstractItemView::NoEditTriggers);
    ui->playlistView->horizontalHeader()->setStretchLastSection(true);


    m_player = new QMediaPlayer(this);
    m_playlist = new QMediaPlaylist(m_player);
    m_player->setPlaylist(m_playlist);

    m_playlist->setPlaybackMode(QMediaPlaylist::Loop);

    connect(ui->btn_previous, &QToolButton::clicked, m_playlist, &QMediaPlaylist::previous);
    connect(ui->btn_next, &QToolButton::clicked, m_playlist, &QMediaPlaylist::next);
    connect(ui->btn_play, &QToolButton::clicked, m_player, &QMediaPlayer::play);
    connect(ui->btn_pause, &QToolButton::clicked, m_player, &QMediaPlayer::pause);
    connect(ui->btn_stop, &QToolButton::clicked, m_player, &QMediaPlayer::stop);
    connect(ui->btn_del, &QToolButton::clicked, m_player, &QMediaPlayer::stop);

    /// Устанавливаем громкость воспроизведения треков
    m_volumeButton = new VolumeButton(this);
    m_volumeButton->setToolTip(tr("Volume"));
    m_volumeButton->setVolume(m_player->volume());
    m_volumeButton->setStyleSheet("QToolButton::menu-indicator{image:none;}");
    connect(m_volumeButton, &VolumeButton::volumeChanged, m_player, &QMediaPlayer::setVolume);
    ui->horizontalLayout_2->addWidget(m_volumeButton);

    ///Устанавливаем перемотку треков и время вопроизыведения
    connect(m_player, &QMediaPlayer::positionChanged, this, updatePosition);
    connect(m_player, &QMediaPlayer::durationChanged, this, updateDuration);
    connect(ui->positionSlider, &QAbstractSlider::valueChanged, this, &Widget::setPosition);

    /// коннекты для кнопок сворачивания/максимизации/минимизации/закрытия
    connect(ui->btn_minimize, &QToolButton::clicked, this, &QWidget::showMinimized);
    connect(ui->btn_maximize, &QToolButton::clicked, [this](){
        if (this->isMaximized()) {
            ui->btn_maximize->setStyleSheet(Style::getMaximizeStyleSheet());
            this->layout()->setMargin(9);
            this->showNormal();
        } else {
            ui->btn_maximize->setStyleSheet(Style::getRestoreStyleSheet());
            this->layout()->setMargin(0);
            this->showMaximized();
        }
    });    

    connect(ui->btn_close, &QToolButton::clicked, this, &QWidget::close);

    connect(ui->playlistView, &QTableView::doubleClicked, [this](const QModelIndex &index){
        m_playlist->setCurrentIndex(index.row());
    });

    connect(m_playlist, &QMediaPlaylist::currentIndexChanged, [this](int index){
        ui->currentTrack->setText(m_playListModel->data(m_playListModel->index(index, 0)).toString());
    });

    connect(m_playlist, &QMediaPlaylist::currentIndexChanged, [this](int index){
            ui->playlistView->selectRow(index);});

    ui->positionSlider->setVisible(false);
    ui->positionLabel->setVisible(false);
    ui->btn_stop->setVisible(false);
    ui->btn_pause->setVisible(false);
    ui->btn_next->setVisible(false);
    ui->btn_play->setVisible(false);
    ui->btn_previous->setVisible(false);
    ui->btn_random->setVisible(false);
    ui->currentTrack->setText("");

    createTaskbar();
    createThumbnailToolBar();

    setWindowTitle("B-Player");
}

Widget::~Widget()
{
    delete ui;
    delete m_playListModel;
    delete m_playlist;
    delete m_player;
}

void Widget::on_btn_add_clicked()
{
    QStringList files = QFileDialog::getOpenFileNames(this,
                                                      tr("Open files"),
                                                      QString(),
                                                      tr("Audio Files(*.wav *.mp3)"));

    foreach (QString filePath, files) {
        QList<QStandardItem *> items;
        items.append(new QStandardItem(QDir(filePath).dirName()));
        items.append(new QStandardItem(filePath));
        m_playListModel->appendRow(items);
        m_playlist->addMedia(QUrl(filePath));
    };

    ui->positionSlider->setVisible(true);
    ui->positionLabel->setVisible(true);
    ui->btn_stop->setVisible(true);
    ui->btn_pause->setVisible(true);
    ui->btn_next->setVisible(true);
    ui->btn_play->setVisible(true);
    ui->btn_previous->setVisible(true);
    ui->btn_random->setVisible(true);

    m_playListModel->setHorizontalHeaderLabels(QStringList()  << tr("AUDIO TRACK")
                                               << tr("FILE PATH"));
}

void Widget::on_btn_del_clicked()
{
    m_playlist->removeMedia(ui->playlistView->currentIndex().row());
    m_playListModel->removeRow(ui->playlistView->currentIndex().row());
    ui->currentTrack->setText("");
}

void Widget::on_btn_random_clicked()
{
    static int check=4;
    if(check%2==0) {
    ui->btn_random->setStyleSheet(Style::getSequentialStyleSheet());
    m_playlist->setPlaybackMode(QMediaPlaylist::Random);
    } else {
    ui->btn_random->setStyleSheet(Style::getRandomStyleSheet());
    m_playlist->setPlaybackMode(QMediaPlaylist::Sequential);
    }
    check++;
}

QPoint Widget::previousPosition() const
{
    return m_previousPosition;
}

void Widget::setPreviousPosition(QPoint previousPosition)
{
    if (m_previousPosition == previousPosition)
        return;

    m_previousPosition = previousPosition;
    emit previousPositionChanged(previousPosition);
}

void Widget::mousePressEvent(QMouseEvent *event)
{
    if (event->button() == Qt::LeftButton ) {
        m_leftMouseButtonPressed = checkResizableField(event);
        setPreviousPosition(event->pos());
    }
    return QWidget::mousePressEvent(event);
}

void Widget::mouseReleaseEvent(QMouseEvent *event)
{
    if (event->button() == Qt::LeftButton) {
        m_leftMouseButtonPressed = None;
    }
    return QWidget::mouseReleaseEvent(event);
}

void Widget::mouseMoveEvent(QMouseEvent *event)
{
    // При перемещении мыши, проверяем статус нажатия левой кнопки мыши
    switch (m_leftMouseButtonPressed) {
    case Move: {
        // При этом проверяем, не максимизировано ли окно
        if (isMaximized()) {
            // При перемещении из максимизированного состояния
            // Необходимо вернуть окно в нормальное состояние и установить стили кнопки
            // А также путём нехитрых вычислений пересчитать позицию окна,
            // чтобы оно оказалось под курсором
            ui->btn_maximize->setStyleSheet(Style::getMaximizeStyleSheet());
            this->layout()->setMargin(9);
            auto part = event->screenPos().x() / width();
            this->showNormal();
            auto offsetX = width() * part;
            setGeometry(event->screenPos().x() - offsetX, 0, width(), height());
            setPreviousPosition(QPoint(offsetX, event->y()));
        } else {
            // Если окно не максимизировано, то просто перемещаем его относительно
            // последней запомненной позиции, пока не отпустим кнопку мыши
            auto dx = event->x() - m_previousPosition.x();
            auto dy = event->y() - m_previousPosition.y();
            setGeometry(x() + dx, y() + dy, width(), height());
        }
        break;
    }
    case Top: {
        // Для изменения размеров также проверяем на максимизацию
        // поскольку мы же не можем изменить размеры у максимизированного окна
        if (!isMaximized()) {
        auto dy = event->y() - m_previousPosition.y();
        setGeometry(x(), y() + dy, width(), height() - dy);
        }
        break;
    }
    case Bottom: {
        if (!isMaximized()) {
            auto dy = event->y() - m_previousPosition.y();
            setGeometry(x(), y(), width(), height() + dy);
            setPreviousPosition(event->pos());
        }
        break;
    }
    case Left: {
        if (!isMaximized()) {
            auto dx = event->x() - m_previousPosition.x();
            setGeometry(x() + dx, y(), width() - dx, height());
        }
        break;
    }
    case Right: {
        if (!isMaximized()) {
            auto dx = event->x() - m_previousPosition.x();
            setGeometry(x(), y(), width() + dx, height());
            setPreviousPosition(event->pos());
        }
        break;
    }
    default:
        // Если курсор перемещается по окну без зажатой кнопки,
        // то просто отслеживаем в какой области он находится
        // и изменяем его курсор
        checkResizableField(event);
        break;
    }
    return QWidget::mouseMoveEvent(event);
}

Widget::MouseType Widget::checkResizableField(QMouseEvent *event)
{
    QPointF position = event->screenPos();  // Определяем позицию курсора на экране
    qreal x = this->x();                    // координаты окна приложения, ...
    qreal y = this->y();                    // ... то есть координату левого верхнего угла окна
    qreal width = this->width();            // А также ширину ...
    qreal height = this->height();          // ... и высоту окна

    // Определяем области, в которых может находиться курсор мыши
    // По ним будет определён статус клика);
    QRectF rectTop(x + 9, y, width - 18, 7);
    QRectF rectBottom(x + 9, y + height - 7, width - 18, 7);
    QRectF rectLeft(x, y + 9, 7, height - 18);
    QRectF rectRight(x + width - 7, y + 9, 7, height - 18);
    QRectF rectInterface(x + 9, y + 9, width - 18, height - 18);

    // И в зависимости от области, в которой находится курсор
    // устанавливаем внешний вид курсора и возвращаем его статус
    if (rectTop.contains(position)) {
        setCursor(Qt::SizeVerCursor);
        return Top;
    } else if (rectBottom.contains(position)) {
        setCursor(Qt::SizeVerCursor);
        return Bottom;
    } else if (rectLeft.contains(position)) {
        setCursor(Qt::SizeHorCursor);
        return Left;
    } else if (rectRight.contains(position)) {
        setCursor(Qt::SizeHorCursor);
        return Right;
    } else if (rectInterface.contains(position)) {
        setCursor(QCursor());
        return Move;
    } else {
        setCursor(QCursor());
        return None;
    }
}

void Widget::createTaskbar()
{
    m_taskbarButton = new QWinTaskbarButton(this);
    m_taskbarButton->setWindow(windowHandle());

    m_taskbarProgress = m_taskbarButton->progress();
    connect(ui->positionSlider, &QAbstractSlider::valueChanged, m_taskbarProgress, &QWinTaskbarProgress::setValue);
    connect(ui->positionSlider, &QAbstractSlider::rangeChanged, m_taskbarProgress, &QWinTaskbarProgress::setRange);

    connect(m_player, &QMediaPlayer::stateChanged, this, &Widget::updateTaskbar);
}

void Widget::updateTaskbar()
{
    switch (m_player->state()) {
    case QMediaPlayer::PlayingState:
        m_taskbarButton->setOverlayIcon(style()->standardIcon(QStyle::SP_MediaPlay));
        m_taskbarProgress->show();
        m_taskbarProgress->resume();
        break;
    case QMediaPlayer::PausedState:
        m_taskbarButton->setOverlayIcon(style()->standardIcon(QStyle::SP_MediaPause));
        m_taskbarProgress->show();
        m_taskbarProgress->pause();
        break;
    case QMediaPlayer::StoppedState:
        m_taskbarButton->setOverlayIcon(style()->standardIcon(QStyle::SP_MediaStop));
        m_taskbarProgress->hide();
        break;
    }
}

void Widget::createThumbnailToolBar()
{
    thumbnailToolBar = new QWinThumbnailToolBar(this);
    thumbnailToolBar->setWindow(windowHandle());

    playToolButton = new QWinThumbnailToolButton(thumbnailToolBar);
    playToolButton->setEnabled(false);
    playToolButton->setToolTip(tr("Play"));
    playToolButton->setIcon(style()->standardIcon(QStyle::SP_MediaPlay));
    connect(playToolButton, &QWinThumbnailToolButton::clicked, this, &Widget::togglePlayback);

    forwardToolButton = new QWinThumbnailToolButton(thumbnailToolBar);
    forwardToolButton->setEnabled(false);
    forwardToolButton->setToolTip(tr("Fast forward"));
    forwardToolButton->setIcon(style()->standardIcon(QStyle::SP_MediaSeekForward));
    connect(forwardToolButton, &QWinThumbnailToolButton::clicked, this, &Widget::seekForward);

    backwardToolButton = new QWinThumbnailToolButton(thumbnailToolBar);
    backwardToolButton->setEnabled(false);
    backwardToolButton->setToolTip(tr("Rewind"));
    backwardToolButton->setIcon(style()->standardIcon(QStyle::SP_MediaSeekBackward));
    connect(backwardToolButton, &QWinThumbnailToolButton::clicked, this, &Widget::seekBackward);

    thumbnailToolBar->addButton(backwardToolButton);
    thumbnailToolBar->addButton(playToolButton);
    thumbnailToolBar->addButton(forwardToolButton);

    connect(m_player, &QMediaPlayer::positionChanged, this, &Widget::updateThumbnailToolBar);
    connect(m_player, &QMediaPlayer::durationChanged, this, &Widget::updateThumbnailToolBar);
    connect(m_player, &QMediaPlayer::stateChanged, this, &Widget::updateThumbnailToolBar);
}

void Widget::updateThumbnailToolBar()
{
    playToolButton->setEnabled(m_player->duration() > 0);
    backwardToolButton->setEnabled(m_player->position() > 0);
    forwardToolButton->setEnabled(m_player->position() < m_player->duration());

    if (m_player->state() == QMediaPlayer::PlayingState) {
        playToolButton->setToolTip(tr("Pause"));
        playToolButton->setIcon(style()->standardIcon(QStyle::SP_MediaPause));
    } else {
        playToolButton->setToolTip(tr("Play"));
        playToolButton->setIcon(style()->standardIcon(QStyle::SP_MediaPlay));
    }
}

void Widget::togglePlayback()
{
    if (m_player->state() == QMediaPlayer::PlayingState) {
        m_player->pause();
    } else {
        m_player->play();
    }
}

void Widget::seekForward()
{
    ui->positionSlider->triggerAction(QSlider::SliderPageStepAdd);
}

void Widget::seekBackward()
{
    ui->positionSlider->triggerAction(QSlider::SliderPageStepSub);
}

static QString formatTime(qint64 timeMilliSeconds)
{
    qint64 seconds = timeMilliSeconds / 1000;
    const qint64 minutes = seconds / 60;
    seconds -= minutes * 60;
    return QStringLiteral("%1:%2").arg(minutes, 2, 10, QLatin1Char('0')).arg(seconds, 2, 10, QLatin1Char('0'));
}

void Widget::updatePosition(qint64 position)
{
    ui->positionSlider->setValue(position);
    ui->positionLabel->setText(formatTime(position));
}

void Widget::updateDuration(qint64 duration)
{
    ui->positionSlider->setRange(0, duration);
    ui->positionSlider->setEnabled(duration > 0);
    ui->positionSlider->setPageStep(duration / 10);
}

void Widget::setPosition(int position)
{
    if (qAbs(m_player->position() - position) > 99) {
        m_player->setPosition(position);
    }
}
