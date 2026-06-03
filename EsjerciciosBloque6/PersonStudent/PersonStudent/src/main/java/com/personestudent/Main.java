package com.personestudent;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("John Doe", "123 Main St");
        System.out.println(person);

        Student student = new Student("Jane Doe", "456 Elm St", "Computer Science", 2, 5000.0);
        System.out.println(student);

        Staff staff = new Staff("Bob Smith", "789 Oak St", "University of Example", 50000.0);
        System.out.println(staff);
    }
}