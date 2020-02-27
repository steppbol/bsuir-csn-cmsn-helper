----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 15.10.2019 00:28:39
-- Design Name: 
-- Module Name: test_bench - Behavioral
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

entity test_bench is
--  Port ( );
end test_bench;

architecture Behavioral of test_bench is
    component counter is
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
               RS8out : out STD_LOGIC
         );
     end component;
     component counter_for_test is
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
                RS8out : out STD_LOGIC
          );
    end component;
    signal CCLR : STD_LOGIC := '0';
    signal CCKEN : STD_LOGIC := '1';
    signal CCK : STD_LOGIC := '0';
    signal CLOAD : STD_LOGIC := '1';
    signal RCK : STD_LOGIC := '0';
    signal A : STD_LOGIC := '0';
    signal B : STD_LOGIC := '0';
    signal C : STD_LOGIC := '0';
    signal D : STD_LOGIC := '0';
    signal E : STD_LOGIC := '0';
    signal F : STD_LOGIC := '0';
    signal G : STD_LOGIC := '0';
    signal H : STD_LOGIC := '0';
    signal RCO_root : STD_LOGIC;
    signal RS1out_root : STD_LOGIC;
    signal RS2out_root : STD_LOGIC;
    signal RS3out_root : STD_LOGIC;
    signal RS4out_root : STD_LOGIC;
    signal RS5out_root : STD_LOGIC;
    signal RS6out_root : STD_LOGIC;
    signal RS7out_root : STD_LOGIC;
    signal RS8out_root : STD_LOGIC;
    signal RCO_test : STD_LOGIC;
    signal RS1out_test : STD_LOGIC;
    signal RS2out_test : STD_LOGIC;
    signal RS3out_test : STD_LOGIC;
    signal RS4out_test : STD_LOGIC;
    signal RS5out_test : STD_LOGIC;
    signal RS6out_test : STD_LOGIC;
    signal RS7out_test : STD_LOGIC;
    signal RS8out_test : STD_LOGIC;
begin
    uut1: counter port map (
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
        RCO => RCO_root,
        RS1out => RS1out_root,
        RS2out => RS2out_root,
        RS3out => RS3out_root,
        RS4out => RS4out_root,
        RS5out => RS5out_root,
        RS6out => RS6out_root,
        RS7out => RS7out_root,
        RS8out => RS8out_root
    );
    uut2: counter_for_test port map (
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
        RCO => RCO_test,
        RS1out => RS1out_test,
        RS2out => RS2out_test,
        RS3out => RS3out_test,
        RS4out => RS4out_test,
        RS5out => RS5out_test,
        RS6out => RS6out_test,
        RS7out => RS7out_test,
        RS8out => RS8out_test
    );
    --CCLR <= '1' after 256ns;
    process
    begin
        CCLR <= '1';
        wait for 32ns;
        CCLR <= '0';
        wait for 224ns;
        CCLR <= '1';
        --wait for 8576ns;
        wait for 8320ns;
    end process;
    --RCK  <= '1' after 32ns, '0' after 48ns, '1' after 64ns, '0' after 80ns, '1' after 272ns, '0' after 288ns;
    process
    begin
        RCK <= '0';
        wait for 32ns;
        RCK <= '1';
        wait for 16ns;
        RCK <= '0';
        wait for 16ns;
        RCK <= '1';
        wait for 16ns;
        RCK <= '0';
        wait for 192ns;
        RCK <= '1';
        wait for 16ns;
        RCK <= '0';
        --wait for 8576ns;                        
        wait for 8288ns;                        
    end process;
    --CLOAD <= '1' after 0ns, '0' after 304ns, '1' after 320ns;
    process
    begin
        CLOAD <= '0';
        wait for 32ns;
        CLOAD <= '1';
        --wait for 128ns;
        --CLOAD <= '0';
        --wait for 64ns;
        --CLOAD <= '1';
        --wait for 80ns;
        wait for 272ns;
        CLOAD <= '0';
        wait for 16ns;
        CLOAD <= '1';        
        --wait for 8576ns;
        wait for 8256ns;
    end process;
    --CCKEN <= '0' after 384ns;    
    process
    begin
        --CCKEN <= '1';
        --wait for 128ns;
        --CCKEN <= '0';
        --wait for 64ns;
        CCKEN <= '1';
        wait for 384ns;
        CCKEN <= '0';
        wait for 8192ns;
    end process;
    
    A <= not A after 8576ns;
    B <= not B after 8576 * 2 ns;
    C <= not C after 8576 * 4 ns;
    D <= not D after 8576 * 8 ns;
    E <= not E after 8576 * 16 ns;
    F <= not F after 8576 * 32 ns;
    G <= not G after 8576 * 64 ns;
    H <= not H after 8576 * 128 ns;    
    --CCLR <= '1' after 256ns;
    --RCK  <= '1' after 32ns, '0' after 48ns, '1' after 64ns, '0' after 80ns, '1' after 272ns, '0' after 288ns;
    --CLOAD <= '1' after 0ns, '0' after 304ns, '1' after 320ns;
    --CCKEN <= '0' after 384ns;
    CCK <= not CCK after 16ns;
    
    test: process
    begin
        if (not(RCO_root = RCO_test) or not(RS1out_root = RS1out_test) or not(RS2out_root = RS2out_test) 
        or not(RS3out_root = RS3out_test) or not(RS4out_root = RS4out_test) or not(RS5out_root = RS5out_test) 
        or not(RS6out_root = RS6out_test) or not(RS7out_root = RS7out_test) or not(RS8out_root = RS8out_test))
        then
            report "CCLR: " & std_logic'image(CCLR) & " CCKEN:" & std_logic'image(CCKEN) & " CCK:" & std_logic'image(CCK)
             & " CLOAD: " & std_logic'image(CLOAD) & " RCK: " & std_logic'image(RCK);
             
             report "ACTUAL: " & " RS1out_root: " & std_logic'image(RS1out_root)& " RS2out_root: " & std_logic'image(RS2out_root)
                               & " RS3out_root: " & std_logic'image(RS3out_root)& " RS4out_root: " & std_logic'image(RS4out_root)
                               & " RS5out_root: " & std_logic'image(RS5out_root)& " RS6out_root: " & std_logic'image(RS6out_root)
                               & " RS7out_root: " & std_logic'image(RS7out_root)& " RS8out_root: " & std_logic'image(RS8out_root) 
                               & " RCO_root: "&std_logic'image(RCO_root);
             report "EXPECTED: " & " RS1out_test: " & std_logic'image(RS1out_test)& " RS2out_test: " & std_logic'image(RS2out_test)
                                 & " RS3out_test: " & std_logic'image(RS3out_test)& " RS4out_test: " & std_logic'image(RS4out_test)
                                 & " RS5out_test: " & std_logic'image(RS5out_test)& " RS6out_test: " & std_logic'image(RS6out_test)
                                 & " RS7out_test: " & std_logic'image(RS7out_test)& " RS8out_test: " & std_logic'image(RS8out_test) 
                                 & " RCO_test: "&std_logic'image(RCO_test);
   
            report "ERROR" severity failure;
        end if;
        wait for 16ns;
    end process;
    
    end_test: process
    begin
        wait for 8576 * 130 ns;
        report "TEST PASSED SUCCESSFULLY" severity failure;    
    end process;
end Behavioral;
