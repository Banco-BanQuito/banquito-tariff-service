# Banquito Tariff Service

Microservicio RF-04 para calcular la comision transaccional por lotes de pagos del Switch BanQuito.

Este servicio recibe la cantidad de transacciones exitosas de un lote, busca la tarifa aplicable en PostgreSQL, calcula la comision subtotal, calcula IVA, devuelve el valor total de cobro y registra el calculo en la tabla `service_charge`.

## Responsabilidad del microservicio

El `tariff-service` solamente calcula tarifas. No debita dinero, no mueve fondos, no genera asientos contables y no llama directamente al Core Bancario.

Flujo esperado:

```text
Paul / Switch
  -> llama a tariff-service
  -> recibe totalCharge
  -> envia a Oscar / Core Bancario:
     payrollTotalAmount + commissionAmount
```

Payload que Paul debe enviar a Oscar usando el resultado de este micro:

```json
{
  "payrollTotalAmount": 10000.00,
  "commissionAmount": 66.24
}
```

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- H2 solo para pruebas automatizadas

## Puerto

El servicio corre en:

```text
REST: http://localhost:8086
gRPC: localhost:9090
```

Configurado en:

```properties
server.port=8086
grpc.server.port=9090
```

## Base de datos

Base usada por el micro:

```text
PostgreSQL
Host: localhost
Puerto: 5432
BD: tariffdb
```

Configuracion actual en `src/main/resources/application.properties`:

```properties
spring.datasource.url=${POSTGRES_URL:jdbc:postgresql://localhost:5432/tariffdb}
spring.datasource.username=${POSTGRES_USER:postgres}
spring.datasource.password=${POSTGRES_PASSWORD:1234}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=none
```

Si tu PostgreSQL usa otras credenciales, puedes cambiarlas en `application.properties` o definir variables de entorno:

```powershell
$env:POSTGRES_URL="jdbc:postgresql://localhost:5432/tariffdb"
$env:POSTGRES_USER="banquito"
$env:POSTGRES_PASSWORD="banquito123"
```

## Tablas usadas

### payment_tariff

Tabla de rangos de tarifas. El micro la consulta para saber cuanto cobrar por transaccion.

Estructura esperada:

```sql
CREATE TABLE IF NOT EXISTS payment_tariff (
    id BIGSERIAL PRIMARY KEY,
    min_transactions INTEGER NOT NULL,
    max_transactions INTEGER NOT NULL,
    unit_fee NUMERIC(10,2) NOT NULL,
    description VARCHAR(50) NOT NULL
);
```

Rangos esperados:

| Rango de transacciones | Precio por tx |
| --- | --- |
| 1 - 10 | 1.00 |
| 11 - 50 | 0.90 |
| 51 - 100 | 0.80 |
| 101 - 500 | 0.70 |
| 501 - 1000 | 0.60 |
| 1001 - 9999 | 0.50 |

Carga de datos:

```sql
INSERT INTO payment_tariff
(min_transactions, max_transactions, unit_fee, description)
VALUES
(1, 10, 1.00, '1-10 tx'),
(11, 50, 0.90, '11-50 tx'),
(51, 100, 0.80, '51-100 tx'),
(101, 500, 0.70, '101-500 tx'),
(501, 1000, 0.60, '501-1000 tx'),
(1001, 9999, 0.50, '1001-9999 tx');
```

### service_charge

Tabla historica. Se llena cada vez que Paul llama al endpoint de calculo.

Estructura esperada:

```sql
CREATE TABLE IF NOT EXISTS service_charge (
    id BIGSERIAL PRIMARY KEY,
    batch_id VARCHAR(100) NOT NULL,
    successful_tx INTEGER NOT NULL,
    unit_fee NUMERIC(10,2) NOT NULL,
    commission_subtotal NUMERIC(14,2) NOT NULL,
    iva_rate NUMERIC(4,2) NOT NULL,
    iva_amount NUMERIC(14,2) NOT NULL,
    total_charge NUMERIC(14,2) NOT NULL,
    tariff_range_applied VARCHAR(50) NOT NULL,
    calculated_at TIMESTAMP NOT NULL
);
```

## Como iniciar el proyecto

### 1. Levantar PostgreSQL

Si tienes `docker-compose` configurado:

```powershell
docker-compose up -d postgres
```

Si usas PostgreSQL instalado localmente, solo verifica que este escuchando en `localhost:5432`.

### 2. Verificar tablas y datos

En pgAdmin, DBeaver, IntelliJ Database o `psql`, revisa:

```sql
SELECT * FROM payment_tariff;
```

Deben existir los 6 rangos.

### 3. Ejecutar el microservicio

Desde terminal:

```powershell
.\mvnw.cmd spring-boot:run
```

O desde IntelliJ ejecutando:

```text
BanquitoTariffServiceApplication
```

## Endpoints

### Health

Verifica que el micro este arriba.

```http
GET http://localhost:8086/api/v2/tariff/health
```

Respuesta esperada:

```json
{
  "service": "tariff-service",
  "version": "2.0",
  "status": "UP"
}
```

### Consultar rangos

Devuelve las tarifas configuradas en `payment_tariff`.

```http
GET http://localhost:8086/api/v2/tariff/ranges
```

### Calcular comision

Endpoint principal llamado por Paul.

```http
GET http://localhost:8086/api/v2/tariff/calculate?successful_tx=72&batchId=test-batch-001
```

Parametros:

| Parametro | Tipo | Descripcion |
| --- | --- | --- |
| `successful_tx` | Integer | Cantidad de transacciones exitosas. Debe ser mayor o igual a 1. |
| `batchId` | String | Identificador del lote calculado. |

Respuesta esperada para 72 transacciones:

```json
{
  "successfulTx": 72,
  "unitFee": 0.80,
  "commissionSubtotal": 57.60,
  "ivaRate": 0.15,
  "ivaAmount": 8.64,
  "totalCharge": 66.24,
  "tariffRangeApplied": "51-100 tx"
}
```

Explicacion del calculo:

```text
72 tx entran en el rango 51-100
unitFee = 0.80
commissionSubtotal = 72 * 0.80 = 57.60
ivaAmount = 57.60 * 0.15 = 8.64
totalCharge = 57.60 + 8.64 = 66.24
```

Despues de llamar el endpoint, se guarda un registro en `service_charge`.

Verificacion:

```sql
SELECT *
FROM service_charge
WHERE batch_id = 'test-batch-001';
```

## Comunicacion gRPC para Paul

Ademas del endpoint REST, el micro expone un servidor gRPC en:

```text
localhost:9090
```

El contrato esta definido en:

```text
src/main/proto/tariff.proto
```

Servicio gRPC:

```proto
service TariffGrpcService {
  rpc CalculateTariff (TariffCalculationGrpcRequest) returns (TariffCalculationGrpcResponse);
}
```

Request:

```proto
message TariffCalculationGrpcRequest {
  int32 successful_tx = 1;
  string batch_id = 2;
}
```

Response:

```proto
message TariffCalculationGrpcResponse {
  int32 successful_tx = 1;
  string unit_fee = 2;
  string commission_subtotal = 3;
  string iva_rate = 4;
  string iva_amount = 5;
  string total_charge = 6;
  string tariff_range_applied = 7;
}
```

Los valores monetarios viajan como `string` para no perder precision decimal.

Ejemplo conceptual de cliente gRPC para Paul:

```java
ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 9090)
        .usePlaintext()
        .build();

TariffGrpcServiceGrpc.TariffGrpcServiceBlockingStub stub =
        TariffGrpcServiceGrpc.newBlockingStub(channel);

TariffCalculationGrpcResponse response = stub.calculateTariff(
        TariffCalculationGrpcRequest.newBuilder()
                .setSuccessfulTx(72)
                .setBatchId("test-batch-001")
                .build()
);

BigDecimal commissionAmount = new BigDecimal(response.getTotalCharge());

channel.shutdown();
```

Luego Paul envia a Oscar/Core:

```json
{
  "payrollTotalAmount": 10000.00,
  "commissionAmount": 66.24
}
```

Importante: tanto REST como gRPC usan la misma logica interna `TariffCalculationService`, por eso ambos calculan igual y ambos registran el resultado en `service_charge`.

## Pruebas manuales

### Prueba 1: Health

```powershell
Invoke-WebRequest -Uri "http://localhost:8086/api/v2/tariff/health" -UseBasicParsing
```

Debe devolver estado `UP`.

### Prueba 2: Calculo rango 51-100

```powershell
Invoke-WebRequest -Uri "http://localhost:8086/api/v2/tariff/calculate?successful_tx=72&batchId=test-batch-001" -UseBasicParsing
```

Debe devolver `totalCharge` igual a `66.24`.

### Prueba 3: Verificar historico

Ejecutar en PostgreSQL:

```sql
SELECT batch_id, successful_tx, unit_fee, commission_subtotal, iva_amount, total_charge, tariff_range_applied
FROM service_charge
ORDER BY id DESC;
```

Debe aparecer el lote calculado.

### Prueba gRPC

Para probar gRPC puedes usar `grpcurl` si lo tienes instalado. Primero levanta el micro:

```powershell
mvn spring-boot:run
```

Luego ejecuta:

```powershell
grpcurl -plaintext -d "{\"successful_tx\":72,\"batch_id\":\"test-batch-grpc-001\"}" localhost:9090 banquito.tariff.TariffGrpcService/CalculateTariff
```

Respuesta esperada:

```json
{
  "successfulTx": 72,
  "unitFee": "0.80",
  "commissionSubtotal": "57.60",
  "ivaRate": "0.15",
  "ivaAmount": "8.64",
  "totalCharge": "66.24",
  "tariffRangeApplied": "51-100 tx"
}
```

### Prueba 4: Sin tarifa aplicable

Si envias una cantidad fuera de los rangos cargados:

```http
GET http://localhost:8086/api/v2/tariff/calculate?successful_tx=10000&batchId=test-batch-no-range
```

Debe devolver error `404` indicando que no existe tarifa para ese lote.

### Prueba 5: Parametro invalido

```http
GET http://localhost:8086/api/v2/tariff/calculate?successful_tx=0&batchId=test-batch-invalid
```

Debe devolver error `400`, porque `successful_tx` debe ser minimo 1.

## Pruebas automatizadas

El proyecto incluye una prueba de contexto Spring Boot.

Ejecutar:

```powershell
.\mvnw.cmd test
```

Resultado esperado:

```text
BUILD SUCCESS
```

Durante pruebas se usa H2 en memoria con el perfil `test`, configurado en:

```text
src/test/resources/application-test.properties
```

Esto evita depender de PostgreSQL para compilar y validar el contexto.

## Estructura del proyecto

```text
src/main/java/ec/edu/espe/banquito/banquitotariffservice
|-- controller
|   |-- HealthController.java
|   `-- TariffController.java
|-- dto
|   |-- TariffCalculationRequest.java
|   |-- TariffCalculationResponse.java
|   `-- TariffRangeResponse.java
|-- exception
|   |-- GlobalExceptionHandler.java
|   `-- TariffNotFoundException.java
|-- model
|   |-- PaymentTariff.java
|   `-- ServiceCharge.java
|-- repository
|   |-- PaymentTariffRepository.java
|   `-- ServiceChargeRepository.java
`-- service
    |-- TariffCalculationService.java
    `-- TariffQueryService.java
```

## Relacion con RF-04

RF-04 indica que el Switch debe delegar el movimiento de fondos al Core Bancario.

Este micro no hace esa delegacion directamente. Su aporte al RF-04 es entregar a Paul el monto de comision calculada:

```json
{
  "totalCharge": 66.24
}
```

Paul debe tomar ese valor y llamar a Oscar/Core Bancario con:

```json
{
  "payrollTotalAmount": 10000.00,
  "commissionAmount": 66.24
}
```

El Core Bancario es el unico responsable de procesar internamente el cobro.

## Errores comunes

### Whitelabel Error Page / 500

Generalmente ocurre cuando la base `tariffdb` no tiene las tablas esperadas o no tiene los datos de `payment_tariff`.

Verifica:

```sql
SELECT * FROM payment_tariff;
SELECT * FROM service_charge;
```

### Error de credenciales PostgreSQL

Revisa `application.properties` o configura variables:

```powershell
$env:POSTGRES_USER="postgres"
$env:POSTGRES_PASSWORD="1234"
```

### No existe tarifa

Si el numero de transacciones no cae en ningun rango, el micro devuelve `404`.

Ejemplo: si tus rangos llegan hasta `9999`, `successful_tx=10000` no tendra tarifa.
