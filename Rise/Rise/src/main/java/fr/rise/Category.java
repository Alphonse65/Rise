package fr.rise;

public enum Category {
    COMBAT("Combat"), MOUVEMENT("Mouvement"), VISUEL("Visuel"), MONDE("Monde"), BOTS("Bots"), UTILE("Utile");
    public final String label;
    Category(String l) { label = l; }
}
