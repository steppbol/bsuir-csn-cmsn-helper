----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 16.09.2019 17:43:01
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
Port (     I1 : in STD_LOGIC;
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
signal x0, x1, x2, x3, x4 : STD_LOGIC;
signal x5, x6, x7, x8 : STD_LOGIC;
signal x9, x10, x11, x12, x13 : STD_LOGIC;

begin

x13 <= not(not(I8) or not(I9));
Y3 <= x13;

x0 <= not I1 and I2 and I4 and I6 and x13;
x1 <= not I3 and I4 and I6 and x13;
x2 <= not I5 and I6 and x13;
x3 <= not I7 and x13;
x4 <= not I9;
Y0 <= not(x0 or x1 or x2 or x3 or x4);

x5 <= not I2 and I4 and I5 and x13;
x6 <= not I3 and I4 and I5 and x13;
x7 <= not I6 and x13;
x8 <= not I7 and x13;
Y1 <= not(x5 or x6 or x7 or x8);

x9 <= not I4 and x13;
x10 <= not I5 and x13;
x11 <= not I6 and x13;
x12 <= not I7 and x13;
Y2 <= not(x9 or x10 or x11 or x12);

end Behavioral;
