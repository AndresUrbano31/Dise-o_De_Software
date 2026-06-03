package com.editorial;

public class Publicacion {
    private String titulo;
    private double precio;

    public Publicacion(String titulo, double precio) {
        this.titulo = titulo;
        this.precio = precio;
    }

    public Publicacion(){
        this.titulo = "";
        this.precio = 0;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Publicacion [titulo=" + titulo + ", precio=" + precio + "]";
    }
}
