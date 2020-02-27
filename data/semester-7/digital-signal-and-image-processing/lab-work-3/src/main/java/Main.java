package main.java;

public class Main {
    private static final String RED_BOLD = "\033[1;31m";    // RED
    private static final String RESET = "\033[0m";  // Text Reset
    private static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    private static final String WHITE = "\033[0;37m";   // WHITE
    private static final String BLUE_BACKGROUND = "\033[44m";   // BLUE

    public static void main(String[] args) {
        Hopfield hopfield = new Hopfield();
        int[] crashedImage;

        System.out.println("===================LETTER A===================");
        for (int i = 10; i <= 100; i += 10) {
            System.out.println("==========" + i + "%==========");
            crashedImage = hopfield.crashImage(Instances.LETTER_A, i);
            printResult(crashedImage);
            System.out.println();
            printResult(hopfield.findImage(crashedImage));
        }
        System.out.println("===================LETTER P===================");
        for (int i = 10; i <= 100; i += 10) {
            System.out.println("==========" + i + "%==========");
            crashedImage = hopfield.crashImage(Instances.LETTER_P, i);
            printResult(crashedImage);
            System.out.println();
            printResult(hopfield.findImage(crashedImage));
        }
        System.out.println("===================LETTER N===================");
        for (int i = 10; i <= 100; i += 10) {
            System.out.println("==========" + i + "%==========");
            crashedImage = hopfield.crashImage(Instances.LETTER_N, i);
            printResult(crashedImage);
            System.out.println();
            printResult(hopfield.findImage(crashedImage));
        }
    }

    private static void printResult(int[] letter) {
        for (int i = 0; i < letter.length; i++) {
            if (i != 0 && i % 10 == 0) {
                System.out.println();
            }
            if (letter[i] == 1) {
                System.out.printf(RED_BOLD + BLUE_BACKGROUND + "%2d" + RESET, letter[i]);
            } else
                System.out.printf(WHITE + BLACK_BACKGROUND + "%2d" + RESET, letter[i]);
        }
        System.out.println();
    }
}
