package com.rms.automation.enums;

public enum UIEEnums {

    IMPORT("IMPORT"),
    UPLOAD("UPLOAD"),
    DOWNSTREAM("DOWNSTREAM");

    private final String uie;

    public String getUie() {
        return uie;
    }

    private UIEEnums(String uie) {
        this.uie = uie;
    }

    public static UIEEnums fromString(String text) {
        for (UIEEnums uieEnums : UIEEnums.values()) {
            if (uieEnums.uie.equalsIgnoreCase(text)) {
                return uieEnums;
            }
        }
        throw new IllegalArgumentException("Wrong UIE, check column ifUploadImportExpo");
    }

}
