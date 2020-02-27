----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 01.11.2019 00:24:51
-- Design Name: 
-- Module Name: schema - Behavioral
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

entity schema is
    Port ( 
        ledsmain: out std_logic_vector(3 downto 0);
        ledsboard: out std_logic_vector(3 downto 0);
        pushbuttons: in std_logic_vector(4 downto 0);
        dipswitch: in std_logic_vector(3 downto 0);
        --clock diff pair
        sysclk_p: in std_logic;
        sysclk_n: in std_logic
    );
end schema;

architecture Behavioral of schema is
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
    
    component ibufds
        Port (
            i, ib : in std_logic; 
            o : out std_logic);
    end component;
    
    component DIVIDER is
        Port ( CLK_IN : in STD_LOGIC;
               CLK_OUT : out STD_LOGIC);
    end component;
    
    signal CCLR: std_logic := '0';
    signal CCKEN: std_logic := '0';
    signal CLOAD: std_logic := '0';
    signal CCK : std_logic;
    signal CCK_NO_DIV : std_logic;

    signal RS1out: std_logic := '0';
    signal RS2out: std_logic := '0';
    signal RS3out: std_logic := '0';
    signal RS4out: std_logic := '0';
    signal RS5out: std_logic := '0';   
    signal RS6out: std_logic := '0';
    signal RS7out: std_logic := '0';
    signal RS8out: std_logic := '0';
    signal RCO: std_logic := '0';
    signal mode: std_logic := '0';
begin
    CCLR <= not pushbuttons(0);
    CCKEN <= not pushbuttons(1);
    CLOAD <= not pushbuttons(2);
    mode <= not pushbuttons(3);
    
    counter1: counter port map(
        CCLR => CCLR,
        CCKEN => CCKEN,
        CCK => CCK,
        CLOAD => CLOAD,
        RCK => CCK,
        A => dipswitch(0),
        B => dipswitch(0),
        C => dipswitch(1),
        D => dipswitch(1),
        E => dipswitch(2),
        F => dipswitch(2),
        G => dipswitch(3),
        H => dipswitch(3),
        RS1out => RS1out,
        RS2out => RS2out,
        RS3out => RS3out,
        RS4out => RS4out,
        RS5out => RS5out,
        RS6out => RS6out,
        RS7out => RS7out,
        RS8out => RS8out,
        RCO => RCO
    );
    
    ledsmain(0) <= mode and RS1out;
    ledsmain(1) <= mode and RS2out;
    ledsmain(2) <= mode and RS3out;
    ledsmain(3) <= mode and RS4out;
    ledsboard(0) <= mode and RS5out;
    ledsboard(1) <= mode and RS6out;
    ledsboard(2) <= mode and RS7out;
    ledsboard(3) <= (mode and RS8out) or (not mode and RCO);
    
    buffds: ibufds port map (
        i => sysclk_p, 
        ib => sysclk_n, 
        o => CCK_NO_DIV
    );
    
    div: DIVIDER port map (
        CLK_IN => CCK_NO_DIV, 
        CLK_OUT => CCK
    );
end Behavioral;
