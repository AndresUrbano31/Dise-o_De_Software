package edu.co.ucc;
public class Llanta {
    private float tamaño;
    private TipoLlanta tipoLlanta;

    public Llanta(float tamaño, TipoLlanta tipoLlanta) {
        this.tamaño = tamaño;
        this.tipoLlanta = tipoLlanta;
    }

    public float getTamaño() {
        return tamaño;
    }

    public TipoLlanta getTipoLlanta() {
        return tipoLlanta;
    }

    public void setTamaño(float tamaño) {
        this.tamaño = tamaño;
    }

    public void setTipoLlanta(TipoLlanta tipoLlanta) {
        this.tipoLlanta = tipoLlanta;
    }

    @Override
    public String toString() {
        return "Llanta{" +
                "tamaño=" + tamaño +
                ", tipoLlanta=" + tipoLlanta +
                '}';
    }
}


