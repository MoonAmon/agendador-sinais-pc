package com.agendador.view;

import com.agendador.controller.AgendadorController;
import com.agendador.model.Agendamento;
import com.agendador.model.DiaSemana;
import com.agendador.tray.SystemTrayManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Janela principal da aplicação
 */
public class MainWindow extends JFrame implements AgendadorController.AgendadorListener {
    
    private final AgendadorController controller;
    private SystemTrayManager trayManager;
    
    // Componentes da interface
    private JTable tabelaAgendamentos;
    private DefaultTableModel modeloTabela;
    private JTextField campoNome;
    private JTextField campoArquivo;
    private JSpinner spinnerHora;
    private JSpinner spinnerMinuto;
    private JSpinner spinnerDuracao;
    private JComboBox<String> comboDispositivo;
    private JCheckBox[] checkboxesDias;
    private JTextArea areaObservacoes;
    private JButton btnSalvar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnTestar;
    private JButton btnIniciarParar;
    private JLabel labelStatus;
    private Agendamento agendamentoEditando;
    
    public MainWindow(AgendadorController controller) {
        this.controller = controller;
        this.controller.adicionarListener(this);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        carregarAgendamentos();
        atualizarStatus();
    }
    
    /**
     * Inicializa os componentes da interface
     */
    private void initializeComponents() {
        setTitle("Agendador de Sinais - v1.0");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        
        // Tabela de agendamentos
        String[] colunas = {"ID", "Nome", "Horário", "Duração (s)", "Dispositivo", "Dias", "Ativo"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaAgendamentos = new JTable(modeloTabela);
        tabelaAgendamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Formulário
        campoNome = new JTextField(20);
        campoArquivo = new JTextField(30);
        campoArquivo.setEditable(false);
        
        spinnerHora = new JSpinner(new SpinnerNumberModel(8, 0, 23, 1));
        spinnerMinuto = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        spinnerDuracao = new JSpinner(new SpinnerNumberModel(30, 1, 3600, 1));
        
        // Dispositivos de áudio
        comboDispositivo = new JComboBox<>();
        atualizarDispositivosAudio();
        
        // Checkboxes para dias da semana
        DiaSemana[] dias = DiaSemana.values();
        checkboxesDias = new JCheckBox[dias.length];
        for (int i = 0; i < dias.length; i++) {
            checkboxesDias[i] = new JCheckBox(dias[i].getDescricao());
        }
        
        areaObservacoes = new JTextArea(3, 20);
        areaObservacoes.setLineWrap(true);
        areaObservacoes.setWrapStyleWord(true);
        
        // Botões
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnRemover = new JButton("Remover");
        btnTestar = new JButton("Testar");
        btnIniciarParar = new JButton("Pausar");
        
        labelStatus = new JLabel("Status: Iniciando...");
    }
    
    /**
     * Configura o layout da interface
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior - controles
        JPanel painelSuperior = new JPanel(new FlowLayout());
        painelSuperior.add(btnIniciarParar);
        painelSuperior.add(new JSeparator(SwingConstants.VERTICAL));
        painelSuperior.add(labelStatus);
        
        // Painel central - dividido em lista e formulário
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        
        // Painel esquerdo - lista de agendamentos
        JPanel painelLista = new JPanel(new BorderLayout());
        painelLista.setBorder(new TitledBorder("Agendamentos"));
        painelLista.add(new JScrollPane(tabelaAgendamentos), BorderLayout.CENTER);
        
        JPanel painelBotoesLista = new JPanel(new FlowLayout());
        painelBotoesLista.add(btnEditar);
        painelBotoesLista.add(btnRemover);
        painelBotoesLista.add(btnTestar);
        painelLista.add(painelBotoesLista, BorderLayout.SOUTH);
        
        // Painel direito - formulário
        JPanel painelFormulario = createFormularioPanel();
        
        splitPane.setLeftComponent(painelLista);
        splitPane.setRightComponent(painelFormulario);
        
        add(painelSuperior, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Cria o painel do formulário
     */
    private JPanel createFormularioPanel() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new TitledBorder("Novo/Editar Agendamento"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Nome
        gbc.gridx = 0; gbc.gridy = row;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campoNome, gbc);
        
        // Arquivo de áudio
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        painel.add(new JLabel("Arquivo:"), gbc);
        gbc.gridx = 1;
        JPanel painelArquivo = new JPanel(new BorderLayout());
        painelArquivo.add(campoArquivo, BorderLayout.CENTER);
        JButton btnSelecionar = new JButton("...");
        btnSelecionar.addActionListener(e -> selecionarArquivo());
        painelArquivo.add(btnSelecionar, BorderLayout.EAST);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(painelArquivo, gbc);
        
        // Horário
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        painel.add(new JLabel("Horário:"), gbc);
        gbc.gridx = 1;
        JPanel painelHorario = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelHorario.add(spinnerHora);
        painelHorario.add(new JLabel(":"));
        painelHorario.add(spinnerMinuto);
        painel.add(painelHorario, gbc);
        
        // Duração
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painel.add(new JLabel("Duração (seg):"), gbc);
        gbc.gridx = 1;
        painel.add(spinnerDuracao, gbc);
        
        // Dispositivo
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painel.add(new JLabel("Dispositivo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(comboDispositivo, gbc);
        
        // Dias da semana
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painel.add(new JLabel("Dias:"), gbc);
        gbc.gridx = 1;
        JPanel painelDias = new JPanel(new GridLayout(4, 2));
        for (JCheckBox checkbox : checkboxesDias) {
            painelDias.add(checkbox);
        }
        painel.add(painelDias, gbc);
        
        // Observações
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        painel.add(new JScrollPane(areaObservacoes), gbc);
        
        // Botão salvar
        row++;
        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(btnSalvar, gbc);
        
        return painel;
    }
    
    /**
     * Configura os event handlers
     */
    private void setupEventHandlers() {
        // Seleção na tabela
        tabelaAgendamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                atualizarBotoes();
            }
        });
        
        // Botões
        btnSalvar.addActionListener(e -> salvarAgendamento());
        btnEditar.addActionListener(e -> editarAgendamento());
        btnRemover.addActionListener(e -> removerAgendamento());
        btnTestar.addActionListener(e -> testarAgendamento());
        btnIniciarParar.addActionListener(e -> toggleAgendador());
        
        // Fechar janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (trayManager != null) {
                    trayManager.esconderParaTray();
                } else {
                    System.exit(0);
                }
            }
        });
    }
    
    /**
     * Seleciona arquivo de áudio
     */
    private void selecionarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Arquivos de Áudio", "wav", "mp3", "aiff", "au"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            
            if (controller.validarArquivoAudio(arquivo.getAbsolutePath())) {
                campoArquivo.setText(arquivo.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this,
                    "Arquivo de áudio inválido ou não suportado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Salva o agendamento
     */
    private void salvarAgendamento() {
        try {
            Agendamento agendamento = criarAgendamentoFromForm();
            
            if (agendamentoEditando != null) {
                agendamento.setId(agendamentoEditando.getId());
                controller.atualizarAgendamento(agendamento);
                JOptionPane.showMessageDialog(this, "Agendamento atualizado com sucesso!");
            } else {
                controller.salvarAgendamento(agendamento);
                JOptionPane.showMessageDialog(this, "Agendamento salvo com sucesso!");
            }
            
            limparFormulario();
            carregarAgendamentos();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar agendamento: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cria um agendamento a partir do formulário
     */
    private Agendamento criarAgendamentoFromForm() {
        // Validações
        if (campoNome.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (campoArquivo.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Arquivo de áudio é obrigatório");
        }
        
        Set<DiaSemana> diasSelecionados = new HashSet<>();
        for (int i = 0; i < checkboxesDias.length; i++) {
            if (checkboxesDias[i].isSelected()) {
                diasSelecionados.add(DiaSemana.values()[i]);
            }
        }
        if (diasSelecionados.isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos um dia da semana");
        }
        
        Agendamento agendamento = new Agendamento();
        agendamento.setNome(campoNome.getText().trim());
        agendamento.setCaminhoAudio(campoArquivo.getText().trim());
        
        int hora = (Integer) spinnerHora.getValue();
        int minuto = (Integer) spinnerMinuto.getValue();
        agendamento.setHorario(LocalTime.of(hora, minuto));
        
        agendamento.setDuracaoSegundos((Integer) spinnerDuracao.getValue());
        agendamento.setDispositivoAudio((String) comboDispositivo.getSelectedItem());
        agendamento.setDiasSemana(diasSelecionados);
        agendamento.setObservacoes(areaObservacoes.getText().trim());
        
        return agendamento;
    }
    
    /**
     * Edita o agendamento selecionado
     */
    private void editarAgendamento() {
        int selectedRow = tabelaAgendamentos.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) modeloTabela.getValueAt(selectedRow, 0);
            try {
                List<Agendamento> agendamentos = controller.buscarTodosAgendamentos();
                agendamentoEditando = agendamentos.stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst()
                    .orElse(null);
                
                if (agendamentoEditando != null) {
                    preencherFormulario(agendamentoEditando);
                    btnSalvar.setText("Atualizar");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar agendamento: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Preenche o formulário com dados do agendamento
     */
    private void preencherFormulario(Agendamento agendamento) {
        campoNome.setText(agendamento.getNome());
        campoArquivo.setText(agendamento.getCaminhoAudio());
        
        LocalTime horario = agendamento.getHorario();
        spinnerHora.setValue(horario.getHour());
        spinnerMinuto.setValue(horario.getMinute());
        
        spinnerDuracao.setValue(agendamento.getDuracaoSegundos());
        comboDispositivo.setSelectedItem(agendamento.getDispositivoAudio());
        
        // Limpar checkboxes
        for (JCheckBox checkbox : checkboxesDias) {
            checkbox.setSelected(false);
        }
        
        // Selecionar dias
        for (DiaSemana dia : agendamento.getDiasSemana()) {
            checkboxesDias[dia.ordinal()].setSelected(true);
        }
        
        areaObservacoes.setText(agendamento.getObservacoes());
    }
    
    /**
     * Remove o agendamento selecionado
     */
    private void removerAgendamento() {
        int selectedRow = tabelaAgendamentos.getSelectedRow();
        if (selectedRow >= 0) {
            int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover este agendamento?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
            
            if (resposta == JOptionPane.YES_OPTION) {
                Long id = (Long) modeloTabela.getValueAt(selectedRow, 0);
                try {
                    controller.removerAgendamento(id);
                    carregarAgendamentos();
                    JOptionPane.showMessageDialog(this, "Agendamento removido com sucesso!");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao remover agendamento: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Testa o agendamento selecionado
     */
    private void testarAgendamento() {
        int selectedRow = tabelaAgendamentos.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) modeloTabela.getValueAt(selectedRow, 0);
            try {
                List<Agendamento> agendamentos = controller.buscarTodosAgendamentos();
                Agendamento agendamento = agendamentos.stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst()
                    .orElse(null);
                
                if (agendamento != null) {
                    controller.testarAgendamento(agendamento);
                    JOptionPane.showMessageDialog(this,
                        "Teste iniciado! O áudio será reproduzido por " + 
                        agendamento.getDuracaoSegundos() + " segundos.",
                        "Teste", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao testar agendamento: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Liga/desliga o agendador
     */
    private void toggleAgendador() {
        if (controller.isAgendadorAtivo()) {
            controller.pararAgendador();
        } else {
            controller.iniciarAgendador();
        }
        atualizarStatus();
    }
    
    /**
     * Limpa o formulário
     */
    private void limparFormulario() {
        campoNome.setText("");
        campoArquivo.setText("");
        spinnerHora.setValue(8);
        spinnerMinuto.setValue(0);
        spinnerDuracao.setValue(30);
        comboDispositivo.setSelectedIndex(0);
        
        for (JCheckBox checkbox : checkboxesDias) {
            checkbox.setSelected(false);
        }
        
        areaObservacoes.setText("");
        agendamentoEditando = null;
        btnSalvar.setText("Salvar");
    }
    
    /**
     * Carrega os agendamentos na tabela
     */
    private void carregarAgendamentos() {
        try {
            List<Agendamento> agendamentos = controller.buscarTodosAgendamentos();
            
            modeloTabela.setRowCount(0);
            
            for (Agendamento agendamento : agendamentos) {
                String diasStr = agendamento.getDiasSemana().stream()
                    .map(d -> d.name().substring(0, 3))
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
                
                Object[] row = {
                    agendamento.getId(),
                    agendamento.getNome(),
                    agendamento.getHorario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    agendamento.getDuracaoSegundos(),
                    agendamento.getDispositivoAudio(),
                    diasStr,
                    agendamento.isAtivo() ? "Sim" : "Não"
                };
                
                modeloTabela.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar agendamentos: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Atualiza os dispositivos de áudio
     */
    private void atualizarDispositivosAudio() {
        comboDispositivo.removeAllItems();
        comboDispositivo.addItem("Dispositivo Padrão");
        
        List<String> dispositivos = controller.obterDispositivosAudio();
        for (String dispositivo : dispositivos) {
            comboDispositivo.addItem(dispositivo);
        }
    }
    
    /**
     * Atualiza o status da aplicação
     */
    private void atualizarStatus() {
        boolean isRunning = controller.isAgendadorAtivo();
        boolean isPlaying = controller.isReproducing();
        
        if (isPlaying) {
            labelStatus.setText("Status: Reproduzindo áudio");
            labelStatus.setForeground(Color.RED);
        } else if (isRunning) {
            labelStatus.setText("Status: Agendador ativo");
            labelStatus.setForeground(Color.GREEN);
        } else {
            labelStatus.setText("Status: Agendador pausado");
            labelStatus.setForeground(Color.ORANGE);
        }
        
        btnIniciarParar.setText(isRunning ? "Pausar" : "Iniciar");
    }
    
    /**
     * Atualiza os botões baseado na seleção
     */
    private void atualizarBotoes() {
        boolean hasSelection = tabelaAgendamentos.getSelectedRow() >= 0;
        btnEditar.setEnabled(hasSelection);
        btnRemover.setEnabled(hasSelection);
        btnTestar.setEnabled(hasSelection);
    }
    
    /**
     * Cria um ícone simples para a aplicação
     */
    private Image createAppIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.BLUE);
        g2d.fillOval(2, 2, 28, 28);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(8, 16, 8, 6);
        g2d.fillRect(16, 8, 2, 12);
        g2d.fillOval(18, 6, 4, 4);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Define o gerenciador de system tray
     */
    public void setSystemTrayManager(SystemTrayManager trayManager) {
        this.trayManager = trayManager;
    }
    
    // Implementação do AgendadorController.AgendadorListener
    
    @Override
    public void onAgendadorIniciado() {
        SwingUtilities.invokeLater(this::atualizarStatus);
    }
    
    @Override
    public void onAgendadorParado() {
        SwingUtilities.invokeLater(this::atualizarStatus);
    }
    
    @Override
    public void onAgendamentoExecutado(Agendamento agendamento) {
        SwingUtilities.invokeLater(() -> {
            atualizarStatus();
            if (trayManager != null) {
                trayManager.mostrarNotificacao(
                    "Executando: " + agendamento.getNome(),
                    TrayIcon.MessageType.INFO);
            }
        });
    }
    
    @Override
    public void onReproducaoConcluida(Agendamento agendamento) {
        SwingUtilities.invokeLater(this::atualizarStatus);
    }
    
    @Override
    public void onErro(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            if (trayManager != null) {
                trayManager.mostrarNotificacao(
                    "Erro: " + mensagem,
                    TrayIcon.MessageType.ERROR);
            }
        });
    }
}
