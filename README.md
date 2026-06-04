# AirWatch 🌍
### Plataforma de Monitoramento de Qualidade do Ar
**Grupo Solsticio | FIAP 2026 | Java Advanced**

---

## 👥 Integrantes

| RM | Nome | Turma |
|---|---|---|
| RM565760 | Enrico Delesporte | 2TDSPG |

---

## 🔗 Links do Projeto

| Recurso | URL |
|---|---|
| **Repositório GitHub** | https://github.com/Solsticio-AirWatch/AirWatch-Java_Back_End |
| **API em Nuvem (Deploy)** | http://102.37.101.26:8080 |
| **Swagger UI** | http://102.37.101.26:8080/swagger-ui.html |
| **Health Check** | http://102.37.101.26:8080/actuator/health |
| **Vídeo de Apresentação** | (adicionar link YouTube) |
| **Vídeo Pitch** | (adicionar link YouTube) |

---

## 📌 Sobre o Projeto

O **AirWatch** é uma plataforma de monitoramento de qualidade do ar em tempo real que conecta tecnologia espacial a um problema urbano real. Utiliza dados de satélites (NASA POWER, ESA Sentinel-5P, OpenAQ) e sensores IoT (ESP32) para informar cidadãos e gestores públicos sobre índices de poluição atmosférica.

**Tema GS:** Economia Espacial — satélites como o Sentinel-5P da ESA orbitam a ~824km monitorando emissões de CO₂, NO₂ e partículas finas globalmente.

**ODS atendidos:** ODS 3 (Saúde), ODS 11 (Cidades Sustentáveis), ODS 13 (Ação Climática)

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework web |
| Spring Data JPA | 3.2.5 | ORM / persistência |
| Spring Security | 3.2.5 | Autenticação e autorização |
| Spring HATEOAS | 3.2.5 | Links de navegação na API |
| Spring Validation | 3.2.5 | Validação de dados |
| JWT (jjwt) | 0.12.5 | Tokens de autenticação |
| Lombok | latest | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |
| Oracle JDBC | 21.9.0 | Driver do banco |
| Maven | 3.9.6 | Gerenciador de dependências |

---

## 🏗️ Arquitetura

```
src/main/java/br/com/fiap/airwatch/
├── country/          # api / dto / model / repository / service
├── city/             # api / dto / model / repository / service
├── users/            # api / dto / model / repository / service
├── sensor/           # api / dto / model / repository / service
├── airreading/       # api / dto / model / repository / service
├── alertconfig/      # api / dto / model / repository / service
├── alertevent/       # api / dto / model / repository / service
├── integrationlog/   # api / dto / model / repository / service
├── config/           # SecurityConfig, SwaggerConfig, WebConfig,
│                     # CorsConfig, WebConfig, UserDetailsConfig
│   └── security/     # JwtService, JwtAuthFilter
└── exception/        # GlobalExceptionHandler, ResourceNotFoundException
```

**Padrões adotados:**
- Domain-based package structure
- DTO com Java Records
- Repository Pattern (JpaRepository)
- Exception Handler centralizado (@RestControllerAdvice)
- HATEOAS com links de navegação nos GETs por ID
- JWT stateless (sem session)
- Usuário não-root nos containers Docker

---

## 🗄️ Banco de Dados

8 tabelas relacionais em Oracle 21c:

| Tabela | Descrição |
|---|---|
| `COUNTRY` | Países monitorados |
| `CITY` | Cidades com geolocalização |
| `USERS` | Usuários com autenticação JWT |
| `SENSOR` | Sensores IoT e estações externas |
| `AIR_READING` | Leituras de qualidade do ar |
| `ALERT_CONFIG` | Configurações de alertas por usuário |
| `ALERT_EVENT` | Histórico de alertas disparados |
| `INTEGRATION_LOG` | Log de chamadas às APIs de satélite |

---

## 🔗 Endpoints

### Autenticação (sem token)
| Método | Endpoint | Descrição |
|---|---|---|
| POST | /api/auth/register | Criar usuário |
| POST | /api/auth/login | Login — retorna JWT |

### Recursos (GET sem token, POST/PUT/DELETE requerem Bearer Token)
| Método | Endpoint | Descrição |
|---|---|---|
| GET/POST | /api/countries | Listar / Criar país |
| GET/PUT/DELETE | /api/countries/{id} | Buscar / Atualizar / Deletar |
| GET/POST | /api/cities | Listar / Criar cidade |
| GET/PUT/DELETE | /api/cities/{id} | Buscar / Atualizar / Deletar |
| GET/POST | /api/sensors | Listar / Criar sensor |
| GET/PUT/DELETE | /api/sensors/{id} | Buscar / Atualizar / Deletar |
| GET/POST | /api/air-readings | Listar / Criar leitura |
| GET | /api/air-readings/city/{id} | Leituras por cidade |
| GET/POST | /api/alert-configs | Listar / Criar alerta |
| PUT/DELETE | /api/alert-configs/{id} | Atualizar / Deletar |
| PATCH | /api/alert-configs/{id}/toggle | Ativar/desativar alerta |
| GET/POST | /api/alert-events | Listar / Criar evento |
| PATCH | /api/alert-events/{id}/send | Marcar como enviado |
| GET/POST | /api/integration-logs | Listar / Criar log |
| GET | /api/integration-logs/errors | Listar erros |

> Documentação completa: **http://102.37.101.26:8080/swagger-ui.html**

---

## 🚀 Como Executar — Local (Perfil FIAP)

### Pré-requisitos
- Java 17+
- Maven 3.9+
- IntelliJ IDEA

### Passo 1 — Clonar o repositório
```bash
git clone https://github.com/Solsticio-AirWatch/AirWatch-Java_Back_End.git
cd AirWatch-Java_Back_End
```

### Passo 2 — Configurar o perfil
No IntelliJ: `Edit Configurations → Active profiles → fiap`

Ou via terminal:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=fiap
```

### Passo 3 — Executar os scripts SQL no banco FIAP
Conecte em `oracle.fiap.com.br:1521/orcl` com `RM565760 / 150606` e execute:
1. `airwatch_ddl.sql` — cria as tabelas
2. `airwatch_dml.sql` — insere dados de teste
3. `airwatch_plsql.sql` — cria procedures, triggers e functions

### Passo 4 — Acessar
```
http://localhost:8080/swagger-ui.html
```

---

## 🚀 Como Executar — Docker (Local)

```bash
# Subir API + Oracle juntos
docker compose up --build -d

# Aguardar Oracle (DATABASE IS READY TO USE!)
docker compose logs -f oracle-rm565760

# Testar
curl http://localhost:8080/actuator/health
```

---

## 🚀 Como Executar — Nuvem (Azure VM)

A aplicação está deployada em: **http://102.37.101.26:8080**

Para redeployar:
```bash
# Na VM via SSH
ssh azureuser@102.37.101.26
cd ~/AirWatch-Java_Back_End
git pull origin main
docker compose up --build -d
```

---

## 🔑 Testando a API

### 1. Registrar e logar
```bash
# Registrar
curl -X POST http://102.37.101.26:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@airwatch.com","password":"admin123","role":"ADMIN"}'

# Login — guarde o token retornado
curl -X POST http://102.37.101.26:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@airwatch.com","password":"admin123"}'
```

### 2. Usar o token
```bash
export TOKEN="eyJ..."

curl http://102.37.101.26:8080/api/countries \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Via Swagger
```
1. Acesse: http://102.37.101.26:8080/swagger-ui.html
2. Execute POST /api/auth/login
3. Copie o token
4. Clique em "Authorize" (cadeado)
5. Cole: Bearer SEU_TOKEN
6. Teste qualquer endpoint
```

### 4. Exemplo de resposta com HATEOAS
```json
{
  "id": 1,
  "name": "Brazil",
  "isoCode": "BR",
  "continent": "South America",
  "createdAt": "2026-05-01T08:00:00",
  "_links": {
    "self": { "href": "http://102.37.101.26:8080/api/countries/1" },
    "countries": { "href": "http://102.37.101.26:8080/api/countries" }
  }
}
```

---

## 🔒 Segurança

- **JWT** com expiração de 24h
- **Endpoints públicos:** todos os GETs, `/api/auth/**`, Swagger, Actuator
- **Endpoints protegidos:** POST, PUT, PATCH, DELETE requerem Bearer Token
- **CORS** configurado para aceitar qualquer origem
- **Senhas** armazenadas com bcrypt

---

## 🌐 Perfis de Ambiente

| Perfil | Banco | Como ativar |
|---|---|---|
| `fiap` | oracle.fiap.com.br (banco FIAP) | Active profiles: fiap |
| `local` | localhost:1521 (Docker local) | Active profiles: local |
| `prod` | oracle-rm565760:1521 (Azure VM) | Active profiles: prod |

---

*AirWatch © Grupo Solsticio - FIAP 2026*
