sseg segment stack
    db 512 DUP (?)
sseg ends    

data segment  
    intro db "Enter string...", 0Dh,0Ah,"$"
    first_substr db 0Dh,0Ah, "Enter first substr: ",0Dh,0Ah,"$"
    second_substr db 0Dh,0Ah, "Enter second substr: ",0Dh,0Ah,"$" 
    answer db 0Dh,0Ah, "Answer:  ",0Dh,0Ah,"$"
    lengthError db "Length error",0Dh,0Ah,"$"
    string db 202 dup("$")
    find_str db 202 dup("$")
    change_str db 202 dup("$") 
    stringLength db 200 
data ends 

code segment
     
start: 
                 
    mov ax,     data    ;set data segment for string
    mov ds, ax
    mov es,ax  

    lea dx, intro 
    call output   
  
    mov al, stringLength    ;load max string length in al
    mov [string], al        ;load max string length 
    mov [string + 1], 0     ;null fact length 
    lea dx, string           
    call input

    lea dx, first_substr 
    call output 
    
    mov [find_str], 200     ;load max string length in first byte
    mov [find_str + 1], 0   ;load max string length
    lea dx, find_str        ;load string to dx 
    call input

    lea dx, second_substr 
    call output 
    
    mov [change_str], 200    ;load max string length
    mov [change_str + 1], 0  ;load max string length
    lea dx, change_str       ;load string to dx 
    call input

    xor bx,bx
    xor cx,cx
       
go: 
    mov cl,bl               ;start search after last replacement
    mov ch,bl               ;we will start put change_str start with ch, because ch pointing after replaced substr(see pasteStr)
    
    lea si,string           ;set string in si
    add si,2                ;moving si ptr to first string element
    add si,bx               ;moving si ptr to fisrt string element
    
    lea di,string           ;set string in di
    add di,2                ;moving si ptr to first string element
    add di,bx               ;moving si ptr to fisrt string element   

    lea bx,find_str         ;set find_str in bx
    add bx,2                ;moving bx ptr to fisrt string character

    mov al, [string + 1]    ;saving fact string length into al 
    mov stringLength, al    ;saving fact length
    
    mov dh,[find_str+1]     ;saving fact length of find_str
    mov dl, cl
                 
    xor ax,ax
    
    cld
    
find_equal:
    cmp stringLength, cl    ;check abroad
    jbe finish              ;if went to the abroad not found finish
    lodsb                   ;get sym from si in al and inc si
    cmp byte ptr[bx], al    ;compare symbols
    je l_equal              ;if equal go l_equal
    jmp not_equal           ;else not_equal

l_equal:
    inc cl          ;inc string iterator
    inc bx          ;go to the next sym in find_str
    inc ah          ;num of equal sym
    
    cmp dh,ah       ;if we find substring
    je add_str      ;go change substr
    jmp find_equal  ;if not found all substr continue finding

not_equal:
    inc di                                
    mov si,di
    inc dl
    mov cl,dl
    
    mov ch,cl           ;remember start of changable str
    xor ah,ah           ;ah=0 num of equal sym
    lea bx,find_str     ;reset bx pointer
    add bx,2            ;point on first sym
    jmp find_equal
    
add_str:
    push ax
    push bx   
    xor ax,ax
    xor bx,bx
    
    mov al, stringLength    ;check if we trying paste substr and our string will be more than 200 symbols
    
    mov bl, [find_str+1]
    sub ax, bx
    mov bl, [change_str+1]   
    add ax, bx
    
    cmp ax, 200             ;if(stringLength - findStrLength + changeStr < 200)
    jl continueWorkWithStr
    
    lea dx,answer
    call output
    
    lea dx,lengthError
    call output
    
    mov ax,4c00h
    int 21h
    
continueWorkWithStr:
    pop bx 
    pop ax            ;if all good, recover AX
    
    std               ;set flag of direction
    
    lea bx,string       ;set string
    mov al,stringLength ;save string length
    add al,1            ;add 1 because str[2]==(first string element)(first two bytes service)
                        ;(if len 5, str[5] prelast sym, hence we need to add +1 for stay on last sym)
    xor ah,ah           ;null ah
    add bx,ax           ;move pointer on last sym in string
                        
    mov si,bx           ;move stringPointer in si
    mov dh, stringLength;remember string length in dh
    sub dh,cl           ;num of elem in stack
    dec stringLength    ;dec length, because numeration start from zero. example "qwerty" length=6, but 'y' has 5 address
    jmp pushInStack     ;push excess sym after substr in stack
      
pushInStack:

    cmp stringLength,cl ;while(length>=cl) cl - point on first substr sym 
    ja addItem          ;unsigned above
    cmp stringLength,cl ;jbne - doesn't work in equal case 
    je addItem          ;if equal
    jmp pasteStr        ;if add all sym in stack, start to pasteStr
    
addItem:

    std                 ;add in stack and movsb
    lodsb               ;get sym from string and string++
    xor ah,ah
    push ax
    dec stringLength
    jmp pushInStack     

pasteStr:

    cld
    
    lea si,change_str
    add si,2   
    
    xor ax,ax
    mov al,ch              ;start point where we will put syms
    
    lea di,string
    add di,2
    add di, ax             ;move stringPointer on sym, where we will put changeSubstr
    
    add al,[change_str+1]  ;num of sym before substr + num of sym change_str
    xor bx,bx
    mov bl,al              ;store index, after inserted substr
    add al,dh              ;and + num sym in stack
    

    mov [string+1],al      ;change fact length in str
    
    xor cx,cx
    mov cl,[change_str+1]
     
    rep movsb              ;move sym from si to di
    
    mov cl,dh              ;return num of elem in stack
     
getFromStack:      

    cmp cl,0               ;if no string elements in stack, case: replace lasts symbols in string(we push in stack nothing)
    jne continueGetFromStack
    mov si,di              ;because in finish we mov di,si 
                           ;(because di point to after replaced substr and si point higher or lower, previous case)
    jmp finish       
    
continueGetFromStack:
    
    xor ax,ax
    pop ax
    
    cld
    stosb                  ;put sym from al to di and di++
    loop getFromStack
    
    jmp go        
                    
input proc near
    mov ah,0Ah
    int 21h
    ret
input endp

output proc near
    mov ah,9
    int 21h
    ret
output endp

finish: 

    xor ax,ax         
    mov ax,'$'
    mov di,si             ;si point to after last symbol
    stosb                 ;put terminator in the end of string
    
    lea dx,answer
    call output
    
    lea dx,string
    add dx,2
    call output
    
    mov ax,4c00h
    int 21h
end start

code ends
