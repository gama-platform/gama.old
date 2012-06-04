package msi.gama.jogl.utils.myarcball;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 5:18:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArcBall {
    private static final float Epsilon = 1.0e-5f;

    Vector3f StVec;          //Saved click vector
    Vector3f EnVec;          //Saved drag vector
    float adjustWidth;       //Mouse bounds width
    float adjustHeight;      //Mouse bounds height

    public ArcBall(float NewWidth, float NewHeight) {
        StVec = new Vector3f();
        EnVec = new Vector3f();
        setBounds(NewWidth, NewHeight);
    }

    public void mapToSphere(Point point, Vector3f vector) {
        //Copy paramter into temp point
        Point2f tempPoint = new Point2f(point.x, point.y);

        //Adjust point coords and scale down to range of [-1 ... 1]
        tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
        tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

        //Compute the square of the length of the vector to the point from the center
        float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

        //If the point is mapped outside of the sphere... (length > radius squared)
        if (length > 1.0f) {
            //Compute a normalizing factor (radius / sqrt(length))
            float norm = (float) (1.0 / Math.sqrt(length));
            // FIXME hdviet added 02/06/2012
            // Error : value of Z always = 0 -> after compute perpendicular vertor
            // 		   value of X & Y always = 0
            //Return the "normalized" vector, a point on the sphere
            vector.x = tempPoint.x * norm;
            vector.y = tempPoint.y * norm;
            vector.z = 0.0f;
        } else    //Else it's on the inside
        {
            //Return a vector to a point mapped inside the sphere sqrt(radius squared - length)
            vector.x = tempPoint.x;
            vector.y = tempPoint.y;
            vector.z = (float) Math.sqrt(1.0f - length);
        }

    }

    public void setBounds(float NewWidth, float NewHeight) {
        assert((NewWidth > 1.0f) && (NewHeight > 1.0f));

        //Set adjustment factor for width/height
        adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
        adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
    }

    //Mouse down
    public void click(Point NewPt) {
        mapToSphere(NewPt, this.StVec);
    }

    //Mouse drag, calculate rotation
    public void drag(Point NewPt, Quat4f NewRot) {
        //Map the point to the sphere
        this.mapToSphere(NewPt, EnVec);

        //Return the quaternion equivalent to the rotation
        if (NewRot != null) {
            Vector3f Perp = new Vector3f();

            //Compute the vector perpendicular to the begin and end vectors
            Vector3f.cross(Perp, StVec, EnVec);

            //Compute the length of the perpendicular vector
            if (Perp.length() > Epsilon)    //if its non-zero
            {
                //We're ok, so return the perpendicular vector as the transform after all
                NewRot.x = Perp.x;
                NewRot.y = Perp.y;
                NewRot.z = Perp.z;
                //In the quaternion values, w is cosine (theta / 2), where theta is rotation angle
                NewRot.w = Vector3f.dot(StVec, EnVec);
               /* System.out.println("------------"+
                		"x =" + NewRot.x + " " +
                		"y =" + NewRot.y + " "  +
                		"z =" + NewRot.z + " " +
                		"w =" + NewRot.w + " " );*/
            } else                                    //if its zero
            {
                //The begin and end vectors coincide, so return an identity transform
                NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
            }
        }
    }

}
