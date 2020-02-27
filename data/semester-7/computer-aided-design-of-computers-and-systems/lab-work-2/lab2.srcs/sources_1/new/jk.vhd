----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 09/17/2019 04:20:26 PM
-- Design Name: 
-- Module Name: jk - Behavioral
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

entity jk is
    Port ( 
           NOT_R : in STD_LOGIC;
           NOT_S : in STD_LOGIC;
           J : in STD_LOGIC;
           K : in STD_LOGIC;
           CLK : in STD_LOGIC;
           Q : out STD_LOGIC := '0';
           NOT_Q : out STD_LOGIC := '1');
end jk;

architecture Behavioral of jk is

begin
    process(CLK, NOT_R, NOT_S)
    variable current_state: std_logic :='0';
    begin
        if  NOT_R='0' then
            current_state :='0';
        elsif NOT_S='0' then
            current_state :='1';
        elsif CLK'event and CLK= '1' then
            if J='1' and K='1' then
                current_state := not current_state;
            elsif J='1' then
                current_state := '1';
            elsif K='1' then
                current_state := '0';       
            end if;
        end if;
     Q <= current_state;
     NOT_Q <= not current_state;  
    end process;  
end Behavioral;
