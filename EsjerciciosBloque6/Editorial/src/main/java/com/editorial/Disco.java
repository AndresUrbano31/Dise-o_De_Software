package com.editorial;

public class Disco extends Publicacion{
    private float duracionMinutos;

    public Disco(String titulo, double precio, float duracionMinutos) {
        super(titulo, precio);
        this.duracionMinutos = duracionMinutos;
    }

    public Disco(float duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Disco() {
        super();
        this.duracionMinutos = 0;
    }

    public float getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(float duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    @Override
    public String toString() {
        return "Disco [duracionMinutos=" + duracionMinutos + "]";
    }
}

