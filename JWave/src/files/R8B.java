package files;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface R8B extends Library {
    R8B INSTANCE = (R8B) Native.loadLibrary("r8bsrc.dll", R8B.class);

    R8B SYNC_INSTANCE = (R8B) Native.synchronizedLibrary(INSTANCE);
}
