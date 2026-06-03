package com.editorial;

public enum Idioma {
    ESPAÑOL("español"),
    INGLES("ingles"),
    PORTUGUES("portugues");

    private final String nombre;

    Idioma(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
