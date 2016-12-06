package GameObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by Hoangelato on 30/11/2016.
 */
public class ConveyorNonSwitchDown extends ConveyorNonSwitch {
    protected ConveyorNonSwitchDown(int posX, int posY) {
        super(posX, posY);
        loadImage();
    }

    @Override
    public void loadImage() {
        super.loadImage();
        try {
            this.sprite = ImageIO.read(new File("resource/conveyor/nonswitch/nonswitch_down.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
