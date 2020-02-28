# Requirements Document
### Contents
1. [Introduction](#1) <br>
  1.1 [Purpose](#1.1) <br>
  1.2 [Business Requirements](#1.2) <br>
    1.2.1 [Project Boundaries](#1.2.1) <br>
1.3. [Analogues](#1.3) <br>
  2. [User Requirements](#2) <br>
    2.1. [Software Interfaces](#2.1) <br>
    2.2. [User Interfaces](#2.2) <br>
    2.3. [User Characteristics](#2.3) <br>
    2.4. [Assumptions and Dependencies](#2.4) <br>
3. [System Requirements](#3.) <br>
  3.1 [Functional Requirements](#3.1) <br>
  3.2 [Non-Functional Requierements](#3.2) <br>
    3.2.1 [Software Quality Attributes](#3.2.1) <br>
    3.2.2 [External Interfaces](#3.2.2)
### Glossary
* Audio file is a file that you have on your computer and which has not yet been added to the playlist of the audio player, for example, ".mp3", ".wav", etc.
* Audio recording is an audio file that you added to the playlist of the audio player and ready to play.

### 1\. Intoduction <a name="1"></a>
### 1.1\. Purpose <a name="1.1"></a>
There are a lot of music players, but most of them significantly burden the system due to large amount of resources they use, which is probably one of the most essential qualities. 

So, why not create a music player which can be lightly loaded with various skins and add-ins, and which is able to play all the popular digital audio formats? The only one answer to this question is the following project - B-Player.
### 1.2\. Business Requirementse <a name="1.2"></a>
#### 1.2.1\. Project Boundaries <a name="1.2.1"></a>
The application allows you to listen to the audio files that the user added ti the playlist.
### 1.3\. Analogues <a name="1.3"></a>
This project is in some way a simplified version ["AIMP"](http://www.aimp.ru/) and ["JetAudio" company "Cowon"](http://www.jetaudio.com/), that greatly simplifies the use of.
### 2\. User Requirements <a name="2"></a>
#### 2.1\. Software Interfaces <a name="2.1"></a>
The project uses the Qt framework and does not interact with external systems and services.
#### 2.2\. User Interfaces <a name="2.2"></a>
The GUI of the project is presented with the help of mocaps [main window](https://raw.githubusercontent.com/steppbol/B-Player/master/documentation/mockups/MainWindow.png) and [volume control method](https://raw.githubusercontent.com/steppbol/B-Player/master/documentation/mockups/ShowVolume.png).
The main window requires a separate view:

Button | Event
--- | ---
"Volume" | A scroll bar pops up, with which you can adjust the sound
"Play" | Play of the selected
"Stop" | Stop playing audio recordings
"Next" | Play next audio file
"Previos" | Play a previous audio file
"Add" | An explorer is opened, through which you can select audio files for play
"Remove" | Delete selected audio from playlist
"Random" | Changing the order of audio playback

#### 2.3\. User Characteristics <a name="2.3"></a>
Target Audios:
* People of the older age group who used only electromechanical playback devices and are familiar with their interfaces.
* Users who need a compact audio player that occupies a small space on the screen.
#### 2.4\. Assumptions and Dependencies <a name="2.4"></a>
When you run this project on Linux, you need to use files with the extension ".wav", because the files with the extension ".mp3" are not supported by the QMediaPlayer element of the Qt framework.
### 3\. System Requirements <a name="3"></a>
Run the application on the following operating systems:
* Windows
* Linux
#### 3.1\. Functional Requirements <a name="3.1"></a>
The user is given the opportunities provided in the table.

Function | Requirements
--- | ---
Adding audio files to a playlist | The application should allow the user to add selected audio files to the playlist, by clicking on the "Add"
Delete an audio recording from a playlist | The application should provide an opportunity to delete the selected audio from the playlist, when you click on the "Remove"
Change the volume of the audio recording | The application should provide the ability to change the playback volume using the scroll bar, which is called by pressing the "Volume"
Changing the order of audio playback | The application should provide the ability to change the playback order: in arbitrary and sequential order, when you press the "Random"
Ability to rewind audio recordings | The application should provide the ability to rewind the audio recording in seconds using the scroll bar on the main window
Ability to switch audio recording | The application should provide the ability to switch to the next or previous audio in the playlist by pressing the "Play" and "Previos" keys respectively

#### 3.2\. Non-Functional Requierements <a name="3.2"></a>
##### 3.2.1\. Software Quality Attributes <a name="3.2.1"></a>
Important quality attributes for this application are: quick start, low resource consumption and high performance, namely the addition of audio files to the playlist.
Also quality attributes are: Easy to use due to a minimal interface, fast response speed to change the state of the button, that is, the delay between pressing a key and starting the music file, Portability between Windows and Linux.
##### 3.2.2\. External Interfaces <a name="3.2.2"></a>
The application should be designed in the same style and designed support for the visually impaired: large buttons and large print.
