package com.frequentis.tdd.data;

import com.frequentis.tdd.User;

public final class Users {
    public static User random(){
        return new User(Randoms.randomAlphabetic("firstName_"),Randoms.randomAlphabetic("lastName_"),Randoms.randomAlphabetic("email_"));
    }

    public static User randomWithId() {
        return new User(Randoms.randomLong(), Randoms.randomAlphabetic("firstName_"),Randoms.randomAlphabetic("lastName_"),Randoms.randomAlphabetic("email_"));
    }
}
