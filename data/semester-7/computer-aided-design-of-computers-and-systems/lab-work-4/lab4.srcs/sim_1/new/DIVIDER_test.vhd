----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 31.10.2019 23:03:28
-- Design Name: 
-- Module Name: DIVIDER_test - Behavioral
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

entity DIVIDER_test is
--  Port ( );
end DIVIDER_test;

architecture Behavioral of DIVIDER_test is
    component DIVIDER
        port (
            CLK_IN: in std_logic;
            CLK_OUT: out std_logic);
    end component;
    
    signal CLK_IN: std_logic := '0';
    signal CLK_OUT: std_logic;
    
    constant CLK_PERIOD: time := 2 ps;  
begin
    test: divider port map (CLK_IN => CLK_IN, CLK_OUT => CLK_OUT);
    
    CLK_IN <= not CLK_IN after CLK_PERIOD / 2;
end Behavioral;
