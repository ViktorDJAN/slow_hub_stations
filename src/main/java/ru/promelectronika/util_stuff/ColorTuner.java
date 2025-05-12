package ru.promelectronika.util_stuff;

public class ColorTuner {

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE = "\u001B[37m";
    public static final String CYAN = "\u001B[36m";


    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";

    public static void simple(String string){
        System.out.println(RED + string + RESET);
    }

    public static void printRedText(Object string){
        System.out.println(RED + string + RESET);
    }
    public static void printBlackText(Object string){
        System.out.println(BLACK + string + RESET);
    }

    public static void printBlueText(Object string){
        System.out.println(BLUE + string + RESET);
    }
    public static void printPurpleText(Object string){
        System.out.println(PURPLE + string + RESET);
    }
    public static void printGreenText(Object string){
        System.out.println(GREEN + string + RESET);
    }
    public static void printYellowText(Object string){
        System.out.println(YELLOW + string + RESET);
    }

    public static void printWhiteText(Object string){
        System.out.println(WHITE + string + RESET);
    }

    public static void printCyanText(Object string){
        System.out.println(CYAN + string + RESET);
    }


    public static void blackBackgroundWhiteText(Object string){ //
        System.out.println(BLACK_BACKGROUND + WHITE + string + RESET);
    }
    public static void blackBackgroundYellowText(Object string){ //
        System.out.println(BLACK_BACKGROUND + YELLOW + string + RESET);
    }

    public static void blackBackgroundCyanText(Object string){
        System.out.println(BLACK_BACKGROUND + CYAN + string + RESET);//
    }

    public static void blackBackgroundRedText(Object string){
        System.out.println(BLACK_BACKGROUND + RED + string + RESET);//
    }

    public static void whiteBackgroundBlackText(Object string){
        System.out.println(WHITE_BACKGROUND + BLACK + string + RESET);
    }

    public static void whiteBackgroundPurpleText(Object string){
        System.out.println(WHITE_BACKGROUND + PURPLE + string + RESET);
    }

    public static void whiteBackgroundBlueText(Object string){
        System.out.println(WHITE_BACKGROUND + BLUE + string + RESET);
    }
    public static void whiteBackgroundRedText(Object string){
        System.out.println(WHITE_BACKGROUND + RED + string + RESET);
    }

    public static void yellowBackgroundBlackText(Object string){
        System.out.println(YELLOW_BACKGROUND + BLACK + string + RESET);
    }

    public static void yellowBackgroundBlueText(Object string){
        System.out.println(YELLOW_BACKGROUND + BLUE + string + RESET);
    }

    public static void cyanBackgroundBlackText(Object string){
        System.out.println(CYAN_BACKGROUND + BLACK + string + RESET);
    }

    public static void purpleBackgroundBlackText(Object string){
        System.out.println(PURPLE_BACKGROUND + BLACK + string + RESET);
    }

    public static void greenBackgroundYellowText(Object string){
        System.out.println(GREEN_BACKGROUND + YELLOW + string + RESET);
    }

    public static void greenBackgroundBlackText(Object string){
        System.out.println(GREEN_BACKGROUND + BLACK + string + RESET);
    }

    public static void redBackgroundBlackText(Object string){
        System.out.println(RED_BACKGROUND + BLACK + string + RESET);
    }












}

