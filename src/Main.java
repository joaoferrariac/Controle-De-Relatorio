import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

class ContatoGUI extends JFrame {

    private List<Contato> contatos = new ArrayList<>();
    private Connection conexao;

    private JTextField nomeField, emailField, telefoneField, idContatoField;
    private JComboBox<String> sexoComboBox;
    private JTextArea listaContatosArea;
    private JButton cadastrarButton, listarButton, alterarButton, excluirButton, exibirButton;

    public ContatoGUI() {
        conectarAoBancoDeDados();
        setTitle("Controle de Cadastros");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        nomeField = new JTextField(20);
        emailField = new JTextField(20);
        telefoneField = new JTextField(20);
        idContatoField = new JTextField(5);

        String[] opcoesSexo = {"M", "F"};
        sexoComboBox = new JComboBox<>(opcoesSexo);

        cadastrarButton = new JButton("Cadastrar");
        listarButton = new JButton("Listar");
        alterarButton = new JButton("Alterar");
        excluirButton = new JButton("Excluir");
        exibirButton = new JButton("Exibir");

        listaContatosArea = new JTextArea(10, 30);

        setLayout(new FlowLayout());

        add(new JLabel("Nome:"));
        add(nomeField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Telefone:"));
        add(telefoneField);
        add(new JLabel("Sexo:"));
        add(sexoComboBox);
        add(cadastrarButton);
        add(listarButton);
        add(new JLabel("ID do Contato:"));
        add(idContatoField);
        add(alterarButton);
        add(excluirButton);
        add(exibirButton);
        add(new JScrollPane(listaContatosArea));

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarContato();
            }
        });

        listarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarContatos();
            }
        });

        alterarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alterarContato();
            }
        });

        excluirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirContato();
            }
        });

        exibirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirContato();
            }
        });
    }

    private void conectarAoBancoDeDados() {
        try {
            String url = "jdbc:mysql://localhost:3306/contatos";
            String usuario = "root";
            String senha = "";

            conexao = DriverManager.getConnection(url, usuario, senha);

            criarTabelaContatos();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarTabelaContatos() {
        try (PreparedStatement statement = conexao.prepareStatement(
                "CREATE TABLE IF NOT EXISTS contatos (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "nome VARCHAR(255) NOT NULL," +
                        "email VARCHAR(255)," +
                        "telefone VARCHAR(20)," +
                        "sexo VARCHAR(1)" +
                        ")"
        )) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao criar a tabela de contatos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cadastrarContato() {
        String nome = nomeField.getText();
        String email = emailField.getText();
        String telefone = telefoneField.getText();
        String sexo = (String) sexoComboBox.getSelectedItem();

        try (PreparedStatement statement = conexao.prepareStatement(
                "INSERT INTO contatos (nome, email, telefone, sexo) VALUES (?, ?, ?, ?)"
        )) {
            statement.setString(1, nome);
            statement.setString(2, email);
            statement.setString(3, telefone);
            statement.setString(4, sexo);

            statement.executeUpdate();

            nomeField.setText("");
            emailField.setText("");
            telefoneField.setText("");

            JOptionPane.showMessageDialog(this, "Contato cadastrado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar o contato", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarContatos() {
        contatos.clear();

        listaContatosArea.setText("");

        try (PreparedStatement statement = conexao.prepareStatement(
                "SELECT * FROM contatos"
        )) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");
                String telefone = resultSet.getString("telefone");
                String sexo = resultSet.getString("sexo");

                Contato contato = new Contato(id, nome, email, telefone, sexo);
                contatos.add(contato);

                listaContatosArea.append(contato.toString() + "\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao listar os contatos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarContato() {
        int id = Integer.parseInt(idContatoField.getText());
        Contato contato = buscarContatoPorId(id);

        if (contato != null) {
            String novoNome = nomeField.getText();
            String novoEmail = emailField.getText();
            String novoTelefone = telefoneField.getText();
            String novoSexo = (String) sexoComboBox.getSelectedItem();

            try (PreparedStatement statement = conexao.prepareStatement(
                    "UPDATE contatos SET nome = ?, email = ?, telefone = ?, sexo = ? WHERE id = ?"
            )) {
                statement.setString(1, novoNome);
                statement.setString(2, novoEmail);
                statement.setString(3, novoTelefone);
                statement.setString(4, novoSexo);
                statement.setInt(5, id);

                statement.executeUpdate();

                contato.setNome(novoNome);
                contato.setEmail(novoEmail);
                contato.setTelefone(novoTelefone);
                contato.setSexo(novoSexo);

                JOptionPane.showMessageDialog(this, "Contato alterado com sucesso!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao alterar o contato", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Contato não encontrado", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirContato() {
        int id = Integer.parseInt(idContatoField.getText());
        Contato contato = buscarContatoPorId(id);

        if (contato != null) {
            try (PreparedStatement statement = conexao.prepareStatement(
                    "DELETE FROM contatos WHERE id = ?"
            )) {
                statement.setInt(1, id);
                statement.executeUpdate();

                contatos.remove(contato);

                JOptionPane.showMessageDialog(this, "Contato excluído com sucesso!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao excluir o contato", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Contato não encontrado", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirContato() {
        int id = Integer.parseInt(idContatoField.getText());
        Contato contato = buscarContatoPorId(id);

        if (contato != null) {
            JOptionPane.showMessageDialog(this, contato.toString(), "Informações do Contato", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Contato não encontrado", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Contato buscarContatoPorId(int id) {
        for (Contato contato : contatos) {
            if (contato.getId() == id) {
                return contato;
            }
        }
        return null;
    }

    private static class Contato {
        private static int contadorIds = 1;
        private int id;
        private String nome;
        private String email;
        private String telefone;
        private String sexo;

        public Contato(int id, String nome, String email, String telefone, String sexo) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.telefone = telefone;
            this.sexo = sexo;

            if (id >= contadorIds) {
                contadorIds = id + 1;
            }
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelefone() {
            return telefone;
        }

        public void setTelefone(String telefone) {
            this.telefone = telefone;
        }

        public String getSexo() {
            return sexo;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }

        @Override
        public String toString() {
            String resultado = "ID: " + id + ", Nome: " + nome + ", Sexo: " + sexo;

            if (email != null && !email.isEmpty()) {
                resultado += ", Email: " + email;
            } else {
                resultado += ", Email não foi informado";
            }

            if (telefone != null && !telefone.isEmpty()) {
                resultado += ", Telefone: " + telefone;
            } else {
                resultado += ", Telefone não foi informado";
            }

            return resultado;
        }

        public Color getCorDeFundo() {
            return "F".equals(sexo) ? Color.YELLOW : Color.GREEN;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ContatoGUI gui = new ContatoGUI();
                gui.setVisible(true);
                gui.setLocationRelativeTo(null);

                gui.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowActivated(java.awt.event.WindowEvent evt) {
                        gui.getContentPane().setBackground(gui.contatos.isEmpty() ? Color.WHITE : gui.contatos.get(0).getCorDeFundo());
                    }
                });
            }
        });
    }
}
