package polly;

import polly.data.User;

public class MD5Tool {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Bitte nur einen Parameter angeben.");
            return;
        }
        
        String pw = args[0];
        User tmp = new User("", pw, 0);
        System.out.println(tmp.getHashedPassword());
    }
}