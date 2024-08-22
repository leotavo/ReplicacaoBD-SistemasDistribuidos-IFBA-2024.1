Instituto Federal da Bahia

Análise e Desenvolvimento de Sistemas

INF020 – Sistemas Distribuídos – Semestre: 2024.1 - Data: 14/08/2024

Especificação de Trabalho

A partir do código-fonte exemplo de RMI apresentado em aula, desenvolver um sistema de replicação em Banco de Dados (replicação ativa) baseado no conceito de comunicação em grupo. 

O grupo tem um Líder (iniciando com o ID 0) e permite novos membros solicitem adesão ao grupo. Todos os testes devem utilizar 3 membros do grupo. Cada membro deve estar associado a uma base de dados.

Deve-se criar uma interface de cliente SQL que ao se enviar um comando envie ao Líder do Grupo. O Líder do Grupo deve enviar a mensagem ao grupo, observando a entrega uniforme. 

Um comando SQL entregue como mensagem será executado na ordem definida pelo grupo. 

Desta forma a replicação ativa será realizada baseada na comunicação em grupo.

O Líder deve ter um mecanismo de detecção de defeito dos Membros, excluindo o membro defeituoso do grupo. Não é necessário implementar a detecção de falhas do líder e a eleição.

O trabalho pode também ser implementado usando um middleware/framework de computação distribuída, apresente uma outra forma de fazer a replicação. Pode-se usar, por exemplo, RabbitMQ, JMS, JGroups, OSE Python, ActiveMQ, JGroups, Google Cloud Pub/Sub, Kafka, REST, dentre outros.

O trabalho deve ser feito em grupo de até 3 estudantes e apresentado no dia 05/09/2024 (repositório github do código fonte).
