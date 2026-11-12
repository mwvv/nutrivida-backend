package com.nutrivida.backend.util;

import com.nutrivida.backend.model.Producto;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.model.Venta;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Genera entidades con datos falsos (DataFaker) que respetan las reglas de V1__crear_tablas.sql.
 * Las entidades se crean sin id; cada test asigna el id que necesite.
 */
public final class DatosFalsos {

    private static final Faker FAKER = new Faker(Locale.of("es"));

    private DatosFalsos() {
    }

    public static Producto producto() {
        Producto p = new Producto();
        p.setCodigo(FAKER.regexify("[a-z]{4,12}-[a-z]{3,10}"));                   // slug único, <= 60
        p.setNombre(recortar(FAKER.commerce().productName(), 60));                 // 3 a 60 caracteres
        p.setTipo(FAKER.options().option("Producto", "Servicio"));                 // CHECK tipo
        p.setPrecio(FAKER.number().numberBetween(1, 10_000_000));                  // 1 a 9999999
        p.setUnidad(FAKER.options().option("envase", "pack", "plan", "sesion"));
        p.setResumen(FAKER.lorem().characters(10, 120));                           // 10 a 120 caracteres
        p.setDescripcion(FAKER.lorem().paragraph());
        return p;
    }

    public static Usuario usuario() {
        Usuario u = new Usuario();
        u.setRun(FAKER.regexify("[1-9][0-9]{5,7}[0-9K]"));                         // 7 a 9 caracteres, sin puntos ni guion
        u.setNombre(recortar(FAKER.name().firstName(), 50));
        u.setApellidos(recortar(FAKER.name().lastName() + " " + FAKER.name().lastName(), 100));
        u.setCorreo(FAKER.regexify("[a-z]{3,10}\\.[a-z]{3,10}")
                + FAKER.options().option("@duoc.cl", "@profesor.duoc.cl", "@gmail.com"));
        u.setContrasena(FAKER.regexify("[A-Za-z0-9]{4,10}"));                       // 4 a 10 caracteres
        u.setFechaNacimiento(FAKER.timeAndDate().birthday(18, 80));
        u.setRegionId(FAKER.number().numberBetween(1, 17));
        u.setComunaId(FAKER.number().numberBetween(1, 50));
        u.setObjetivo(FAKER.options().option(
                "Bajar de peso", "Ganar masa muscular", "Mantener mi peso", "Alimentarme más saludable"));
        u.setTipoAlimentacion(FAKER.options().option("Omnívora", "Vegetariana", "Vegana", "Otra"));
        u.setRestricciones(null);
        u.setRol("CLIENTE");
        return u;
    }

    public static Venta venta() {
        Venta v = new Venta();
        v.setFecha(LocalDate.now().minusDays(FAKER.number().numberBetween(0, 365)));
        v.setUsuarioId(FAKER.number().numberBetween(1L, 1_000L));
        v.setTotal(FAKER.number().numberBetween(0, 1_000_000));                    // total >= 0
        return v;
    }

    private static String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
