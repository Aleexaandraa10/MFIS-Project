package ro.festival.model;

public enum FestivalDay{
    DAY1, DAY2, DAY3;

    @Override
    public String toString() {
        return name().replace("DAY", "Day " );
    }
}

// enum (enumeration) este un tip special de clasa care defineste un set fix de constante predefinite
// folosesc enum pentru securitate la valori valide
// pot avea doar DAY1, DAY2, DAY3, deci nu pot aparea greseli ca DAY5 sau null