package com.nutrivida.backend.dto;

import com.nutrivida.backend.util.DatosFalsos;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidacionDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrar() {
        factory.close();
    }

    private static <T> Set<String> camposInvalidos(T dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    // ---------- Producto ----------

    @Test
    void productoValidoNoTieneErrores() {
        assertTrue(camposInvalidos(DatosFalsos.productoRequest()).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Otro", "producto", "", "Servicios"})
    void productoConTipoFueraDeProductoOServicioEsInvalido(String tipo) {
        ProductoDTO.Request dto = DatosFalsos.productoRequest();
        dto.setTipo(tipo);

        assertTrue(camposInvalidos(dto).contains("tipo"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, 10_000_000})
    void productoConPrecioFueraDeRangoEsInvalido(int precio) {
        ProductoDTO.Request dto = DatosFalsos.productoRequest();
        dto.setPrecio(precio);

        assertEquals(Set.of("precio"), camposInvalidos(dto));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9_999_999})
    void productoConPrecioEnLosLimitesEsValido(int precio) {
        ProductoDTO.Request dto = DatosFalsos.productoRequest();
        dto.setPrecio(precio);

        assertTrue(camposInvalidos(dto).isEmpty());
    }

    @Test
    void productoConNombreCortoOLargoEsInvalido() {
        ProductoDTO.Request corto = DatosFalsos.productoRequest();
        corto.setNombre("ab");
        ProductoDTO.Request largo = DatosFalsos.productoRequest();
        largo.setNombre("x".repeat(61));

        assertTrue(camposInvalidos(corto).contains("nombre"));
        assertTrue(camposInvalidos(largo).contains("nombre"));
    }

    @Test
    void productoConResumenFueraDeRangoEsInvalido() {
        ProductoDTO.Request corto = DatosFalsos.productoRequest();
        corto.setResumen("muy corto");
        ProductoDTO.Request largo = DatosFalsos.productoRequest();
        largo.setResumen("x".repeat(121));

        assertTrue(camposInvalidos(corto).contains("resumen"));
        assertTrue(camposInvalidos(largo).contains("resumen"));
    }

    @Test
    void productoVacioReportaLosCamposObligatorios() {
        Set<String> campos = camposInvalidos(new ProductoDTO.Request());

        assertTrue(campos.containsAll(
                Set.of("codigo", "nombre", "tipo", "precio", "unidad", "resumen", "descripcion")));
    }

    // ---------- Usuario ----------

    @Test
    void usuarioValidoNoTieneErrores() {
        assertTrue(camposInvalidos(DatosFalsos.usuarioRequest()).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"alguien@hotmail.com", "alguien@duoc.com", "sin-arroba.gmail.com", "alguien@gmail.com.ar"})
    void usuarioConCorreoDeDominioNoPermitidoEsInvalido(String correo) {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setCorreo(correo);

        assertTrue(camposInvalidos(dto).contains("correo"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"alguien@duoc.cl", "alguien@profesor.duoc.cl", "alguien@gmail.com", "ALGUIEN@GMAIL.COM"})
    void usuarioConCorreoDeDominioPermitidoEsValido(String correo) {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setCorreo(correo);

        assertTrue(camposInvalidos(dto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12345678901"})
    void usuarioConContrasenaFueraDeRangoEsInvalido(String contrasena) {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setContrasena(contrasena);

        assertEquals(Set.of("contrasena"), camposInvalidos(dto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "1234567890"})
    void usuarioConRunFueraDeRangoEsInvalido(String run) {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setRun(run);

        assertTrue(camposInvalidos(dto).contains("run"));
    }

    @Test
    void usuarioConObjetivoOTipoDeAlimentacionDesconocidoEsInvalido() {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setObjetivo("Ser famoso");
        dto.setTipoAlimentacion("Carnívora");

        Set<String> campos = camposInvalidos(dto);

        assertTrue(campos.contains("objetivo"));
        assertTrue(campos.contains("tipoAlimentacion"));
    }

    @Test
    void usuarioConFechaDeNacimientoFuturaEsInvalido() {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setFechaNacimiento(LocalDate.now().plusDays(1));

        assertEquals(Set.of("fechaNacimiento"), camposInvalidos(dto));
    }

    @Test
    void usuarioSinFechaDeNacimientoNiRestriccionesEsValido() {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setFechaNacimiento(null);
        dto.setRestricciones(null);

        assertTrue(camposInvalidos(dto).isEmpty());
    }

    @Test
    void usuarioSinRegionNiComunaEsInvalido() {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        dto.setRegionId(null);
        dto.setComunaId(null);

        assertTrue(camposInvalidos(dto).containsAll(Set.of("regionId", "comunaId")));
    }

    // ---------- Venta ----------

    @Test
    void ventaValidaNoTieneErrores() {
        assertTrue(camposInvalidos(DatosFalsos.ventaRequest()).isEmpty());
    }

    @ParameterizedTest
    @NullSource
    void ventaSinFechaEsValida(LocalDate fecha) {
        VentaDTO.Request dto = DatosFalsos.ventaRequest();
        dto.setFecha(fecha);

        assertTrue(camposInvalidos(dto).isEmpty());
    }

    @Test
    void ventaConTotalNegativoEsInvalida() {
        VentaDTO.Request dto = DatosFalsos.ventaRequest();
        dto.setTotal(-1);

        assertEquals(Set.of("total"), camposInvalidos(dto));
    }

    @Test
    void ventaConTotalCeroEsValida() {
        VentaDTO.Request dto = DatosFalsos.ventaRequest();
        dto.setTotal(0);

        assertTrue(camposInvalidos(dto).isEmpty());
    }

    @Test
    void ventaSinUsuarioNiTotalEsInvalida() {
        Set<String> campos = camposInvalidos(new VentaDTO.Request());

        assertTrue(campos.containsAll(Set.of("usuarioId", "total")));
    }
}
