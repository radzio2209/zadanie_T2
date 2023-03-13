package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GUI extends JFrame implements ActionListener {

    private JButton importButton, exportButton;
    private JTable table;



    public GUI() {
        setTitle("Program");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // utworzenie przycisku importu
        importButton = new JButton("Importuj z pliku");
        importButton.addActionListener(this);

        // utworzenie przycisku eksportu
        exportButton = new JButton("Eksportuj do pliku");
        exportButton.addActionListener(this);

        // utworzenie modelu tabeli
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nazwa producenta");
        model.addColumn("Przekątna ekranu");
        model.addColumn("Rozdzielczość ekranu");
        model.addColumn("Rodzaj powierzchni ekranu");
        model.addColumn("Czy ekran jest dotykowy");
        model.addColumn("Nazwa procesora");
        model.addColumn("Liczba rdzeni fizycznych");
        model.addColumn("Prędkość taktowania MHz");
        model.addColumn("Wielkość pamięci RAM");
        model.addColumn("Pojemność dysku");
        model.addColumn("Rodzaj dysku");
        model.addColumn("Nazwa układu graficznego");
        model.addColumn("Pamięć układu graficznegoPamięć układu graficznego");
        model.addColumn("Nazwa systemu operacyjnego");
        model.addColumn("Rodzaj napędu fizycznego w komputerze");


        // utworzenie tabeli
        table = new JTable(model);

        //dodanie edycji tabeli
        table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));


        // dodanie komponentów do interfejsu
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == importButton) {
            // wczytanie danych z pliku i dodanie ich do tabeli
            try (BufferedReader br = new BufferedReader(new FileReader("katalog.txt"))) {
                String line;
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    // sprawdzenie czy pola są puste i dodanie "brak" w przypadku pustego pola
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].isEmpty()) {
                            parts[i] = "brak";
                        }
                    }
                    model.addRow(parts);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == exportButton) {
            // zapisanie danych z tabeli do pliku
            try (FileWriter fw = new FileWriter("katalog2.txt")) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        fw.write(model.getValueAt(i, j) + ";");
                    }
                    fw.write("\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
/*
* TableCellListener tcl = new TableCellListener(table, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                int row = tcl.getRow();
                int column = tcl.getColumn();
                Object oldValue = tcl.getOldValue();
                Object newValue = tcl.getNewValue();

                if (column == 0) {
                    try {
                        int newNumber = Integer.parseInt(newValue.toString());
                        if (newNumber < 0) {
                            // Wyświetlamy informację o błędzie
                            JOptionPane.showMessageDialog(table, "Numer musi być dodatni.", "Błąd", JOptionPane.ERROR_MESSAGE);
                            // Przywracamy starą wartość
                            table.getModel().setValueAt(oldValue, row, column);
                        }
                    } catch (NumberFormatException ex) {
                        // Wyświetlamy informację o błędzie
                        JOptionPane.showMessageDialog(table, "Nieprawidłowy format numeru.", "Błąd", JOptionPane.ERROR_MESSAGE);
                        // Przywracamy starą wartość
                        table.getModel().setValueAt(oldValue, row, column);
                    }
                }
            }
        });

        table.getModel().addTableModelListener(tcl);
* */
    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setVisible(true);
    }
}