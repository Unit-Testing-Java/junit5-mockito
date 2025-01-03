package com.simplilearn.mavenproject.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.simplilearn.mavenproject.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    
    Cuenta cuenta;
    
    private TestInfo testInfo;
    private TestReporter testReporter;
    
    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }
    
    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }
    
    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("iniciando el metodo.");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
            + " con las etiquetas " + testInfo.getTags());
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo de prueba.");
    }
    
    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("el nombre")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")) {
                System.out.println("hacer algo con la etiqueta cuenta");
            }
//		cuenta.setPersona("Andres");
            String esperado = "Andres";
            String actual = cuenta.getPersona();
            assertNotNull(actual, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, actual, () -> "El nombre de la cuenta no es el que se esperaba: se esperaba " + esperado
                + " sin embargo fue " + actual);
            assertTrue(actual.equals("Andres"), () -> "nombre cuenta esperada debe ser igual a la real");
            
        }
        
        @Test
        @DisplayName("el saldo, que no sea null, mayor que cero, valor esperado")
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
    }
    
    @Nested
    class CuentaOperacionesTest {
        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));    // debe restarse de la cuenta
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }
        
        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100)); // debe agregarse a la cuenta
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }
        
        @Tag("cuenta")
        @Tag("banco")
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
    }
    
    @Tag("cuenta")
    @Tag("error")
    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "dinero insuficiente";
        assertEquals(esperado, actual);
    }
    
    @Test
    @Tag("cuenta")
    @Tag("banco")
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
    
    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        
        }
        
        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {
        
        }
        
        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        
        }
    }
    
    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {
        
        }
        
        @Test
        @EnabledOnJre(JRE.JAVA_15)
        void soloJdk15() {
        
        }
        
        @Test
        @DisabledOnJre(JRE.JAVA_15)
        void testNoJdk15() {
        
        }
    }
    
    @Nested
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ": " + v));
            
        }
        
        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "21.0.4")
        void testJavaVersion() {
        
        }
        
        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        
        }
        
        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
        void testNO64() {
        
        }
        
        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "Jhon")
        void testUsername() {
        
        }
        
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        
        }
    }
    
    @Nested
    class VariableAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }
        
        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "/opt/homebrew/Cellar/openjdk@21/21.0.4")
        void testJavaHome() {
        
        }
        
        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testProcesadores() {
        
        }
        
        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {
        
        }
        
        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        
        }
    }
    
    @Test
    @DisplayName("test Saldo Cuenta Dev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    @DisplayName("test Saldo Cuenta Dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
    
    @DisplayName("Probando debito cuenta repetir!")
    @RepeatedTest(value = 5, name = "Repetición número {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repetición " + info.getCurrentRepetition());
        }
        
        cuenta.debito(new BigDecimal(100));    // debe restarse de la cuenta
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }
    
    @Tag("param")
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));    // debe restarse de la cuenta
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        
        @ParameterizedTest
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));    // debe restarse de la cuenta
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        
        @ParameterizedTest
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));    // debe restarse de la cuenta
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }
    
    @Tag("param")
    @ParameterizedTest
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));    // debe restarse de la cuenta
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
    
    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }
    
    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        
        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }
        
        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }
}
