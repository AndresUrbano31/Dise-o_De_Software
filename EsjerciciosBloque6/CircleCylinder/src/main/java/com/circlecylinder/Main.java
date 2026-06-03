package com.circlecylinder;

public class Main {
    public static void main(String[] args) {
        Circle circle = new Circle(5.0, "blue");
        System.out.println(circle);
        System.out.println(circle.getArea());

        Cylinder cylinder = new Cylinder(5.0,3.0,"red");
        System.out.println(cylinder);
        System.out.println(cylinder.getVolume());



    }
}