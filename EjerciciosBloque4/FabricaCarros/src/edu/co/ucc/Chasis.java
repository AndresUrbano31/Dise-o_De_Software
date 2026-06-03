package edu.co.ucc;

public class Chasis {
    private float peso;
    private MaterialChasis materialChasis;

    public Chasis(float peso, MaterialChasis materialChasis) {
        this.peso = peso;
        this.materialChasis = materialChasis;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public void setMaterialChasis(MaterialChasis materialChasis) {
        this.materialChasis = materialChasis;
    }

    public MaterialChasis getMaterialChasis() {
        return materialChasis;
    }

    @Override
    public String toString() {
        return "Chasis{" +
                "peso=" + peso +
                ", materialChasis=" + materialChasis +
                '}';
    }
}
