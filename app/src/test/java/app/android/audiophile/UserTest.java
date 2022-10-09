package app.android.audiophile;

import junit.framework.TestCase;

public class UserTest extends TestCase {

    User u;

    public void setUp() throws Exception {
        super.setUp();

        u = new User("t@t.com", "uname", "pass","abcd1","sfg");
    }

    public void testInsertIntoDb() {
        u.InsertIntoDb();
    }
}