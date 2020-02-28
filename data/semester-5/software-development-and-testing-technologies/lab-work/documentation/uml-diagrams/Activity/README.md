# Содержание
1. [Добавление аудиофайла](#1)
2. [Начало воспроизведения](#2)
3. [Изменение порядка воспроизведения](#3)
4. [Воспроизведение следующей аудиозаписи](#4)
5. [Изменение громкости](#5)
6. [Остановка воспроизведения](#6)
7. [Удаление аудиозаписи](#7)

### 1. Добавление аудиофайла<a name="1"></a>
При нажатии кнопки "Add" откроется окно проводника для выбора аудиофайлов для записи в плейлист. Необходимо выбрать аудиофайлы и нажать "OK". При нажатии "Cancel" аудиофайлы не добавятся в плейлист, а окно закроется.

![Добавление аудиофайла](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Add.png)

### 2. Начало воспроизведения<a name="2"></a>
При нажатии кнопки "Play" начнется воспроизведение выбранной аудиозаписи из плейлиста. Приложение отобразит ее название в главном окне.

![Начало воспроизведения](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Play.png)
  
### 3. Изменение порядка воспроизведения<a name="3"></a>
При нажатии кнопки "Random" изменится порядок воспроизведния аудиозаписей из плейлиста.
* Кнопка "Random" нажата в первый раз - переключение порядка воспроизведения на произвольный.
* Кнопка "Random" нажата во второй раз - переключение порядка воспроизведения на последовательный.
При последующих нажатиях будет чередоваться произвольный и последовательный порядки.

![Изменение порядка воспроизведения](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Random.png)

### 4. Воспроизведение следующей аудиозаписи<a name="4"></a>
При нажатии кнопки "Next" начнется воспроизведение следующей аудиозаписи в плейлисти или первой, если плейлист закончился.

![Воспроизведение следующей аудиозаписи](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Next.png)

### 5. Изменение громкости<a name="5"></a>
При нажатии кнопки "Volume" отобразится ползунок для изменения громкости в переделах от 0 до 100. 

![Изменение громкости](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Volume.png)

### 6. Остановка воспроизведения<a name="6"></a>
При нажатии кнопки "Pause" происходит остановка воспроизведения аудиозаписи на текущем прогрессе воспроизведния, чтобы в дальнейшем продолжить.

![Остановка воспроизведения](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Stop.png)

### 7. Удаление аудиозаписи<a name="7"></a>
При нажатии клавиши "Delete" происходит удаление выбранной пользователем аудиозаписи из плейлиста.

![Удаление аудиозаписи](https://raw.githubusercontent.com/steppbol/B-Player/master/docs/Project%20Documentation/UMLDiagrams/Activity/Delete.png)
