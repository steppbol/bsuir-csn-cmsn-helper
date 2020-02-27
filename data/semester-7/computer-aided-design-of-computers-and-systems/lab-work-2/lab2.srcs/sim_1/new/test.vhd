----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 29.09.2019 17:57:32
-- Design Name: 
-- Module Name: test - Behavioral
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

entity test is
--  Port ( );
end test;

architecture Behavioral of test is
    component counter
        Port ( CCLR : in STD_LOGIC;
               CCKEN : in STD_LOGIC;
               CCK : in STD_LOGIC;
               CLOAD : in STD_LOGIC;
               RCK : in STD_LOGIC;
               A : in STD_LOGIC;
               B : in STD_LOGIC;
               C : in STD_LOGIC;
               D : in STD_LOGIC;
               E : in STD_LOGIC;
               F : in STD_LOGIC;
               G : in STD_LOGIC;
               H : in STD_LOGIC;
               RCO : out STD_LOGIC;
               RS1out : out STD_LOGIC;
               RS2out : out STD_LOGIC;
               RS3out : out STD_LOGIC;
               RS4out : out STD_LOGIC;
               RS5out : out STD_LOGIC;
               RS6out : out STD_LOGIC;
               RS7out : out STD_LOGIC;
               RS8out : out STD_LOGIC);
    end component;
    signal CCLR : STD_LOGIC := '0';
    signal CCKEN : STD_LOGIC := '1';
    signal CCK : STD_LOGIC := '0';
    signal CLOAD : STD_LOGIC := '1';
    signal RCK : STD_LOGIC := '0';
    signal A : STD_LOGIC := '0';
    signal B : STD_LOGIC := '0';
    signal C : STD_LOGIC := '1';
    signal D : STD_LOGIC := '1';
    signal E : STD_LOGIC := '1';
    signal F : STD_LOGIC := '1';
    signal G : STD_LOGIC := '1';
    signal H : STD_LOGIC := '1';
    signal RCO : STD_LOGIC;
    signal RS1out : STD_LOGIC;
    signal RS2out : STD_LOGIC;
    signal RS3out : STD_LOGIC;
    signal RS4out : STD_LOGIC;
    signal RS5out : STD_LOGIC;
    signal RS6out : STD_LOGIC;
    signal RS7out : STD_LOGIC;
    signal RS8out : STD_LOGIC;
begin
    uut: counter port map (
        CCLR => CCLR,
        CCKEN => CCKEN,
        CCK => CCK,
        CLOAD => CLOAD,
        RCK => RCK,
        A => A,
        B => B,
        C => C,
        D => D,
        E => E,
        F => F,
        G => G,
        H => H,
        RCO => RCO,
        RS1out => RS1out,
        RS2out => RS2out,
        RS3out => RS3out,
        RS4out => RS4out,
        RS5out => RS5out,
        RS6out => RS6out,
        RS7out => RS7out,
        RS8out => RS8out
    );
    
    CCLR <= '1' after 128ns;
    RCK  <= '1' after 150ns, '0' after 200ns;
    CLOAD <= '0' after 250ns, '1' after 300ns;
    CCKEN <= '0' after 360ns;
    CCK <= not CCK after 16ns;
end Behavioral;
