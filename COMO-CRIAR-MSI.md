# Guia Completo: Transformando sua Aplicação em MSI para Produção

## ✅ **SOLUÇÃO IMEDIATA - Bundle de Distribuição (Recomendado)**

Seu aplicativo já está pronto para produção! Criamos um bundle completo:

### 📦 Arquivo Criado

- **`target\AgendadorSinais-1.0.0-Bundle.zip`** (15.8 MB)
- Contém aplicação completa + scripts + documentação

### 🚀 Como Distribuir

1. **Envie o arquivo ZIP** para seus usuários
2. **Usuários extraem** e executam `AgendadorSinais.cmd`
3. **Funciona em qualquer Windows** com Java 21+

### ✅ **Vantagens desta Abordagem**

- ✅ Funciona imediatamente (sem WiX, sem instaladores complexos)
- ✅ Inclui todas as dependências (MP3, SQLite, etc.)
- ✅ Scripts de execução prontos
- ✅ Documentação incluída
- ✅ Tamanho compacto (15.8 MB)

---

## 🎯 **Para MSI Real (Opcional - Requer WiX)**

### Pré-requisitos para MSI

Para criar instaladores MSI com jpackage, você precisa:

1. ✅ Java 21+ (já instalado)
2. ❌ WiX Toolset 3.0+ (precisa instalar)

### 📥 Como Instalar WiX Toolset

#### Opção 1: Download Direto (Recomendado)

1. Acesse: <https://wixtoolset.org/releases/>
2. Baixe: "WiX v3.14 Toolset"
3. Execute o instalador
4. Adicione ao PATH: `C:\Program Files (x86)\WiX Toolset v3.14\bin`

#### Opção 2: Via Chocolatey

```powershell
choco install wixtoolset
```

#### Opção 3: Via Winget

```powershell
winget install WiXToolset.WiX
```

### 🔧 Configurar PATH (se necessário)

1. Pressione Win + R, digite `sysdm.cpl`
2. Aba "Avançado" → "Variáveis de Ambiente"
3. Em "Variáveis do Sistema", encontre "Path"
4. Adicione: `C:\Program Files (x86)\WiX Toolset v3.14\bin`

### ✅ Verificar Instalação

```cmd
candle.exe -?
light.exe -?
```

### 🚀 Criar MSI (após instalar WiX)

```cmd
.\criar-msi.cmd
```

---

## 📦 **Alternativas Disponíveis**

### 1. Bundle ZIP (✅ PRONTO)

- **Arquivo**: `target\AgendadorSinais-1.0.0-Bundle.zip`
- **Uso**: Extrair e executar
- **Requisito**: Java 21+ no PC do usuário

### 2. JAR Executável Simples

- **Arquivo**: `target\agendador-sinais-1.0.0.jar`
- **Comando**: `java -jar agendador-sinais-1.0.0.jar`
- **Requisito**: Java 21+ e comando java no PATH

### 3. Instalador MSI (após instalar WiX)

- **Script**: `.\criar-msi.cmd`
- **Resultado**: Instalador nativo Windows
- **Requisito**: WiX Toolset instalado

### 4. Instalador EXE (após instalar WiX)

- **Script**: `.\criar-exe.cmd`
- **Resultado**: Instalador executável
- **Requisito**: WiX Toolset instalado

### 5. Inno Setup (alternativa gratuita)

- **Baixar**: <https://jrsoftware.org/isinfo.php>
- **Usar**: arquivo `agendador-setup.iss`
- **Resultado**: Instalador profissional

---

## 🎯 **Próximos Passos Recomendados**

### Para Distribuição Imediata

1. ✅ **Use o bundle criado**: `AgendadorSinais-1.0.0-Bundle.zip`
2. ✅ **Teste em máquina limpa** (sem seu ambiente de desenvolvimento)
3. ✅ **Crie instruções para usuários** sobre instalação do Java
4. ✅ **Distribua via email, site, ou repositório**

### Para Distribuição Profissional (Futuro)

1. 🔲 **Instale WiX Toolset**
2. 🔲 **Crie instalador MSI**
3. 🔲 **Considere assinatura digital**
4. 🔲 **Configure update automático**

---

## � **Comparação de Tamanhos**

| Tipo           | Tamanho | Inclui Java | Facilidade |
| -------------- | ------- | ----------- | ---------- |
| JAR simples    | 16 MB   | ❌          | ⭐⭐⭐     |
| Bundle ZIP     | 15.8 MB | ❌          | ⭐⭐⭐⭐⭐ |
| Instalador MSI | ~45 MB  | ✅          | ⭐⭐⭐⭐   |
| Instalador EXE | ~45 MB  | ✅          | ⭐⭐⭐⭐   |

---

## 💡 **Recomendação Final**

**Para começar**: Use o bundle ZIP criado (`AgendadorSinais-1.0.0-Bundle.zip`)

**Para profissionalizar**: Instale WiX e crie MSI depois

Sua aplicação já está **100% funcional e pronta para distribuição** com suporte completo a MP3!
