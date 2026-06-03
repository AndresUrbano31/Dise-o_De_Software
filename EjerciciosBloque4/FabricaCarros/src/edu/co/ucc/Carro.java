package edu.co.ucc;

import java.util.List;

public class Carro {
    private String color;
    private Chasis chasis;
    private List<Llanta> llanta;

    public Carro(String color, Chasis chasis, List<Llanta> llanta) {
        this.color = color;
        this.chasis = chasis;
        this.llanta = llanta;
    }

    public String getColor() {
        return color;
    }

    public Chasis getChasis() {
        return chasis;
    }

    public List<Llanta> getLlanta() {
        return llanta;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setChasis(Chasis chasis) {
        this.chasis = chasis;
    }

    public void setLlanta(List<Llanta> llanta) {
        this.llanta = llanta;
    }

    @Override
    public String toString() {
        return "Carro{" +
                "color='" + color + '\'' +
                ", chasis=" + chasis +
                ", llanta=" + llanta +
                '}';
    }
}
