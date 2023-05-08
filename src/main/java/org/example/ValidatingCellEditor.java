package org.example;

import javax.swing.*;
import java.awt.*;

public class ValidatingCellEditor extends DefaultCellEditor {
    private JTextField textField;

    public ValidatingCellEditor(JTextField textField) {
        super(textField);
        this.textField = textField;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textField.setText(value.toString());
        textField.setBackground(Color.WHITE);
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean stopCellEditing() {
        String text = textField.getText();
        // Tu możesz dodać swoje walidacje w zależności od kolumny lub zawartości wprowadzonego tekstu
        if (text.isEmpty()) {
            textField.setBackground(Color.RED);
            return false;
        }
        textField.setBackground(Color.WHITE);
        return super.stopCellEditing();
    }
}
