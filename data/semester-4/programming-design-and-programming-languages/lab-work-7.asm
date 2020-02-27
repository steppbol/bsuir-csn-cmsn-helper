.model small
.286

.data

cmd_size db ?
maxCMDSize equ 127
cmd_text db maxCMDSize + 2 dup(0)
folderPath db maxCMDSize + 2 dup(0)

DTAsize equ 2Ch
DTAblock db DTAsize dup(0)

startText db "SUPERPARENT Program is started", '$'
endText db "SUPERPARENT Program is ended", '$'
reallocErrorMessage db "Bad relocation memory. Error code: ", '$'
runEXEErrorText db "Error running other program. Error code: ", '$'
badFolderErrorText db "Open directory error", '$'

                                        ;EXEC Parameter Block
EPBstruct 	dw 0                        ;0 - используется текущее окружение
			dw offset line,0
			dw 005Ch, 0, 006Ch, 0		;Адреса FCB (File control block) программы
line db 125
	 db " /?"
line_text db 122 dup(?)

EPBlen dw $ - EPBstruct

extensionEXEFile db "*.exe", 0

DataLength=$-cmd_size

.stack 100h
.code

newline MACRO
	push ax
	push dx
	
	mov dl, 10
	mov ah, 02h
	int 21h

	mov dl, 13
	mov ah, 02h
	int 21h

	pop dx
	pop ax
ENDM

println MACRO info
	push ax
	push dx

	mov ah, 09h
	mov dx, offset info
	int 21h

	newline

	pop dx
	pop ax
ENDM

printErrorCode MACRO        ;Вывод символа ошибки
	add al, '0'
	mov dl, al
	mov ah, 06h
	int 21h

	newline
ENDM

main:
	                                                                ;Перевыделение памяти
	mov ah, 4Ah                                                     ;4Ah - изменить размер блока памяти
	mov bx, ((CodeLength / 16) + 1) + ((DataLength / 16) + 1) + 32	;Требуемый размер в параграфах
	int 21h

	jnc startOfMainProgram                                          ;Переход, если перенос не установлен
                                                                    ;если CF=1 => ошибка(в AX - код ошибки)
	println reallocErrorMessage

	printErrorCode

	mov ax, 1

	jmp endMain

startOfMainProgram:
	mov ax, @data
	mov es, ax

	xor ch, ch
	mov cl, ds:[80h]			
	mov cmd_size, cl 		    ;Сохраняем размер строки
	mov bh, cl
	dec bh
	mov si, 82h                 ;Переписываем в cmd_text переданный путь
	mov di, offset cmd_text
	rep movsb

	mov ds, ax                  ;DS указывает на данные

	println startText
    mov cmd_size, bh
   
	call parseCMD
	
	mov ah, 3Bh                             ;Сменить каталог
    mov dx, offset folderPath
    int 21h
    jc openDirectoryError                   ;CF = 1 => ошибка

	call findFirstFile
	cmp ax, 0
	jne endMain				    ;Если были ошибки выходим из программы

	call runEXE
	cmp ax, 0
	jne endMain				    ;Если были ошибки выходим из программы

runFile:
	call findNextFile
	cmp ax, 0
	jne endMain				    ;Если были ошибки выходим из программы

	call runEXE
	cmp ax, 0
	jne endMain				    ;Если были ошибки выходим из программы

	jmp runFile

openDirectoryError:
	println badFolderErrorText
	
endMain:
	println endText

	mov ah, 4Ch                 ;Завершение программы
	int 21h

parseCMD PROC
	push bx
    push cx
    push dx
    
	mov si, offset cmd_text
    mov di, offset folderPath
    mov cl, cmd_size
    xor ch,ch
    rep movsb
    
    pop dx
    pop cx
	pop bx
	ret	
ENDP

runEXE PROC
	push bx
	push dx

	mov ax, 4B00h				    ;Загрузить и выполнить программу
	mov bx, offset EPBstruct        ;Адрес блока параметров EPB
	mov dx, offset DTAblock + 1Eh	;Получаем имя файла из DTA
	int 21h
	
	jnc runEXEAllGood

	println runEXEErrorText
	printErrorCode

	mov ax, 1

	jmp runEXEEnd

runEXEAllGood:
	mov ax, 0

runEXEEnd:
	pop dx
	pop bx
	ret
ENDP

installDTA PROC
	mov ah,1Ah                      ;1Ah - установить область DTA
    mov dx, offset DTAblock
    int 21h
    ret
ENDP

findFirstFile PROC
	call installDTA

    mov ah,4Eh                      ;Найти первый файл 
    xor cx,cx               		;Атрибут файла для сравнения 
    mov dx, offset extensionEXEFile ;Адрес строки с именем файла для поиска (маска .exe)
    int 21h

	jnc findFirstFileAllGood

	mov ax, 1

	jmp findFirstFileEnd

findFirstFileAllGood:
	mov ax, 0

findFirstFileEnd:

	ret
ENDP

findNextFile PROC
	call installDTA

	mov ah,4Fh                    ;Найти следующий файл
    mov dx, offset DTAblock       ;DTA должна содержать данные от предыдущего вызова 4Eh или 4Fh
    int 21h

	jnc findNextFileAllGood

	mov ax, 1

	jmp findNextFileEnd

findNextFileAllGood:
	mov ax, 0

findNextFileEnd:

	ret
ENDP

CodeLength = $ - main

end main