Projeto usando HttpClient para consumir a API Back4App
Para criar um CRUD completo em Java Swing primeiro dividi o código em várias partes: criação da interface gráfica (GUI), integração com a API, e 
implementação das funcionalidades do CRUD.
Funcionalidades CRUD: Adicionar, Alterar, Deletar, Consultar por ID, Consultar Todos, Gerar Relatório PDF, Limpar Campos, Sair, Gerar Relatório PDF,
Gerar Relatório CSV, Criar Data Base SQLITE.

Explicação das Funcionalidades:
Geração de Relatório PDF.
Utiliza a biblioteca iText para criar um arquivo PDF.
Cria uma tabela no PDF com os dados da tabela Swing.
Dependência pow.xml
artifactId: itext7-core
version: 8.0.4

Geração de Relatório CSV.
Utiliza a biblioteca Apache Commons CSV para criar um arquivo CSV.
Escreve os dados da tabela Swing no arquivo CSV.
Dependência pow.xml
artifactId: commons-csv
version: 1.8

Criação do Banco de Dados.
Utiliza o driver SQLite para criar um banco de dados SQLite.
Cria a tabela funcionarios e insere os dados da tabela Swing.
A tabela é sobrescrita sempre que a opção de menu é selecionada.
Dependência pow.xml
artifactId: sqlite-jdbc
version: 3.34.0

Manipular arquivos JSON
artifactId:json-simple
version:1.1.1

Os campos abaixo devem ser substituidos pelos dados do seu projeto na API
API_URL = "https://parseapi.back4app.com/parse/classes/Cliente/";
APP_ID = "oO0Jp6JIAjwxnOmfOMnNenYM0FXSZjO52Mo1bjzn_RiCci2705clf";
API_KEY = "tTmLEx6Ju4ZtpFQsp0FhpJHPxgmeDItIsnAYJ7aU_RiCci2705clf";

Observações:
Certifique-se de ter as bibliotecas necessárias no classpath do seu projeto.
Os métodos para gerar relatórios e criar o banco de dados atualizam os dados da tabela Swing antes de gerar os relatórios ou criar o banco de dados.
As mensagens de sucesso e erro são exibidas usando JOptionPane para interação do usuário.
