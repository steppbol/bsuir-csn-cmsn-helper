.model tiny
.code
.386

org 100h

start:
    jmp install_handler

old_handler dd 0
;-------------------------------------------------------------------
checkOnMoves proc                                      ; Check on moves
    pusha
    
    cmp ah, arrowUp
    jne notArrowUp
    call canMoveUp
    mov checkFlag, 1
    jmp checkOnMovesExit

notArrowUp:
    cmp ah, arrowDown
    jne notArrowDown
    call canMoveDown
    mov checkFlag, 1
    jmp checkOnMovesExit

notArrowDown:        
    cmp ah, arrowLeft
    jne notArrowLeft
    call canMoveLeft
    mov checkFlag, 1
    jmp checkOnMovesExit

notArrowLeft:       
    cmp ah, arrowRight
    jne checkOnMovesExit
    call canMoveRight   
    mov checkFlag, 1
    
checkOnMovesExit:
    
    call checkFinishOfGame
    cmp finishGameFlag, 1
    jne exitChk
    call printWinMessage

exitChk:
    
    call printScore
    call HideCursor
    
    popa
    ret    
endp
;///////////////////////////////////PRINT LEVEL DESTINATION///////////////////////////////////
printCurLvlBlocksDst proc
    pusha
    push es
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    mov cx, currentSize
    
    push 0b800h
    pop es
    
fillDst:
    mov di, [bx] 
    
    inc di
    mov byte ptr es:[di], destinationColor

    add bx, 2
    loop fillDst      
    
    pop es
    popa
    
    ret
endp    
;***********************************PRINT LEVEL BLOCKS****************************************
printCurLvlBlocks proc
    pusha
    push es
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    mov cx, currentSize
    
    push 0b800h
    pop es
    
fillBlocks:
    mov di, [bx] 
    mov al, 04h
    
    mov byte ptr es:[di], al            ; Заполянем консоль пробелами и устанавливаем атрибуты
    inc di
    mov byte ptr es:[di], blockColor

    add bx, 2
    loop fillBlocks      
    
    pop es
    popa
    
    ret
endp    
;===================================PRINT LEVEL BOARD==========================================
printCurLvlBoard proc
    pusha
    push es
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    mov cx, currentSize
    
    push 0b800h
    pop es
    
fillBoard:
    mov di, [bx] 
    mov al, 23h
    
    mov byte ptr es:[di], al            ; Заполянем консоль пробелами и устанавливаем атрибуты
    inc di
    mov byte ptr es:[di], boardColor

    add bx, 2
    loop fillBoard      
    
    pop es
    popa
    
    ret
endp 
;=======================================================================================
checkOnMenu proc
    push bx
    push ax
    
    cmp ah, 02h
    jne not1Level 
    call printFirstLvl
    mov finishGameFlag, 0   
    jmp finishCheckOnMenu
    
not1Level:
    cmp ah, 03h
    jne not2Level 
    call printSecondLvl
    mov finishGameFlag, 0   
    jmp finishCheckOnMenu
    
not2Level:
    cmp ah, 04h
    jne not3Level 
    call printThirdLvl
    mov finishGameFlag, 0   
    jmp finishCheckOnMenu
    
not3Level:
    cmp ah, 05h
    jne not4Level 
    call printFourthLvl
    mov finishGameFlag, 0   
    jmp finishCheckOnMenu
    
not4Level:
    cmp ah, 06h
    jne not5Level 
    call printFifthLvl
    mov finishGameFlag, 0   
    jmp finishCheckOnMenu
    
not5Level:
    cmp ah, 07h
    jne finishCheckOnMenu 
    call printSixthLvl
    mov finishGameFlag, 0   
    jmp finishCheckOnMenu
    
finishCheckOnMenu:
    
    cmp ah, 01h
    jne exitMenu
    mov ax, 4CFFh
    int 21h
    
exitMenu:
    
     
    pop ax
    pop bx
    ret
endp


;========================================CHECK FINISH OF GAME==================
checkFinishOfGame proc
    pusha
    push es
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    mov cx, currentSize
    mov bx, currentLevel
    
    push 0b800h
    pop es
    
checkBlock:
    mov di, [bx] 
    
    cmp byte ptr es:[di], 04h            ; Если во всех точка назначения есть ромб
    jne notFinishOfGame

    add bx, 2
    loop checkBlock
          
    mov finishGameFlag, 1
    jmp exitCheckFinish
    
notFinishOfGame:
    mov finishGameFlag, 0
    
exitCheckFinish:
    
    pop es
    popa
    
    ret
endp    

;====================================HANDLER============================
new_handler proc far
    pushf                                ; Сохраняем регистры флагов
    call cs:old_handler                  ; Вызываем старый обработчик
        
    pusha                                ; Сохраняем регистры    
    push ds                              
    push es
    push cs
    pop ds
    
checkTypedButton:    
    xor ah, ah
    int 16h
    
    call checkOnMenu  
    cmp checkFlag, 1
    je finishHandle
    
    cmp finishGameFlag, 0
    jne finishHandle
    call checkOnMoves
    cmp checkFlag, 1 
    je finishHandle 
    
    
finishHandle:
	
    mov checkFlag, 0
    
    pop es
    pop ds
    popa    
    iret    
new_handler endp 
;==================CAN MOVE LEFT==================
canMoveLeft proc
    pusha
    push es   
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    push 0b800h
    pop es
    
    mov di, manPos
    sub di, 2
                                                    ; если перед нами ромб, чекаем можем ли мы толкнуть его в ту сторону
    cmp byte ptr es:[di], 04h
    jne noBlockInLeft
    sub di, 2
    cmp byte ptr es:[di], ' '
    jne exitCanMoveLeft
    mov byte ptr es:[di], 04h
    add di, 2
    mov byte ptr es:[di], 01h
    add di, 2
    mov byte ptr es:[di], ' '
    sub manPos, 2
    inc score
    jmp exitCanMoveLeft 
    
noBlockInLeft:
    
    cmp byte ptr es:[di], '#'
    je exitCanMoveLeft
    mov byte ptr es:[di], 01h
    add di, 2
    mov byte ptr es:[di], ' '
    sub manPos, 2
    
exitCanMoveLeft:
    
    pop es
    popa 
    ret
endp  
;==================CAN MOVE RIGHT==================   
canMoveRight proc
    pusha
    push es   
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    push 0b800h
    pop es
    
    mov di, manPos
    add di, 2
    
    cmp byte ptr es:[di], 04h
    jne noBlockInRight
    add di, 2
    cmp byte ptr es:[di], ' '
    jne exitCanMoveRight
    mov byte ptr es:[di], 04h
    sub di, 2
    mov byte ptr es:[di], 01h
    sub di, 2
    mov byte ptr es:[di], ' '
    add manPos, 2
    inc score
    jmp exitCanMoveRight 
    
noBlockInRight:
    
    cmp byte ptr es:[di], '#'
    je exitCanMoveRight
    mov byte ptr es:[di], 01h
    sub di, 2
    mov byte ptr es:[di], ' '
    add manPos, 2
    
exitCanMoveRight:
    
    pop es
    popa 
    ret
endp       
;==================CAN MOVE DOWN==================   
canMoveDown proc
    pusha
    push es   
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    push 0b800h
    pop es
    
    mov di, manPos
    add di, 160
    
    cmp byte ptr es:[di], 04h
    jne noBlockInDown
    add di, 160
    cmp byte ptr es:[di], ' '
    jne exitCanMoveDown
    mov byte ptr es:[di], 04h
    sub di, 160
    mov byte ptr es:[di], 01h
    sub di, 160
    mov byte ptr es:[di], ' '
    add manPos, 160
    inc score
    jmp exitCanMoveDown 
    
noBlockInDown:
    
    cmp byte ptr es:[di], '#'
    je exitCanMoveDown
    mov byte ptr es:[di], 01h
    sub di, 160
    mov byte ptr es:[di], ' '
    add manPos, 160
    
exitCanMoveDown:
    
    pop es
    popa 
    ret
endp           
;==================CAN MOVE UP==================   
canMoveUp proc
    pusha
    push es   
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    push 0b800h
    pop es
    
    mov di, manPos
    sub di, 160
    
    cmp byte ptr es:[di], 04h
    jne noBlockInUp
    sub di, 160
    cmp byte ptr es:[di], ' '
    jne exitCanMoveUp
    mov byte ptr es:[di], 04h
    add di, 160
    mov byte ptr es:[di], 01h
    add di, 160
    mov byte ptr es:[di], ' '
    sub manPos, 160
    inc score
    jmp exitCanMoveUp 
    
noBlockInUp:
    
    cmp byte ptr es:[di], '#'
    je exitCanMoveUp
    mov byte ptr es:[di], 01h
    add di, 160
    mov byte ptr es:[di], ' '
    sub manPos, 160

exitCanMoveUp:
    
    pop es
    popa 
    ret
endp 
;====================PUT MAN IN POS==================
putManInPos proc    
    pushf
    pusha
    push es   
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    push 0b800h
    pop es
    xor di, di
    xor ax, ax
    
    add di, manPos   
        
    mov al, 01h 
    
    mov byte ptr es:[di], al            ; Заполянем консоль пробелами и устанавливаем атрибуты
    inc di

    pop es
    popa
    popf
    
    ret
endp    
;======================================PRINT MENU==============================
printMenu proc
    pusha
    push es
    
    push 0b800h
    pop es
    
    mov di, 40
    lea bx, yourMoves
    call printMenuLine
    
    mov di, 520
    lea bx, chooseLevel 
    call printMenuLine
    
    mov di, 680
    lea bx, arrows
    call printMenuLine  
    
    pop es
    popa
    ret    
endp
;============================================PRINT MENU LINE========================
printMenuLine proc
    pusha
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    xor ax, ax  
printPoint:    
    mov al, [bx] 
    cmp al, 0
    je finishPrint
    
    mov byte ptr es:[di], al            
    inc di
    mov byte ptr es:[di], textColor
    inc di
    inc bx
    jmp printPoint    
finishPrint:

    popa
    ret        
endp                                                            
;=============================PRINT 1 LEVEl==================
printFirstLvl proc
    pusha
    
    call clearWindow
    
    mov score, 0
    
    lea bx, level1
    
    mov ax, level1Size
    mov currentSize, ax
    call printCurLvlBoard
    
    lea bx, level1Blocks
    mov ax, fourBlocks
    mov currentSize, ax
    call printCurLvlBlocks
    
    lea bx, level1BlocksDestination
    mov currentLevel, bx
    call printCurLvlBlocksDst
    
    mov ax, level1ManPos
    mov manPos, ax
    call putManInPos
    call printMenu
    
    popa
    ret
endp
;==========================PRINT 2 LEVEL=============================
printSecondLvl proc
    pusha
    
    call clearWindow
    
    mov score, 0
    
    lea bx, level2  
    
    mov ax, level2Size
    mov currentSize, ax
    call printCurLvlBoard
    
    lea bx, level2Blocks
    mov ax, threeBlocks
    mov currentSize, ax
    call printCurLvlBlocks
    
    lea bx, level2BlocksDestination
    mov currentLevel, bx
    call printCurLvlBlocksDst
    
    mov ax, level2ManPos
    mov manPos, ax
    call putManInPos
    call printMenu
    
    popa
    ret
endp    
;==========================PRINT 3 LEVEL=============================
printThirdLvl proc
    pusha
    
    call clearWindow
    
    mov score, 0
    
    lea bx, level3  
    
    mov ax, level3Size
    mov currentSize, ax
    call printCurLvlBoard
    
    lea bx, level3Blocks
    mov ax, threeBlocks
    mov currentSize, ax
    call printCurLvlBlocks
    
    lea bx, level3BlocksDestination
    mov currentLevel, bx
    call printCurLvlBlocksDst
    
    mov ax, level3ManPos
    mov manPos, ax
    call putManInPos
    call printMenu
    
    popa
    ret
endp
;==========================PRINT 4 LEVEL=============================
printFourthLvl proc
    pusha
    
    call clearWindow
    
    mov score, 0
    
    lea bx, level4  
    
    mov ax, level4Size
    mov currentSize, ax
    call printCurLvlBoard
    
    lea bx, level4Blocks
    mov ax, fourBlocks
    mov currentSize, ax
    call printCurLvlBlocks
    
    lea bx, level4BlocksDestination
    mov currentLevel, bx
    call printCurLvlBlocksDst
    
    mov ax, level4ManPos
    mov manPos, ax
    call putManInPos
    call printMenu
    
    popa
    ret
endp
;==========================PRINT 5 LEVEL=============================
printFifthLvl proc
    pusha
    
    call clearWindow
    
    mov score, 0
    
    lea bx, level5  
    
    mov ax, level5Size
    mov currentSize, ax
    call printCurLvlBoard
    
    lea bx, level5Blocks
    mov ax, fiveBlocks
    mov currentSize, ax
    call printCurLvlBlocks
    
    lea bx, level5BlocksDestination
    mov currentLevel, bx
    call printCurLvlBlocksDst
    
    mov ax, level5ManPos
    mov manPos, ax
    call putManInPos
    call printMenu
    
    popa
    ret
endp
;==========================PRINT 6 LEVEL=============================
printSixthLvl proc
    pusha
    
    call clearWindow
    
    mov score, 0
    
    lea bx, level6  
    
    mov ax, level6Size
    mov currentSize, ax
    call printCurLvlBoard
    
    lea bx, level6Blocks
    mov ax, fiveBlocks
    mov currentSize, ax
    call printCurLvlBlocks
    
    lea bx, level6BlocksDestination
    mov currentLevel, bx
    call printCurLvlBlocksDst
    
    mov ax, level6ManPos
    mov manPos, ax
    call putManInPos
    call printMenu
    
    popa
    ret
endp
       
;==========================PRIRNT WIN MSG============================
printWinMessage proc
    pusha
    push es
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    push 0b800h
    pop es
    
    lea bx, finishGameMsg 
    mov di, 1340  
       
printWinMsg:    
    mov al, [bx] 
    cmp al, 0
    
    je finishPrintWinMsg
    
    mov byte ptr es:[di], al            
    inc di
    mov byte ptr es:[di], textColor
    inc di
    inc bx
    
    jmp printWinMsg
    
finishPrintWinMsg:
    pop es
    popa
    ret
endp    
;==========================CLEAR WINDOW=========================
clearWindow proc
    push ax                              ; Сохраняем значения регистров
    push es
    push di
    
    cld                                  ; Настраиваем консоль
    mov ax, 3h
    int 10h
    
    xor di, di
    mov ax, 0b800h
    mov es, ax                           ; Указываем на видеобуфер
    
    mov cx, numOfDosWindowSymbols        ; numOfDosWindowSymbols = 2000 - кол-во символов, которое возможно вывести на консоль 
     
fillSymbol:
    mov byte ptr es:[di], ' '            ; Заполянем консоль пробелами и устанавливаем атрибуты
    inc di
    mov byte ptr es:[di], textColor
    inc di
    loop fillSymbol 
                                         ; Возвращаем сохранённые значения в регистры
    pop di
    pop es
    pop ax
    ret
endp 
;===========================================PRINT SCORE==========================
printScore proc
    pusha
    push es
    
    mov ah, 0
    mov al, 10000011b
    int 10h
    
    push 0b800h
    pop es
    
    mov di, 66
    
    xor ax, ax
    mov al, score
    div ten
    
    add al, '0'
    add ah, '0'
    
    mov byte ptr es:[di], al            
    inc di
    mov byte ptr es:[di], textColor 
    inc di
    mov byte ptr es:[di], ah            
    inc di
    mov byte ptr es:[di], textColor
    
    pop es
    popa
    ret
endp
;==========================HIDE CURSOR======================
HideCursor proc
        mov ah,3
        mov bh,0
        int 10h
        mov ch,20h
        mov ah,1
        int 10h
        ret
endp
;============================================== DATA=======================================
numOfDosWindowSymbols                   equ 2000
textColor                               equ 00000111b   ; black(0000) on white backround(0111)
boardColor                              equ 01110100b
blockColor                              equ 00000111b
destinationColor                        equ 01000111b
dosWidthWithAttr db 160

manPosX dw 0
manPosY dw 0
manPos dw 0


level1     dw 4,6,8,164,168,324,328,330,332,334,480,482,484,494,640,650,652,654,800,802,804,806,810,966,970,1126,1128,1130
level1Size dw 28
level1Blocks dw 486, 490,646,808
level1BlocksDestination dw 166, 492,642,968
level1ManPos dw 648

level2     dw 2,4,6,8,10,162,170,322,330,334,336,338,482,490,494,498,642,644,646,650,652,654,658,804,806,818,964,972,978,1124,1132,1134,1136,1138,1284,1286,1288,1290,1292
level2Size dw 39
level2Blocks dw 326,328,486
level2BlocksDestination dw 496,656,816
level2ManPos dw 164

level3     dw 2,4,6,8,162,168,170,172,322,332,480,482,484,488,492,494,640,644,648,654,800,810,814,960,974,1120,1122,1124,1126,1128,1130,1132,1134
level3Size dw 33
level3Blocks dw 326, 804, 970
level3BlocksDestination dw 642,802,962
level3ManPos dw 164

level4     dw 2,4,6,8,10,12,14,162,174,176,178,320,322,326,328,330,338,480,498,640,648,656,658,800,802,808,816,962,964,966,968,970,972,974,976
level4Size dw 35
level4Blocks dw 324,488,494,652
level4BlocksDestination dw 644,646,804,806
level4ManPos dw 484

level5     dw 2,4,6,8,160,162,168,320,328,480,482,488,490,640,642,650,800,810,960,970,1120,1122,1124,1126,1128,1130
level5Size dw 26
level5Blocks dw 324,484,646,804,966
level5BlocksDestination dw 802,962,964,966,968
level5ManPos dw 322

level6     dw 6,8,10,12,14,16,18,160,162,164,166,178,320,330,332,334,338,480,484,488,498,500,640,644,654,660,800,804,816,820,960,966,976,980,1120,1122,1132,1136,1140,1142,1144,1282,1286,1288,1290,1304,1442,1454,1456,1464,1602,1604,1606,1608,1610,1612,1614,1616,1618,1620,1622,1624
level6Size dw 62
level6Blocks dw 648,652,810,968,972
level6BlocksDestination dw 328,656,810,964,1292
level6ManPos dw 1302

currentSize dw 0
currentLevel dw 1

yourMoves  db "Your moves: ",0    ;12
chooseLevel db "Choose level: 1-6",0 ;17
finishGameMsg db "Your are won",0 
arrows db "Controls: Up - ", 18h," ","Down - ",19h," ","Left - ",1Bh," ","Right - ", 1Ah,0

threeBlocks dw 3
fourBlocks dw 4
fiveBlocks dw 5

finishGameFlag db 0

score db 0

checkFlag db 0

two db 2
ten db 10  

arrowUp equ 48h
arrowLeft equ 4Bh
arrowDown equ 50h
arrowRight equ 4Dh

install_handler:
    
    cli
    mov ah, 35h                       ; Функция получения адреса обработчика прерывания
	mov al, 09h                       ; прерывание, обработчик которого необходимо получить (09 - прерывание от клавиатуры)
	int 21h
	
	                                 ; Сохраняем старый обработчик
	mov word ptr old_handler, bx     ; смещение
	mov word ptr old_handler + 2, es ; сегмент
	
	push ds
	pop es
	
	mov ah, 25h                       ; Функция замены обработчика прерывания
	mov al, 09h                       ; Прерывание, обработчк которого будет заменен
	mov dx, offset new_handler        ; Загружаем в dx смещение нового обработчика прерывания, который будет установлен на место старого обработчика 
	int 21h
    sti
    
    mov ah, 31h                       ; Делаем программу резидентной
    mov al, 0
    mov dx, (install_handler - start + 100h) / 16 + 1
    int 21h
    
    ret
end start
