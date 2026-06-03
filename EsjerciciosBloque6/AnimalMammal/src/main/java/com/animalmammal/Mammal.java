package com.animalmammal;

/*
 * Clase Mammal — subclase de Animal, superclase de Cat y Dog.
 * Un mamífero ES un animal, por eso extiende Animal.
 * En este ejercicio Mammal no agrega atributos nuevos,
 * pero sí sobreescribe toString() con su propio formato.
 *
 * Jerarquía:
 *   Object → Animal → Mammal → Cat
 *                            → Dog
 */
public class Mammal extends Animal {

    // ─── CONSTRUCTOR ──────────────────────────────────────────────────────────
    /**
     * Constructor con nombre.
     * "super(name)" delega al constructor de Animal para
     * inicializar el atributo "name" (que es private en Animal
     * y Mammal no puede tocarlo directamente).
     *
     * @param name → nombre del mamífero
     */
    public Mammal(String name) {
        super(name); // llama a Animal(name) — DEBE ser la primera línea
    }


    // ─── toString ─────────────────────────────────────────────────────────────
    // Sobreescribe toString() de Animal.
    // "super.toString()" reutiliza el de Animal para no repetir código.
    //
    // Formato: Mamaml[Animal[name="Pelusa"]]
    //           ↑ (el diagrama tiene ese typo, lo respetamos)
    @Override
    public String toString() {
        return "Mamaml[" + super.toString() + "]";
        // super.toString() → Animal[name="Pelusa"]
        // resultado final → Mamaml[Animal[name="Pelusa"]]
    }
}
