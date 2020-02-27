.386P
.model large

;-----------Структура сегментного дескриптора-------------------
struct_deskriptor  struc  
    limit        dw 0     ;Лимит сегмента (15:00)    
    base_low     dw 0     ;Адрес базы, младшая часть (15:0)
    base_medium  db 0     ;Адрес базы, средняя часть (23:16)
    access       db 0     ;Байт доступа 
    attribs      db 0     ;Лимит сегмента (19:16) и атрибуты
    base_hight   db 0     ;Адрес базы, старшая часть
struct_deskriptor  ends    
;-------------------------------------------------------------------------------- 

;----------Структура дескриптора таблицы прерываний-------
struct_interrupt_deskriptor  struc 
    offset_low       dw 0           ;Адрес обработчика (0:15)
    selectors        dw 0           ;Селектор кода, содержащего код обработчика
    parametrs        db 0           ;Параметры
    access           db 0           ;Уровень доступа
    offset_hight     dw 0           ;Адрес обработчика (31:16)
struct_interrupt_deskriptor  ends 
;--------------------------------------------------------------------------------  

;--------Структура idtr-----------------------------------------------------     
struct_idtr struc          
    limit          dw 0    ;16-bit table limit
    idtr_low       dw 0    ;Смещение биты (0-15)
    idtr_hight     dw 0    ;Смещение биты (31-16)
struct_idtr  ends  
;--------------------------------------------------------------------------------

access_read        EQU 00000010B   ;бит чтения, возможность чтения из другого сегмента 
access_code        EQU 10011000B	;AR сегмента кода: присутствие, сегмент, тип сегмента
access_data        EQU 10010010B	;AR сегмента данных: присутствие, сегмент, чтение/запись
access_stack       EQU 10010010B   ;AR сегмента стека
access_idt           EQU 10010010B    ;AR таблицы idt    
access_interrupt EQU 10001110B  ;AR таблицы INT
access_trap         EQU 10001111B
access_dpl_three   EQU 01100000B  ;X<DPL,DPL>XXXXX - привелегии доступа, доступ может получить любой 


;Флаги уровней доступа сегментов	;76543210 : 
                       ; 7--(1-физ память),
	          ; 6,5--(привелегии),
		; 4--Если этот бит установлен, то дескриптор определяет сегмент кода или данных,
		;    а если сброшен, то системный объект (например, сегмент состояния задачи, локальную дескрипторную таблицу, шлюз).
		; 3--(1,1-для кода, 1,0-для данных),
		; 2--(0-рост стека вверх для данных,для кода-бит подчинения)
		; 1--(0-процессор только выполняет,нельзя считывать для кода, 1-чтение и запись для данных)
		; 0--(бит обращение к сегменту)
;Если процессор обращался к сегменту для чтения или записи данных или для выполнения кода, размещённых в нём,
;то бит A будет установлен (равен 1), иначе - сброшен (0). 

;Сегмент кода реального режима 
;****************************************************************************      
code_for_real_mode segment para use16
code_for_real_mode_begin   = $

    assume cs:code_for_real_mode,DS:data,ES:data   ;Инициализация регистров 
	
start:
	mov ax, 03 ; очистка экрана
	int 10h

    mov ax,data ;Инициализиция сегментных регистров
    mov ds,ax                                   
    mov es,ax                          
    lea dx,message_for_get_in_protected_mode
    mov ah,9h
    int 21h
    call input ;Выбор клавиши для смены режима
                                                  
;============================================================                                                  
;Открываем линию A20 - открытие вентиля для дальнейшей работы c 32 битной памятью(для защищенного режима)
    in  al,92h                                                                              
    or  al,2   ;установка 1-го бита в единицу                                                   
    out 92h,al                                
;============================================================	

;============================================================	
;Сохраняем маски прерываний     
    in      al,21h
    mov     mask_master,al;ведущий                 
    in      al,0A1h
    mov     mask_slave,al;ведомый  
;============================================================

;============================================================	
;Запрет маскируемых и немаскируемых прерываний        
    cli ;Запрет маскируемых прерываний
    in  al,70h	
	or	al,10000000b ;Установить 7 бит в 1 для запрета немаскируемых прерываний
	out	70h,al 
;============================================================
	
;============================================================	
;Заполняем глобальную таблицу дескрипторов            
    mov ax,data	    ;записываем адрес начала сегмента данных в dl
    mov dl,ah	    ;формируем в dl:ax физический адрес, соответствующий сегментному адресу data
    xor dh,dh      
    shl ax,4	    ;сдвиг ax влево
    shr dx,4	    ;сдвиг dx вправо                
	                                                    
    mov si,ax		;сохраняем для того,чтобы всегла знать начало сегмента
    mov di,dx		;сохраняем для того,чтобы всегла знать начало сегмента
;============================================================

;============================================================	
;Заполняем дескриптор gdt
    lea bx,gdt_gdt
    add ax,offset gdt ; добавим смещение для вычисления реального физ адреса
    adc dx,0 ; cf=1
    mov [bx][struct_deskriptor.base_low],ax ;сразу 32 битный не можем так как base в разных частях структуры
    mov [bx][struct_deskriptor.base_medium],dl
    mov [bx][struct_deskriptor.base_hight],dh   
;============================================================

;============================================================	
;Заполняем дескриптор сегмента кода реального режима
    lea bx,gdt_code_for_real_mode
    mov ax,cs
    xor dh,dh
    mov dl,ah
    shl ax,4
    shr dx,4
    mov [bx][struct_deskriptor.base_low],ax
    mov [bx][struct_deskriptor.base_medium],dl
    mov [bx][struct_deskriptor.base_hight],dh
;============================================================

;============================================================	
;Заполняем дескриптор сегмента данных
    lea bx,gdt_data
    mov ax,si
    mov dx,di
    mov [bx][struct_deskriptor.base_low],ax
    mov [bx][struct_deskriptor.base_medium],dl
    mov [bx][struct_deskriptor.base_hight],dh
;============================================================

;============================================================	
;Заполняем дескриптор сегмента стека
    lea bx, gdt_stack
    mov ax,ss
    xor dh,dh
    mov dl,ah
    shl ax,4
    shr dx,4
    mov [bx][struct_deskriptor.base_low],ax
    mov [bx][struct_deskriptor.base_medium],dl
    mov [bx][struct_deskriptor.base_hight],dh
;============================================================

;============================================================	
  ;Заполняем дескриптор кода защищенного режима
    lea bx,gdt_code_for_protected_mode
    mov ax,code_for_protected_mode
    xor dh,dh
    mov dl,ah
    shl ax,4
    shr dx,4
    mov [bx][struct_deskriptor.base_low],ax
    mov [bx][struct_deskriptor.base_medium],dl
    mov [bx][struct_deskriptor.base_hight],dh        
    or  [bx][struct_deskriptor.attribs],40h	 ;40h - 10000000 (работа с физ. памятью)
;============================================================

;============================================================	
;Заполняем дескриптор idt
    lea bx,gdt_idt
    mov ax,si
    mov dx,di
    add ax,offset idt
    adc dx,0
    mov [bx][struct_deskriptor.base_low],ax
    mov [bx][struct_deskriptor.base_medium],dl
    mov [bx][struct_deskriptor.base_hight],dh        
    mov idtr.idtr_low,ax
    mov idtr.idtr_hight,dx 
;============================================================

;============================================================
;Заполняем таблицу дескрипторов шлюзов прерываний
    irpc    index, 0 ;Заполнить вектор 20 заглушкой
        lea eax,irg_master_dummy ;заглушка для аппаратных прерываний ведущего контроллера 
        mov idt_2&index.offset_low, ax ;offs_l  и offd_h нельзя объединть т.к они не рядом в структуре
        shr eax,16
        mov idt_2&index.offset_hight, ax
    endm
	
	lea eax, interrupt_for_keyboard;Поместить обработчик прерывания клавиатуры на 21 шлюз
    mov idt_keyboard.offset_low,ax
    shr eax, 16
    mov idt_keyboard.offset_hight,ax
	
    irpc    index, 345678 ;заглушки для векторов
        lea eax,irg_master_dummy
        mov idt_2&index.offset_low, ax
        shr eax,16
        mov idt_2&index.offset_hight, ax
    endm           
    
    irpc    index, 9ABCDEF ;заглушки для векторов
        lea eax,irg_slave_dummy
        mov idt_2&index.offset_low,ax
        shr eax,16
        mov idt_2&index.offset_hight,ax
    endm
	
    lgdt fword ptr gdt_gdt;Загрузить регистр gdtr(загрузка таблицы глобальных дескрипторов)
    lidt fword ptr idtr   ;Загрузить регистр idtr(загрузка таблицы дескрипторов прерываний)
    mov eax,cr0           ;Получить управляющий режимом работы процессора регистр cr0
    or  al,00000001b      ;Установить бит pe в 1 - разрешение режима защиты
    mov cr0,eax           ;Записать измененный cr0 и тем самым включить защищенный режим
	
    db  0eah	;код дальнего jmp
    dw  $+4		;адрес следующей команды
    dw  code_for_real_mode_descriptor
;============================================================        

;============================================================	
;Переинициализируем остальные сегментные регистры на дескрипторы
    mov ax,data_descriptor
    mov ds,ax                         
    mov es,ax                         
    mov ax,stack_descriptor
    mov ss,ax                         
    xor ax,ax                             
    lldt ax ;Обнулить регистр LDTR - не использовать таблицы локальных дескрипторов
	
    push cs ;Сегмент кода
    push offset  back_to_real_mode ;Смещение точки возврата
    lea  edi,to_protected_mode ;Получить точку входа в защищенный режим
    mov  eax,code_for_protected_mode_descriptor ;Получить дескриптор кода защищенного режима
    push eax                                
    push edi 
;============================================================                                   

;============================================================	
;Переинициализируем контроллер прерываний 
    mov al,00010000b ;ICW1 - переинициализация контроллера прерываний
    out 20h,al       
    out 0A0h,al      
    mov al,20h       ;ICW2 - номер базового вектора прерываний
    out 21h,al       ;ведущего контроллера
    mov al,28h       
    out 0A1h,al      ;ведомого контроллера
    mov al,04h       ;ICW3 - ведущий контроллер подключен к 3 линии
    out 21h,al       
    mov al,02h       ;ICW3 - ведомый контроллер подключен к 3 линии
    out 0A1h,al      
    mov al,11h       ;ICW4 - режим специальной полной вложенности для ведущего контроллера
    out 21h,al          ;11h-00010001 послединй бит -8086 4-master
    mov al,01h       ;ICW4 - режим обычной полной вложенности для ведомого контроллера
    out 0A1h,al       
    mov al, 0        ;Размаскировать прерывания
    out 21h,al                                 
    out 0A1h,al         
;============================================================

;============================================================	
;Разрешим маскируемые и немаскируемые прерывания
    in  al,70h	
	and	al,01111111b ;Установить 7 бит в 0 для запрета немаскируемых прерываний
	out	70h,al
    sti ;Разрешить маскируемые прерывания
;============================================================

;============================================================	
 ;Переходим к сегменту кода защищенного режима
    db 66h  ;Изменяем разрядность регистра                                    
    retf ;в push точка входа в защищенный режим
	
 back_to_real_mode: ;Точка возврата в реальный режим
    cli ;Запрет прерываний
    in  al,70h	                               
	or	al,10000000b ;Разрешение немаскируемых прерываний        
	out	70h,al 
;============================================================

;============================================================	
;Переинициализируем контроллер прерываний                                 
    mov al,00010000b                            
    out 20h,al                                  
    out 0A0h,al                                 
    mov al,8h                                   
    out 21h,al                                  
    mov al,70h                                 
    out 0A1h,al     
    mov al,04h      
    out 21h,al       
    mov al,02h           
    out 0A1h,al      
    mov al,11h             
    out 21h,al        
    mov al,01h                   
    out 0A1h,al
;============================================================

;============================================================	
;Подготовим сегментные регистры для возврата в реальный режим          
    mov gdt_code_for_real_mode.limit,0ffffh ;Установка лимита сегмента кода
    mov gdt_data.limit,0ffffh ;Установка лимита сегмента данных
    mov gdt_stack.limit,0ffffh ;Установка лимита сегмента стека
	
    db  0eah ;Перезагрузить регистр cs
    dw  $+4
    dw  code_for_real_mode_descriptor ;На сегмент кода реального режима
	
    mov ax,data_descriptor ;Загрузим сегментные регистры дескриптором сегмента данных
    mov ds,ax                                   
    mov es,ax                                                                   
    mov ax,stack_descriptor
    mov ss,ax ;Загрузим регистр стека дескриптором стека     
;============================================================    
    
;============================================================	
  ;Включям реальный режим
    mov eax,cr0
    and al,11111110b ;Обнулим 0 бит регистра cr0(feh)
    mov cr0,eax 
	
    db  0eah;код дальнего jmp
    dw  $+4 ;4 байтовый адрес перехода
    dw  code_for_real_mode ;Перезагрузим регистр кода
	
    mov ax,segment_stack
    mov ss,ax                      
    mov ax,data
    mov ds,ax                      
    mov es,ax
    xor ax,ax
    mov idtr.limit, 3ffh ;правая граница векторов прерываний  
    mov dword ptr  idtr+2, 0            
    lidt fword ptr idtr  
;============================================================               

;============================================================	
;Восстановим маски прерываний
    mov al,mask_master
    out 21h,al ;ведущий     
    mov al,mask_slave
    out 0A1h,al;ведомый 
;============================================================

;============================================================	
;Разрешим маскируемые и немаскируемые прерывания
    in  al,70h	
	and	al,01111111b ;Установить 7 бит в 0 для разрешения немаскируемых прерываний
	out	70h,al

    sti ;Разрешить маскируемые прерывания
;============================================================

;============================================================	
 ;Закроем вентиль A20
    in  al,92h
    and al,11111101b ;Обнулить 1 бит - запретить линию A20
    out 92h, al
;============================================================

;============================================================	
;Выходим из программы
    mov ax,03h
    int 10h ;Очистим экран
	
    lea dx,message_for_real_mode
    mov ah,09h
    int 21h                        
    mov ax,4c00h ;выход из программы
    int 21h  
;============================================================             

;============================================================	
input proc near ;Процедура выбора клавиши для переключения режимов
    mov ah, 00h	;Ввод символа
	xor al, al	
	int 16h
	mov input_key, ah

	lea dx,message_for_getch
   	mov ah,9h
   	int 21h
	
	mov ah, 01h	;для помехи выходу сразу после нажатия клавиши
	xor al, al						   
    int 21h
return:
    	ret
input endp 
;============================================================


size_code_for_real_mode    = $ - code_for_real_mode_begin    ;Лимит сегмента кода
code_for_real_mode ends  
;****************************************************************************

;Сегмент кода защищенного режима
;****************************************************************************  
code_for_protected_mode  segment para use32
code_for_protected_mode_begin   = $
    assume cs:code_for_protected_mode,ds:data,es:data ;Указание сегментов для компиляции

;============================================================	
to_protected_mode: ;Точка входа в защищенный режим
    call cls                              
	xor  edi,edi ;В edi смещение на экране
    lea  esi,message_for_protected_mode ;В esi адрес буфера
    call output         

    add  edi,160                               
    lea  esi,message_for_input
    call output ;Вывести поле для вывода скан-кода клавиатуры
    mov edi,320                
	
while:             
    jmp  while ;ожидаем прерывания клавиатуры       
	
exit_from_iterrupt:;Точка выхода для выхода напрямую из обработчика прерываний
    popad
    pop es
    pop ds
    pop eax         ;Снять со стека старый EIP
    pop eax         ;CS  
    pop eax         ;И EFLAGS
    sti             ;Разрешаем прерывания
    db 66H          ;Выход из 32-битного сегмента кода    
    retf            ;Переход в 16-битный сегмент кода    

;============================================================
full_value_to_hex proc near ;Процедура перевода числа в шеснадцатеричный вид
    push ax
    mov ah,al             
    shr al,4              
    call dec_to_hex     
    mov [di],al           
    inc di                
    mov al,ah             
    and al,0Fh            
    call dec_to_hex     
    mov [di],al           
    inc di                
pop ax
    ret    
full_value_to_hex endp
;============================================================
;============================================================
dec_to_hex proc near ;Процедура перевода цифры в шеснадцатеричный вид
    add al,'0'            
    cmp al,'9'            
    jle end_dec_to_hex           
    add al,7              
end_dec_to_hex:
    ret        
dec_to_hex endp
;============================================================
;============================================================
irg_master_dummy proc near ;Заглушка для прерываний ведущего контроллера
    push eax
    mov  al,20h  
    out  20h,al
    pop  eax
    iretd
irg_master_dummy endp
irg_slave_dummy  proc near ;Заглушка для прерываний ведомого контроллера
    push eax
    mov  al,20h
    out  20h,al
    out  0A0h,al
    pop  eax
    iretd
irg_slave_dummy  endp
;============================================================
;============================================================
interrupt_for_keyboard proc near ;Обработчик прерывания от клавиатуры
    push ds
    push es
    pushad   	
    in   al, 60h ;Считать скан код последней нажатой клавиши    
	cmp  al, input_key ;Сравниваем код клавиши с той, которая была задана вначале               
    je   exit_interrupt ;если равны то выход из защищенного режима   
    mov  ds:[scan_code],al               
    lea  edi,ds:[buffer_for_scan_code]
    mov  al,ds:[scan_code]
    xor  ah,ah
    call full_value_to_hex                         
    mov  edi,172            ;отступ для вывода сообщения
    lea  esi,buffer_for_scan_code                   
    call output                    
    jmp  return_interrupt 	
exit_interrupt:
    mov  al,20h
    out  20h,al
    db 0eah
    dd offset exit_from_iterrupt  ;Возвращаем все прерывния
    dw code_for_protected_mode_descriptor  
return_interrupt:
    mov  al,20h
    out  20h,al                                
    popad 
    pop es
    pop ds
    iretd                              
interrupt_for_keyboard endp   
;============================================================
;============================================================
cls  proc near ;Процедура очистки консоли
    push es
    pushad
    mov  ax,text_gdt    
    mov  es,ax
    xor  edi,edi
    mov  ecx,2000  ;размер экрана
    mov  ax,2400h  ;символ пробела с атрибутом синего цвета ah-цвет al-символ
    rep  stosw	   ;повторять пока не пройдем по всем элементам экрана
    popad
    pop  es
    ret
cls  endp
;============================================================
;============================================================
output proc near  ;Процедура вывода текстового буфера, оканчивающегося 0
    push es
    pushad
    mov  ax,text_gdt;Поместить в es селектор текста
    mov  es,ax
show:        ;Цикл по выводу буфера
    lodsb                                       
    cmp al,00h
    je   exit_output  ;Если дошло до 0, то конец выхода
    stosb
    inc  edi
    jmp  show
exit_output: ;Выход из процедуры вывода
    popad
    pop  es
    ret
output  endp 
;============================================================
size_code_for_protected_mode     =       $ - code_for_protected_mode_begin
code_for_protected_mode  ends

;Сегмент данных реального/защищенного режима 
;****************************************************************************
	data    segment para use16                 ;Сегмент данных реального/защищенного режима - сохранение сегментов данных, кода....для доступа к ним в защищенном режиме
	data_begin      = 	$     	
;-----------gdt - глобальная таблица дескрипторов------------------------
    gdt_begin  = 	$
    gdt label   word  ;Метка начала gdt
    gdt_0       struct_deskriptor <0,0,0,0,0,0> ;при обращении произайдет прерывание                  
    gdt_gdt     struct_deskriptor <gdt_size-1,,,access_data,0,> ;лимит сегмента,,,байт доступа, атрибуты   
    gdt_code_for_real_mode struct_deskriptor <size_code_for_real_mode-1,,,access_code,0,>;сегмент кода(реальный режим)
    gdt_data    struct_deskriptor <size_data-1,,,access_data+access_dpl_three,0,>      
    gdt_stack   struct_deskriptor <1000h-1,,,access_data,0,>                    
    gdt_text    struct_deskriptor <2000h-1,8000h,0Bh,access_data+access_dpl_three,0,0> 
    gdt_code_for_protected_mode struct_deskriptor <size_code_for_protected_mode-1,,,access_code+access_read,0,>;сегмент кода(защищенный режим)
    gdt_idt     struct_deskriptor <size_idt-1,,,access_idt,0,>                  
    gdt_size    = ($ - gdt_begin);Размер gdt
;-----------------------------------------------------------------------------------    
;-----Селлекторы сегментов-----------------------------------------------
    code_for_real_mode_descriptor = (gdt_code_for_real_mode - gdt_0)
    data_descriptor    = (gdt_data - gdt_0)      
    stack_descriptor   = (gdt_stack - gdt_0)
    text_gdt    = (gdt_text - gdt_0)  
    code_for_protected_mode_descriptor = (gdt_code_for_protected_mode - gdt_0)
;------------------------------------------------------------------------------------------
;-----idt - таблица дескрипторов прерываний------------------------------
    idtr    struct_idtr  <size_idt,0,0>;Формат регистра ITDR  (лимит, ст. и мл. слово смещения)
    idt     label   word;Метка начала idt
    idt_begin   = $
;------------------------------------------------------------------------	
    irpc    index, 0123456789ABCDEF
    	;создаем массив из структур struct_interrupt_deskriptor
        idt_0&index struct_interrupt_deskriptor <0, code_for_protected_mode_descriptor, 0, access_trap, 0>        
	endm    ; 00...0F   (1--адрес обработчика,  2--селектор кода, 3--параметры, 4--уровень доступа, 5--адрес обработчика)
;------------------------------------------------------------------------
    irpc    index, 0123456789ABCDEF     
        idt_1&index struct_interrupt_deskriptor <0, code_for_protected_mode_descriptor, 0, access_trap, 0>; 10...1F
    endm
;------------------------------------------------------------------------
    idt_keyboard struct_interrupt_deskriptor <0,code_for_protected_mode_descriptor,0,access_interrupt,0>    	
    irpc    index, 03456789ABCDEF    
        idt_2&index        struct_interrupt_deskriptor <0, code_for_protected_mode_descriptor, 0, access_interrupt, 0>; 22...2F
    endm
;------------------------------------------------------------------------	
    size_idt  =  $ - idt_begin
;------------------------------------------------------------------------
;------------------------------------------------------------------------	
    message_for_real_mode             db "================================[REAL MODE]=====================================", "$"
    message_for_protected_mode        db "==============================[PROTECTED MODE]==================================", 0
    message_for_input                 db "INPUT:",0
    message_for_get_in_protected_mode db "PRESS SPECIAL KEY FOR GET IN PROTECTED MODE:", "$"
    message_for_getch       	      db 0dh,0ah,"PRESS KEY FOR GET IN PROTECTED MODE:", "$"
	
    mask_master             db  1 dup(?) ;Значение регистра масок ведущего контроллера
    mask_slave              db  1 dup(?) ;Значение регистра масок ведомого контроллера
    scan_code               db  1 dup(?)       
    buffer_for_scan_code    db  2 dup(' '),0      
    input_key           db  1 dup(?)  
	size_data   = $ - data_begin             ;Размер сегмента данных 
;------------------------------------------------------------------------
data    ends
;****************************************************************************
;Сегмент стека реального/защищенного режима
segment_stack segment para stack
    db  1000h dup(?)
segment_stack  ends
end start