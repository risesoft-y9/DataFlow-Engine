package net.risedata.jdbc.utils;

public class GenerityModel {

	private String name;
	private Class<?> generityClass;

	/**
	 * @param name
	 * @param generityClass
	 */
	public GenerityModel(String name, Class<?> generityClass) {
		this.name = name;
		this.generityClass = generityClass;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @return the generityClass
	 */
	public Class<?> getGenerityClass() {
		return generityClass;
	}



	@Override
	public String toString() {
		return "GenerityModel [name=" + name + ", generityClass=" + generityClass + "]";
	}

}
