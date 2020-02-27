.model	small
.stack	100h
.data
.386            
MaxArrayLength              equ 30            
            
ArrayLength                 dw  ?
InputArrayLengthMsgStr      db  'Input array length: $'
maxNull                     db 0Ah,0Dh,'All infinity','$',0Ah,0Dh                                
ErrorInputMsgStr            db  0Ah,0Dh,'Incorrect value!',0Ah,0Dh, '$' 
ErrorInputArrayLengthMsgStr db  0Ah,0Dh,'Array length should be not less than 0 and not bigger than 30!', '$'
                                
InputMsgStr                 db 0Ah, 0Dh,'Input element (-32 768..32 767) : $'    
CurrentEl                   db  2 dup(0)

Answer                      db  20 dup('$'), 0Ah,0Dh, '$'
                                
NumBuffer                   dw 0

NumLength                   db 7
EnterredNum                 db 9 dup('$')              

nextStr                     db 0Ah,0Dh,'$'       
ten                         dw 10
two                         dw 2	
precision                   db  5             
minus                       db  0  
Max                         dw  0
MaxTemp                     dw  0
Array                       dw  MaxArrayLength dup (0)
Temp                        dd 0
OutputArray                 dd MaxArrayLength dup(0) 
                                
                              
.code      

start:                            ;
mov	ax,@data                      ;
mov	ds,ax                         ;
                                  ;
xor	ax,ax                         ;
                               ;
call inputMas                     ;
call FindMax                      ;
call MakeNormal
                                  ;
inputMas proc                       
    call inputArrayLength         ;
    call inputArray               ;                      
    ret                           ;
endp     

inputArrayLength proc near
    mov cx, 1           
    inputArrayLengthLoop:
       call ShowInputArrayLengthMsg
       push cx                    ;
       call inputElementBuff
       pop cx
       mov ArrayLength,ax
       cmp ArrayLength,0
       jle lengthError
       cmp ArrayLength,30
       jg  lengthError
                            
    loop inputArrayLengthLoop     
    ret      
endp

lengthError:
    call ErrorInput
    jmp  inputArrayLengthLoop
    
inputArray proc
    xor di,di                     
                                               
    mov cx,ArrayLength            
    inputArrayLoop:
       call ShowInputMsg
       push cx                    ;
       call inputElementBuff
       pop cx      
       
       mov Array[di], ax 

       add di,2                     
    loop inputArrayLoop           
    ret      
endp  



resetNumBuffer proc
    mov NumBuffer, 0    
    ret
endp    

inputElementBuff proc                                     
    
    xor ax,ax
    xor cx,cx
    
    mov al,NumLength
    
    mov [EnterredNum],al
    mov [EnterredNum+1],0
    lea dx,EnterredNum
    call input
    
    mov cl,[EnterredNum+1]
    lea si,EnterredNum
    add si,2
    
    xor ax,ax 
    xor bx,bx
    xor dx,dx
    mov dx,10        
    NextSym:
         xor ax,ax
         lodsb
         cmp bl,0
         je checkMinus
    
    checkSym:
         
         cmp al,'0'
         jl badNum
         cmp al,'9'
         jg badNum
         
         sub ax,'0'
         mov bx,ax
         xor ax,ax
         mov ax,NumBuffer
         
         imul dx
         jo badNum
         cmp minus,1
         je doSub
         add ax, bx
         comeBack:
         jo badNum
         mov NumBuffer,ax
         mov bx,1
         mov dx,10
         
    loop NextSym 
    
    mov ax,NumBuffer
    
    ;cmp minus,0
    ;je finish
    ;mov dx,-1
    ;mul dx
    mov minus,0
    
    
    finish: 
    call resetNumBuffer                        
    ret 
doSub:
    sub ax,bx
    jmp comeBack       
checkMinus:
    inc bl
    cmp al, '-'
    
    je SetMinus
    
    jmp checkSym
                  
SetMinus:
    mov minus,1
    dec cx
    cmp cx,0
    je badNum
    jmp NextSym
    
badNum:
    clc
    mov minus,0
    call ErrorInput
    call resetNumBuffer
    jmp inputElementBuff                            
endp
     
input proc near
    mov ah,0Ah
    int 21h
    ret
input endp

ErrorInput proc                   ;
    lea dx, ErrorInputMsgStr      ;
    mov ah, 09h                   ;
    int 21h                       ;
    ret                           ;
endp                              ;
      

ShowInputArrayLengthMsg proc
    push ax
    push dx
      
    mov ah,09h                      
    lea dx, InputArrayLengthMsgStr           
    int 21h  
    
    pop dx
    pop ax 
     
    ret
endp          
                                  ;
ShowInputMsg proc                     ;
    push ax
    push dx                      ;
                                  ;
    mov ah,09h                    ;output command
    lea dx, InputMsgStr           ;show input msg to user
    int 21h   
    
    pop dx
    pop ax                    
    ret                           
endp                        
                                  ;
FindMax proc near
    xor di,di
                          
    mov cx, ArrayLength
    mov ax,Array[di]
    add di,2
    dec cx
    cmp cx,0
    je endFind
    find:
        cmp cx,0
        je endFind
        mov dx,Array[di]
        cmp ax,dx
        jl saveMax
        add di,2
    loop find
    
    jmp endFind
    
    saveMax:
        mov ax,dx
        add di,2
        dec cx
        
        jmp find    
    endFind:
    mov Max,ax
    mov MaxTemp,ax               
    ret                           ;
endp                              ;

MakeNormal proc near
    cmp Max,0
    je  divNull
    xor cx,cx
    mov cl,byte ptr [ArrayLength]
    xor di,di
    xor si,si
    xor ax,ax
    xor dx,dx
    
    make:
        mov ax,MaxTemp
        mov Max,ax
        mov minus,0
        cmp cl,0
        je goEnd
        xor dx,dx
        mov ax, Array[si]
        xor ch,ch
        lea di,Answer
        cmp Max,0
        jg setZnak
        cmp Max,0
        jl setPlus
        return2:
        mov [di],'+'
        inc di
    makeNum: 
        cmp ch,precision
        jg saveNum 
        mov bx,ax
        
        idiv Max
        
        cmp minus,0
        jne jump
        
        
        call makeMainPart
        cmp minus,1
        je incMinus
        
        jump:
        cmp ax,0
        je increase
        
        add al,'0'
        
        mov [di],al
        inc di
        
        mov ax,dx
        imul ten
        inc ch
        jmp makeNum
        
    incMinus:
        mov ax,dx
        mul ten
        inc minus
        jmp makeNum
            
    increase:        
        add al,'0'
        
        mov [di],al
        inc di
        
        cmp minus,0
        je firstSymbol
        return:
        inc ch
                         
        mov ax,bx

        imul ten
        
        jmp makeNum
        
    saveNum:
        call output 
        add si,2
        jmp make
        
    goEnd:
        mov ax,4c00h
        int 21h    
    ret    
endp
    
makeMainPart proc near
    push dx
    push cx
    push bx
    xor cx,cx

st: 
    xor dx,dx
    idiv ten
    cmp ax,0
    jnz go1
    cmp dx,0
    jnz go1
    jmp fin    
go1:
    inc cx
    push dx
    jmp st
fin:

loop1:
    cmp cx,0
    jz ifNoMainPart
    pop bx
    add bx,'0'
    mov  [di],bx
    inc di
loop loop1

mov [di],'.'
inc di
    
jmp fin2

ifNoMainPart:
    mov [di],'0'
    inc di
    mov [di],'.'
    inc di
fin2:
    mov minus,1
    pop bx
    pop cx
    pop dx        
    ret
endp
    
setPlus:
    push ax
    mov ax,Max
    mov bx,-1
    imul bx
    mov Max,ax
    pop ax
    imul bx
    jmp return2

setZnak:
    cmp ax,0
    jge return2
    mov [di],'-'
    inc di
    mov bx,-1
    imul bx
    jmp makeNum

;firstSymbol1:
 ;       mov al,'.'
  ;      mov [di],al
   ;     inc di
    ;    jmp return1
       
firstSymbol:
        mov al,'.'
        mov [di],al
        inc di
        mov minus,1
        jmp return
;
output proc
    lea dx,nextStr
    mov ah,09h
    int 21h                       ;
    lea dx, Answer
    mov ah,09h
    int 21h
    dec cl                
    ret                           ;
endp                              ;
divNull:
lea dx,maxNull
    mov ah,09h
    int 21h
    mov ax,4c00h
    int 21h    
    ret                                ;
end	start                         ;