package GameObject;

/**
 * Created by admin on 11/5/2016.
 */
public class BoxReceiver {
    protected ColorBox color;

    public boolean Finish(ColorBox boxColor){
        if(boxColor != this.color){
            //tru diem
            return false;
        }
        //cong diem
        return true;
    }
}
