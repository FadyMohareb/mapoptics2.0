package DataTypes;

import Algorithms.DetectSV;

/*
 * @author Anisha
 *
 * */
@Deprecated
public class Translocation extends SV {

    public Translocation() {
        super();
    }

    @Override
    public void setType() {
        this.type = "Translocation";
    }
}
