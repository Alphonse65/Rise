package fr.rise;

public abstract class Setting<T> {
    public final String name;
    public T value;

    protected Setting(String name, T value) { this.name = name; this.value = value; }

    public static class Bool extends Setting<Boolean> {
        public Bool(String n, boolean v) { super(n, v); }
        public boolean on() { return value; }
    }

    public static class Num extends Setting<Double> {
        public final double min, max;
        public final boolean integer;
        public Num(String n, double v, double min, double max, boolean integer) {
            super(n, v); this.min = min; this.max = max; this.integer = integer;
        }
        public int i() { return (int) Math.round(value); }
        public double d() { return value; }
        public void setRatio(double r) {
            double v = min + (max - min) * Math.max(0, Math.min(1, r));
            value = integer ? (double) Math.round(v) : Math.round(v * 10) / 10.0;
        }
        public double ratio() { return (value - min) / (max - min); }
    }

    public static class Choice extends Setting<Integer> {
        public final String[] options;
        public Choice(String n, int def, String... options) { super(n, def); this.options = options; }
        public int idx() { return value; }
        public String get() { return options[value]; }
        public void next() { value = (value + 1) % options.length; }
    }

    public static class Text extends Setting<String> {
        public Text(String n, String v) { super(n, v); }
        public String s() { return value; }
    }
}
