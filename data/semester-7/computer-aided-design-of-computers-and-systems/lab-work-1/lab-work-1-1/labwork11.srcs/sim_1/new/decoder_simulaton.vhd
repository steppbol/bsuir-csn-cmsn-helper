----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 16.09.2019 17:48:43
-- Design Name: 
-- Module Name: decoder_simulaton - Behavioral
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

entity decoder_simulaton is
end;

architecture Behavioral of decoder_simulaton is

component decoder
Port (
I1: in STD_LOGIC;
I2: in STD_LOGIC;
I3: in STD_LOGIC;
I4: in STD_LOGIC;
I5: in STD_LOGIC;
I6: in STD_LOGIC;
I7: in STD_LOGIC;
I8: in STD_LOGIC;
I9: in STD_LOGIC;
Y0: out STD_LOGIC;
Y1: out STD_LOGIC;
Y2: out STD_LOGIC;
Y3: out STD_LOGIC);
end component;

signal I1: STD_LOGIC:= '0';
signal I2: STD_LOGIC:= '0';
signal I3: STD_LOGIC:= '0';
signal I4: STD_LOGIC:= '0';
signal I5: STD_LOGIC:= '0';
signal I6: STD_LOGIC:= '0';
signal I7: STD_LOGIC:= '0';
signal I8: STD_LOGIC:= '0';
signal I9: STD_LOGIC:= '0';
signal Y0: STD_LOGIC;
signal Y1: STD_LOGIC;
signal Y2: STD_LOGIC;
signal Y3: STD_LOGIC;

begin
uut: decoder port map (
I1 => I1,
I2 => I2,
I3 => I3,
I4 => I4,
I5 => I5,
I6 => I6,
I7 => I7,
I8 => I8,
I9 => I9,
Y0 => Y0,
Y1 => Y1,
Y2 => Y2,
Y3 => Y3);

I1 <= not I1 after 4ns;
I2 <= not I2 after 8ns;
I3 <= not I3 after 16ns;
I4 <= not I4 after 32ns;
I5 <= not I5 after 64ns;
I6 <= not I6 after 128ns;
I7 <= not I7 after 256ns;
I8 <= not I8 after 512ns;
I9 <= not I9 after 1024ns;

end Behavioral;
