package com.animalmammal;

/*
 * Clase Dog — subclase de Mammal.
 * Un perro ES un mamífero, que a su vez ES un animal.
 *
 * Dog agrega DOS versiones de greets():
 *   - greets()              → imprime "Woof"  (saludo normal)
 *   - greets(another:Dog)   → imprime "Woooof" (saludo a otro perro)
 *
 * Esto se llama SOBRECARGA (overloading): mismo nombre de método
 * pero distinto parámetro. NO confundir con sobreescritura (override).
 *
 * Sobrecarga    = mismo nombre, distintos parámetros, MISMA clase.
 * Sobreescritura = mismo nombre, mismos parámetros, DISTINTA clase (padre→hijo).
 */
public class Dog extends Mammal {

    // ─── CONSTRUCTOR ──────────────────────────────────────────────────────────
    /**
     * Constructor con nombre.
     * @param name → nombre del perro (ej. "Rex")
     */
    public Dog(String name) {
        super(name); // sube a Mammal(name) → Animal(name)
    }


    // ─── MÉTODOS PROPIOS DE DOG ───────────────────────────────────────────────

    /**
     * Saludo simple: el perro ladra "Woof".
     * Se llama cuando el perro saluda en general.
     */
    public void greets() {
        System.out.println("Woof");
    }

    /**
     * Saludo especial: el perro ladra "Woooof" cuando
     * saluda a OTRO perro específico.
     *
     * Esto es SOBRECARGA — mismo método "greets" pero
     * con un parámetro diferente (another:Dog).
     * Java distingue cuál usar según lo que se le pase.
     *
     * @param another → el otro perro al que saluda
     */
    public void greets(Dog another) {
        System.out.println("Woooof");
        // "another" está disponible aquí si necesitamos
        // hacer algo con ese perro (ej. imprimir su nombre)
    }


    // ─── toString ─────────────────────────────────────────────────────────────
    // Formato: Dog[Mamaml[Animal[name="Rex"]]]
    @Override
    public String toString() {
        return "Dog[" + super.toString() + "]";
    }
}
