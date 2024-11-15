/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author Acer
 */
public class PengelolaanKontak extends javax.swing.JFrame {

    /**
     * Creates new form PengelolaanKontak
     */
    public PengelolaanKontak() {
        initComponents();
        
        // Initialize the table model with columns
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone", "Category"}, 0);
        contactsTable.setModel(tableModel); // Set the model to contactsTable

        // Load data when form is created
        refreshTable();
    }
// private JTextField nameField, phoneField;
//    private JComboBox<String> categoryBox;
//    private JButton addButton, editButton, deleteButton, searchButton;
//    private JTable contactsTable;
      private DefaultTableModel tableModel;
    // Database connection
    private Connection connect() {
        String url = "jdbc:sqlite:contacts.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                initializeDB(conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
 // Add this method for importing CSV
    private void importCSV() throws CsvValidationException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose CSV File to Import");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (CSVReader reader = new CSVReader(new FileReader(selectedFile))) {
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // Skip the header if needed
                    if (nextLine[0].equals("ID")) continue;

                    String name = nextLine[1];
                    String phone = nextLine[2];
                    String category = nextLine[3];
                    
                    // Insert into table or database
                    addContactFromCSV(name, phone, category);
                }
                JOptionPane.showMessageDialog(this, "Data imported successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading CSV file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
       // Method to insert contact from CSV to the table and database
    private void addContactFromCSV(String name, String phone, String category) {
        String sql = "INSERT INTO contacts(name, phone, category) VALUES(?,?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, category);
            pstmt.executeUpdate();
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding contact to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Method to initialize the database and table
    private void initializeDB(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS contacts (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "name TEXT NOT NULL, " +
                     "phone TEXT NOT NULL, " +
                     "category TEXT NOT NULL);";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void refreshTable() {
        System.out.println("refreshTable called.");  // Debug statement

        // Ensure tableModel is initialized
        if (tableModel == null) {
            System.err.println("Error: tableModel is still not initialized in refreshTable.");
            return;
        }

        String sql = "SELECT * FROM contacts";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getString("phone"), 
                    rs.getString("category")
                });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
//        String category = (String) categoryBox.getSelectedItem();
        String category = (String) categoryKontak.getSelectedValue();


        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and phone cannot be empty.");
            return;
        }

        String sql = "INSERT INTO contacts(name, phone, category) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, category);
            pstmt.executeUpdate();
            refreshTable();
            nameField.setText("");
            phoneField.setText("");
            categoryBox.setSelectedIndex(0);
            categoryKontak.setSelectedIndex(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editContact() {
        int row = contactsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contact to edit.");
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();
        String name = nameField.getText();
        String phone = phoneField.getText();
//        String category = (String) categoryBox.getSelectedItem();
        String category = (String) categoryKontak.getSelectedValue();


        if(name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and phone cannot be empty.");
            return;
        }

        String sql = "UPDATE contacts SET name = ?, phone = ?, category = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, category);
            pstmt.setInt(4, Integer.parseInt(id));
            pstmt.executeUpdate();
            refreshTable();
            nameField.setText("");
            phoneField.setText("");
            categoryBox.setSelectedIndex(0);
            categoryKontak.setSelectedIndex(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteContact() {
        int row = contactsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();

        String sql = "DELETE FROM contacts WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.executeUpdate();
            refreshTable();
            nameField.setText("");
            phoneField.setText("");
            categoryBox.setSelectedIndex(0);
            categoryKontak.setSelectedIndex(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

   private void searchContacts() {
    String searchQuery = searchData.getText();
    if (searchQuery.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Search data cannot be empty.");
        return;
    }
    String type = (String) categoryBox.getSelectedItem();
    String sql = null;

    // Tentukan query SQL berdasarkan kategori yang dipilih
    if ("Nama".equals(type)) {
        sql = "SELECT * FROM contacts WHERE name LIKE ?";
    } else if ("Telepon".equals(type)) {
        sql = "SELECT * FROM contacts WHERE phone LIKE ?";
    }

    try (Connection conn = this.connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        // Set parameter pencarian
        pstmt.setString(1, "%" + searchQuery + "%");

        // Eksekusi query dan ambil hasilnya
        ResultSet rs = pstmt.executeQuery();
        tableModel.setRowCount(0); // Clear existing data
        while (rs.next()) {
            tableModel.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("category")
            });
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        categoryBox = new javax.swing.JComboBox<>();
        nameField = new javax.swing.JTextField();
        phoneField = new javax.swing.JTextField();
        searchData = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        contactsTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        categoryKontak = new javax.swing.JList<>();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "FORM PENGELOLAAN KONTAK", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 24))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Masukan Nama :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(26, 32, 9, 7);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Masukan No. Telp :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(7, 32, 9, 7);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Pilih Jenis Cari :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(7, 32, 9, 7);
        jPanel1.add(jLabel3, gridBagConstraints);

        categoryBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama", "Telepon" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(7, 21, 7, 7);
        jPanel1.add(categoryBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(26, 21, 7, 7);
        jPanel1.add(nameField, gridBagConstraints);

        phoneField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneFieldActionPerformed(evt);
            }
        });
        phoneField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                phoneFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                phoneFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(7, 21, 7, 7);
        jPanel1.add(phoneField, gridBagConstraints);

        searchData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDataActionPerformed(evt);
            }
        });
        searchData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchDataKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 28);
        jPanel1.add(searchData, gridBagConstraints);

        contactsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        contactsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(contactsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = -150;
        gridBagConstraints.insets = new java.awt.Insets(7, 32, 7, 28);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        addButton.setText("Tambah");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 18, 7);
        jPanel1.add(addButton, gridBagConstraints);

        editButton.setText("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 18, 7);
        jPanel1.add(editButton, gridBagConstraints);

        deleteButton.setText("Hapus");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 18, 7);
        jPanel1.add(deleteButton, gridBagConstraints);

        jButton4.setText("Keluar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 18, 28);
        jPanel1.add(jButton4, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Cari :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        jPanel1.add(jLabel4, gridBagConstraints);

        jButton1.setText("Simpan (CSV)");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 26);
        jPanel1.add(jButton1, gridBagConstraints);

        jButton2.setText("Import (CSV)");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 18, 27);
        jPanel1.add(jButton2, gridBagConstraints);

        categoryKontak.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Keluarga", "Teman", "Teman Kerja" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(categoryKontak);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = -20;
        gridBagConstraints.insets = new java.awt.Insets(7, -34, 7, 0);
        jPanel1.add(jScrollPane2, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Pilih Categori Kontak");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, -30, 0);
        jPanel1.add(jLabel5, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
        addContact();
    }//GEN-LAST:event_addButtonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        deleteContact();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
        editContact();
    }//GEN-LAST:event_editButtonActionPerformed

    private void searchDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDataActionPerformed
        // TODO add your handling code here:
   if (!searchData.getText().isEmpty()){
        searchContacts();
        } else {
        refreshTable();
        }
    }//GEN-LAST:event_searchDataActionPerformed

    private void searchDataKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchDataKeyReleased
        // TODO add your handling code here:
        if (!searchData.getText().isEmpty()){
        searchContacts();
        } else {
        refreshTable();
        }
    }//GEN-LAST:event_searchDataKeyReleased

    private void saveToCSV() {
    // Get the current date to append to the file name (optional)
    String fileName = "contacts_data.csv";
    
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
        // Write the header row
        writer.write("ID,Name,Phone,Category");
        writer.newLine();

        // Write data from the table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                // Append each cell value followed by a comma, except for the last one
                row.append(tableModel.getValueAt(i, j));
                if (j < tableModel.getColumnCount() - 1) {
                    row.append(",");
                }
            }
            writer.write(row.toString());
            writer.newLine();
        }
        JOptionPane.showMessageDialog(this, "Data saved to " + fileName);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving data to CSV: " + e.getMessage());
        e.printStackTrace();
    }
}
    
private void filterKeyTyped(java.awt.event.KeyEvent evt) {
    char c = evt.getKeyChar();  // Get the character from the event
    
    // Check if the character is a digit or special keys like backspace or delete
    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
        Toolkit.getDefaultToolkit().beep();  // Play beep sound
        JOptionPane.showMessageDialog(null, "Masukkan hanya angka!"); // Inform user only numbers are allowed
        evt.consume();  // Consume the event to prevent further processing
    }

    // Check the length of the text in the phone field
    JTextField source = (JTextField) evt.getSource();
    if (source.getText().length() >= 12) {
        Toolkit.getDefaultToolkit().beep();  // Play beep sound
        JOptionPane.showMessageDialog(null, "Masukkan maksimal 12 angka saja!");
        source.setText("");
        evt.consume();  // Consume the event to prevent additional characters
    }
}
    
    private void phoneFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneFieldKeyReleased
        // TODO add your handling code here:
        filterKeyTyped(evt);
    }//GEN-LAST:event_phoneFieldKeyReleased

    private void phoneFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneFieldActionPerformed
        
    }//GEN-LAST:event_phoneFieldActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        saveToCSV();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void phoneFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneFieldKeyTyped
        // TODO add your handling code here:
        filterKeyTyped(evt);
    }//GEN-LAST:event_phoneFieldKeyTyped

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            importCSV();
        } catch (CsvValidationException ex) {
            Logger.getLogger(PengelolaanKontak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void contactsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactsTableMouseClicked
        // TODO add your handling code here:
        int row = contactsTable.getSelectedRow();
        nameField.setText(contactsTable.getValueAt(row,1).toString());
        phoneField.setText(contactsTable.getValueAt(row,2).toString());
//        categoryBox.setSelectedItem(contactsTable.getValueAt(row,3).toString());
        categoryKontak.setSelectedValue(contactsTable.getValueAt(row, 3).toString(), true);
        editButton.setEnabled(true);
        deleteButton.setEnabled(true);
        addButton.setEnabled(false);
    }//GEN-LAST:event_contactsTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontak().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox<String> categoryBox;
    private javax.swing.JList<String> categoryKontak;
    private javax.swing.JTable contactsTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField phoneField;
    private javax.swing.JTextField searchData;
    // End of variables declaration//GEN-END:variables
}
