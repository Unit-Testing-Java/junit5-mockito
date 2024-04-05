package com.simplilearn.mavenproject.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.simplilearn.mavenproject.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

class CuentaTest {

	Cuenta cuenta;

	@BeforeAll
	@BeforeEach
	void initMetodoTest() {
		this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		System.out.println("iniciando el metodo.");
	}

	@AfterEach
	void tearDown() {
		System.out.println("finalizando el metodo de prueba.");
	}

	@Test
	@DisplayName("Probando nombre de la cuenta corriente")
	void testNombreCuenta() {
//		cuenta.setPersona("Andres");

		String esperado = "Andres";
		String actual = cuenta.getPersona();
		assertNotNull(actual, () -> "La cuenta no puede ser nula");
		assertEquals(esperado, actual, () -> "El nombre de la cuenta no es el que se esperaba: se esperaba " + esperado
					+ " sin embargo fue " + actual);
		assertTrue(actual.equals("Andres"), () -> "nombre cuenta esperada debe ser igual a la real");

	}

	@Test
	@DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado")
	void testSaldoCuenta() {
		assertNotNull(cuenta.getSaldo());
		assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
		assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
	}

	@Test
	@DisplayName("Testeando referencias que sean iguales con el método equals.")
	void testReferenciaCuenta() {
		cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
		Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

		//assertNotEquals(cuenta2, cuenta);
		assertEquals(cuenta2, cuenta);
	}

	@Test
	void testDebitoCuenta() {
		cuenta.debito(new BigDecimal(100));	// debe restarse de la cuenta
		assertNotNull(cuenta.getSaldo());
		assertEquals(900, cuenta.getSaldo().intValue());
		assertEquals("900.12345", cuenta.getSaldo().toPlainString());
	}

	@Test
	void testCreditoCuenta() {
		cuenta.credito(new BigDecimal(100)); // debe agregarse a la cuenta
		assertNotNull(cuenta.getSaldo());
		assertEquals(1100, cuenta.getSaldo().intValue());
		assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
	}

	@Test
	void testDineroInsuficienteExceptionCuenta() {
		Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
			cuenta.debito(new BigDecimal(1500));
		});
		String actual = exception.getMessage();
		String esperado = "dinero insuficiente";
		assertEquals(esperado, actual);
	}

	@Test
	void testTransferirDineroCuentas() {
		Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

		Banco banco = new Banco();
		banco.setNombre("Banco del Estado");
		banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

		assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
		assertEquals("3000", cuenta1.getSaldo().toPlainString());

	}

	@Test
	//@Disabled
	@DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
	void testRelacionBancoCuentas() {
		//fail();
		Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

		Banco banco = new Banco();
		banco.addCuenta(cuenta1);
		banco.addCuenta(cuenta2);

		banco.setNombre("Banco del Estado");
		banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()),
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Andres"))
                        .findFirst()
                        .get()
                        .getPersona()
                ),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Andres")))
        );
	}
}
