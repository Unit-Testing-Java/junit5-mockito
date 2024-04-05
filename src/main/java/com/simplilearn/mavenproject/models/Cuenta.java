package com.simplilearn.mavenproject.models;

import com.simplilearn.mavenproject.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

// shift + command + test -> create new test
public class Cuenta {
	private String persona;
	private BigDecimal saldo;
	private Banco banco;
	
	public Cuenta(String persona, BigDecimal saldo) {
		this.saldo = saldo;
		this.persona = persona;
	}
	
	public String getPersona() {
		return persona;
	}
	
	public void setPersona(String persona) {
		this.persona = persona;
	}
	
	public BigDecimal getSaldo() {
		return saldo;
	}
	
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public void debito(BigDecimal monto) {
		BigDecimal nuevoSaldo = this.saldo.subtract(monto);
		if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new DineroInsuficienteException("dinero insuficiente");
		}
		this.saldo = nuevoSaldo;
	}

	public void credito(BigDecimal monto) {
		this.saldo = this.saldo.add(monto);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cuenta)) {
			return false;
		}
		if (this.persona == null || this.saldo == null) {
			return false;
		}
		Cuenta c = (Cuenta) obj;
		return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
	}
}
