
package utils;

public class Wector {
private double d;
public double x,y,z;
 public   Wector(Wector r){
    x=r.x;
    y=r.y;
    z=r.z;
    }
 public   Wector(){
    x=0;
    y=0;
    z=0;    
    }
 public   Wector(double a,double b, double c){
    x=a;y=b;z=c;
    }
 public Wector(float array[]){
    x = array[0];
    y = array[1];
    z = array[2];
 }
public boolean rownosc(Wector w){
if((int)Math.round(dlugosc())==(int)Math.round(w.dlugosc())){

        if((int)Math.round(CosKata(w))==1){
        return true;
        }
}
return false;
}


 public double ilSkalarny(Wector ob){//iloczyn skalarny dwoch wektorow
 return x*ob.x+y*ob.y+z*ob.z;
 }

 public double CosKata(Wector ob){//cosinus kata pomiedzy wektorami
 return ilSkalarny(ob)/(dlugosc()*ob.dlugosc());
 }
 public static double cosinus(Wector v1,Wector v2){
    double dotp = v1.ilSkalarny(v2);
    return dotp/(v1.dlugosc()*v2.dlugosc());
 }
 public double dlugosc(){// dlugosc wektora
 return Math.sqrt(x*x+y*y+z*z);
 }
public static double dlugosc(float array[]){
    Wector v = new Wector(array);
    return v.dlugosc();
}
 public Wector suma(Wector ob){//suma dwoch wektorow
 return new Wector(x+ob.x,y+ob.y,z+ob.z);
 }

 public Wector roznica(Wector ob){//roznica dwoch wektorow
 return new Wector(x-ob.x,y-ob.y,z-ob.z);
 }

 public Wector wektorRazySkalar(double a){// wektor pomnozony przez skalar
 return new Wector(x*a,y*a,z*a);
 }

 public Wector wektorDzielonyPrzezSkalar(double a){//
 return new Wector(x/a,y/a,z/a);
 }

 public void normuj(){//wektor unormowany
 d=dlugosc();
 if(d!=0){
 x/=d;y/=d;z/=d;
 }
 }
 public static void normuj(float array[]){
    float d =(float) new Wector(array[0],array[1],array[2]).dlugosc();
    array[0]/=d;
    array[1]/=d;
    array[2]/=d;
 }

 public void zeruj(){
    x = y = z = 0;
 }

 public double odlegloscPunktow(Wector ob){
 return roznica(ob).dlugosc();
 }

 public Wector ilWektorowy(Wector ob){
 return new Wector(y*ob.z-z*ob.y,z*ob.x-x*ob.z,x*ob.y-y*ob.x);
 }
 public static float[] ilWektorowy(float tab1[],float tab2[]){
    float n[] = new float[3];
        Wector v1 = new Wector(tab1);
        Wector v2 = new Wector(tab2);
        n = v1.ilWektorowy(v2).toFloatArray();
    return n;
 }
 public float[] toFloatArray(){
    float array[] = new float[3];
        array[0] = (float)x;
        array[1] = (float)y;
        array[2] = (float)z;
    return array;
 }
 public String toString(){
    return "("+(int)Math.round(x)+" , "+(int)Math.round(y)+" , "+(int)Math.round(z)+")";

 }
}
