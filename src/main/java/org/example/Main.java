package org.example;

import org.example.service.ServiceImpl;

public class Main {
    public static void main(String[] args) {
        ServiceImpl service = new ServiceImpl();
        service.process();
    }
}