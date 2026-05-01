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
   
