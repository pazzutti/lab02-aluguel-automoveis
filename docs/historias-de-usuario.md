## Histórias de Usuário

## HU01 — Cadastrar-se como cliente
*Como* pessoa interessada em alugar um automóvel, *eu quero* me cadastrar informando meus dados
pessoais, minha profissão e minhas entidades empregadoras, *para que* eu possa usar o sistema e
ter meus pedidos analisados.

*Critérios de aceitação*
- O cadastro exige RG, CPF, nome, endereço e profissão.
- É possível informar até 3 entidades empregadoras, cada uma com o rendimento auferido.
- O cadastro é recusado se o CPF já estiver registrado.

## HU02 — Cadastrar-se como agente
*Como* representante de uma empresa ou de um banco, *eu quero* cadastrar minha instituição como
agente, *para que* eu possa analisar e avaliar os pedidos de aluguel.

*Critérios de aceitação*
- O cadastro registra os dados da instituição e o tipo de agente (empresa ou banco).
- O tipo informado define as funcionalidades liberadas; apenas bancos concedem crédito.
- O cadastro é recusado se a instituição já estiver registrada.

## HU03 — Realizar login
*Como* usuário do sistema, *eu quero* acessar o sistema informando meu identificador e minha
senha, *para que* apenas pessoas autorizadas usem minha conta e o sistema saiba quais
funcionalidades liberar para o meu perfil.

*Critérios de aceitação*
- Com credenciais corretas, o acesso é liberado e o perfil (cliente, empresa ou banco) é identificado.
- Com senha incorreta, o sistema informa a falha sem revelar se o usuário existe.
- Nenhuma funcionalidade do sistema fica disponível antes do login.

## HU04 — Manter meus dados cadastrais
*Como* usuário cadastrado, *eu quero* consultar e atualizar meus dados, *para que* minhas
informações estejam corretas quando um pedido meu for analisado.

*Critérios de aceitação*
- É possível alterar endereço, profissão e entidades empregadoras.
- RG e CPF não podem ser alterados após o cadastro.
- O limite de 3 entidades empregadoras continua valendo na alteração.

## HU05 — Consultar automóveis disponíveis
*Como* cliente, *eu quero* consultar os automóveis disponíveis, *para que* eu escolha o veículo
antes de fazer o pedido.

*Critérios de aceitação*
- A listagem exibe placa, ano, marca e modelo de cada automóvel.
- É possível filtrar por marca, modelo e ano.
- Automóveis já vinculados a um contrato em execução não aparecem como disponíveis.

## HU06 — Criar pedido de aluguel
*Como* cliente, *eu quero* criar um pedido escolhendo o automóvel e a modalidade de contrato,
*para que* minha solicitação seja analisada por um agente.

*Critérios de aceitação*
- O cliente escolhe entre locação, assinatura ou leasing.
- O pedido é gravado com identificador próprio e status "não avaliado".
- O pedido só é aceito se o cliente tiver pelo menos uma entidade empregadora com rendimento registrado.

## HU07 — Consultar meus pedidos
*Como* cliente, *eu quero* consultar meus pedidos e a situação de cada um, *para que* eu acompanhe
o andamento das minhas solicitações.

*Critérios de aceitação*
- A listagem exibe identificador, automóvel, modalidade, data e situação do pedido.
- O cliente visualiza apenas os próprios pedidos.
- Pedidos já avaliados exibem o parecer e a justificativa do agente.

## HU08 — Alterar um pedido
*Como* cliente, *eu quero* alterar um pedido que ainda não foi avaliado, *para que* eu corrija o
automóvel ou a modalidade sem precisar refazer a solicitação.

*Critérios de aceitação*
- A alteração só é permitida enquanto o pedido não tiver parecer registrado.
- Pedidos já avaliados não oferecem a opção de alteração.
- A alteração registra a data e a hora da modificação.

## HU09 — Cancelar um pedido
*Como* cliente, *eu quero* cancelar um pedido que ainda não foi avaliado, *para que* eu desista da
solicitação sem gerar compromisso.

*Critérios de aceitação*
- O cancelamento só é permitido enquanto o pedido não tiver parecer registrado.
- O sistema pede confirmação antes de cancelar.
- O pedido cancelado sai da fila de análise dos agentes.

## HU10 — Consultar pedidos pendentes de análise
*Como* agente, *eu quero* ver a lista dos pedidos que aguardam avaliação, *para que* eu organize
meu trabalho de análise.

*Critérios de aceitação*
- A fila exibe apenas pedidos ainda não avaliados.
- Pedidos cancelados pelo cliente somem da fila automaticamente.
- É possível ordenar a fila pela data de criação do pedido.

## HU11 — Analisar um pedido
*Como* agente, *eu quero* consultar os dados de identificação, a profissão e os rendimentos do
contratante, *para que* eu avalie a capacidade financeira dele.

*Critérios de aceitação*
- A tela exibe RG, CPF, nome, endereço, profissão e as entidades empregadoras com seus rendimentos.
- O sistema apresenta o rendimento total somado do contratante.
- Ao iniciar a análise, o pedido passa para a situação "em análise".

## HU12 — Registrar parecer sobre o pedido
*Como* agente, *eu quero* registrar o parecer como favorável ou desfavorável com uma justificativa,
*para que* a decisão fique documentada e o cliente seja informado.

*Critérios de aceitação*
- O parecer exige o resultado (favorável ou desfavorável) e uma justificativa.
- O sistema grava o agente responsável, a data e a hora do parecer.
- Depois do parecer, o cliente não pode mais alterar nem cancelar o pedido.

## HU13 — Ser avisado do resultado da análise
*Como* cliente, *eu quero* ser notificado quando meu pedido for avaliado, *para que* eu tome a
decisão sem precisar conferir o sistema o tempo todo.

*Critérios de aceitação*
- A notificação é enviada assim que o parecer é registrado.
- A notificação informa o pedido e o resultado da avaliação.
- A notificação fica disponível para consulta dentro do sistema.

## HU14 — Decidir sobre um pedido aprovado
*Como* cliente, *eu quero* aceitar ou recusar um pedido com parecer favorável, *para que* eu
decida se o aluguel avança para a execução do contrato.

*Critérios de aceitação*
- A decisão só é oferecida para pedidos com parecer favorável.
- Ao aceitar, o pedido segue para a geração do contrato.
- Ao recusar, o pedido é encerrado.

## HU15 — Ter o contrato gerado conforme a modalidade
*Como* cliente, *eu quero* que o contrato seja gerado de acordo com a modalidade escolhida, *para
que* a execução do aluguel tenha respaldo formal.

*Critérios de aceitação*
- Na locação, o contrato registra o prazo e a data prevista de devolução do veículo.
- Na assinatura, o contrato registra o valor da mensalidade e a recorrência.
- No leasing, o contrato só é concluído depois da concessão do contrato de crédito por um banco.

## HU16 — Conceder o contrato de crédito do leasing
*Como* banco agente, *eu quero* conceder o contrato de crédito ligado a um aluguel na modalidade
leasing, *para que* o contrato de longo prazo possa ser executado.

*Critérios de aceitação*
- A funcionalidade só está disponível para agentes do tipo banco.
- Cada contrato de crédito é vinculado a um único contrato de leasing.
- O contrato de leasing fica pendente enquanto o crédito não for concedido.

## HU17 — Registrar o proprietário do automóvel
*Como* sistema, *eu quero* registrar o proprietário do automóvel conforme a modalidade
contratada, *para que* a titularidade do veículo fique sempre correta.

*Critérios de aceitação*
- Na locação e na assinatura, o automóvel permanece como propriedade da empresa.
- No leasing, o automóvel fica registrado como propriedade do banco durante a vigência do crédito.
- Todo automóvel tem, a qualquer momento, exatamente um proprietário registrado.

## HU18 — Registrar a devolução do veículo
*Como* empresa, *eu quero* registrar a devolução do veículo ao fim de um contrato de locação,
*para que* o automóvel volte a ficar disponível para novos pedidos.

*Critérios de aceitação*
- A devolução só pode ser registrada para contratos de locação em execução.
- A data efetiva da devolução é gravada no contrato.
- Ao registrar a devolução, o contrato é encerrado e o automóvel volta a ficar disponível.

## HU19 — Consultar meus contratos
*Como* usuário cadastrado, *eu quero* consultar os contratos dos quais participo, *para que* eu
acompanhe prazos, valores e situação.

*Critérios de aceitação*
- O cliente vê apenas os contratos em que é contratante.
- O agente vê apenas os contratos em que atuou como empresa ou banco.
- A consulta exibe modalidade, automóvel, datas e situação do contrato.

## HU20 — Manter o cadastro de automóveis
*Como* empresa agente, *eu quero* cadastrar e manter os automóveis, *para que* eles fiquem
disponíveis para os clientes.

*Critérios de aceitação*
- O cadastro exige placa, ano, marca e modelo.
- A placa é única no sistema.
- Automóveis ligados a contratos em execução não podem ser excluídos.

## HU21 — Acessar o sistema pela internet
*Como* usuário, *eu quero* acessar o sistema pelo navegador do meu computador, *para que* eu use
os serviços sem instalar nenhum programa.

*Critérios de aceitação*
- Todas as funcionalidades são acessíveis pela internet, via navegador.
- As páginas são montadas dinamicamente a partir dos dados do sistema de gestão.
- O conteúdo exibido muda conforme o perfil de quem está logado.

---

## Requisitos Funcionais

RF01 - Permitir o cadastro de clientes com RG, CPF, nome, endereço e profissão.
RF02 - Permitir o registro de até 3 entidades empregadoras por cliente, cada uma com seu rendimento.
RF03 - Permitir o cadastro de agentes, diferenciando empresas de bancos.
RF04 - Autenticar o usuário e liberar as funcionalidades conforme o perfil.
RF05 - Permitir a consulta e a atualização dos dados cadastrais do usuário.
RF06 - Manter o cadastro de automóveis com placa, ano, marca e modelo.
RF07 - Permitir a consulta aos automóveis disponíveis.
RF08 - Permitir ao cliente criar pedidos de aluguel indicando automóvel e modalidade.
RF09 - Permitir ao cliente consultar seus próprios pedidos e a situação de cada um.
RF10 - Permitir ao cliente alterar um pedido enquanto ele não tiver sido avaliado.
RF11 - Permitir ao cliente cancelar um pedido enquanto ele não tiver sido avaliado.
RF12 - Disponibilizar aos agentes a lista de pedidos pendentes de avaliação.
RF13 - Permitir ao agente consultar os dados de identificação, profissão e rendimentos do contratante.
RF14 - Calcular e exibir o rendimento total do contratante.
RF15 - Permitir ao agente registrar parecer favorável ou desfavorável, com justificativa, autor e data.
RF16 - Encaminhar ao cliente os pedidos com parecer favorável e encerrar os de parecer desfavorável.
RF17 - Notificar o cliente sobre o resultado da avaliação do pedido.
RF18 - Permitir ao cliente aceitar ou recusar o avanço do pedido aprovado para contrato.
RF19 - Gerar o contrato de aluguel conforme a modalidade: locação, assinatura ou leasing.
RF20 - Permitir ao banco agente conceder o contrato de crédito vinculado a um contrato de leasing.
RF21 - Registrar o proprietário do automóvel (cliente, empresa ou banco) conforme a modalidade.
RF22 - Permitir à empresa registrar a devolução do veículo nos contratos de locação.
RF23 - Permitir ao usuário consultar os contratos dos quais participa.
RF24 - Controlar a situação do pedido ao longo de todo o seu ciclo de vida.
RF25 - Construir dinamicamente as páginas web conforme o perfil do usuário e os dados do sistema de gestão.