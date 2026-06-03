package com.animalmammal;

/*
 * Clase Animal — superclase raíz de toda la jerarquía.
 * Representa cualquier animal con un nombre.
 * Solo tiene UN constructor porque todo animal debe tener nombre.
 */
public class Animal {

    // Atributo privado: nombre del animal.
    // "private" → solo esta clase puede accederlo directamente.
    // Las subclases deben usar getName() para leerlo.
    private String name;


    // ─── CONSTRUCTOR ──────────────────────────────────────────────────────────
    /**
     * Único constructor — obliga a que todo animal tenga nombre.
     * No existe constructor vacío porque un animal sin nombre
     * no tendría sentido en este diseño.
     *
     * @param name → nombre del animal (ej. "Pelusa", "Rex")
     */
    public Animal(String name) {
        this.name = name;
    }


    // ─── GETTER ───────────────────────────────────────────────────────────────
    /** Devuelve el nombre del animal. */
    public String getName() {
        return name;
    }


    // ─── toString ─────────────────────────────────────────────────────────────
    // Sobreescribe el toString() de Object (clase raíz de Java).
    // Formato: Animal[name="Pelusa"]
    //
    // @Override obliga a Java a verificar que este método
    // realmente existe en la clase padre (Object). Siempre
    // se usa al sobreescribir métodos heredados.
    @Override
    public String toString() {
        return "Animal[name=\"" + name + "\"]";
    }
}
