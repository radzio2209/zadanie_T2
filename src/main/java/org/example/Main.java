package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String filePath = "katalog.txt";
        String[] headers = {
                "Nazwa producenta", "Przekątna ekranu", "Rozdzielczość ekranu",
                "Rodzaj powierzchni ekranu", "Czy ekran jest dotykowy", "Nazwa procesora",
                "Liczba rdzeni fizycznych", "Prędkość taktowania MHz", "Wielkość pamięci RAM",
                "Pojemność dysku", "Rodzaj dysku", "Nazwa układu graficznego",
                "Pamięć układu graficznego", "Nazwa systemu operacyjnego",
                "Rodzaj napędu fizycznego w komputerze"
        };

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int rowNum = 1;
            Map<String, Integer> laptopCountsByManufacturer = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                //wyszukanie pustych wartosci i wstawienie wartosci brak
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].isEmpty()) {
                        fields[i] = "brak";
                    }
                }
                // wyświetlanie nagłówków kolumn
                if (rowNum == 1) {
                    for (String header : headers) {
                        System.out.printf("%-35s", header);
                    }
                    System.out.println();
                }

                // wyświetlanie danych
                for (int i = 0; i < fields.length; i++) {
                    if(i == 0){
                        System.out.printf("%-35s",rowNum+" "+fields[i]);
                    }else {
                        System.out.printf("%-35s",fields[i]);
                    }

                }

                // zliczanie liczby laptopów każdego z producentów
                String manufacturer = fields[0];
                if (laptopCountsByManufacturer.containsKey(manufacturer)) {
                    int count = laptopCountsByManufacturer.get(manufacturer);
                    laptopCountsByManufacturer.put(manufacturer, count + 1);
                } else {
                    laptopCountsByManufacturer.put(manufacturer, 1);
                }
                System.out.println();
                rowNum++;
            }

            // wyświetlenie podziału na kolumny
            System.out.println("-".repeat(headers.length * 35));

            // wyświetlenie liczby laptopów każdego z producentów
            System.out.println("Liczba laptopów każdego z producentów:");
            for (String manufacturer : laptopCountsByManufacturer.keySet()) {
                int count = laptopCountsByManufacturer.get(manufacturer);
                System.out.printf("%s: %d%n", manufacturer, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}