package com.animalmammal;

/*
 * Clase Main — punto de entrada del programa.
 * Prueba todos los objetos y demuestra:
 *   1. toString() anidado
 *   2. greets() en Cat y Dog
 *   3. Sobrecarga de greets() en Dog
 *   4. Polimorfismo con array de Animal
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. toString() anidado ───────────────────────────────────────────────
        // Cada clase llama al super.toString() del padre,
        // produciendo cadenas cada vez más anidadas.
        System.out.println("═══ toString() ═══");

        Animal a = new Animal("Genérico");
        System.out.println(a);
        // → Animal[name="Genérico"]

        Mammal m = new Mammal("Mamífero");
        System.out.println(m);
        // → Mamaml[Animal[name="Mamífero"]]

        Cat cat = new Cat("Pelusa");
        System.out.println(cat);
        // → Cat[Mamaml[Animal[name="Pelusa"]]]

        Dog dog = new Dog("Rex");
        System.out.println(dog);
        // → Dog[Mamaml[Animal[name="Rex"]]]

        System.out.println();


        // ── 2. greets() básico ─────────────────────────────────────────────────
        System.out.println("═══ greets() ═══");

        cat.greets();  // → Meow
        dog.greets();  // → Woof

        System.out.println();


        // ── 3. Sobrecarga de greets() en Dog ───────────────────────────────────
        // Java decide qué versión usar según el argumento que recibe.
        // Sin argumento → greets()        → "Woof"
        // Con Dog       → greets(Dog)     → "Woooof"
        System.out.println("═══ sobrecarga greets(Dog) ═══");

        Dog dog2 = new Dog("Fido");
        dog.greets();       // → Woof   (saludo normal)
        dog.greets(dog2);   // → Woooof (saludo a otro perro)

        System.out.println();


        // ── 4. Polimorfismo ─────────────────────────────────────────────────────
        // Una variable de tipo Animal puede guardar Cat o Dog.
        // toString() llama al método de la clase REAL del objeto.
        System.out.println("═══ polimorfismo ═══");

        Animal[] animales = {
                new Animal("Genérico"),
                new Mammal("Mamífero"),
                new Cat("Michi"),
                new Dog("Toby")
        };

        for (Animal animal : animales) {
            System.out.println(animal); // polimorfismo: cada uno imprime su toString()

            // instanceof + casting para llamar greets()
            // (greets no está en Animal, está en Cat y Dog)
            if (animal instanceof Cat) {
                ((Cat) animal).greets();
            } else if (animal instanceof Dog) {
                ((Dog) animal).greets();
            }
        }
    }
}

/*  ── SALIDA ESPERADA ──────────────────────────────────────────────────────
═══ toString() ═══
Animal[name="Genérico"]
Mamaml[Animal[name="Mamífero"]]
Cat[Mamaml[Animal[name="Pelusa"]]]
Dog[Mamaml[Animal[name="Rex"]]]

═══ greets() ═══
Meow
Woof

═══ sobrecarga greets(Dog) ═══
Woof
Woooof

═══ polimorfismo ═══
Animal[name="Genérico"]
Mamaml[Animal[name="Mamífero"]]
Cat[Mamaml[Animal[name="Michi"]]]
Meow
Dog[Mamaml[Animal[name="Toby"]]]
Woof
*/