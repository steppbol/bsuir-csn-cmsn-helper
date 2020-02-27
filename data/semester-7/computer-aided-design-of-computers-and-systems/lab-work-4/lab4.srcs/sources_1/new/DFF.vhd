----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 31.10.2019 22:44:41
-- Design Name: 
-- Module Name: DFF - Behavioral
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

entity DFF is
    Port ( D : in STD_LOGIC;
       CLK : in STD_LOGIC;
       Q : out STD_LOGIC;
       NOT_Q : out STD_LOGIC);
end DFF;

architecture Behavioral of DFF is
    component JK is
    Port ( 
        NOT_R : in STD_LOGIC;
        NOT_S : in STD_LOGIC;
        J : in STD_LOGIC;
        K : in STD_LOGIC;
        CLK : in STD_LOGIC;
        Q : out STD_LOGIC;    
        NOT_Q : out STD_LOGIC);
    end component;

    signal NOT_CLK: STD_LOGIC := '0';
    signal NOT_D: STD_LOGIC;
begin
    NOT_CLK <= not CLK;
    NOT_D <= not D;

    JK_trigger: JK port map(
        NOT_R => '1',
        NOT_S => '1',
        J => D,
        K => NOT_D,
        CLK => NOT_CLK,
        Q => Q,
        NOT_Q => NOT_Q    
    );
end Behavioral;
