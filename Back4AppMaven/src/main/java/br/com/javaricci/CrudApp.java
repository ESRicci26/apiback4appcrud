package br.com.javaricci;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class CrudApp extends JFrame {

    private JTextField txtId, txtName, txtPrice, txtStock, txtSelling;
    private JTable table;
    private DefaultTableModel tableModel;

    private static final String API_URL = "https://parseapi.back4app.com/parse/classes/Cliente/";
    private static final String APP_ID = "oO0Jp6JIAjwxnOmfOMnNenYM0FXSZjO52Mo1bjzn_RiCci2705clf";
    private static final String API_KEY = "tTmLEx6Ju4ZtpFQsp0FhpJHPxgmeDItIsnAYJ7aU_RiCci2705clf";

    public CrudApp() {
        setTitle("CRUD BACK4APP API");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Nome:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Salário:"));
        txtPrice = new JTextField();
        panel.add(txtPrice);

        panel.add(new JLabel("Estoque:"));
        txtStock = new JTextField();
        panel.add(txtStock);

        panel.add(new JLabel("V/F:"));
        txtSelling = new JTextField();
        panel.add(txtSelling);

        JButton btnAdd = new JButton("Adicionar");
        btnAdd.addActionListener(e -> addClient());
        panel.add(btnAdd);

        JButton btnUpdate = new JButton("Alterar");
        btnUpdate.addActionListener(e -> updateClient());
        panel.add(btnUpdate);

        JButton btnDelete = new JButton("Deletar");
        btnDelete.addActionListener(e -> deleteClient());
        panel.add(btnDelete);

        JButton btnFindById = new JButton("Consultar por ID");
        btnFindById.addActionListener(e -> findClientById());
        panel.add(btnFindById);

        JButton btnFindAll = new JButton("Consultar Todos");
        btnFindAll.addActionListener(e -> findAllClients());
        panel.add(btnFindAll);

        JButton btnClear = new JButton("Limpar Campos");
        btnClear.addActionListener(e -> clearFields());
        panel.add(btnClear);

        JButton btnExit = new JButton("Sair");
        btnExit.addActionListener(e -> System.exit(0));
        panel.add(btnExit);

        add(panel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nome", "Salário", "Estoque", "V/F"}, 0);
        table = new JTable(tableModel);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                txtStock.setText(tableModel.getValueAt(row, 3).toString());
                txtSelling.setText(tableModel.getValueAt(row, 4).toString());
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opções");

        JMenuItem createDbItem = new JMenuItem("Criar DataBase SQLITE");
        createDbItem.addActionListener(e -> createDatabase());
        menu.add(createDbItem);

        JMenuItem generatePdfItem = new JMenuItem("Gerar Relatório PDF");
        generatePdfItem.addActionListener(e -> generatePdfReport());
        menu.add(generatePdfItem);

        JMenuItem generateCsvItem = new JMenuItem("Gerar Relatório CSV");
        generateCsvItem.addActionListener(e -> generateCsvReport());
        menu.add(generateCsvItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void addClient() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            JSONObject json = new JSONObject();
            json.put("cliName", txtName.getText());
            json.put("cliPreco", Double.parseDouble(txtPrice.getText()));
            json.put("cliEstoque", Integer.parseInt(txtStock.getText()));
            json.put("estaVendendo", Boolean.parseBoolean(txtSelling.getText()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("X-Parse-Application-Id", APP_ID)
                    .header("X-Parse-REST-API-Key", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                JOptionPane.showMessageDialog(this, "Registro Adicionado com Sucesso!");
                findAllClients();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao Adicionar Registro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClient() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String id = txtId.getText();
            JSONObject json = new JSONObject();
            json.put("cliName", txtName.getText());
            json.put("cliPreco", Double.parseDouble(txtPrice.getText()));
            json.put("cliEstoque", Integer.parseInt(txtStock.getText()));
            json.put("estaVendendo", Boolean.parseBoolean(txtSelling.getText()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + id))
                    .header("X-Parse-Application-Id", APP_ID)
                    .header("X-Parse-REST-API-Key", API_KEY)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Registro Alterado com Sucesso!");
                findAllClients();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao Alterar Registro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteClient() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String id = txtId.getText();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + id))
                    .header("X-Parse-Application-Id", APP_ID)
                    .header("X-Parse-REST-API-Key", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Registro Deletado com Sucesso!");
                findAllClients();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao Deletar Registro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findClientById() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String id = txtId.getText();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + id))
                    .header("X-Parse-Application-Id", APP_ID)
                    .header("X-Parse-REST-API-Key", API_KEY)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                JSONObject client = (JSONObject) parser.parse(response.body());

                txtId.setText((String) client.get("objectId"));
                txtName.setText((String) client.get("cliName"));
                txtPrice.setText(client.get("cliPreco").toString());
                txtStock.setText(client.get("cliEstoque").toString());
                txtSelling.setText(client.get("estaVendendo").toString());
            } else {
                JOptionPane.showMessageDialog(this, "Registro NÃO Existe", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findAllClients() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("X-Parse-Application-Id", APP_ID)
                    .header("X-Parse-REST-API-Key", API_KEY)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(response.body());
                JSONArray results = (JSONArray) jsonObject.get("results");

                tableModel.setRowCount(0);
                for (Object obj : results) {
                    JSONObject client = (JSONObject) obj;
                    tableModel.addRow(new Object[]{
                            client.get("objectId"),
                            client.get("cliName"),
                            client.get("cliPreco"),
                            client.get("cliEstoque"),
                            client.get("estaVendendo")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao buscar Registros", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtSelling.setText("");
    }

    private void generatePdfReport() {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream("report.pdf"));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("Relatório de Funcionários API Backup4App");
            document.add(title);

            Table table = new Table(new float[]{2, 4, 2, 2, 2});
            table.addCell("ID");
            table.addCell("Nome");
            table.addCell("Salario");
            table.addCell("Estoque");
            table.addCell("V/F");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                table.addCell(tableModel.getValueAt(i, 0).toString());
                table.addCell(tableModel.getValueAt(i, 1).toString());
                table.addCell(tableModel.getValueAt(i, 2).toString());
                table.addCell(tableModel.getValueAt(i, 3).toString());
                table.addCell(tableModel.getValueAt(i, 4).toString());
            }

            document.add(table);
            document.close();
            JOptionPane.showMessageDialog(this, "PDF Relatório Gerado com Sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateCsvReport() {
        try (FileWriter out = new FileWriter("report.csv");
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("ID", "Nome", "Salario", "Estoque", "V/F"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                printer.printRecord(
                        tableModel.getValueAt(i, 0).toString(),
                        tableModel.getValueAt(i, 1).toString(),
                        tableModel.getValueAt(i, 2).toString(),
                        tableModel.getValueAt(i, 3).toString(),
                        tableModel.getValueAt(i, 4).toString()
                );
            }
            JOptionPane.showMessageDialog(this, "CSV Relatório Gerado com Sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDatabase() {
        String url = "jdbc:sqlite:cliente.db";

        String sql = "CREATE TABLE IF NOT EXISTS funcionarios (\n"
                + "	objectId TEXT PRIMARY KEY,\n"
                + "	cliName TEXT NOT NULL,\n"
                + "	cliPreco REAL,\n"
                + "	cliEstoque INTEGER,\n"
                + "	estaVendendo BOOLEAN\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS funcionarios");
            stmt.execute(sql);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String insert = "INSERT INTO funcionarios(objectId, cliName, cliPreco, cliEstoque, estaVendendo) VALUES("
                        + "'" + tableModel.getValueAt(i, 0).toString() + "', "
                        + "'" + tableModel.getValueAt(i, 1).toString() + "', "
                        + tableModel.getValueAt(i, 2).toString() + ", "
                        + tableModel.getValueAt(i, 3).toString() + ", "
                        + tableModel.getValueAt(i, 4).toString()
                        + ");";
                stmt.execute(insert);
            }

            JOptionPane.showMessageDialog(this, "Banco Dados Criado e Dados Inseridos na Tabela com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CrudApp().setVisible(true);
        });
    }
}
