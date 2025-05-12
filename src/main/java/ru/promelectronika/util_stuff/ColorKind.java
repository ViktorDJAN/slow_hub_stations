package ru.promelectronika.util_stuff;

public enum ColorKind {


    RED_TEXT("printRedText"),
    BLACK_TEXT("printBlackText"),
    BLUE_TEXT("printBlueText"),
    PURPLE_TEXT("printPurpleText"),
    GREEN_TEXT("printGreenText"),
    YELLOW_TEXT("printYellowText"),
    WHITE_TEXT("printWhiteText"),
    CYAN_TEXT("printCyanText"),

    BLACK_BG_WHITE_TEXT("blackBackgroundWhiteText"),
    BLACK_BG_YELLOW_TEXT("blackBackgroundYellowText"),
    BLACK_BG_CYAN_TEXT("blackBackgroundCyanText"),
    BLACK_BG_RED_TEXT("blackBackgroundRedText"),
    WHITE_BG_BLACK_TEXT("whiteBackgroundBlackText"),
    WHITE_BG_PURPLE_TEXT("whiteBackgroundPurpleText"),
    WHITE_BG_BLUE_TEXT("whiteBackgroundBlueText"),
    WHITE_BG_RED_TEXT("whiteBackgroundRedText"),
    YELLOW_BG_BLACK_TEXT("yellowBackgroundBlackText"),
    YELLOW_BG_BLUE_TEXT("yellowBackgroundBlueText"),
    CYAN_BG_BLACK_TEXT("cyanBackgroundBlackText"),
    PURPLE_BG_BLACK_TEXT("purpleBackgroundBlackText"),
    GREEN_BG_YELLOW_TEXT("greenBackgroundYellowText"),
    GREEN_BG_BLACK_TEXT("greenBackgroundBlackText"),
    RED_BG_BLACK_TEXT("redBackgroundBlackText");

    private final String methodName;

    ColorKind(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}