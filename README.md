# vemk-pedir-api
api in Java 17, spring boot 3.x

Esse é um projeto para demonstrar minhas decisões ao desenvolver em Java/React.

Etapa 1:
Comecei o projeto configurando o básico no Spring Initializr

 ** Java 17, spring 3.5.14, maven

  Spring Web

  Spring Data JPA

  Validation

  Lombok

  H2

  Spring Security

Adicionados .env, gitignore e configurações básicas.

Etapa 2:
Aqui fiz algumas perguntas para a IA (GPT) para decidir a arquitetura do projeto. Não usarei microserviços, DDD ou algo avançado.

 ** Spring Web + arquitetura em camadas (controller -> service -> repository -> model/dto)


Etapa 3:
Aqui eu inicio o entendimento dos requisitos, com o uso da IA(sonnet 4.6) crio um plano de etapas para implementar os requisitos
 **Plano em etapas (incremental e validável):
   
   Etapa 1: Modelo e persistência mínima
   Objetivo: salvar pedido bruto e pedido estruturado no banco.
   Entregas:
   Criar entidades Pedido e ItemPedido.
   Relacionamento 1:N entre pedido e itens.
   Campos principais: textoOriginal, cliente, dataEntrega, itens, createdAt.
   Critério de pronto: conseguimos criar e listar pedidos estruturados no banco H2.

   Etapa 2: Parser inicial de texto livre
   Objetivo: transformar frase em estrutura de pedido.
   Entregas:
   IAParsingService com parser heurístico inicial (regex/regras).
   Extrair quantidade + produto.
   Detectar data simples (amanhã, hoje, data explícita básica).
   Critério de pronto: frase exemplo vira estrutura correta com itens e data.
   
   Etapa 3: Endpoints do desafio
   Objetivo: expor API conforme especificação.
   Entregas:
   POST /pedido
   GET /pedidos
   GET /pedido/{id}
   DTOs de entrada/saída alinhados ao formato esperado.
   Tratamento de erros (id inexistente, payload inválido).
   Critério de pronto: coleção de requests (Postman/Insomnia) passando.
   
   Etapa 4: Frontend React básico (em pasta separada)
   Objetivo: fluxo completo ponta a ponta.
   Entregas:
   Campo de texto livre para pedido.
   Lista de pedidos estruturados.
   Visualização de detalhes por id.
   Serviço de API centralizado.
   Critério de pronto: criar pedido pela UI e ver na listagem.
   
   Etapa 5: refinamento
   Objetivo: qualidade mínima para evoluir.
   Entregas:
   Testes de parser.
   Testes de controller.
   Pequena documentação de uso.
   Critério de pronto: execução estável e previsível.


Observações:

Um ponto comum é que, ao pedir alguma criação ou edição e afins, a IA acaba não fazendo coisas básicas como ENVs ou configurações; por isso, é sempre indicado checar tudo antes do push para o git.

Outro ponto foi o desenvolvimento de um parsing bem simples, usando regex para editar os dados; com isso, no meu entendimento, fugiu do que eu gostaria, que é o uso de LLMs, para que tenha sentido no teste. Optei por usar o parsing simples de fallback, deixando apenas quando falha.

Perda de contexto: outras vezes a IA perdia o contexto e refazia pontos que já tinha evoluído ao fazer o review notava esses erros, que são comuns. Eu corrigi e optei por ter uma pasta de contextos, nada complexo, apenas uma pasta com alguns .md que deixo no gitignore para não "sujar" o projeto.

---

## Como rodar o projeto

### Pré-requisitos
- Java 17+
- Maven (ou use o wrapper `./mvnw` incluído no projeto)

### 1. Configure o `.env`

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
APP_SECURITY_USER=dev
APP_SECURITY_PASSWORD=change-me
GEMINI_API_KEY=sua-chave-aqui
```

### 2. Suba o backend

```bash
set -a && source .env && set +a && ./mvnw spring-boot:run
```

O servidor inicia na porta **8080** e fica aguardando requisições.  
Para encerrar, use `Ctrl+C`.

### 3. Autenticação

A API usa **HTTP Basic Auth**. Todas as requisições devem incluir o header:

```
Authorization: Basic <base64(user:password)>
```

Exemplo com as credenciais padrão (`dev:change-me`):

```
Authorization: Basic ZGV2OmNoYW5nZS1tZQ==
```

### 4. Endpoints disponíveis

| Método | URL | Descrição |
|--------|-----|-----------|
| `POST` | `/pedido` | Cria um novo pedido |
| `GET` | `/pedidos` | Lista todos os pedidos |
| `GET` | `/pedido/{id}` | Busca pedido por ID |

### 5. Exemplo de requisição

```json
POST /pedido
{
  "textoOriginal": "Quero 2 pizzas e 3 sucos para amanhã"
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "textoOriginal": "Quero 2 pizzas e 3 sucos para amanhã",
  "cliente": "desconhecido",
  "dataEntrega": "2026-05-03",
  "itens": [
    { "produto": "pizzas", "quantidade": 2 },
    { "produto": "sucos", "quantidade": 3 }
  ],
  "createdAt": "2026-05-02T14:30:00"
}
```

### 6. Frontend

O CORS está liberado para `http://localhost:5173` (Vite) e `http://localhost:3000`.  
Configure o `.env` do frontend com:

```env
VITE_API_URL=http://localhost:8080
```
