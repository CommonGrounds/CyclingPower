package dev.java4now.web.pojo;

import dev.java4now.web.model.User;
import dev.webfx.platform.ast.AstObject;
import dev.webfx.platform.ast.ReadOnlyAstObject;
import dev.webfx.stack.com.serial.spi.impl.SerialCodecBase;

// IMPORTANT - obavezno definisanje provider-a u application/webfx.xml
public final class UserSerialCodec extends SerialCodecBase<User> {
    // Codec identifier - should be unique
    private static final String CODEC_ID = "User";

    // Constant keys for serialization
    private static final String USERNAME_KEY = "name";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    // Constructor
    public UserSerialCodec() {
        super(User.class, CODEC_ID);
    }

    @Override
    public void encode(User user, AstObject serial) {
        // Encode each field using appropriate methods
//        encodeString(serial, CODEC_ID, CODEC_ID);
        encodeString(serial, USERNAME_KEY, user.getName());
        encodeString(serial, PASSWORD_KEY, user.getPassword());
        encodeString(serial, EMAIL_KEY, user.getEmail());
    }

    @Override
    public User decode(ReadOnlyAstObject serial) {
        // Decode fields to reconstruct the User object
        return new User(
                decodeString(serial, USERNAME_KEY),
                decodeString(serial, PASSWORD_KEY),
                decodeString(serial, EMAIL_KEY)
        );
    }
}
