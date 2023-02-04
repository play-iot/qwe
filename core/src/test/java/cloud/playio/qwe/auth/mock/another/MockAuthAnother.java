package cloud.playio.qwe.auth.mock.another;

import cloud.playio.qwe.auth.AuthN;
import cloud.playio.qwe.auth.AuthZ;

@AuthN
public class MockAuthAnother {

    public boolean access() {return true;}

    @AuthZ(access = "any")
    public boolean create() {return true;}

}
