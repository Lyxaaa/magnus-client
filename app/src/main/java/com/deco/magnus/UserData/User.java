package com.personal.deco3801_app.UserData;

public class User {
    class IncorrectCredentialsException extends Exception {
        public IncorrectCredentialsException(String errorMessage) {
            super(errorMessage);
        }
    }
    private final int id;
    private final String fname;
    private final String lname;

    private User(int id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
    }

    public void login(String name, String password) throws IncorrectCredentialsException {
        if (!userExists(name)) {
            throw new IncorrectCredentialsException("User " + name + " does not exist");
        }
    }

    private boolean userExists(String name) {
        return true;
    }

    private int detailsCorrect(String name)

    public String[] getName() {
        return new String[]{fname, lname};
    }

    public int getId() {
        return id;
    }
}

