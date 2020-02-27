.model tiny
.code
org 100h  

;------------------------START---------------------------------------------------------------	
start:
	jmp main  
	
;-------------------DATA----------------------------------------------------------------
startHour db 0        ;\
startMinutes db 0     ; - Время начала работы
startSeconds db 0     ;/

durationHour db 0     ;\                     
durationMinutes db 0  ; - Продолжительность сигнала 
durationSeconds db 0  ;/                     

stopHour db 0         ;\                     
stopMinutes db 0      ; - Время остановки вывода сигнала
stopSeconds db 0      ;/                     

badCMDArgsMessage db "ERROR! INCORRECT PARAMETERS! (Format: HH MM SS HH MM SS)", '$'   
endProgramMessage db "THE END!", '$'
startProgramMessage db "THE ALARM SET!", '$'

AlarmON db 0

widthOfBanner equ 40
allWidth equ 80  

W equ 2057h
A equ 2041h
Q equ 2051h 
E equ 2045h 

U equ 2055h
P equ 2050h
 
H equ 2048h
O equ 2030h

tchk equ 2021h

green equ 2020h
black equ 0020h 

wakeUpText 	dw widthOfBanner dup(green) 
			dw green, 38 dup(black), green
			dw green, 12 dup(green), W, A, Q, E, green, U, P, green, H, E, O, tchk, tchk ,tchk,  12 dup(green), green 
			dw green, 38 dup(black), green
			dw widthOfBanner dup(green)

offWakeUp	dw widthOfBanner dup(black)
			dw widthOfBanner dup(black)
			dw widthOfBanner dup(black)
			dw widthOfBanner dup(black)
			dw widthOfBanner dup(black)

oldInterruptHandler dd 0  

;-------------------DATA END------------------------------------------------------------

;*******************[PRINT BANNER]***************************** 
;Ввод: SI: то что надо напечатать.
printMatrix PROC
	push es
	push 0B800h             ; В si находится смещение выводимого сообщения
	pop es

	mov di, 3*allWidth*2 + (allWidth - widthOfBanner)
    
	mov cx, 5
loopPrintMatrix:
	push cx

	mov cx, widthOfBanner
	rep movsw

	add di, 2*(allWidth - widthOfBanner)

	pop cx
	loop loopPrintMatrix

	pop es  
	
	ret
ENDP
;*******************[PRINT BANNER END]*************************  


;**************[НОВЫЙ ОБРАБОТЧИК ПРЕРЫВАНИЙ]**************************
interruptHandler PROC                             ; Новый обработчик прерывания.                                                     
  
startProc:  
    pushf                                         ; Сохраняем флаги.                                                                               
	                                                                                                         
	   
    call cs:oldInterruptHandler                   ; Вызываем старый обработчик прерывания.                                          
	push    ds          ;\                                                                                                   
    push    es          ; \                                                                                                          
	push    ax          ;  \                                                                                                        
	push    bx          ;   \                                                                                
    push    cx          ;    - Сохраняем регистры.                                                                                                       
    push    dx          ;   /                                                                                                        
	push    di          ;  /                                                                                                                                                                                                                                       
	push    cs          ; /                                                                                                             
	pop     ds          ;/                                                                               
                                                                                                                                   
	call getTime
    
onClock:                                                 
	cmp ch, startHour                            
	jne offClock                                 
	cmp cl, startMinutes                                                                                                           
	jne offClock                                 ;Проверка на возможность включения будильника.                                    
	cmp dh, startSeconds                         ;Если текущее время не равно времени срабатывания будильника - прекращаем проверу                                                                                 
	jne offClock                                 
	                                                                                                                                                                                                                                                           
	mov dl, AlarmON                              ; Определяем текущее состояние будильника                                                                                                            
	cmp dl, 0                                                                                                                                                                                            
	jne offClock                                 ; Если буульник не включен - прекращаем проверку                                
                                                                                                                        
    call printMsgClock                                                                                                                                                                                                                                                                          
                                                                                                                                    
	jmp exitFromProc                                                                                                                  
                                                 ; Проверка на возможность выключения будильника                                
offClock:                                                                                                                         	                                                                                                                                
	cmp ch, stopHour                             ; Если текущее время != время выключения - прекращаем проверку                 
	jne exitFromProc                                                                                                                
	cmp cl, stopMinutes                                                                                                             
	jne exitFromProc                                                                                                                 
	cmp dh, stopSeconds                                                                  
	jne exitFromProc                                                                                                                 
 	                                                                                                                                                                                                
	mov dl, AlarmON                              ; Определяем текущее состояние будильника                                                                                
	cmp dl, 1                                    ; Если будильник не включен - заканчиваем проверку                                                                              
	jne exitFromProc                                                    
    
    call printMsgBlack    
                                                                                                      
exitFromProc:                                                                                                                        
    pop     di      ;\                                                                                                               
	pop     dx      ; \                                                                                                              
	pop     cx      ;  \                                                                                     
	pop     bx      ;   - Восстанавливаем регистры                                                                                                            
	pop     ax      ;  /                                                                                                             
	pop     es      ; /                                                                                                              
	pop     ds      ;/
		                                                                                                                   
	iret   
    
ENDP    

getTime PROC
    mov     ah,02h                               ;02H читать время из постоянных (CMOS) часов реального времени             
	int     1Ah                                  ;выход: CH = часы в коде BCD   (пример: CX = 1243H = 12:43)                     
                                                 ;CL = минуты в коде BCD                                                   
                                                 ;DH = секунды в коде BCD                                                  
                                                 ;CF = 1, если часы не работают 
    ret
ENDP
                
;-------ВЫВОД НА ЭКРАН БУДИЛЬНИКА-------------
printMsgClock PROC
	mov si, offset wakeUpText                       ; Загружаем в si необходимое сообщение                                                              
	call printMatrix                                ; Вызываем вывод сообщения, находящегося в si                                                                                 
	mov dl, 1                                       ; Устанавливаем состояние будильника в 1                                        
	mov AlarmON, dl                                 ; Заканчиваем обработку 
	
	ret
ENDP    

;-------ВЫВОД НА ЭКРАН ЧЕРНОГО КВАДРАТА------
printMsgBlack PROC                                                                                                                                            
	mov si, offset offWakeUp                    ; Убираем сообщение будильника                                                      
	call printMatrix                            ; Загружаем в si, сообщение, скрывающее оповещение "Wake Up"                       
	mov dl, 0                                                                                                              
	mov AlarmON, dl                             ; Устанавливаем состояние будильника в 0    
	
	ret
ENDP                                                                                                                       
;**************[НОВЫЙ ОБРАБОТЧИК ПРЕРЫВАНИЙ КОНЕЦ]********************      
;Вычисление времени работы программы.  


sizeOfProgram:
;---------------------------------------------------------------------------------------      

;******************[SET NEW HANDLER]***************************
;Результат в AX. Если все хорошо - 0, если нет 1
setNewInterrupt PROC
	push bx dx        ;Сохраняем.

	cli                ; Разрешаем прерывания    
	
    call getInterrupt       
    call setInterrupt  

	sti              ; Запрещаем прерывания

	mov ax, 0        ;Возвращаемое значение.

	pop dx
	pop bx 
	
	ret
ENDP       
      
;--СОХРАНЯЕМ СТАРЫЙ ОБРАБОТЧИК------------------       
getInterrupt PROC
	mov ah, 35h        ;Получаем адрес обработчика прерывания.
	mov al, 1Ch        ;Номер прерывания, 1C - прерывание таймера
	int 21h  
	
	mov word ptr [offset oldInterruptHandler], bx      ;Сохраняем смещение обработчика.
	mov word ptr [offset oldInterruptHandler + 2], es  ;Сохраняем сегмента обработчика.

	push ds			;Восстаналиваем старое значение ES. DS - адрес обработчика прерываний. 
	pop es    
	
	ret
ENDP 
       
;--УСТАНАВЛИВАЕМ НОВЫЙ ОБРАБОТЧИК---------------               
setInterrupt PROC  
	
	mov ah, 25h     ;Устанавливаем адрес обработчика прерываний
	mov al, 1Ch     ;Программное прерывание. Вызывается 18,2 раза в секунду обработчиком прерывания таймера.
	mov dx, offset interruptHandler
	int 21h
	
	ret
	
ENDP
;******************[SET NEW HANDLER END]*********************** 
                                                               
;******************[NEW LINE]**********************************
newline PROC
	push ax
	push dx

	;--ВЫВОД НОВОЙ СТРОКИ--------------------
	mov dl, 10
	mov ah, 02h
	int 21h

	mov dl, 13
	mov ah, 02h
	int 21h

	pop dx
	pop ax
	ret
ENDP
;******************[NEW LINE END]*******************************

;******************[PRINT]**************************************
println PROC
	push ax
	push dx

	mov ah, 09h
	int 21h

	call newline

	pop dx
	pop ax
	ret
ENDP
;******************[PRINT END]*********************************
   
;******************[PARSE CONCOLE]************************************ 
;Результат в AX. Если все хорошо - 0, если нет 1
parseCMD PROC   
    
    push bx             ;\
	push cx             ; \
	push dx             ;  - Сохраняем значения регистров.
	push si             ; /
	push di             ;/                            
                                                                                               
	cld                                   ;Снимаем флаги.  
    
    call parseFirstArg                                                                
                                                                                               
parseCMDloop:                                                                                  
	mov dl, [si]                                                                               
	inc si                                ;Загружаем в dl очередной символ из командной строки.   
	cmp dl, ' '                           ;Следующий элемент.                                                     
	je spaceFounded                       ;Если пробел, то прыгаем в SpaceIsFound             
                                                                                               
	cmp dl, '0'                                                                                
	jl badCMDArgs                         ;Проверяем на корректность ввода (0-9).                                                     
	cmp dl, '9'                                     
	jg badCMDArgs                                                                              
                                                                                               
	sub dl, '0'                                                                                
	mov bl, 10                            ;Переводим в число, то что распарсили в DL.                                                     
	mul bl                                                                                     
	add ax, dx                                                                                 
                                          ;Переходим в  ошибку, если >= 60.                       
	cmp ax, 60                                                                                 
	jae badCMDArgs				          ;Проверяем правильность.                          
	cmp ax, 24                            ;Если больше 24, проверяем значения часов.                                                     
	jae checkHour

	loop parseCMDloop

spaceFounded:
	mov byte ptr es:[di], al
	cmp di, offset durationSeconds        ; Если последний введенный элемент - продолжительность в секундах - ввод корректный.
	je setExitCode

	inc di 
	
	call skipAllSpacesProc
	
	dec si
	inc cx
	    	    
	xor ax, ax

	loop parseCMDloop
	jmp setExitCode

checkHour:
	cmp si, offset startHour              ; Проверяем, если текущее вводимое значение - 
	                                      ; час начала или продолжительность в часах - ввод некорректен
	je badCMDArgs
	cmp si, offset durationHour
	je badCMDArgs
	
	loop parseCMDloop
	jmp spaceFounded

badCMDArgs:
	mov dx, offset badCMDArgsMessage
	call println
	mov ax, 1

	jmp endProc

setExitCode:
	mov ax, 0

endProc:
	pop di                 ;\
	pop si                 ; \
	pop dx                 ;  - ;Достаем из стека. 
	pop cx                 ; /
	pop bx                 ;/     
	
	ret	
	
ENDP 

;---ПАРСИНГ ПЕРВОГО ЭЛЕМЕНТА(ЧАСЫ)---------------------
parseFirstArg PROC                                      
	mov bx, 80h                         ;Смещение на 80h, начинаем с 81h работу              
	mov cl, cs:[bx]                                                         
	xor ch, ch                                                                                 
                                                                                               
	xor dx, dx                                                                                 
	mov di, 81h                                                                                
                                                                                               
    call skipSpacesInBeginning
	                                                                                  
	xor ax, ax                                                                                 
                                        ;Заносим в будильник часы.                           
	mov si, di                                                                                 
	mov di, offset startHour   
       
    ret
ENDP

;---ПРОПУСК ПРОБЕЛОВ ПОСЛЕ 0 АРГУМЕНТА-------------------
skipSpacesInBeginning PROC
                                                                                                                                                                   
	mov al, ' '                         ;Пропускаем пробелы в начале.                          
	repne scasb	                        ;Находим проблелы в строке. 
	                                    ;Пропускаем их
	mov al, ' '
	repe scasb
	dec di
	inc cx 
	
	ret
ENDP

;---ПРОПУСК ПРОБЕЛОВ ПОСЛЕ КАЖДОГО АРГУМЕНТА------------
skipAllSpacesProc PROC
	skipAllSpaces: 
	    push bx
	    
	    mov al, ' '
	    mov bl, [si]   
	    inc si
	    dec cx
	    
	    cmp bl, al
	    
	    pop bx
	    je skipAllSpaces
	
	ret
ENDP
;******************[PARSE CONCOLE END]************************* 
     
;******************[CALCULATE]*********************************
calcucateStopTime PROC  
    
    call calculateSeconds
    call calculateMinutes
    call calculateHours     
 
   
   ; Переводим значения времени начала/ продолжительности/ остановки сингала в BCD код
   ; для последующего корректног взаимодействия с системной функцией получения времени
    
    mov cx, 9                     ; Загружаеи в cx 9, чтобы перевести все числа                                                    
	mov bl, 10                    ; т.е. 9 = 3*3 = (Часы + Минуты + Секунды) * (Время начала + продолжительность + Время остановки)
	mov si, offset startHour      ; =//= в bx 10                                                                                   
                                  ; Устанавливаем si на startHour, т.е время начала будильника в часах  

convertLoop:                                                 
	xor ah, ah                    ; Обнуляем ah                                                                                                                             
	mov al, [si]                  ; Загружаем очередной символ                                                                    
	div bl                        ; Делим на 10. Частное - al, остаток - ah                                                       
                                                                                        
	mov dl, al                    ; Загружаеи в dl al, т.е.  частное  от деления на 10                                                                                                
                                                                              
	shl dl, 4                     ; Сдвиг влево на 4 (необходимо для BCD формата)                                                                                                      
	add dl, ah                    ; Добавляем в dl ah, т.е  остаток от деления на 10                                               
	mov [si], dl                  ; Переписываем элемент в si на новый в формате bcd                                               
                                                                              
	inc si                                                                                                                      
	loop convertLoop

	ret  
ENDP   

;--СЕКУНДЫ---------------------------------
calculateSeconds PROC  

	xor ah, ah
	mov al, startSeconds
	add al, durationSeconds
	mov bl, 60			
	div bl
	
	mov stopSeconds, ah  ;Частное храним в al, остаток в ah. Заносим секунды остановки.
	
	ret
	
ENDP

;--МИНУТЫ---------------------------------
calculateMinutes PROC  
          
	xor ah, ah
	add al, startMinutes
	add al, durationMinutes
	mov bl, 60			;Максимальное значение минут + 1.
	div bl

	mov stopMinutes, ah ;Частное храним в al, остаток в ah. 
	                    ;Заносим минуты остановки. После деление в al может быть 1, то есть перенос.  
          
    ret
	
ENDP  

;--ЧАСЫ-----------------------------------
calculateHours PROC  
	xor ah, ah
	add al, startHour
	add al, durationHour
	mov bl, 24			;Максимальное значение часов + 1.
	div bl

	mov stopHour, ah     ;Частное храним в al, остаток в ah. Заносим часы остановки.    
	
	ret
	
ENDP    
;******************[CALCULATE END]*****************************                                                                                                                             
;---------------------------------------------------------------------------------------

;******************[MAIN]**************************************
main:  
    mov ax, 03
    int 10h
    
	call parseCMD
	cmp ax, 0   
	jne endMain				;Если ошибка - завершаем программу.
	
	mov dx, offset startProgramMessage
	call println   
	 	 
	call calcucateStopTime  ;Вычисляем время вывода будильника.                         	                        
	call setNewInterrupt 
	
	cmp ax, 0
	jne endMain				;Если ошибка - завершаем программу

	mov ah, 31h             ; Оставляем программу резидентной
	mov al, 0          
	
	mov dx, (sizeOfProgram - start + 100h) / 16 + 1 ; Заносим в dx размер программы + PSP,
	                                                ; делим на  16, т.к. в dx необходимо занести размер в 16 байтных параграфах
	int 21h

endMain:    
	ret
;******************[MAIN END]***********************************
end start  

;------------------------START END------------------------------------------------------