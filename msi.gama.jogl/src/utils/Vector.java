package utils;

//version 2.0

//import javax.vecmath.Matrix3d;

public class Vector{
    public double x;
    public double y;
    public double z;
    public double length;


    public Vector(){
        zero();
    }
    /**
     * Create Vector with third parameter equals zero;
     */
    public Vector(double x0,double y0){
        x = x0;
        y = y0;
        z = 0;
        length = length();
    }

    public Vector(double x0,double y0,double z0){
        x = x0;
        y = y0;
        z = z0;
        length = length();
    }
    /**
     * Copy Vector vec without reference.
     * @param vec- vector to copy
     */
    public Vector(Vector vec){
        x = vec.x;
        y = vec.y;
        z = vec.z;
        length = length();
    }
    /**
     * change double array to Vector class 
     */
    public Vector(double array[]){
        x = array[0];
        y = array[1];
        z = array[2];
        length = length();
    }
    /**
     *
     * @return Returns length of vector
     */
    public double length(){        
        return Math.sqrt(x*x+y*y+z*z);
    }

    /**
     * Calculate dot product with Vetor ob
     * @return - dot porduct
     */
    public double dot(Vector ob){//iloczyn skalarny dwoch wektorow
        return x*ob.x+y*ob.y+z*ob.z;
    }

    /**
     * Returns angle between vectors in radians
     */
    public double angle(Vector ob){//cosinus kata pomiedzy wektorami
        return dot(ob)/(length*ob.length());
    }

    /**
     * Returns sum of vectors
     */
    public Vector sum(Vector ob){//suma dwoch wektorow
        return new Vector(x+ob.x,y+ob.y,z+ob.z);
    }
    /**
     * Returns difference of two vectors.
     */
    public Vector diff(Vector ob){//roznica dwoch wektorow
        return new Vector(x-ob.x,y-ob.y,z-ob.z);
    }
    /**
     * Multiply Vector by number
     * @param a - number
     * @return new Vector
     */
    public Vector scale(double a){// wektor pomnozony przez skalar
        return new Vector(x*a,y*a,z*a);
    }

 
    /**
     * Normalize verctor to length equals 1
     */
     public void normalize(){//wektor unormowany
         if(length!=0){
            x/= length;
            y/= length;
            z/= length;
            length = 1;
         }
     }
 
     /**
      * Change all vector  components to zero
      */
     public void zero(){
        x = y = z = 0;
     }
     /**
      * returns distance between to points i space.      
      * @return distance
      */
     public double distance(Vector ob){
        return diff(ob).length;
     }
     /**
      * returns cross product.
      * @return new Vector
      */
     public Vector cross(Vector ob){
        return new Vector(y*ob.z-z*ob.y,z*ob.x-x*ob.z,x*ob.y-y*ob.x);
     }
     /**
      * Return vector as a float array
      */
     public float[] toFloatArray(){
        float array[] = new float[3];
            array[0] = (float)x;
            array[1] = (float)y;
            array[2] = (float)z;
        return array;
     }
     public double getElement(int i){
        if(i==0) return x;
        if(i==1) return y;
        if(i==2) return z;
        else return Double.NaN;
     }

//     public Vector matrix3dTimesVector(Matrix3d m){
//        Vector result = new Vector();
//        for(int i=0;i<3;i++){
//            result.x += m.getElement(i, 0)*getElement(i);
//            result.y += m.getElement(i, 1)*getElement(i);
//            result.z += m.getElement(i, 2)*getElement(i);
//        }
//        return result;
//    }


    @Override
    public String toString(){
        return "["+x+","+y+","+z+"]";
    }
}
