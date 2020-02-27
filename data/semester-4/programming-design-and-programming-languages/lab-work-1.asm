  
    .model tiny  
    .data  
    hello db "Hello, world!",0Dh,0Ah,'$'
    .code 
start:
    mov bx, @data        
    
    mov ds, bx
    mov dx, offset hello

    mov ah, 9h
    int 21h

    mov ax, 4c00h
    int 21h   
end start 