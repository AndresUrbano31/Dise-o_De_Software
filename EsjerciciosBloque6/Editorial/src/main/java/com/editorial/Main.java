package com.editorial;
import com.editorial.Libro;
import com.editorial.Disco;
import com.editorial.Video;
import com.editorial.Idioma;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner consola = new Scanner(System.in); // objeto tipo consola
        while (true) {
            System.out.println("**** Aplicacion Editorial ****");
            // Mostramos el menu
            System.out.println("""
                    1. Crear Libro
                    2. Crear Disco
                    3. Crear Video
                    4. Salir
                    """);
            System.out.println("Opcion a realizar?");
            try {
                var operacion = Integer.parseInt(consola.nextLine());
                //Revisar que este dentro de las opciones mencionadas
                if (operacion >= 1 && operacion <= 3) {
                    if (operacion == 1) {
                        // Crear Libro
                        System.out.print("Proporciona titulo del libro:");
                        var titulo = consola.nextLine();
                        System.out.print("Proporciona precio del libro:");
                        var precio = Double.parseDouble(consola.nextLine());
                        System.out.print("Proporciona año de publicacion:");
                        var anioPublicacion = Integer.parseInt(consola.nextLine());
                        System.out.print("Proporciona numero de paginas:");
                        var numeroPaginas = Integer.parseInt(consola.nextLine());
                        
                        Libro miLibro = new Libro(titulo, precio, anioPublicacion, numeroPaginas);
                        System.out.println("Libro creado exitosamente:");
                        System.out.println(miLibro.toString());
                    } else if (operacion == 2) {
                        // Crear Disco
                        System.out.print("Proporciona titulo del disco:");
                        var titulo = consola.nextLine();
                        System.out.print("Proporciona precio del disco:");
                        var precio = Double.parseDouble(consola.nextLine());
                        System.out.print("Proporciona duracion en minutos:");
                        var duracionMinutos = Float.parseFloat(consola.nextLine());
                        
                        Disco miDisco = new Disco(titulo, precio, duracionMinutos);
                        System.out.println("Disco creado exitosamente:");
                        System.out.println(miDisco.toString());
                    } else if (operacion == 3) {
                        // Crear Video
                        System.out.print("Proporciona titulo del video:");
                        var titulo = consola.nextLine();
                        System.out.print("Proporciona precio del video:");
                        var precio = Double.parseDouble(consola.nextLine());
                        System.out.print("Proporciona idioma del video (español/ingles/portugues):");
                        var idiomaStr = consola.nextLine().toLowerCase();
                        Idioma idioma;
                        switch (idiomaStr) {
                            case "español" -> idioma = Idioma.ESPAÑOL;
                            case "ingles" -> idioma = Idioma.INGLES;
                            case "portugues" -> idioma = Idioma.PORTUGUES;
                            default -> {
                                System.out.println("Idioma no válido. Se usará español por defecto.");
                                idioma = Idioma.ESPAÑOL;
                            }
                        }
                        System.out.print("Proporciona duracion en horas:");
                        var duracionHoras = Float.parseFloat(consola.nextLine());
                        
                        Video miVideo = new Video(idioma, duracionHoras, titulo, precio);
                        System.out.println("Video creado exitosamente:");
                        System.out.println(miVideo.toString());
                    }
                } else if (operacion == 4) {
                    System.out.println("Hasta la pronto...");
                    break;
                } else {
                    System.out.println("Opcion erronea:" + operacion);
                }
                System.out.println();
            }
            catch (Exception e) {
                System.out.println("Ocurrio un error:" + e.getMessage());
            }
        }
    }
}