package utils;

public class Wector {

	private double d;
	public double x, y, z;

	public Wector(final Wector r) {
		x = r.x;
		y = r.y;
		z = r.z;
	}

	public Wector() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Wector(final double a, final double b, final double c) {
		x = a;
		y = b;
		z = c;
	}

	public Wector(final float array[]) {
		x = array[0];
		y = array[1];
		z = array[2];
	}

	public boolean rownosc(final Wector w) {
		if ( (int) Math.round(dlugosc()) == (int) Math.round(w.dlugosc()) ) {

			if ( (int) Math.round(CosKata(w)) == 1 ) { return true; }
		}
		return false;
	}

	public double ilSkalarny(final Wector ob) {// iloczyn skalarny dwoch wektorow
		return x * ob.x + y * ob.y + z * ob.z;
	}

	public double CosKata(final Wector ob) {// cosinus kata pomiedzy wektorami
		return ilSkalarny(ob) / (dlugosc() * ob.dlugosc());
	}

	public static double cosinus(final Wector v1, final Wector v2) {
		double dotp = v1.ilSkalarny(v2);
		return dotp / (v1.dlugosc() * v2.dlugosc());
	}

	public double dlugosc() {// dlugosc wektora
		return Math.sqrt(x * x + y * y + z * z);
	}

	public static double dlugosc(final float array[]) {
		Wector v = new Wector(array);
		return v.dlugosc();
	}

	public Wector suma(final Wector ob) {// suma dwoch wektorow
		return new Wector(x + ob.x, y + ob.y, z + ob.z);
	}

	public Wector roznica(final Wector ob) {// roznica dwoch wektorow
		return new Wector(x - ob.x, y - ob.y, z - ob.z);
	}

	public Wector wektorRazySkalar(final double a) {// wektor pomnozony przez skalar
		return new Wector(x * a, y * a, z * a);
	}

	public Wector wektorDzielonyPrzezSkalar(final double a) {//
		return new Wector(x / a, y / a, z / a);
	}

	public void normuj() {// wektor unormowany
		d = dlugosc();
		if ( d != 0 ) {
			x /= d;
			y /= d;
			z /= d;
		}
	}

	public static void normuj(final float array[]) {
		float d = (float) new Wector(array[0], array[1], array[2]).dlugosc();
		array[0] /= d;
		array[1] /= d;
		array[2] /= d;
	}

	public void zeruj() {
		x = y = z = 0;
	}

	public double odlegloscPunktow(final Wector ob) {
		return roznica(ob).dlugosc();
	}

	public Wector ilWektorowy(final Wector ob) {
		return new Wector(y * ob.z - z * ob.y, z * ob.x - x * ob.z, x * ob.y - y * ob.x);
	}

	public static float[] ilWektorowy(final float tab1[], final float tab2[]) {
		float n[] = new float[3];
		Wector v1 = new Wector(tab1);
		Wector v2 = new Wector(tab2);
		n = v1.ilWektorowy(v2).toFloatArray();
		return n;
	}

	public float[] toFloatArray() {
		float array[] = new float[3];
		array[0] = (float) x;
		array[1] = (float) y;
		array[2] = (float) z;
		return array;
	}

	@Override
	public String toString() {
		return "(" + (int) Math.round(x) + " , " + (int) Math.round(y) + " , " +
			(int) Math.round(z) + ")";

	}
}
