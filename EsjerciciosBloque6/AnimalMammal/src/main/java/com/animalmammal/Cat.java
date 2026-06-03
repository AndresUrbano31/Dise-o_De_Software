package com.animalmammal;

/*
 * Clase Cat — subclase de Mammal.
 * Un gato ES un mamífero, que a su vez ES un animal.
 *
 * Cat agrega:
 *   - greets()         → imprime "Meow"
 *   - toString()       → formato anidado con Mammal y Animal
 */
public class Cat extends Mammal {

    // ─── CONSTRUCTOR ──────────────────────────────────────────────────────────
    /**
     * Constructor con nombre.
     * Sube la cadena: Cat → Mammal → Animal.
     * Cada super() pasa el nombre un nivel arriba hasta que
     * Animal lo almacena en su atributo privado "name".
     *
     * @param name → nombre del gato (ej. "Pelusa")
     */
    public Cat(String name) {
        super(name); // llama a Mammal(name), que llama a Animal(name)
    }


    // ─── MÉTODOS PROPIOS DE CAT ───────────────────────────────────────────────

    /**
     * El gato saluda imprimiendo "Meow" en consola.
     * void → no devuelve ningún valor.
     */
    public void greets() {
        System.out.println("Meow");
    }


    // ─── toString ─────────────────────────────────────────────────────────────
    // Formato: Cat[Mamaml[Animal[name="Pelusa"]]]
    //
    // super.toString() llama al de Mammal, que llama al de Animal,
    // produciendo la cadena anidada completa.
    @Override
    public String toString() {
        return "Cat[" + super.toString() + "]";
        // super.toString() → Mamaml[Animal[name="Pelusa"]]
        // resultado final → Cat[Mamaml[Animal[name="Pelusa"]]]
    }
}
