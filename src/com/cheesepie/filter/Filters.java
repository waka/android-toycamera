package com.cheesepie.filter;

public enum Filters {

	VINTAGEGREEN ("vgreen"),
	TILTSHIFT ("tiltshift"),
	MONOTONE ("monotone"),
	SEPIA ("sepia"),
	MOSAIC ("mosaic"),
	DEFAULT ("default");
	
	private Filters(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public IFilter get() {
		switch (this) {
		case TILTSHIFT: return new TiltShiftFilter();
		case VINTAGEGREEN:  return new VintageGreenFilter();
		case MONOTONE:  return new MonotoneFilter();
		case SEPIA:     return new SepiaFilter();
		case MOSAIC:    return new FaceMosaicFilter();
		case DEFAULT:
		default:        return new DefaultFilter();
		}
	}
	
	private String name;
}
