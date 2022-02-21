package ar.com.cdt.javaUTN.models;

public class EstudianteModel {
	
	public String nombre;
	public String apellido;
	public long DNI;
	public int edad;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public long getDNI() {
		return DNI;
	}
	public void setDNI(long dNI) {
		DNI = dNI;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	@Override
	public String toString() {
		return "EstudianteModel [nombre=" + nombre + ", apellido=" + apellido + ", DNI=" + DNI + ", edad=" + edad + "]";
	}
	
}
