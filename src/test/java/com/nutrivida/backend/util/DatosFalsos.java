package com.nutrivida.backend.util;

import com.nutrivida.backend.dto.ProductoDTO;
import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.dto.VentaDTO;
import com.nutrivida.backend.model.Producto;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.model.Venta;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.List;

/**
 * Genera datos de prueba válidos con DataFaker. Cada método devuelve datos nuevos y
 * que cumplen las validaciones de los DTO, para que cada test modifique solo lo que prueba.
 */
public final class DatosFalsos {

    private static final Faker FAKER = new Faker();

    private static final List<String> OBJETIVOS = List.of(
            "Bajar de peso", "Ganar masa muscular", "Mantener mi peso", "Alimentarme más saludable");
    private static final List<String> ALIMENTACIONES = List.of("Omnívora", "Vegetariana", "Vegana", "Otra");
    private static final List<String> TIPOS = List.of("Producto", "Servicio");
    private static final List<String> UNIDADES = List.of("envase", "pack", "plan", "sesión");

    private DatosFalsos() {
    }

    // ---------- Producto ----------

    public static ProductoDTO.Request productoRequest() {
        ProductoDTO.Request r = new ProductoDTO.Request();
        r.setCodigo(FAKER.lorem().word() + "-" + FAKER.numerify("####"));
        r.setNombre(recortar(FAKER.commerce().productName(), 60));
        r.setTipo(elegir(TIPOS));
        r.setPrecio(FAKER.number().numberBetween(1, 9999999));
        r.setUnidad(elegir(UNIDADES));
        r.setResumen(recortar(FAKER.lorem().sentence(10), 120));
        r.setDescripcion(FAKER.lorem().paragraph());
        return r;
    }

    public static Producto producto(Long id) {
        ProductoDTO.Request r = productoRequest();
        Producto p = new Producto();
        p.setId(id);
        p.setCodigo(r.getCodigo());
        p.setNombre(r.getNombre());
        p.setTipo(r.getTipo());
        p.setPrecio(r.getPrecio());
        p.setUnidad(r.getUnidad());
        p.setResumen(r.getResumen());
        p.setDescripcion(r.getDescripcion());
        return p;
    }

    public static ProductoDTO.Response productoResponse(Long id) {
        ProductoDTO.Request r = productoRequest();
        ProductoDTO.Response resp = new ProductoDTO.Response();
        resp.setId(id);
        resp.setCodigo(r.getCodigo());
        resp.setNombre(r.getNombre());
        resp.setTipo(r.getTipo());
        resp.setPrecio(r.getPrecio());
        resp.setUnidad(r.getUnidad());
        resp.setResumen(r.getResumen());
        resp.setDescripcion(r.getDescripcion());
        return resp;
    }

    public static String json(ProductoDTO.Request r) {
        return "{\"codigo\":%s,\"nombre\":%s,\"tipo\":%s,\"precio\":%s,\"unidad\":%s,\"resumen\":%s,\"descripcion\":%s}"
                .formatted(str(r.getCodigo()), str(r.getNombre()), str(r.getTipo()), r.getPrecio(),
                        str(r.getUnidad()), str(r.getResumen()), str(r.getDescripcion()));
    }

    // ---------- Usuario ----------

    public static UsuarioDTO.Request usuarioRequest() {
        UsuarioDTO.Request r = new UsuarioDTO.Request();
        r.setRun(FAKER.numerify("########"));
        r.setNombre(recortar(FAKER.name().firstName(), 50));
        r.setApellidos(recortar(FAKER.name().lastName() + " " + FAKER.name().lastName(), 100));
        r.setCorreo((recortar(FAKER.internet().username(), 40) + FAKER.numerify("##") + "@gmail.com").toLowerCase());
        r.setContrasena(FAKER.internet().password(4, 10));
        r.setFechaNacimiento(LocalDate.now().minusYears(FAKER.number().numberBetween(18, 60)));
        r.setRegionId(FAKER.number().numberBetween(1, 15));
        r.setComunaId(FAKER.number().numberBetween(1, 300));
        r.setObjetivo(elegir(OBJETIVOS));
        r.setTipoAlimentacion(elegir(ALIMENTACIONES));
        r.setRestricciones(null);
        return r;
    }

    public static Usuario usuario(Long id, String rol) {
        UsuarioDTO.Request r = usuarioRequest();
        Usuario u = new Usuario();
        u.setId(id);
        u.setRun(r.getRun());
        u.setNombre(r.getNombre());
        u.setApellidos(r.getApellidos());
        u.setCorreo(r.getCorreo());
        u.setContrasena("$2a$10$hashDePruebaQueNoImportaParaEstosTests");
        u.setFechaNacimiento(r.getFechaNacimiento());
        u.setRegionId(r.getRegionId());
        u.setComunaId(r.getComunaId());
        u.setObjetivo(r.getObjetivo());
        u.setTipoAlimentacion(r.getTipoAlimentacion());
        u.setRestricciones(r.getRestricciones());
        u.setRol(rol);
        return u;
    }

    public static UsuarioDTO.Response usuarioResponse(Long id, String rol) {
        UsuarioDTO.Request r = usuarioRequest();
        UsuarioDTO.Response resp = new UsuarioDTO.Response();
        resp.setId(id);
        resp.setRun(r.getRun());
        resp.setNombre(r.getNombre());
        resp.setApellidos(r.getApellidos());
        resp.setCorreo(r.getCorreo());
        resp.setFechaNacimiento(r.getFechaNacimiento());
        resp.setRegionId(r.getRegionId());
        resp.setComunaId(r.getComunaId());
        resp.setObjetivo(r.getObjetivo());
        resp.setTipoAlimentacion(r.getTipoAlimentacion());
        resp.setRestricciones(r.getRestricciones());
        resp.setRol(rol);
        return resp;
    }

    public static String json(UsuarioDTO.Request r) {
        return ("{\"run\":%s,\"nombre\":%s,\"apellidos\":%s,\"correo\":%s,\"contrasena\":%s,"
                + "\"fechaNacimiento\":%s,\"regionId\":%s,\"comunaId\":%s,\"objetivo\":%s,"
                + "\"tipoAlimentacion\":%s,\"restricciones\":%s}")
                .formatted(str(r.getRun()), str(r.getNombre()), str(r.getApellidos()), str(r.getCorreo()),
                        str(r.getContrasena()), str(r.getFechaNacimiento() == null ? null : r.getFechaNacimiento().toString()),
                        r.getRegionId(), r.getComunaId(), str(r.getObjetivo()), str(r.getTipoAlimentacion()),
                        str(r.getRestricciones()));
    }

    // ---------- Venta ----------

    public static VentaDTO.Request ventaRequest() {
        VentaDTO.Request r = new VentaDTO.Request();
        r.setFecha(LocalDate.now().minusDays(FAKER.number().numberBetween(0, 30)));
        r.setUsuarioId((long) FAKER.number().numberBetween(1, 100));
        r.setTotal(FAKER.number().numberBetween(0, 500000));
        return r;
    }

    public static Venta venta(Long id) {
        VentaDTO.Request r = ventaRequest();
        Venta v = new Venta();
        v.setId(id);
        v.setFecha(r.getFecha());
        v.setUsuarioId(r.getUsuarioId());
        v.setTotal(r.getTotal());
        return v;
    }

    public static VentaDTO.Response ventaResponse(Long id) {
        VentaDTO.Request r = ventaRequest();
        VentaDTO.Response resp = new VentaDTO.Response();
        resp.setId(id);
        resp.setFecha(r.getFecha());
        resp.setUsuarioId(r.getUsuarioId());
        resp.setTotal(r.getTotal());
        return resp;
    }

    public static String json(VentaDTO.Request r) {
        return "{\"fecha\":%s,\"usuarioId\":%s,\"total\":%s}"
                .formatted(str(r.getFecha() == null ? null : r.getFecha().toString()), r.getUsuarioId(), r.getTotal());
    }

    // ---------- utilidades ----------

    private static String elegir(List<String> opciones) {
        return opciones.get(FAKER.number().numberBetween(0, opciones.size()));
    }

    private static String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    /** Convierte un texto en literal JSON (con comillas y escapes) o en null. */
    private static String str(String valor) {
        if (valor == null) {
            return "null";
        }
        return "\"" + valor.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
