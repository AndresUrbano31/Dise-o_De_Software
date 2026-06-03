package edu.co.ucc;

import java.util.ArrayList;

public class Planta {
private Llanta tipoLlanta;
private Chasis tipoChasis;
private ArrayList<String> colores;

    public Carro fabricar() {
        return new Carro(tipoLlanta, tipoChasis, colores);
    }
}
