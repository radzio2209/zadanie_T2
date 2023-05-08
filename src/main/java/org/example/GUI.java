package org.example;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.*;
import java.util.*;
import java.util.List;


public class GUI extends JFrame implements ActionListener {

    private final JButton importButton;
    private final JButton exportButton;
    private final JButton importXMLButton;
    private final JButton exportXMLButton;
    private final JButton importButtonDB;
    private  final JButton exportButtonDB ;
    private JTable table;
    private JLabel recordInfoLabel;

    private List<String[]> previousData = new ArrayList<>();



    public GUI() {
        setTitle("Program");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 600);

        // utworzenie przycisku importu
        importButton = new JButton("Importuj z pliku");
        importButton.addActionListener(this);

        // utworzenie przycisku eksportu
        exportButton = new JButton("Eksportuj do pliku");
        exportButton.addActionListener(this);

        // utworzenie przyciskow import iexport dla xml
        importXMLButton = new JButton("Importuj dane z XML");
        importXMLButton.addActionListener(this);
        exportXMLButton = new JButton("Eksportuj dane do XML");
        exportXMLButton.addActionListener(this);

        // utworzenie przyciskow import,export dla db
        importButtonDB = new JButton("Importuj dane z bazy danych");
        importButtonDB.addActionListener(this);
        exportButtonDB = new JButton("Eksportuj dane z bazy danych");
        exportButtonDB.addActionListener(this);


/*
        int columnNames = 0;
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        // utworzenie modelu tabeli
*/      DefaultTableModel model = new DefaultTableModel();

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

       // table.setDefaultEditor(Object.class, new ValidatingCellEditor(new JTextField()));


        // w części inicjalizacji GUI
        recordInfoLabel = new JLabel("");
        JPanel recordInfoPanel = new JPanel(new FlowLayout());
        recordInfoPanel.add(recordInfoLabel);

        // dodanie komponentów do interfejsu
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importXMLButton);
        buttonPanel.add(exportXMLButton);
        buttonPanel.add(importButtonDB);
        buttonPanel.add(exportButtonDB);
        buttonPanel.add(recordInfoPanel); // dodajemy recordInfoPanel do buttonPanel

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                updateRowColor(table, row, Color.WHITE);
            }
        });

        CustomTableCellRenderer customRenderer = new CustomTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

    }

    public class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private Map<Integer, Color> rowColors = new HashMap<>();

        public void setRowColor(int row, Color color) {
            rowColors.put(row, color);
        }

        public Color getRowColor(int row) {
            return rowColors.getOrDefault(row, null);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                Color color = rowColors.get(row);
                component.setBackground(color != null ? color : table.getBackground());
            }

            return component;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == importButton) {
            List<String[]> newData = loadDataFromFile();
            highlightDuplicates(newData);
        }
        else if (e.getSource() == exportButton) {
            // zapisanie danych z tabeli do pliku
            try (FileWriter fw = new FileWriter("katalog.txt")) {
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
        else if (e.getSource() == importXMLButton) {
            Laptops laptopList = importXMLData("katalog.xml");
            highlightAndAddXMLData(laptopList);
        } else if (e.getSource() == exportXMLButton) {
            // zapisanie danych z tabeli do pliku XML
            try {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                Laptops laptopList = new Laptops();

                for (int i = 0; i < model.getRowCount(); i++) {
                    // Tworzenie obiektów Laptop na podstawie danych z tabeli
                    // Uzupełnij odpowiednimi polami tabeli
                    Laptop laptop = new Laptop(i);
                    laptop.setManufacturer((String) model.getValueAt(i, 0));
                    laptop.setScreen(new Screen((String) model.getValueAt(i, 1), (String) model.getValueAt(i, 2), (String) model.getValueAt(i, 3), "tak".equals(model.getValueAt(i, 4))));
                    laptop.setProcessor(new Processor((String) model.getValueAt(i, 5), (String) model.getValueAt(i, 6), (String) model.getValueAt(i, 7)));
                    laptop.setRam((String) model.getValueAt(i, 8));
                    laptop.setDisc(new Disc((String) model.getValueAt(i, 9), (String) model.getValueAt(i, 10)));
                    laptop.setGraphicCard(new GraphicCard((String) model.getValueAt(i, 11), (String) model.getValueAt(i, 12)));
                    laptop.setOs((String) model.getValueAt(i, 13));
                    laptop.setDiscReader((String) model.getValueAt(i, 14));
                    laptopList.getLaptopList().add(laptop);
                }

                laptopList.exportToXML("katalog.xml");
            } catch (JAXBException ex) {
                ex.printStackTrace();
            }
        }
        if (e.getSource() == importButtonDB) {
            List<String[]> newData = loadDataFromDatabase();
            int[] recordCounts = countNewAndDuplicateRecords(newData);
            int newRecords = recordCounts[0];
            int duplicateRecords = recordCounts[1];
            updateRecordInfoLabel(newRecords, duplicateRecords);
        }
        else if (e.getSource() == exportButtonDB) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowCount = model.getRowCount();
            CustomTableCellRenderer customRenderer = (CustomTableCellRenderer) table.getColumnModel().getColumn(0).getCellRenderer();
            List<String[]> rowData = new ArrayList<>();

            for (int i = 0; i < rowCount; i++) {
                Color rowColor = customRenderer.getRowColor(i);
                if (rowColor.equals(Color.GRAY)) {
                    String[] row = new String[model.getColumnCount()];
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        row[j] = (String) model.getValueAt(i, j);
                    }
                    rowData.add(row);
                }
            }

            for (String[] row : rowData) {
                insertRowToDatabase(row);
            }
        }
    }

    private List<String[]> loadDataFromFile() {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("katalog.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                // sprawdzenie czy pola są puste i dodanie "brak" w przypadku pustego pola
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].isEmpty()) {
                        parts[i] = "brak";
                    }
                }
                data.add(parts);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    private void highlightDuplicates(List<String[]> newData) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Usuwanie istniejących wierszy z tabeli

        for (String[] row : newData) {
            boolean duplicate = isDuplicate(row, previousData);
            model.addRow(row);
            int rowIndex = model.getRowCount() - 1;
            updateRowColor(table, rowIndex, duplicate ? Color.RED : Color.GRAY);
        }
        previousData = newData;
        table.repaint(); // Odświeżanie komponentu JTable
    }

    private boolean isDuplicate(String[] newRow, List<String[]> previousData) {
        for (String[] oldRow : previousData) {
            boolean isDuplicateRow = true;
            for (int col = 0; col < newRow.length; col++) {
                if (!oldRow[col].equals(newRow[col])) {
                    isDuplicateRow = false;
                    break;
                }
            }
            if (isDuplicateRow) return true;
        }
        return false;
    }

    private void updateRowColor(JTable table, int row, Color color) {
        CustomTableCellRenderer customRenderer = (CustomTableCellRenderer) table.getColumnModel().getColumn(0).getCellRenderer();
        customRenderer.setRowColor(row, color);
        table.repaint();
    }

    private Laptops importXMLData(String filename) {
        Laptops laptopList = null;
        try {
            laptopList = Laptops.importFromXML(filename);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return laptopList;
    }


    private void highlightAndAddXMLData(Laptops laptopList) {
        if (laptopList == null) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Usuwanie istniejących wierszy z tabeli

        List<String[]> newData = new ArrayList<>();
        for (Laptop laptop : laptopList.getLaptopList()) {
            Object[] rowData = new Object[]{
                    laptop.getManufacturer(),
                    laptop.getScreen().getSize(),
                    laptop.getScreen().getResolution(),
                    laptop.getScreen().getType(),
                    laptop.getScreen().isTouch() ? "tak" : "nie",
                    laptop.getProcessor().getName(),
                    laptop.getProcessor().getPhysicalCores(),
                    laptop.getProcessor().getClockSpeed(),
                    laptop.getRam(),
                    laptop.getDisc().getStorage(),
                    laptop.getDisc().getType(),
                    laptop.getGraphicCard().getName(),
                    laptop.getGraphicCard().getMemory(),
                    laptop.getOs(),
                    laptop.getDiscReader()
            };

            String[] stringRowData = new String[rowData.length];
            for (int i = 0; i < rowData.length; i++) {
                stringRowData[i] = rowData[i].toString();
            }

            newData.add(stringRowData);
        }
        highlightDuplicates(newData);
    }

    private List<String[]> loadDataFromDatabase() {
        List<String[]> data = new ArrayList<>();
        String query = "SELECT * FROM katalog";

        try (java.sql.Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String[] newRow = new String[]{
                        rs.getString("manufacturer"),
                        rs.getString("screen_size"),
                        rs.getString("screen_resolution"),
                        rs.getString("screen_type"),
                        rs.getString("touch_screen"),
                        rs.getString("processor_name"),
                        rs.getString("processor_physical_cores"),
                        rs.getString("processor_clock_speed"),
                        rs.getString("ram"),
                        rs.getString("disc_storage"),
                        rs.getString("disc_type"),
                        rs.getString("graphic_card_name"),
                        rs.getString("graphic_card_memory"),
                        rs.getString("os"),
                        rs.getString("disc_reader")
                };
                data.add(newRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }



    private int[] countNewAndDuplicateRecords(List<String[]> newData) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Usuwanie istniejących wierszy z tabeli

        int newRecords = 0;
        int duplicateRecords = 0;

        for (String[] row : newData) {
            boolean duplicate = isDuplicate(row, previousData);
            model.addRow(row);
            int rowIndex = model.getRowCount() - 1;
            updateRowColor(table, rowIndex, duplicate ? Color.RED : Color.GRAY);

            if (duplicate) {
                duplicateRecords++;
            } else {
                newRecords++;
            }
        }
        previousData = newData;
        table.repaint(); // Odświeżanie komponentu JTable

        return new int[]{newRecords, duplicateRecords};
    }

    private void insertRowToDatabase(String[] row) {
        String query = "INSERT INTO katalog (manufacturer, screen_size, screen_resolution, screen_type, touch_screen, processor_name, processor_physical_cores, processor_clock_speed, ram, disc_storage, disc_type, graphic_card_name, graphic_card_memory, os, disc_reader) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            for (int i = 0; i < row.length; i++) {
                pstmt.setString(i + 1, row[i]);
            }

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

/////////////////////////////////////////////////////////////////
    private void updateRecordInfoLabel(int newRecords, int duplicateRecords) {
        recordInfoLabel.setText("Nowe rekordy: " + newRecords + ", Duplikaty: " + duplicateRecords);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setVisible(true);
    }


}



@XmlRootElement(name = "laptops")
@XmlAccessorType(XmlAccessType.FIELD)
class Laptops {
    @XmlAttribute
    private String moddate;
    @XmlElement(name = "laptop")
    private List<Laptop> laptopList;

    // Getters and setters
    public List<Laptop> getLaptopList() {
        if (laptopList == null) {
            laptopList = new ArrayList<>();
        }
        return laptopList;
    }

    public String getModdate() {
        return moddate;
    }

    public void setModdate(String moddate) {
        this.moddate = moddate;
    }

    public void setLaptopList(List<Laptop> laptopList) {
        this.laptopList = laptopList;
    }

    public static Laptops importFromXML(String xmlFilePath) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Laptops.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(xmlFilePath);
        return (Laptops) unmarshaller.unmarshal(file);
    }

    public void exportToXML(String xmlFilePath) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Laptops.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        File file = new File(xmlFilePath);
        marshaller.marshal(this, file);
    }

}

    @XmlAccessorType(XmlAccessType.FIELD)
    class Laptop {
        @XmlAttribute
        private int id;
        @XmlElement
        private String manufacturer;
        @XmlElement
        private Screen screen;
        @XmlElement
        private Processor processor;
        @XmlElement(name = "ram")
        private String ram;
        @XmlElement
        private Disc disc;
        @XmlElement
        private GraphicCard graphicCard;
        @XmlElement(name = "os")
        private String os;
        @XmlElement
        private String discReader;

        public Laptop(int id) {
            this.id = id;
        }

        public Laptop(){}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public Screen getScreen() {
            return screen;
        }

        public void setScreen(Screen screen) {
            this.screen = screen;
        }

        public Processor getProcessor() {
            return processor;
        }

        public void setProcessor(Processor processor) {
            this.processor = processor;
        }

        public String getRam() {
            return ram;
        }

        public void setRam(String ram) {
            this.ram = ram;
        }

        public Disc getDisc() {
            return disc;
        }

        public void setDisc(Disc disc) {
            this.disc = disc;
        }

        public GraphicCard getGraphicCard() {
            return graphicCard;
        }

        public void setGraphicCard(GraphicCard graphicCard) {
            this.graphicCard = graphicCard;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getDiscReader() {
            return discReader;
        }

        public void setDiscReader(String discReader) {
            this.discReader = discReader;
        }

        // Pozostałe pola
        // Getters and setters
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    class Screen {
        @XmlAttribute
        private String size;
        @XmlAttribute
        private String resolution;
        @XmlAttribute
        private String type;
        @XmlAttribute
        private boolean touch;

        public Screen() {
        }
        public Screen(String size, String resolution, String type, boolean touch) {
            this.size = size;
            this.resolution = resolution;
            this.type = type;
            this.touch = touch;
        }

        public Screen(String size) {
            this.size = size;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isTouch() {
            return touch;
        }

        public void setTouch(boolean touch) {
            this.touch = touch;
        }

        // Getters and setters
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    class Processor {
        @XmlAttribute
        private String name;
        @XmlAttribute
        private String physicalCores;
        @XmlAttribute
        private String clockSpeed;

        public Processor(){}
        public Processor(String name, String physicalCores, String clockSpeed) {
            this.name = name;
            this.physicalCores = physicalCores;
            this.clockSpeed = clockSpeed;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhysicalCores() {
            return physicalCores;
        }

        public void setPhysicalCores(String physicalCores) {
            this.physicalCores = physicalCores;
        }

        public String getClockSpeed() {
            return clockSpeed;
        }

        public void setClockSpeed(String clockSpeed) {
            this.clockSpeed = clockSpeed;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    class Disc {
        @XmlAttribute
        private String storage;
        @XmlAttribute
        private String type;

        public Disc(){}
        public Disc(String storage, String type) {
            this.storage = storage;
            this.type = type;
        }

        public String getStorage() {
            return storage;
        }

        public void setStorage(String storage) {
            this.storage = storage;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        // Getters and setters
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    class GraphicCard {
        @XmlAttribute
        private String name;
        @XmlAttribute
        private String memory;

        public GraphicCard(){}

        public GraphicCard(String name, String memory) {
            this.name = name;
            this.memory = memory;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMemory() {
            return memory;
        }

        public void setMemory(String memory) {
            this.memory = memory;
        }

    }
