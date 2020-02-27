----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 28.09.2019 20:25:05
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
end counter;

architecture Behavioral of counter is
    component jk is
         Port ( 
              NOT_R : in STD_LOGIC := '1';
              NOT_S : in STD_LOGIC := '1';
              J : in STD_LOGIC;
              K : in STD_LOGIC;
              CLK : in STD_LOGIC;
              Q : out STD_LOGIC;    
              NOT_Q : out STD_LOGIC);
    end component;
    signal NOT_A : STD_LOGIC;
    signal NOT_B : STD_LOGIC;
    signal NOT_C : STD_LOGIC;
    signal NOT_D : STD_LOGIC;
    signal NOT_E : STD_LOGIC;
    signal NOT_F : STD_LOGIC;
    signal NOT_G : STD_LOGIC;
    signal NOT_H : STD_LOGIC;
    
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
    
    signal RS_CLK : STD_LOGIC;
    signal RS1_CLK : STD_LOGIC;
    signal RS2_CLK : STD_LOGIC;
    signal RS3_CLK : STD_LOGIC;
    signal RS4_CLK : STD_LOGIC;
    signal RS5_CLK : STD_LOGIC;
    signal RS6_CLK : STD_LOGIC;
    signal RS7_CLK : STD_LOGIC;
    signal RS8_CLK : STD_LOGIC;
    
    signal RS1_out : STD_LOGIC;
    signal RS2_out : STD_LOGIC;
    signal RS3_out : STD_LOGIC;
    signal RS4_out : STD_LOGIC;
    signal RS5_out : STD_LOGIC;
    signal RS6_out : STD_LOGIC;
    signal RS7_out : STD_LOGIC;
    signal RS8_out : STD_LOGIC;
begin
    NOT_A <= not A;
    NOT_B <= not B;
    NOT_C <= not C;
    NOT_D <= not D;
    NOT_E <= not E;
    NOT_F <= not F;
    NOT_G <= not G;
    NOT_H <= not H;
    
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
    RS1_CLK <= not RS_CLK;
    RS2_CLK <= not (RS_CLK and RS1_out);
    RS3_CLK <= not (RS_CLK and RS1_out and RS2_out);
    RS4_CLK <= not (RS_CLK and RS1_out and RS2_out and RS3_out);
    RS5_CLK <= not (RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out);
    RS6_CLK <= not (RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out and RS5_out);
    RS7_CLK <= not (RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out and RS5_out and RS6_out);
    RS8_CLK <= not (RS_CLK and RS1_out and RS2_out and RS3_out and RS4_out and RS5_out and RS6_out and RS7_out);
    
    RCO <= not (RS1_out and RS2_out and RS3_out and RS4_out and RS5_out and RS6_out and RS7_out and RS8_out);
    
    RS1out <= RS1_out;
    RS2out <= RS2_out;
    RS3out <= RS3_out;
    RS4out <= RS4_out;
    RS5out <= RS5_out;
    RS6out <= RS6_out;
    RS7out <= RS7_out;
    RS8out <= RS8_out;
    
    D1: jk port map (
        J => A,
        K => NOT_A,
        CLK => RCK,
        Q => D1out,
        NOT_Q => notD1out
    );
    D2: jk port map (
        J => B,
        K => NOT_B,
        CLK => RCK,
        Q => D2out,
        NOT_Q => notD2out
    );
    D3: jk port map (
        J => C,
        K => NOT_C,
        CLK => RCK,
        Q => D3out,
        NOT_Q => notD3out
    );
    D4: jk port map (
        J => D,
        K => NOT_D,
        CLK => RCK,
        Q => D4out,
        NOT_Q => notD4out
    );
    D5: jk port map (
        J => E,
        K => NOT_E,
        CLK => RCK,
        Q => D5out,
        NOT_Q => notD5out
    );
    D6: jk port map (
        J => F,
        K => NOT_F,
        CLK => RCK,
        Q => D6out,
        NOT_Q => notD6out
    );
    D7: jk port map (
        J => G,
        K => NOT_G,
        CLK => RCK,
        Q => D7out,
        NOT_Q => notD7out
    );
    D8: jk port map (
        J => H,
        K => NOT_H,
        CLK => RCK,
        Q => D8out,
        NOT_Q => notD8out
    );
    RS1: jk port map (
        NOT_S => S1_in,
        NOT_R => R1_in,
        J => RS1_CLK,
        K => RS1_CLK,
        CLK => RS1_CLK,
        Q => RS1_out
    );
    RS2: jk port map (
        NOT_S => S2_in,
        NOT_R => R2_in,
        J => RS2_CLK,
        K => RS2_CLK,
        CLK => RS2_CLK,
        Q => RS2_out
    );
    RS3: jk port map (
        NOT_S => S3_in,
        NOT_R => R3_in,
        J => RS3_CLK,
        K => RS3_CLK,
        CLK => RS3_CLK,
        Q => RS3_out
    );
    RS4: jk port map (
        NOT_S => S4_in,
        NOT_R => R4_in,
        J => RS4_CLK,
        K => RS4_CLK,
        CLK => RS4_CLK,
        Q => RS4_out
    );
    RS5: jk port map (
        NOT_S => S5_in,
        NOT_R => R5_in,
        J => RS5_CLK,
        K => RS5_CLK,
        CLK => RS5_CLK,
        Q => RS5_out
    );
    RS6: jk port map (
        NOT_S => S6_in,
        NOT_R => R6_in,
        J => RS6_CLK,
        K => RS6_CLK,
        CLK => RS6_CLK,
        Q => RS6_out
    );
    RS7: jk port map (
        NOT_S => S7_in,
        NOT_R => R7_in,
        J => RS7_CLK,
        K => RS7_CLK,
        CLK => RS7_CLK,
        Q => RS7_out
    );
    RS8: jk port map (
        NOT_S => S8_in,
        NOT_R => R8_in,
        J => RS8_CLK,
        K => RS8_CLK,
        CLK => RS8_CLK,
        Q => RS8_out
    );
    
end Behavioral;
