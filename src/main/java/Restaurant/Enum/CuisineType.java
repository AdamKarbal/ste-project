package Restaurant.Enum;

public enum CuisineType {
    ITALIENNE("italienne"),
    FRANCAISE("française"),
    JAPONAISE("japonaise"),
    CHINOISE("chinoise"),
    INDIENNE("indienne"),
    MEXICAINE("mexicaine"),
    AMERICAINE("américaine"),
    THAILANDAISE("thaïlandaise"),
    VIETNAMIENNE("vietnamienne"),
    MAROCAINE("marocaine"),
    ESPAGNOLE("espagnole"),
    GRECQUE("grecque"),
    TURQUE("turque"),
    LIBANAISE("libanaise"),
    AFRICAINE("africaine"),
    BRESILIENNE("bresilienne"),
    COREENNE("coreenne"),
    ALLEMANDE("allemande"),
    RUSSE("russe"),
    IRANIENNE("iranienne"),;

    private final String label;

    CuisineType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CuisineType fromString(String value) {
        if (value == null) throw new IllegalArgumentException("valeur nulle interdite");
        for (CuisineType type : values()) {
            if (type.label.equalsIgnoreCase(value.trim()) ||
                    type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type de cuisine inconnu : " + value);
    }
}