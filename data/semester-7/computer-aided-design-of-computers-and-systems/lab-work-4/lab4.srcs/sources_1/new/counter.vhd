----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 31.10.2019 22:49:37
-- Design Name: 
-- Module Name: counter - Behavioral
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

entity counter is
    Port (  CCLR : in STD_LOGIC;
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
            RS8out : out STD_LOGIC
    );
end counter;

architecture Behavioral of counter is
    component DFF is
     Port ( 
          D : in STD_LOGIC;
          CLK : in STD_LOGIC;
          Q : out STD_LOGIC;    
          NOT_Q : out STD_LOGIC);
end component;

component TFF is
    Port (
          NOT_R : in STD_LOGIC;
          NOT_S : in STD_LOGIC; 
          CLK : in STD_LOGIC;
          Q : out STD_LOGIC);
end component;
    signal NOT_RCK : STD_LOGIC;

    signal D1out : STD_LOGIC;
    signal D2out : STD_LOGIC;
    signal D3out : STD_LOGIC;
    signal D4out : STD_LOGIC;
    signal D5out : STD_LOGIC;
    signal D6out : STD_LOGIC;
    signal D7out : STD_LOGIC;
    signal D8out : STD_LOGIC;
    signal notD1out : STD_LOGIC;
    signal notD2out : STD_LOGIC;
    signal notD3out : STD_LOGIC;
    signal notD4out : STD_LOGIC;
    signal notD5out : STD_LOGIC;
    signal notD6out : STD_LOGIC;
    signal notD7out : STD_LOGIC;
    signal notD8out : STD_LOGIC;

    signal S1_in : STD_LOGIC;
    signal S2_in : STD_LOGIC;
    signal S3_in : STD_LOGIC;
    signal S4_in : STD_LOGIC;
    signal S5_in : STD_LOGIC;
    signal S6_in : STD_LOGIC;
    signal S7_in : STD_LOGIC;
    signal S8_in : STD_LOGIC;

    signal R1_in : STD_LOGIC;
    signal R2_in : STD_LOGIC;
    signal R3_in : STD_LOGIC;
    signal R4_in : STD_LOGIC;
    signal R5_in : STD_LOGIC;
    signal R6_in : STD_LOGIC;
    signal R7_in : STD_LOGIC;
    signal R8_in : STD_LOGIC;

    signal RS_CLK : STD_LOGIC := '0';
    signal RS1_CLK : STD_LOGIC := '0';
    signal RS2_CLK : STD_LOGIC := '0';
    signal RS3_CLK : STD_LOGIC := '0';
    signal RS4_CLK : STD_LOGIC := '0';
    signal RS5_CLK : STD_LOGIC := '0';
    signal RS6_CLK : STD_LOGIC := '0';
    signal RS7_CLK : STD_LOGIC := '0';
    signal RS8_CLK : STD_LOGIC := '0';

    signal RS1_out : STD_LOGIC := '0';
    signal RS2_out : STD_LOGIC := '0';
    signal RS3_out : STD_LOGIC := '0';
    signal RS4_out : STD_LOGIC := '0';
    signal RS5_out : STD_LOGIC := '0';
    signal RS6_out : STD_LOGIC := '0';
    signal RS7_out : STD_LOGIC := '0';
    signal RS8_out : STD_LOGIC := '0';
begin
    NOT_RCK <= not RCK;
  
    D1: DFF port map (
        D => A,
        CLK => NOT_RCK,
        Q => D1out,
        NOT_Q => notD1out
    );
    D2: DFF port map (
        D => B,
        CLK => NOT_RCK,
        Q => D2out,
        NOT_Q => notD2out
    );
    D3: DFF port map (
        D => C,
        CLK => NOT_RCK,
        Q => D3out,
        NOT_Q => notD3out
    );
    D4: DFF port map (
        D => D,
        CLK => NOT_RCK,
        Q => D4out,
        NOT_Q => notD4out
    );
    D5: DFF port map (
        D => E,
        CLK => NOT_RCK,
        Q => D5out,
        NOT_Q => notD5out
    );
    D6: DFF port map (
        D => F,
        CLK => NOT_RCK,
        Q => D6out,
        NOT_Q => notD6out
    );
    D7: DFF port map (
        D => G,
        CLK => NOT_RCK,
        Q => D7out,
        NOT_Q => notD7out
    );
    D8: DFF port map (
        D => H,
        CLK => NOT_RCK,
        Q => D8out,
        NOT_Q => notD8out
    );
    
    S1_in <= not (D1out and not CLOAD);
    S2_in <= not (D2out and not CLOAD);
    S3_in <= not (D3out and not CLOAD);
    S4_in <= not (D4out and not CLOAD);
    S5_in <= not (D5out and not CLOAD);
    S6_in <= not (D6out and not CLOAD);
    S7_in <= not (D7out and not CLOAD);
    S8_in <= not (D8out and not CLOAD);

    R1_in <= not (not CCLR or (notD1out and not CLOAD));
    R2_in <= not (not CCLR or (notD2out and not CLOAD));
    R3_in <= not (not CCLR or (notD3out and not CLOAD));
    R4_in <= not (not CCLR or (notD4out and not CLOAD));
    R5_in <= not (not CCLR or (notD5out and not CLOAD));
    R6_in <= not (not CCLR or (notD6out and not CLOAD));
    R7_in <= not (not CCLR or (notD7out and not CLOAD));
    R8_in <= not (not CCLR or (notD8out and not CLOAD));
    
    RS_CLK <= not (CCK and not CCKEN);

    RS1_CLK <= RS_CLK;
    RS2_CLK <= RS_CLK and RS1_out;
    RS3_CLK <= RS_CLK and RS1_out and RS2_out;
    RS4_CLK <= RS_CLK and RS1_out and RS2_out and RS3_out;
    RS5_CLK <= RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out;
    RS6_CLK <= RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out and RS5_out;
    RS7_CLK <= RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out and RS5_out and RS6_out;
    RS8_CLK <= RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out and RS5_out and RS6_out and RS7_out;

    RCO <= not (RS1_out and RS2_out and RS3_out and RS4_out and RS5_out and RS6_out and RS7_out and RS8_out);

    RS1out <= RS1_out;
    RS2out <= RS2_out;
    RS3out <= RS3_out;
    RS4out <= RS4_out;
    RS5out <= RS5_out;
    RS6out <= RS6_out;
    RS7out <= RS7_out;
    RS8out <= RS8_out;

    TFF1: TFF port map (
        NOT_S => S1_in,
        NOT_R => R1_in,
        CLK => RS1_CLK,
        Q => RS1_out
    );
    TFF2: TFF port map (
        NOT_S => S2_in,
        NOT_R => R2_in,
        CLK => RS2_CLK,
        Q => RS2_out
    );
    TFF3: TFF port map (
        NOT_S => S3_in,
        NOT_R => R3_in,
        CLK => RS3_CLK,
        Q => RS3_out
    );
    TFF4: TFF port map (
        NOT_S => S4_in,
        NOT_R => R4_in,
        CLK => RS4_CLK,
        Q => RS4_out
    );
    TFF5: TFF port map (
        NOT_S => S5_in,
        NOT_R => R5_in,
        CLK => RS5_CLK,
        Q => RS5_out
    );
    TFF6: TFF port map (
        NOT_S => S6_in,
        NOT_R => R6_in,
        CLK => RS6_CLK,
        Q => RS6_out
    );
    TFF7: TFF port map (
        NOT_S => S7_in,
        NOT_R => R7_in,
        CLK => RS7_CLK,
        Q => RS7_out
    );
    TFF8: TFF port map (
        NOT_S => S8_in,
        NOT_R => R8_in,
        CLK => RS8_CLK,
        Q => RS8_out
    );    
end Behavioral;
