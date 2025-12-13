@echo off
REM Script de testes para endpoints de Exames (use com a aplicação rodando em http://localhost:8080)
REM Antes de executar, você pode configurar o stub editando application.properties ou iniciando a aplicação com argumentos:
REM java -jar target\medflow-apresentacao-backend-0.0.1-SNAPSHOT.jar --medflow.stub.pacientes.invalid=999 --medflow.stub.medicos.inativos=555 --medflow.stub.medicos.indisponiveis=777 --medflow.stub.tipos.invalid=naoexiste

set BASE_URL=http://localhost:8080

echo ==================================================
echo 1) Teste feliz: agendar exame valido
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":123,\"medicoId\":456,\"tipoExame\":\"raio-x\",\"dataHora\":\"2025-12-20T10:00:00\",\"responsavelId\":1}" %BASE_URL%/exames

echo.
echo ==================================================
echo 2) Teste inválido: dataHora ausente (espera 400)
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":123,\"medicoId\":456,\"tipoExame\":\"raio-x\",\"responsavelId\":1}" %BASE_URL%/exames

echo.
echo ==================================================
echo 3) Teste paciente inválido (configure medflow.stub.pacientes.invalid=999 e use pacienteId=999) (espera 400 domínio)
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":999,\"medicoId\":456,\"tipoExame\":\"raio-x\",\"dataHora\":\"2025-12-21T10:00:00\",\"responsavelId\":1}" %BASE_URL%/exames

echo.
echo ==================================================
echo 4) Teste conflito: cria um agendamento e tenta criar outro para o mesmo paciente na mesma data/hora (o segundo deve falhar)
echo (a) criando primeiro agendamento (guardar o id manualmente se quiser)
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":222,\"medicoId\":333,\"tipoExame\":\"sangue\",\"dataHora\":\"2025-12-22T11:00:00\",\"responsavelId\":1}" %BASE_URL%/exames

echo (b) tentando criar agendamento conflitivo (deve retornar erro de domínio)
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":222,\"medicoId\":444,\"tipoExame\":\"sangue\",\"dataHora\":\"2025-12-22T11:00:00\",\"responsavelId\":1}" %BASE_URL%/exames

echo.
echo ==================================================
echo 5) Teste médico inativo (configure medflow.stub.medicos.inativos=555 e use medicoId=555) (espera 400)
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":321,\"medicoId\":555,\"tipoExame\":\"ultrassonografia\",\"dataHora\":\"2025-12-23T09:00:00\",\"responsavelId\":1}" %BASE_URL%/exames

echo.
echo ==================================================
echo 6) Teste médico indisponível (configure medflow.stub.medicos.indisponiveis=777 e use medicoId=777) (espera 400)
curl -i -H "Content-Type: application/json" -d "{\"pacienteId\":654,\"medicoId\":777,\"tipoExame\":\"sangue\",\"dataHora\":\"2025-12-24T14:00:00\",\"responsavelId\":1}" %BASE_URL%/exames

echo.
echo ==================================================
echo 7) Teste cancelamento sem motivo (espera 400 - validação)
curl -i -X PATCH -H "Content-Type: application/json" -d "{\"motivo\":\"\",\"responsavelId\":1}" %BASE_URL%/exames/1/cancelamento

echo.
echo ==================================================
echo 8) Teste cancelamento com motivo (espera 200 e status CANCELADO)
curl -i -X PATCH -H "Content-Type: application/json" -d "{\"motivo\":\"Paciente não compareceu\",\"responsavelId\":1}" %BASE_URL%/exames/1/cancelamento

echo.
echo ==================================================
echo 9) Teste tentativa de exclusão (DELETE) — passe responsavelId como query param (espera 204 ou 400 dependendo do estado)
curl -i -X DELETE %BASE_URL%/exames/1?responsavelId=1

echo.
echo FIM dos testes. 
echo Note: leia as respostas HTTP e mensagens JSON do servidor para validar cada caso.
pause