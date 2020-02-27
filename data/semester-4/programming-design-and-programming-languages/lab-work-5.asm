.model small

.stack 100h

.data    

startDX               dw  0
tempDX                dw  0
flagTemp              dw  0
flagStart             dw  0

maxCMDSize equ 127
cmd_size              db  ?
cmd_text              db  maxCMDSize + 2 dup(0)
sourcePath            db  129 dup (0) 
tempSourcePath        db  128 dup (0)

;path                  db "kol.txt",0
destinationPath       db  "output.txt",0
extension             db "txt"       
point2                db '.'
buf                   db  0                      
sourceID              dw  0
destinationID         dw  0                                              
                            
newLineSymbol         equ 0Dh
returnSymbol          equ 0Ah                           
endl                  equ 0

enteredString         db 200 dup("$")
enteredStringSize     dw 0

startProcessing       db "Processing started",                                                '$'                      
startText             db  "Program is started",                                               '$'
badCMDArgsMessage     db  "Bad command-line arguments.",                                      '$'
badSourceText         db  "Open error",                                                       '$'    
fileNotFoundText      db  "File not found",                                                   '$'
endText               db  0Dh,0Ah,"Program is ended",                                         '$'
errorReadSourceText   db  "Error reading from source file",                                   '$'

.code

scanf MACRO string
    push ax
    push dx
    
    lea dx, string
    mov ah, 0Ah
    int 21h
    
    pop dx
    pop ax
endm

println MACRO info          ;
	push ax                 ;
	push dx                 ;
                            ;
	mov ah, 09h             ; Команда вывода 
	lea dx, info            ; Загрузка в dx смещения выводимого сообщения
	int 21h                 ; Вызов прервывания для выполнения вывода
                            ;
	mov dl, 0Ah             ; Символ перехода на новую строку
	mov ah, 02h             ; Команда вывода символа
	int 21h                 ; Вызов прерывания
                            ;
	mov dl, 0Dh             ; Символ перехода в начало строки   
	mov ah, 02h             ;
	int 21h                 ;     
                            ;
	pop dx                  ;
	pop ax                  ;
ENDM

strcpy MACRO destination, source, count       ;Макрос, предназначенный для копирования из source в destination заданное количество символов
    push cx
    push di
    push si
    
    xor cx, cx
    
    mov cl, count
    lea si, source
    lea di, destination
    
    rep movsb
    
    pop si
    pop di
    pop cx
ENDM   

incrementTempPos MACRO num          ;Инкрементируем tempDX, если произошло переполнение фиксируем это
    add tempDX, num
    jo overflowTempPos
    jmp endIncrementTempPos
     
overflowTempPos:
    inc flagTemp
    add tempDX, 32769
    jmp endIncrementTempPos
    
endIncrementTempPos:
            
endm 

incrementStartPos proc          ; Инкрементируем startDX, если произошло переполнение фиксируем это
    push ax
    
    mov ax, tempDX
    add startDX, ax
    jo overflow
    jmp endIncrement
     
overflow:
    inc flagStart
    add startDX, 32769
    
endIncrement:
    mov ax, flagTemp
    add flagStart, ax
     
    pop ax
    ret    
endp    

fseekCurrent MACRO settingPos
    push ax                  
	push cx                     
	push dx
	
	mov ah, 42h                 ; Записываем в ah код 42h - ф-ция DOS уставноки указателя файла
	mov al, 1                   ; 1 - перемещение указателя от текущей позиции
	mov cx, 0                   ; Обнуляем cx, 
	mov dx, settingPos	        ; Обнуляем dx, т.е премещаем указатель на 0 символов от начала файла (cx*65536)+dx 
	int 21h                     ; Вызываем прерывания DOS для исполнения команды   
                             
	pop dx                      
	pop cx                      
	pop ax               
ENDM

fseek MACRO fseekPos
    push ax                     
	push cx                     
	push dx
	
	mov ah, 42h                 ; Записываем в ah код 42h - ф-ция DOS уставноки указателя файла
	mov al, 0 			        ; 0 - код перемещения указателя в начало файла 
	mov cx, 0                   ; Обнуляем cx, 
	mov dx, fseekPos            ; Обнуляем dx, т.е премещаем указатель на 0 символов от начала файла (cx*65536)+dx 
	int 21h                     ; Вызываем прерывания DOS для исполнения команды   
                                
	pop dx                      
	pop cx                      
	pop ax    
           
ENDM

setPointer proc                 ; Функция, устанавливающая указатель в файле на позицию, зависящую от startDX и flagStart
    push cx                      
    push bx
    
    mov bx, sourceID
    fseek startDX
    
    cmp flagStart, 0
    je endSetPos
    xor cx, cx    
    mov cx, flagStart
    
setPos1:
    mov bx, sourceID
    fseekCurrent 32767
    loop setPos1 
    
endSetPos:
   
   pop bx
   pop cx
   ret 
endp 

main:
	mov ax, @data           ; Загружаем данные
	mov es, ax              ;
                            ;
	xor ch, ch              ; Обнуляем ch
	mov cl, ds:[80h]		; Количество символов строки, переданной через командную строку
	mov bl, cl
	mov cmd_size, cl 		; В cmd_size загружаем длину командной строки
	dec bl                  ; Уменьшаем значение количества символов в строке на 1, т.к. первый символ пробел
	mov si, 81h             ; Смещение на параметр, переданный через командную строки
	lea di, tempSourcePath        
	
	rep movsb               ; Записать в ячейку адресом ES:DI байт из ячейки DS:SI
	
	mov ds, ax              ; Загружаем в ds данные  
	mov cmd_size, bl        
	
    mov cl, bl
	lea di, cmd_text
	lea si, tempSourcePath
	inc si
	rep movsb
	                        
	println startText       ; Вывод строки о начале работы программы
                            
	call parseCMD           ; Вызов процедуры парсинга командной строки
	cmp ax, 0               
	jne endMain				; Если ax != 0, т.е. при выполении процедуры произошла ошибка - завершаем программу
                            
	call openFiles          ; Вызываем процедуру, которая открывает файл, переданный через командную строку и файл для записи результата
	cmp ax, 0               
	jne endMain				
    
    scanf enteredString         ; Вводим последовательность символов
    xor ax, ax                  ; Запоминаем длину введенной последовательности
    mov al, [enteredString+1]
    mov enteredStringSize, ax
    
    cmp enteredStringSize, 0    ; Если введенная строка пуста нет смысла проверять файл, следовательно заверашем программу 
    je endMain
    println startProcessing                        
	call processingFile       
                            
endMain:                    
	println endText             ; Выводим сообщение о завершении работы программы
                            
	mov ah, 4Ch                 ; Загружаем в ah код команды завершения работы
	int 21h                     ; Вызов прерывания DOS для ее исполнения  
	         
parseCMD proc
    xor ax, ax
    xor cx, cx
    
    cmp cmd_size, 0             ; Если параметр не был передан, то переходим в notFound 
    je notFound
    
    mov cl, cmd_size
    
    lea di, cmd_text
    mov al, cmd_size
    add di, ax
    dec di
    
findPoint:                      ; Ищем точку с конца файла, т.к. после неё идет разширение файла
    mov al, '.'
    mov bl, [di]
    cmp al, bl
    je pointFound
    dec di
    loop findPoint
    
notFound:                       ; Если точка не найдена выводим badCMDArgsMessage и завершаем программу
    println badCMDArgsMessage
    mov ax, 1
    ret
    
pointFound:                     ; Количество символов должно быть равно 3, т.к. "txt", если отлично от этого => файл не подходит
    mov al, cmd_size
    sub ax, cx
    cmp ax, 3
     
    jne notFound
    
    
    xor ax, ax
    lea di, cmd_text
    lea si, extension
    add di, cx
    
    mov cx, 3
    
    repe cmpsb                  ; Сравниваем со строкой Extension расширение файла, если всё совпало - копируем адрес файла в sourcePath 
    jne notFound
    
    strcpy sourcePath, cmd_text, cmd_size
    mov ax, 0
    ret         
endp

openFiles PROC               
	push bx                     
	push dx                                
	push si                                     
                                 
	mov ah, 3Dh			        ; Функция 3Dh - открыть существующий файл
	mov al, 02h			        ; Режим открытия файла - чтение
	lea dx, sourcePath          ; Загружаем в dx название исходного файла 
	int 21h                     
                              
	jb badOpenSource	        ; Если файл не открылся, то прыгаем в badOpenSource
                              
	mov sourceID, ax	        ; Загружаем в sourceId значение из ax, полученное при открытии файла
     
    mov ah, 3Ch                 ; Функция 3Ch - создать файл
    xor cx, cx
    lea dx, destinationPath
    int 21h 
    
    jb badOpenSource
    
    mov ah, 3Dh
    mov al, 02h
    lea dx, destinationPath
    int 21h
    
    jb badOpenSource
    
    mov destinationID, ax
                                
	mov ax, 0			        ; Загружаем в ax 0, т.е. ошибок во время выполнения процедуры не произшло    
	jmp endOpenProc		        ; Прыгаем в endOpenProc и корректно выходим из процедуры
                                
badOpenSource:                  
	println badSourceText       ; Выводим соответсвующее сообщение
	
	cmp ax, 02h                 ; Сравниваем ax с 02h
	jne errorFound              ; Если ax != 02h file error, прыгаем в errorFound
                                
	println fileNotFoundText    ; Выводим сообщение о том, что файл не найден  
                                
	jmp errorFound              ; Прыгаем в errorFound
                               
errorFound:                     
	mov ax, 1
	                   
endOpenProc:
    pop si               
	pop dx                                                     
	pop bx                  
	ret                     
ENDP

processingFile proc             ; Процедура, бработки входного файла
    
for1:
    mov tempDX, 0               ; Обнуляем tempDX и flagTemp 
    mov flagTemp, 0
    
    mov bx, sourceID
    call setPointer             ; Устанавливаем указатель на начало строки
        
    lea si, enteredString       ; Переходим в начало введеной строки
    add si, 2
    
for2:    
    call readSymbolFromFile     ; Считываем символ с файла
    
    incrementTempPos 1
    
    cmp ax, 0                   ; Если ничего не считали => конец файла
    je endFileGG
    cmp [buf], 0                ; Если считали NULL => конец файла
    je endFileGG
    
    cmp [buf], returnSymbol     ; Проверяем не конец строки: cret, new line, \0
    je  endString
    cmp [buf], newLineSymbol
    je  endString
    cmp [buf], endl
    je  endString
          
    xor ax, ax
    xor bx, bx
          
    mov al, buf
    mov bl, [si]
     
    cmp al, bl                  ; Если символа равны идем в doSomething
    je doSomething
    
    jmp for2
    
endString:
    call incrementStartPos       ; Запоминаем начало строки
    jmp for1                     ; Продолжаем обработку
    
    
doSomething:        
    inc si
    
    xor bx, bx      
    mov bl, [si]
    
    cmp bl, newLineSymbol        ; Если дошли до конца введенной строки, то все символа содержатся 
    je stringUdov
    cmp bl, returnSymbol
    je stringUdov
    cmp bl, endl
    je stringUdov
    
    mov tempDX, 0
    mov flagTemp, 0
    
    mov bx, sourceID 
    call setPointer
    
    jmp for2
    
stringUdov:
    call writeStr                ; Записываем в output.txt
    jmp for1
    
endFileGG:
    
    ret
endp

writeStr proc
    mov bx, sourceID 
    call setPointer
    
    mov bx, destinationID
     
    mov tempDX, 1
    mov flagTemp, 0
    
while1:
    call readSymbolFromFile
    call incrementStartPos
    
    cmp ax, 0
    je endAll
    
    cmp [buf], returnSymbol
    je  endWrite
    cmp [buf], endl
    je  endAll
    
    mov ah, 40h
    mov cx, 1
    lea dx, buf
    int 21h
    
    jmp while1
    
endWrite: 
    mov ah, 40h
    mov cx, 1
    lea dx, buf
    int 21h
    
endAll:
        
    ret
endp    
    
readSymbolFromFile proc
    push bx
    push dx
    
    mov ah, 3Fh                     ; Загружаем в ah код 3Fh - код ф-ции чтения из файла
	mov bx, sourceID                ; В bx загружаем ID файла, из которого собираемся считывать
	mov cx, 1                       ; В cx загружаем количество считываемых символов
	lea dx, buf                     ; В dx загружаем смещения буффера, в который будет считывать данные из файла
	int 21h                         ; Вызываем прерывание для выполнения ф-ции
	
	jnb successfullyRead            ; Если ошибок во время счтения не произошло - прыгаем в goodRead
	
	println errorReadSourceText     ; Иначе выводим сообщение об ошибке чтения из файла
	mov ax, 0                       
	    
successfullyRead:

	pop dx                         
	pop bx
	                                
	ret    	   
endp

end main                                                                                                               