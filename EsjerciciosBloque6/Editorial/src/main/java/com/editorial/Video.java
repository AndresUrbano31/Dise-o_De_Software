package com.editorial;

public class Video extends Publicacion {

    private Idioma idioma;
    private float duracionHoras;

    public Video() {
        super();
    }

    public Video(Idioma idioma, float duracionHoras) {
        super();
        this.idioma = idioma;
        this.duracionHoras = duracionHoras;
    }

    public Video(Idioma idioma, float duracionHoras, String titulo, double precio) {
        super(titulo, precio);
        this.idioma = idioma;
        this.duracionHoras = duracionHoras;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public float getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(float duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    @Override
    public String toString() {
        return "Video [titulo=" + getTitulo() +
                ", precio=" + getPrecio() +
                ", idioma=" + idioma +
                ", duracionHoras=" + duracionHoras + "]";
    }
}