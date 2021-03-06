package es.cecabank.ealiaapp.accesobd.model;
// default package
// Generated 19-feb-2016 8:48:24 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * EalEntisAppsParam generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "EAL_ENTIS_APPS_PARAM")
public class EalEntisAppsParam implements java.io.Serializable {

	private EalEntisAppsParamId id;
	private EalEntisAppsParamEsts ealEntisAppsParamEsts;
	private EalEntisAplicaciones ealEntisAplicaciones;
	private String fechaHoraAlta;
	private String fechaHoraEstado;
	private String valorParametro;

	public EalEntisAppsParam() {
	}

	public EalEntisAppsParam(EalEntisAppsParamId id, EalEntisAppsParamEsts ealEntisAppsParamEsts,
			EalEntisAplicaciones ealEntisAplicaciones, String fechaHoraAlta, String fechaHoraEstado,
			String valorParametro) {
		this.id = id;
		this.ealEntisAppsParamEsts = ealEntisAppsParamEsts;
		this.ealEntisAplicaciones = ealEntisAplicaciones;
		this.fechaHoraAlta = fechaHoraAlta;
		this.fechaHoraEstado = fechaHoraEstado;
		this.valorParametro = valorParametro;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "idParametro", column = @Column(name = "ID_PARAMETRO", nullable = false, length = 50)),
			@AttributeOverride(name = "codAplicacion", column = @Column(name = "COD_APLICACION", nullable = false, length = 3)) })
	public EalEntisAppsParamId getId() {
		return this.id;
	}

	public void setId(EalEntisAppsParamId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COD_ESTADO", nullable = false)
	public EalEntisAppsParamEsts getEalEntisAppsParamEsts() {
		return this.ealEntisAppsParamEsts;
	}

	public void setEalEntisAppsParamEsts(EalEntisAppsParamEsts ealEntisAppsParamEsts) {
		this.ealEntisAppsParamEsts = ealEntisAppsParamEsts;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COD_APLICACION", nullable = false, insertable = false, updatable = false)
	public EalEntisAplicaciones getEalEntisAplicaciones() {
		return this.ealEntisAplicaciones;
	}

	public void setEalEntisAplicaciones(EalEntisAplicaciones ealEntisAplicaciones) {
		this.ealEntisAplicaciones = ealEntisAplicaciones;
	}

	@Column(name = "FECHA_HORA_ALTA", nullable = false, length = 16)
	public String getFechaHoraAlta() {
		return this.fechaHoraAlta;
	}

	public void setFechaHoraAlta(String fechaHoraAlta) {
		this.fechaHoraAlta = fechaHoraAlta;
	}

	@Column(name = "FECHA_HORA_ESTADO", nullable = false, length = 16)
	public String getFechaHoraEstado() {
		return this.fechaHoraEstado;
	}

	public void setFechaHoraEstado(String fechaHoraEstado) {
		this.fechaHoraEstado = fechaHoraEstado;
	}

	@Column(name = "VALOR_PARAMETRO", nullable = false, length = 2048)
	public String getValorParametro() {
		return this.valorParametro;
	}

	public void setValorParametro(String valorParametro) {
		this.valorParametro = valorParametro;
	}

}
