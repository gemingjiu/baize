package com.baize.common.core.constant;

/**
 * Token常量
 *
 * @author gemj
 */
public class TokenConstants {
    /**
     * 令牌自定义标识
     */
    public static final String AUTHENTICATION = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
            "b3BlbnNzaC1rZXktdjEAAAAACmFlczI1Ni1jdHIAAAAGYmNyeXB0AAAAGAAAABDs+h7KDY\n" +
            "QSd2PqQu3+FWP7AAAAEAAAAAEAAAIXAAAAB3NzaC1yc2EAAAADAQABAAACAQC9cTVgTQsX\n" +
            "0ltYrJ7ojrAAoCw1xNDaEwQojOHZfBpAaE/wfqOIxwmjy/+dOaB0Tb0y5lmgsNn5oSniNv\n" +
            "LSmJtOFAJERVRmnXgkysBmpuM8XqocpRWcHF+cu8DyZ5uvh1cAZSAKT8wh7PzjQNp527xm\n" +
            "E08pG3+Lva4XbT9sfQyUqGi/O/828ihNQwEzmQ7ZzRl4+wrFJCNztBvd0CYfq6NqgUXHNY\n" +
            "STpYFfBSEt30e4ITmhlMFEW8lUhTlBBFDcH7IiwX1FGFL3T58U6z42Y2rGbWAr1z14knTc\n" +
            "z1qg7hK2Ae/BnJKTq5QHMHgunT8gjKt1/a4KIDKC5rIk3CGXccHSkuUHE9NHoQfMr1OV1V\n" +
            "WOK9pJqa1WaYZuOE+MgiMCkVB97xTXdrZnVw5OaCtZZk98PMkwbJqShXtF8kNw8rHC0BNo\n" +
            "LeMpGWOrv4503MRgnsyuJJRdc/WWH4gpBbdJerUNrhBf8s76Y0L8l3BVI1h8T+apscdp7Y\n" +
            "uP3pCvlHoaQgBGAM8LUfDV4UapbTG7BKFz5xC2TnGF4FTXIek36SQziCH4B/3QDWAn/Y4L\n" +
            "3LF9xQl8wxFiLGR43GynGc5OyKFp6GxY9eL4YcYn99Cz3A78SXQurRzwaSRcBvYnLNVw+4\n" +
            "UTgM5tn1NqR6zp/WignhzCxrMZNtEi7MsBYmKNMuUYkwAAB0CZcCyj48h1niF00erA695C\n" +
            "DEskCdr/wOXjjdERGJSPVttBScs77TSaF2n0XZnZjjXr5y6qHe78IGF9z6jk5Yj4E4IrZ6\n" +
            "HUsyxY3VSLcGJ3yO3G4FIdHF6ytBC1SLjUuGG0Fumui6csV+WhRBJ2M/UFqICgH8R3r8qZ\n" +
            "L5ECX4rp+5WI3s9Alm2unA3vuy4eTqjpGppnJo4tmOnBD2hf231hEx4IEbwf+otZOPlX4/\n" +
            "fq47XPqwOsvrDYiPUCzaXQ/fVEA2NYpgHi8zdO5Z0vzJK8vxFTN8DujOTPTk3KNbQaxvC6\n" +
            "+QzKafn1Y3iNGGVhWiSBlP7U+mq59vZ59338AUsVMYzXLEBkgkLpdOnztvcYhTIX6wMS1c\n" +
            "8Wfdn5VRhEq8Apirtvueb1NZs4UEPfcZH/Ofjbj7+iBajGihyTxGDimVLoI6ThktYS9ny/\n" +
            "6JEmoKsSoi4HFdpbxPVROlku2fgz+cjVgdGr8usmGYjqq45JRoXJD/RqpPU5VBBQsmKeNy\n" +
            "MFPRfnrJnKwJQMVVuc8LBRl0rck3rBWa/2CIDmWuScS61pSw9Cd4TTpvF5zuXCiFeeHQ9V\n" +
            "ZfmckT1mMJmaO9nvc/KP2nuybY38BBFZplAy0ymePc1pM7naF4/ZdTNqGlRa1E5kfirSwH\n" +
            "fDl/s2PIZGp8a8HlmAmjwnjy4qpwR60h7qpiSnHemSnSnsbOGeuuEhdazbhj9pSpknQ5d0\n" +
            "RiVi4SHoe+KTuahha7f1u7DQX4VNAJUzgpqCvKoknvMIfs8XvRFOgPLQf1HKi/ecBUz3SP\n" +
            "08+oWH9nVSh9tttNdt/+R3Ab7sNsb87SwsSdCG88STc935rUJoQPy0BXqiP1dxEqiaH2QZ\n" +
            "tf/BwIXn3sB47tsViMTv4zJYGDM+QE58K9TsO28+ncLHsAaimpblRFTFMMbUYXQJJv40B7\n" +
            "sfPXcYHyXaLkDUEj/vEu+ZxX2Ig93DlxU3Anb0QqWBpiwgRlVxKxpvvcF8bmfd5PlPoutX\n" +
            "90nKrrFm8bXMhqNGgjVPqwvFc1aLjvUKff+O7ojGHsDG9YdAQmVBnhF2O5mbT7CEJplsCX\n" +
            "ftsAlbl5iQ6r3YBbNpbuucGP2W1Mudyxw2Dt/JJMMuC7FVOjqTxj5JpiVJ9CWHd9NoL1GC\n" +
            "UyulT0nZ6QuU8/rdwVcoQ0NCgtK8NZcE/+hzbISm2mGOygewF70JZktbJ0av5MIycY3s4V\n" +
            "xCcvfhZ7wuWLo0AvnX/TnqGfATjlME0CEci4ZN6P8z1J3xpwy2lmyc8OFP4Cl+u3VhfAzw\n" +
            "tQR9GHU2uTIsXbV3gKyi2iReZ0plGQib+tujpMaFoOhm42fmNRIlYQi43eAcW48DDGiRqP\n" +
            "iklE8iSJBkUbCy93wAksiGdceHDo7faD8O/ZnOs9UHUJKItdayiH7a6RnJqI4q2vfn5/mn\n" +
            "jsNsb9ZaJECuGqKIkuYQJmhlG2N8GPFWqtrV7lgS+6gAsswNei2OKWrMQl88sdrELyNekM\n" +
            "VWVhBAEiwY1DeetYGemFNSvbs1YZAI0Xw3cuZNHmjP/hPHBhVFZx9zl5qdntXfSg4HaAHh\n" +
            "cOeX8dDaTB9Lb0lWOAC/yJkm/A8Pue0t/jhPCgKtOj590wjEomeOPeYtM5i9MQ1tTOUHKb\n" +
            "V6UKnar6Qlu/Df5LB0OByX+hilZ7Om9OHmUBdWVSY06B1PuwtDs3Ot7onl0TGBYfwCefcY\n" +
            "IzBKJSoWQTAp6PNqahBhI76M9Ibc06NWaEgyoPIZ2j4LBmDWpyCSxeDpK3bPSJ0qzLuda4\n" +
            "ztKdfIZez2qt571Poew9RxD2EwHGZP7S+R1OkyHMqKtxlXqCdZq9JxF1XuyaWf+F8b0sMj\n" +
            "eqyIBPnaXieTQqQzubVew+hjoF/eRdkuT/XyhYRjunBktCTe6SYbDQkddLVvVpOvXRT2vC\n" +
            "aQiPnTNPAQwZxH8ZvRSFjEjHwrfKvZyvP+hqO+c/xcvdaPlENgqXfI7BAKSBDzwVk/tW7V\n" +
            "MLbjpvO96lIOpW0FiNnJbABH2RJXmUZIXdM+8hwTdqza6LYvV+zyireZX4+DFgm2VUpVhp\n" +
            "o1cX+vC7gEysO112VTKkinH2OrlX1MLTUQOPiIWbtdR8wF5i1d81JtUn8ssZY2H0HAPRPL\n" +
            "UmTPBbwTUtrN3soFMK2NZsPYSLEjf33KjRY7VSa9F/tQZ3rBL81CbBbDGJaWF0PWWSucHx\n" +
            "hozWVGKfMpTu/XzULDa18z6KIMS0U2Uqb9FZxQB3e3jmS+tH8bhIxzEdUesKNe+HJN9nrd\n" +
            "h2wzM8e5nUYswiHO0n8Wlv/YciOUwY3ZleJmfxZB/2N2WCnhX8ScoXCL30mCSHzS+vu4/a\n" +
            "RzMbLxxjiYrfJ24zgfMAFmdxTw690I7Wm1afuLUd69oHrnYueoV/DaClvddDZuHbZtP0kl\n" +
            "Y+49QWXAOQdlaf3KZXE1PSRl4E5YuZK58rnsUia65xHF8T+N03+Um8h8cAi+SzGfsjc8Xd\n" +
            "D2Ow==\n" +
            "-----END OPENSSH PRIVATE KEY-----\n";
}
