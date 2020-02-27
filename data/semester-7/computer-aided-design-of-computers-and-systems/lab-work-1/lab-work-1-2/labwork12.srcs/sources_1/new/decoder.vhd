----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 16.09.2019 18:01:36
-- Design Name: 
-- Module Name: decoder - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity decoder is
Port (         I1 : in STD_LOGIC;
               I2 : in STD_LOGIC;
               I3 : in STD_LOGIC;
               I4 : in STD_LOGIC;
               I5 : in STD_LOGIC;
               I6 : in STD_LOGIC;
               I7 : in STD_LOGIC;
               I8 : in STD_LOGIC;
               I9 : in STD_LOGIC;
               Y0 : out STD_LOGIC;
               Y1 : out STD_LOGIC;
               Y2 : out STD_LOGIC;
               Y3 : out STD_LOGIC);
end decoder;

architecture Behavioral of decoder is
signal FlipFlopInput : STD_LOGIC;
signal FlipFlopOut : STD_LOGIC;
signal x13 : STD_LOGIC:='0';
signal x12 : STD_LOGIC:='0';
signal x11 : STD_LOGIC:='0';
signal x10 : STD_LOGIC:='0';
signal x9 : STD_LOGIC:='0';
signal x8 : STD_LOGIC:='0';
signal x7 : STD_LOGIC:='0';
signal x6 : STD_LOGIC:='0';
signal x5 : STD_LOGIC:='0';
signal x4 : STD_LOGIC:='0';
signal x3 : STD_LOGIC:='0';
signal x2 : STD_LOGIC:='0';
signal x1 : STD_LOGIC:='0';
signal x0 : STD_LOGIC:='0';

begin

process(I1,I2,I4,I9)
    begin
        if((not I1 and I2 and I4 and I6 and x13)='1') then
            x0<='1';
            else x0<='0';
            end if;
end process;

process(I3,I4,I6,x13)
    begin
    if((not I3 and I4 and I6 and x13)='1') then
         x1<='1';
    else x1<='0';
    end if;
    end process;
    
process(I5,I6,x13)
    begin
    if((not I5 and I6 and x13)='1') then
         x2<='1';
    else x2<='0';
    end if;
    end process;
    
process(I7,x13)
    begin
    if((not I7 and x13)='1') then     
         x3<='1';
    else x3<='0';
    end if;
    end process;
    
process(I9)
    begin
    if((not I9)='1') then
         x4<='1';
    else x4<='0';
    end if;
    end process;

process(I2,I4,I5,x13)
    begin
    if((not I2 and I4 and I5 and x13)='1') then
         x5<='1';
    else x5<='0';
    end if;
    end process;
    
process(I3,I4,I5,x13)
    begin
    if((not I3 and I4 and I5 and x13)='1') then
         x6<='1';
    else x6<='0';
    end if;
    end process;
    
process(I6,x13)
    begin
    if((not I6 and x13)='1') then
         x7<='1';
    else x7<='0';
    end if;
    end process;
    
process(I7,x13)
    begin
    if((not I7 and x13)='1') then
         x8<='1';
    else x8<='0';
    end if;
    end process;

process(I4,x13)
    begin
    if((not I4 and x13)='1') then
         x9<='1';
    else x9<='0';
    end if;
    end process;
    
process(I5,x13)
    begin
   if((not I5 and x13)='1') then
        x10<='1';
   else x10<='0';
   end if;
   end process;
   
process(I6,x13)
   begin
   if((not I6 and x13)='1') then
        x11<='1';
   else x11<='0';
   end if;
   end process;
   
process(I7,x13)
   begin
   if((not I7 and x13)='1') then
        x12<='1';
   else x12<='0';
   end if;
   end process;   
           
process(I8,I9)
    begin
		if ((not I8 or not I9)='1') then
		  x13 <= '0';
		else
		  x13 <= '1';
		end if;
end process;

Y0 <= not(x0 or x1 or x2 or x3 or x4);
Y1 <= not(x5 or x6 or x7 or x8);
Y2 <= not(x9 or x10 or x11 or x12);
Y3 <= x13;

end Behavioral;
