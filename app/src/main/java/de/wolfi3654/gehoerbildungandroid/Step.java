package de.wolfi3654.gehoerbildungandroid;

/**
 * Created by root on 12.07.2017.
 */
public enum Step {
    PRIM(0,"reine Prime"),
    KLSEKUNDE(1,"Kleine Sekunde"),
    GRSEKUNDE(2,"Große Sekunde"),
    KLTERZ(3,"Kleine Terz"),
    GRTERZ(4,"Große Terz"),
    QUARTE(5,"Quarte"),
    QUINTE(7,"Quinte"),
    KLSEXTE(8,"Kleine Sexte"),
    GRSEXTE(9,"Große Sexte"),
    KLSEPTIME(10,"Kleine Septime"),
    GRSEPTIME(11,"Große Septime"),
    OKTAVE(12,"Oktave");
    private final String name;
    private final int step;
    private boolean enabled = true;

    private Step(int step, String name){
        this.step = step;
        this.name = name;
    }
    public void toggle(){
        this.enabled = !this.enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getStep() {
        return step;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name + " - "+this.enabled;
    }
}
