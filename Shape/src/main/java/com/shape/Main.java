package com.shape;

/*
 * Clase Main — punto de entrada del programa.
 * Aquí se crean objetos de cada clase y se prueban sus métodos.
 * El método main es el primero que Java ejecuta al iniciar el programa.
 */
public class Main {

    // "public static void main" es la firma obligatoria del método principal.
    // String[] args permite recibir argumentos desde la línea de comandos.
    public static void main(String[] args) {

        // ── Prueba con Shape ────────────────────────────────────────────────────
        // Creamos una figura base con color "yellow" y sin relleno.
        Shape shape = new Shape("yellow", false);
        System.out.println("-- Shape --");
        System.out.println(shape);           // Llama a toString() de Shape
        System.out.println("Color: " + shape.getColor());
        System.out.println("Filled: " + shape.isFilled());
        System.out.println();

        // ── Prueba con Circle ───────────────────────────────────────────────────
        // Creamos un círculo de radio 5, color azul y relleno.
        Circle circle = new Circle(5.0, "blue", true);
        System.out.println("-- Circle --");
        System.out.println(circle);          // toString() anidado de Circle
        // "%.2f" formatea el número con 2 decimales.
        System.out.printf("Area:      %.2f%n", circle.getArea());
        System.out.printf("Perimetro: %.2f%n", circle.getPerimeter());
        System.out.println();

        // ── Prueba con Rectangle ────────────────────────────────────────────────
        // Creamos un rectángulo de 4×6, color verde y sin relleno.
        Rectangle rect = new Rectangle(4.0, 6.0, "green", false);
        System.out.println("-- Rectangle --");
        System.out.println(rect);
        System.out.printf("Area:      %.2f%n", rect.getArea());
        System.out.printf("Perimetro: %.2f%n", rect.getPerimeter());
        System.out.println();

        // ── Prueba con Square ───────────────────────────────────────────────────
        // Creamos un cuadrado de lado 3, color rojo y relleno.
        Square sq = new Square(3.0, "red", true);
        System.out.println("-- Square --");
        System.out.println(sq);              // toString() triplemente anidado
        System.out.printf("Area:      %.2f%n", sq.getArea());
        System.out.printf("Perimetro: %.2f%n", sq.getPerimeter());
        System.out.println();

        // ── Prueba de setSide ────────────────────────────────────────────────────
        // Cambiamos el lado del cuadrado a 7.
        // Gracias a la sobreescritura, width y length se actualizan juntos.
        sq.setSide(7.0);
        System.out.println("Square tras setSide(7):");
        System.out.println(sq);
        System.out.printf("Nuevo lado:  %.1f%n", sq.getSide());
        System.out.printf("Nueva area:  %.2f%n", sq.getArea());
    }
}

/*  ── SALIDA ESPERADA ─────────────────────────────────────────────────────────
    -- Shape --
    Shape[color=yellow,filled=false]
    Color: yellow
    Filled: false

    -- Circle --
    Circle[Shape[color=blue,filled=true],radius=5.0]
    Area:      78.54
    Perimetro: 31.42

    -- Rectangle --
    Rectangle[Shape[color=green,filled=false],width=4.0,length=6.0]
    Area:      24.00
    Perimetro: 20.00

    -- Square --
    Square[Rectangle[Shape[color=red,filled=true],width=3.0,length=3.0]]
    Area:      9.00
    Perimetro: 12.00

    Square tras setSide(7):
    Square[Rectangle[Shape[color=red,filled=true],width=7.0,length=7.0]]
    Nuevo lado:  7.0
    Nueva area:  49.00
*/