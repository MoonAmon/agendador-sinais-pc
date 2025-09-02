package com.agendador.database;

import com.agendador.model.Agendamento;
import com.agendador.model.DiaSemana;

import java.io.File;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;

/**
 * Gerenciador de banco de dados SQLite para persistência
 * Implementa padrão Singleton
 */
public class DatabaseManager {
    
    private static final String DB_NAME = "agendador.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        // Construtor privado para Singleton
        String userHome = System.getProperty("user.home");
        String appDataDir = userHome + File.separator + ".agendador";

        // Criar diretório de dados se não existir
        File appDir = new File(appDataDir);
        if (!appDir.exists()) {
            boolean created = appDir.mkdirs();
            if (!created) {
                System.err.println("Erro ao criar diretório de dados: " + appDataDir);
                // Fallback para temp
                appDataDir = System.getProperty("java.io.tmpdir") + File.separator + "agendador";
                appDir = new File(appDataDir);
                appDir.mkdirs();
            }
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Inicializa o banco de dados e cria as tabelas necessárias
     */
    public void initializeDatabase() throws SQLException {
        try {
            // Carregar explicitamente o driver SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Criar conexão simples - sem propriedades complexas que podem causar problemas
            connection = DriverManager.getConnection(DB_URL);
            
            // Configurar conexão com comandos PRAGMA diretamente
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA journal_mode = WAL");
                stmt.execute("PRAGMA synchronous = NORMAL");
                stmt.execute("PRAGMA cache_size = 1000");
                stmt.execute("PRAGMA temp_store = memory");
            }
            
            connection.setAutoCommit(true);
            
            createTables();
            
            System.out.println("Banco de dados inicializado com sucesso: " + DB_NAME);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite não encontrado: " + e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cria as tabelas necessárias se não existirem
     */
    private void createTables() throws SQLException {
        // Ativar chaves estrangeiras
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        
        String createAgendamentosTable = 
            "CREATE TABLE IF NOT EXISTS agendamentos (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    nome TEXT NOT NULL," +
            "    caminho_audio TEXT NOT NULL," +
            "    horario TEXT NOT NULL," +
            "    duracao_segundos INTEGER NOT NULL," +
            "    dispositivo_audio TEXT," +
            "    ativo INTEGER NOT NULL DEFAULT 1," +
            "    observacoes TEXT," +
            "    created_at TEXT DEFAULT (datetime('now','localtime'))," +
            "    updated_at TEXT DEFAULT (datetime('now','localtime'))" +
            ")";
        
        String createDiasSemanaTable = 
            "CREATE TABLE IF NOT EXISTS agendamento_dias (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    agendamento_id INTEGER NOT NULL," +
            "    dia_semana INTEGER NOT NULL," +
            "    FOREIGN KEY (agendamento_id) REFERENCES agendamentos(id) ON DELETE CASCADE," +
            "    UNIQUE(agendamento_id, dia_semana)" +
            ")";
        
        // Criar índices para performance
        String createIndexAgendamentoHorario = 
            "CREATE INDEX IF NOT EXISTS idx_agendamentos_horario " +
            "ON agendamentos(horario)";
            
        String createIndexAgendamentoAtivo = 
            "CREATE INDEX IF NOT EXISTS idx_agendamentos_ativo " +
            "ON agendamentos(ativo)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createAgendamentosTable);
            stmt.execute(createDiasSemanaTable);
            stmt.execute(createIndexAgendamentoHorario);
            stmt.execute(createIndexAgendamentoAtivo);
            
            System.out.println("Tabelas criadas com sucesso");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Salva um novo agendamento no banco de dados
     */
    public Long salvarAgendamento(Agendamento agendamento) throws SQLException {
        // Inserir o agendamento
        String sql = 
            "INSERT INTO agendamentos (nome, caminho_audio, horario, duracao_segundos, " +
            "dispositivo_audio, ativo, observacoes) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, agendamento.getNome());
            pstmt.setString(2, agendamento.getCaminhoAudio());
            pstmt.setString(3, agendamento.getHorario().toString());
            pstmt.setInt(4, agendamento.getDuracaoSegundos());
            pstmt.setString(5, agendamento.getDispositivoAudio());
            pstmt.setInt(6, agendamento.isAtivo() ? 1 : 0);
            pstmt.setString(7, agendamento.getObservacoes());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar agendamento, nenhuma linha afetada.");
            }
        }
        
        // Obter o ID gerado usando last_insert_rowid()
        Long id = null;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            
            if (rs.next()) {
                id = rs.getLong(1);
                agendamento.setId(id);
                
                // Salvar dias da semana
                salvarDiasSemana(id, agendamento.getDiasSemana());
                
                System.out.println("Agendamento salvo com sucesso: " + agendamento.getNome() + " (ID: " + id + ")");
                return id;
            } else {
                throw new SQLException("Falha ao obter ID do agendamento criado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar agendamento: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Atualiza um agendamento existente
     */
    public void atualizarAgendamento(Agendamento agendamento) throws SQLException {
        String sql = 
            "UPDATE agendamentos " +
            "SET nome = ?, caminho_audio = ?, horario = ?, duracao_segundos = ?, " +
            "dispositivo_audio = ?, ativo = ?, observacoes = ?, updated_at = datetime('now','localtime') " +
            "WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, agendamento.getNome());
            pstmt.setString(2, agendamento.getCaminhoAudio());
            pstmt.setString(3, agendamento.getHorario().toString());
            pstmt.setInt(4, agendamento.getDuracaoSegundos());
            pstmt.setString(5, agendamento.getDispositivoAudio());
            pstmt.setInt(6, agendamento.isAtivo() ? 1 : 0); // SQLite usa INTEGER para boolean
            pstmt.setString(7, agendamento.getObservacoes());
            pstmt.setLong(8, agendamento.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Agendamento não encontrado para atualização: ID " + agendamento.getId());
            }
            
            // Atualizar dias da semana
            removerDiasSemana(agendamento.getId());
            salvarDiasSemana(agendamento.getId(), agendamento.getDiasSemana());
            
            System.out.println("Agendamento atualizado com sucesso: " + agendamento.getNome());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar agendamento: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Remove um agendamento do banco de dados
     */
    public void removerAgendamento(Long id) throws SQLException {
        String sql = "DELETE FROM agendamentos WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Busca todos os agendamentos
     */
    public List<Agendamento> buscarTodosAgendamentos() throws SQLException {
        String sql = "SELECT * FROM agendamentos ORDER BY horario";
        List<Agendamento> agendamentos = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Agendamento agendamento = criarAgendamentoFromResultSet(rs);
                agendamento.setDiasSemana(buscarDiasSemana(agendamento.getId()));
                agendamentos.add(agendamento);
            }
        }
        
        return agendamentos;
    }
    
    /**
     * Busca agendamentos ativos
     */
    public List<Agendamento> buscarAgendamentosAtivos() throws SQLException {
        String sql = "SELECT * FROM agendamentos WHERE ativo = 1 ORDER BY horario";
        List<Agendamento> agendamentos = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Agendamento agendamento = criarAgendamentoFromResultSet(rs);
                agendamento.setDiasSemana(buscarDiasSemana(agendamento.getId()));
                agendamentos.add(agendamento);
            }
        }
        
        return agendamentos;
    }
    
    /**
     * Salva os dias da semana de um agendamento
     */
    private void salvarDiasSemana(Long agendamentoId, Set<DiaSemana> diasSemana) throws SQLException {
        String sql = "INSERT INTO agendamento_dias (agendamento_id, dia_semana) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (DiaSemana dia : diasSemana) {
                pstmt.setLong(1, agendamentoId);
                pstmt.setInt(2, dia.getValor());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    /**
     * Remove os dias da semana de um agendamento
     */
    private void removerDiasSemana(Long agendamentoId) throws SQLException {
        String sql = "DELETE FROM agendamento_dias WHERE agendamento_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, agendamentoId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Busca os dias da semana de um agendamento
     */
    private Set<DiaSemana> buscarDiasSemana(Long agendamentoId) throws SQLException {
        String sql = "SELECT dia_semana FROM agendamento_dias WHERE agendamento_id = ?";
        Set<DiaSemana> diasSemana = new HashSet<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, agendamentoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int diaValor = rs.getInt("dia_semana");
                    diasSemana.add(DiaSemana.fromValor(diaValor));
                }
            }
        }
        
        return diasSemana;
    }
    
    /**
     * Cria um objeto Agendamento a partir de um ResultSet
     */
    private Agendamento criarAgendamentoFromResultSet(ResultSet rs) throws SQLException {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(rs.getLong("id"));
        agendamento.setNome(rs.getString("nome"));
        agendamento.setCaminhoAudio(rs.getString("caminho_audio"));
        agendamento.setHorario(LocalTime.parse(rs.getString("horario")));
        agendamento.setDuracaoSegundos(rs.getInt("duracao_segundos"));
        agendamento.setDispositivoAudio(rs.getString("dispositivo_audio"));
        agendamento.setAtivo(rs.getInt("ativo") == 1); // Converter INTEGER para boolean
        agendamento.setObservacoes(rs.getString("observacoes"));
        return agendamento;
    }
    
    /**
     * Testa a conexão com o banco de dados
     */
    public boolean testarConexao() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeDatabase();
            }
            
            // Teste simples de query
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se a conexão está ativa
     */
    public boolean isConexaoAtiva() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Verifica se as tabelas principais existem
     */
    public boolean verificarTabelas() throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name IN ('agendamentos', 'agendamento_dias')";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
            }
            return count == 2; // Devem existir as duas tabelas
        }
    }
    
    /**
     * Obtém informações do banco de dados
     */
    public String obterInformacoesBanco() throws SQLException {
        StringBuilder info = new StringBuilder();
        
        // Versão do SQLite
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT sqlite_version()")) {
            if (rs.next()) {
                info.append("SQLite Version: ").append(rs.getString(1)).append("\n");
            }
        }
        
        // Contar agendamentos
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM agendamentos")) {
            if (rs.next()) {
                info.append("Total de agendamentos: ").append(rs.getInt(1)).append("\n");
            }
        }
        
        // Verificar PRAGMA settings
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys")) {
            if (rs.next()) {
                info.append("Foreign Keys: ").append(rs.getInt(1) == 1 ? "ON" : "OFF").append("\n");
            }
        }
        
        return info.toString();
    }
    
    /**
     * Fecha a conexão com o banco de dados
     */
    public void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com banco de dados fechada");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
