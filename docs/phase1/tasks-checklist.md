# Tasks checklist

## Funcionalidades obrigatórias

### Fluxos principais

**Criação da Ordem de Serviço (OS):**

- Identificação do cliente por CPF/CNPJ; (✅) :517
- Cadastro de veículo (placa, marca, modelo, ano); (✅) :2373
- Inclusão dos serviços solicitados (exemplo: troca de óleo, alinhamento); (✅) :2571
- Possibilidade de incluir peças e insumos necessários; (✅) :2469
- Orçamento gerado automaticamente com base nos serviços e peças; (✅) :2806
- Envio do orçamento ao cliente para aprovação. (✅) :2876

**Acompanhamento da OS:**

- Status da OS: (✅) WorkOrderTest
  - Recebida;
  - Em diagnóstico;
  - Aguardando aprovação;
  - Em execução;
  - Finalizada;
  - Entregue.
- Alteração automática dos status conforme ações no sistema; (✅) CompleteFlowTest
- Permitir consulta por parte do cliente via API para acompanhar o progresso. (✅) :6595

**Gestão administrativa:**

- CRUD de clientes; (✅) :313 :517
- CRUD de veículos; (✅) :2373 :5358
- CRUD de serviços; (✅) :2571 :3433
- CRUD de peças e insumos, com controle de estoque; (✅) :2469 :3765
- Listagem e detalhamento de ordens de serviço; (✅) :6595
- Monitoramento do tempo médio de execução dos serviços. (✅) :3433

**Segurança e qualidade:**

- Implementação de autenticação JWT para APIs administrativas; (✅) :281
- Validação dos dados sensíveis (CPF/CNPJ, placa de veículo); (✅) CustomerControllerTest:134
- Testes unitários e de integração para os principais fluxos. (✅)

## Requisitos técnicos

- Back-end monolítico. (✅)
- Como será um MVP, é possível criar um Monolito utilizando a arquitetura em camadas. (✅)
- A escolha do banco de dados é livre, mas é necessário justificar a preferência pelo banco utilizado. (✅) https://github.com/rodsordi/15SOAT-TechChallenge/wiki/ADR%E2%80%90001
- APIs RESTful documentadas via Swagger ou similar. (✅) readme
- Dockerfile para build da aplicação. (✅) readme
- docker-compose.yml para orquestrar ambiente completo. (✅) readme
- Testes automatizados com cobertura mínima de 80% nos domínios críticos. (✅) pdf
- Configuração para execução local simples (README.md explicativo). (✅) readme
- Organização em repositório privado com acesso ao usuário soat-architecture (✅)
- --            

## Entregáveis da Fase 1

- Vídeo de até 15 minutos demonstrando todos os pontos (pode ser em grupo ou individual); (❌)
- Documentação DDD (Miro ou equivalente), com:
  - Event Storming completo dos fluxos:
    - Criação e acompanhamento da OS; (✅) pdf
    - Gestão de peças e insumos; (✅) pdf
  - Diagramas conforme apresentado na disciplina de DDD;
    - domain story telling; (✅) pdf
    - c4model; (✅) pdf
  - Linguagem Ubíqua aplicada.
    - dicionário de linguagem ubíqua; (✅) pdf
- Código-fonte no repositório privado, incluindo:
  - APIs conforme requisitos; (✅) readme:68
  - Dockerfile e docker-compose configurados; (✅) readme
  - README.md completo com instruções de uso e objetivos. (✅)
- Relatório com análise de vulnerabilidades:
  - Adicionar no relatório a análise do scan realizado no código. (✅) pdf
- Documento de entrega (PDF) com:
  - Nome do grupo; (✅)
  - Participantes e usernames no Discord; (✅)
  - Link da documentação; (✅) pdf
  - Link do repositório; (✅) pdf
  - Relatório com análise de vulnerabilidades encontradas no sistema. (✅) pdf