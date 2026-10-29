package pe.edu.upeu.autoandes;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
  @org.springframework.boot.test.context.TestConfiguration
  static class TiempoDePrueba {
    @org.springframework.context.annotation.Bean
    @org.springframework.context.annotation.Primary
    java.time.Clock relojFijo() {
      return java.time.Clock.fixed(
          java.time.Instant.parse("2026-10-29T15:00:00Z"), java.time.ZoneId.of("America/Lima"));
    }
  }

  @Test
  void falloEnInsercionDeDetalleRevierteCabecera() throws Exception {
    long antes = jdbc.queryForObject("select count(*) from orden_servicio", Long.class);
    long detalles = jdbc.queryForObject("select count(*) from detalle_orden", Long.class);
    // Fuerza un error de BD después del INSERT de la cabecera: prueba el rollback real.
    jdbc.execute("alter table detalle_orden add constraint prueba_rollback check (cantidad < 3)");
    try {
      String body =
          orden(
              id("vehiculo", "placa", "GGG-777"),
              90000,
              "[" + detalle(id("servicio", "codigo", "SRV-002"), 3) + "]");
      request("POST", "/api/v1/ordenes", body, token("asesor")).andExpect(status().isConflict());
      assertEquals(antes, jdbc.queryForObject("select count(*) from orden_servicio", Long.class));
      assertEquals(detalles, jdbc.queryForObject("select count(*) from detalle_orden", Long.class));
    } finally {
      jdbc.execute("alter table detalle_orden drop constraint prueba_rollback");
    }
  }

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;
  @Autowired JdbcTemplate jdbc;

  String token(String user) throws Exception {
    String body =
        mvc.perform(
                post("/api/v1/auth/login")
                    .contentType("application/json")
                    .content("{\"username\":\"" + user + "\",\"password\":\"Auto2026*\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return json.readTree(body).get("token").asText();
  }

  ResultActions request(String method, String path, String body, String token) throws Exception {
    var r =
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request(
                org.springframework.http.HttpMethod.valueOf(method), path)
            .contentType("application/json");
    if (body != null) r.content(body);
    if (token != null) r.header("Authorization", "Bearer " + token);
    return mvc.perform(r);
  }

  String orden(long vehiculo, int km, String detalles) {
    return "{\"vehiculoId\":"
        + vehiculo
        + ",\"kilometraje\":"
        + km
        + ",\"detalles\":"
        + detalles
        + "}";
  }

  long id(String table, String campo, String valor) {
    return jdbc.queryForObject(
        "select id from " + table + " where " + campo + "=?", Long.class, valor);
  }

  String detalle(long id, int cantidad) {
    return "{\"servicioId\":" + id + ",\"cantidad\":" + cantidad + "}";
  }

  @Test
  void loginContratoYMe() throws Exception {
    var res =
        request(
                "POST",
                "/api/v1/auth/login",
                "{\"username\":\"admin\",\"password\":\"Auto2026*\"}",
                null)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipo").value("Bearer"))
            .andExpect(jsonPath("$.expiraEnSegundos").value(3600))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.permisos.length()").value(8));
    request("GET", "/api/v1/auth/me", null, token("asesor"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("asesor"));
    assertTrue(
        jdbc.queryForObject("select password from usuarios where username='admin'", String.class)
            .startsWith("$2"));
  }

  @Test
  void loginRechazaInactivoYCredencialesConMismoMensaje() throws Exception {
    for (String user : new String[] {"nadie", "inactivo", "admin"})
      request(
              "POST",
              "/api/v1/auth/login",
              "{\"username\":\""
                  + user
                  + "\",\"password\":\""
                  + (user.equals("admin") ? "incorrecta" : "Auto2026*")
                  + "\"}",
              null)
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos"));
  }

  @Test
  void sinTokenYTokenAlteradoSon401() throws Exception {
    for (String ruta :
        new String[] {
          "/api/v1/clientes",
          "/api/v1/servicios",
          "/api/v1/vehiculos",
          "/api/v1/ordenes",
          "/api/v1/reportes/ingresos-por-servicio",
          "/api/v1/auth/me"
        })
      request("GET", ruta, null, null)
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.ruta").value(ruta))
          .andExpect(jsonPath("$.campos").doesNotExist());
    request("GET", "/api/v1/clientes", null, "alterado").andExpect(status().isUnauthorized());
  }

  @Test
  void matrizPermisos() throws Exception {
    String asesor = token("asesor"), jefe = token("jefe");
    request("PATCH", "/api/v1/ordenes/1/anular", "{\"motivo\":\"Motivo suficiente\"}", asesor)
        .andExpect(status().isForbidden());
    request("GET", "/api/v1/reportes/ingresos-por-servicio", null, asesor)
        .andExpect(status().isForbidden());
    request("POST", "/api/v1/servicios", "{}", asesor).andExpect(status().isForbidden());
    request("POST", "/api/v1/ordenes", "{}", jefe).andExpect(status().isForbidden());
    request("POST", "/api/v1/vehiculos", "{}", jefe).andExpect(status().isForbidden());
    request("GET", "/api/v1/reportes/ingresos-por-servicio", null, jefe).andExpect(status().isOk());
  }

  @Test
  void crudServicioYValidacion() throws Exception {
    String t = token("admin");
    request(
            "POST",
            "/api/v1/servicios",
            "{\"codigo\":\"INVALIDO\",\"nombre\":\"x\",\"precio\":0,\"duracionMin\":1}",
            t)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.codigo").exists());
    String body =
        "{\"codigo\":\"SRV-099\",\"nombre\":\"Servicio prueba\",\"precio\":25.50,\"duracionMin\":30}";
    String result =
        request("POST", "/api/v1/servicios", body, t)
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = json.readTree(result).get("id").asLong();
    request("POST", "/api/v1/servicios", body, t).andExpect(status().isConflict());
    request("PUT", "/api/v1/servicios/" + id, body.replace("25.50", "35.50"), t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.precio").value(35.50));
    request("PATCH", "/api/v1/servicios/" + id + "/estado", "{\"activo\":false}", t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.activo").value(false));
    request("DELETE", "/api/v1/servicios/" + id, null, t).andExpect(status().isNoContent());
    request("GET", "/api/v1/servicios/" + id, null, t).andExpect(status().isNotFound());
    request("DELETE", "/api/v1/servicios/" + id("servicio", "codigo", "SRV-001"), null, t)
        .andExpect(status().isConflict());
  }

  @Test
  void crudVehiculoRelacionYErrores() throws Exception {
    String t = token("asesor");
    long cliente = id("cliente", "documento", "70000001");
    String body =
        "{\"placa\":\"ZZZ-999\",\"marca\":\"Toyota\",\"modelo\":\"Yaris\",\"anio\":2021,\"clienteId\":"
            + cliente
            + "}";
    String result =
        request("POST", "/api/v1/vehiculos", body, t)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clienteNombre").value("Cliente de prueba 1"))
            .andExpect(jsonPath("$.cliente").doesNotExist())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = json.readTree(result).get("id").asLong();
    request("POST", "/api/v1/vehiculos", body, t).andExpect(status().isConflict());
    request("PUT", "/api/v1/vehiculos/" + id, body.replace("2021", "2022"), t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.anio").value(2022));
    request("GET", "/api/v1/vehiculos?clienteId=" + cliente, null, t).andExpect(status().isOk());
    request("DELETE", "/api/v1/vehiculos/" + id, null, t).andExpect(status().isNoContent());
    request(
            "POST",
            "/api/v1/vehiculos",
            body.replace("\"clienteId\":" + cliente, "\"clienteId\":999999"),
            t)
        .andExpect(status().isNotFound());
    request("POST", "/api/v1/vehiculos", body.replace("2021", "2028"), t)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.anio").exists());
    request("DELETE", "/api/v1/vehiculos/" + id("vehiculo", "placa", "AAA-111"), null, t)
        .andExpect(status().isConflict());
  }

  @Test
  void ordenCalculaTotalAnulaYExcluyeReporte() throws Exception {
    String t = token("admin");
    long v = id("vehiculo", "placa", "HHH-888"), s = id("servicio", "codigo", "SRV-007");
    String body = orden(v, 90000, "[" + detalle(s, 2) + "]");
    String result =
        request("POST", "/api/v1/ordenes", body, t)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.total").value(200))
            .andExpect(jsonPath("$.usuarioRegistro").value("admin"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    long o = json.readTree(result).get("id").asLong();
    request("GET", "/api/v1/ordenes/" + o, null, t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.detalles[0].precioUnitario").value(100));
    assertEquals(
        1,
        jdbc.queryForObject(
            "select count(*) from detalle_orden where orden_id=?", Integer.class, o));
    request(
            "PATCH",
            "/api/v1/ordenes/" + o + "/anular",
            "{\"motivo\":\"El cliente canceló el trabajo\"}",
            t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("ANULADA"));
    request(
            "PATCH",
            "/api/v1/ordenes/" + o + "/anular",
            "{\"motivo\":\"El cliente canceló el trabajo\"}",
            t)
        .andExpect(status().isConflict());
    String reporte =
        request("GET", "/api/v1/reportes/ingresos-por-servicio", null, t)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertFalse(reporte.contains("SRV-007"));
  }

  @Test
  void tercerDetalleInactivoNoPersisteNada() throws Exception {
    long antes = jdbc.queryForObject("select count(*) from orden_servicio", Long.class),
        detalles = jdbc.queryForObject("select count(*) from detalle_orden", Long.class);
    String ds =
        "["
            + detalle(id("servicio", "codigo", "SRV-001"), 1)
            + ","
            + detalle(id("servicio", "codigo", "SRV-002"), 1)
            + ","
            + detalle(id("servicio", "codigo", "SRV-008"), 1)
            + "]";
    request(
            "POST",
            "/api/v1/ordenes",
            orden(id("vehiculo", "placa", "GGG-777"), 90000, ds),
            token("asesor"))
        .andExpect(status().isConflict());
    assertEquals(antes, jdbc.queryForObject("select count(*) from orden_servicio", Long.class));
    assertEquals(detalles, jdbc.queryForObject("select count(*) from detalle_orden", Long.class));
  }

  @Test
  void kilometrajeDetallesYAnulacionInvalidos() throws Exception {
    String t = token("admin");
    long s = id("servicio", "codigo", "SRV-001"), v = id("vehiculo", "placa", "AAA-111");
    request("POST", "/api/v1/ordenes", orden(v, 44999, "[" + detalle(s, 1) + "]"), t)
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.mensaje").value("El último kilometraje registrado es 45000"));
    request(
            "POST",
            "/api/v1/ordenes",
            orden(v, 45000, "[" + detalle(s, 1) + "," + detalle(s, 1) + "]"),
            t)
        .andExpect(status().isBadRequest());
    request("POST", "/api/v1/ordenes", orden(v, 45000, "[" + detalle(s, 6) + "]"), t)
        .andExpect(status().isBadRequest());
    request("POST", "/api/v1/ordenes", orden(v, 45000, "[]"), t).andExpect(status().isBadRequest());
    long o = id("orden_servicio", "numero", "OS-000001");
    request("PATCH", "/api/v1/ordenes/" + o + "/anular", "{\"motivo\":\"corto\"}", t)
        .andExpect(status().isBadRequest());
    request(
            "PATCH",
            "/api/v1/ordenes/" + o + "/anular",
            "{\"motivo\":\"Orden de un día anterior\"}",
            t)
        .andExpect(status().isConflict());
  }

  @Test
  void filtrosYReporteAgregado() throws Exception {
    String t = token("admin");
    request(
            "GET",
            "/api/v1/ordenes?placa=AAA-111&estado=REGISTRADA&desde=2026-10-28&hasta=2026-10-28",
            null,
            t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    request("GET", "/api/v1/ordenes?placa=NO-EXISTE", null, t)
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
    request("GET", "/api/v1/ordenes?estado=OTRO", null, t).andExpect(status().isBadRequest());
    request(
            "GET",
            "/api/v1/reportes/ingresos-por-servicio?desde=2026-10-28&hasta=2026-10-28",
            null,
            t)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cantidadTotal").value(3))
        .andExpect(jsonPath("$[0].importeTotal").value(270));
  }

  @Test
  void corsYSwagger() throws Exception {
    mvc.perform(
            options("/api/v1/ordenes/1/anular")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "PATCH")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    mvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.paths['/api/v1/ordenes']").exists());
  }
}
